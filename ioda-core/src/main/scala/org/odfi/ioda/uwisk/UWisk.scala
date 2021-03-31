package org.odfi.ioda.uwisk

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.ioda.data.protocols.{MetadataContainer, ProcessingContext}
import org.odfi.ioda.data.types.DataMessage
import org.odfi.ioda.logging.WithLogger
import org.odfi.ioda.uwisk.local.{LWisk, LWiskTrait}
import org.odfi.ioda.uwisk.messages.{EmptyMessage, TestTrigger}


/**
 * This is an instance of a uwisk
 *
 * This is the entry point which references packages, pipelines, actions and triggers them
 *
 */
class UWisk(val baseNamespace: String = "/") extends WithLogger with HarvestedResource {

  def getId = baseNamespace

  // Env
  //------------
  var selectedEnvironment = sys.props.get("org.odfi.ioda.uwisk.env").getOrElse("dev")

  // Runtime requests
  //---------------------
  val wiskImpl = new LWisk

  def getWisk: LWiskTrait = wiskImpl


  // Context
  //--------------


  // Utils
  //-----------
  def splitParams(str: String) = str match {
    case null => Map[String, Any]()
    case params =>
      params.split(",").filter(v => v != null && v.length > 0).map {
        p =>
          this.logger.info("Action parameters: " + p)
          p.split("=") match {

            case bool if (bool.size == 1) =>
              (bool(0).toString, true)
            case value =>
              (value(0).toString, value(1).toString)
          }
      }.toMap
  }


  def splitNamespaceToPackageAndActionQuery(q: String) = {
    """([^#]+#\w+)\/@([^{}]+)(?:\{([\w=_\-\.,]+)\})?""".r.findFirstMatchIn(q) match {
      case None => None
      case Some(m) =>
        val params = splitParams(m.group(3))
        Some(m.group(1), m.group(2), params)

    }
  }

  def splitNamespaceToPackageAndPipelineQuery(q: String) = {
    """([^#]+#\w+)\/(.+)(?:\{([\w=_\-\.,]+)\})?""".r.findFirstMatchIn(q) match {
      case None => None
      case Some(m) =>
        val params = splitParams(m.group(3))
        Some(m.group(1), m.group(2), params)

    }
  }

  def findPackageAndActionQuery(q: String) = {
    splitNamespaceToPackageAndActionQuery(q) match {
      case None => None
      case Some((p, action, params)) =>
        this.wiskImpl.getPackage(p) match {
          case Some(p) =>
            Some(p, action, params)
          case None =>
            println("No Package")
            None
        }
    }
  }

  def findPackageAndPipelineQuery(q: String) = {
    splitNamespaceToPackageAndPipelineQuery(q) match {
      case None => None
      case Some((p, pipelineName, params)) =>
        this.wiskImpl.getPackage(p) match {
          case Some(p) =>
            Some(p, p.getPipeline(pipelineName), params)
          case None => None
        }
    }
  }

  // Packages
  //-------------
  def listPackages = this.wiskImpl.listPackages

  /**
   * Provided package is duplicated
   * @param ns
   * @param p
   * @return
   */
  def importPackage(ns:String,p:wpackage)= {
    registerPackage(ns,p.duplicate)
  }

  /**
   * Create Register package
   *
   * @param ns
   * @param p
   * @return
   */
  def registerPackage(ns: String, p: wpackage) = {


    try {

      // Check
      p.uwisk = this
      p.packageNamespace = baseNamespace + "/" + ns
      p.check

    } catch {
      case e: Throwable =>
        sys.error(s"Could no check package:  ${p.getPackageAbsolutePath} -> " + e.getLocalizedMessage)
    }

    // NS
    val packageNS = ("/" + ns + "/" + p.getPackageName).trim.replaceAll("/+", "/")

    // Register
    this.wiskImpl.registerPackage(packageNS, p)
  }


  def resolveAbsolutePipeline(ns: String) = {

    this.findPackageAndPipelineQuery(ns) match {
      case Some((wpackage, Some(p), params)) =>
        (wpackage, p)
      case other =>
        sys.error(s"Could not find Pipeline in package for name $ns")
    }

  }

  def resolveAbsolutePipelines(ns: List[String]) = ns.map(resolveAbsolutePipeline)

  /**
   * Start Pipeline using ID
   *
   * @param name
   * @param msg
   */
  def startPipeline(name: String, msg: DataMessage = EmptyMessage(),trace : utrace = new utrace()) = {

    this.findPackageAndPipelineQuery(name) match {
      case Some((pack, Some(pipeline), params)) =>

        // Create context
        val context = new ProcessingContext
        params.foreach {
          case (k, v) =>
            context.addMetadata(k, v)
        }

        // Run
        this.runPipeline(pipeline, msg, context,trace)

      case other =>
        sys.error(s"Could not start pipeline $name")
    }

  }

  def runPipeline(spipeline: wpackageTraitpipeline, msg: DataMessage, pipelineContext: ProcessingContext,trace : utrace = new utrace()) = {

    logger.info(s"Starting Pipeline ${spipeline.getAbsoluteName}")

    // Current Pipelines for recusrive stack
    var pipelinesStack = scala.collection.mutable.Stack[(wpackageTraitpipeline, Option[wpackageTraitpipelineTraitstep])]()
    pipelinesStack.addOne((spipeline, None))
    var currentMessage: DataMessage = msg

    // Create top trace
    val topPipeline = trace.addPipelineTrace(spipeline)
    topPipeline.top = true
    topPipeline.runTimeOrCreate.total = 0

    try {
      while (!pipelinesStack.isEmpty) {

        val (pipeline, step) = pipelinesStack.pop()

        this.logger.info(s"- Processing Pipeline ${pipeline.getAbsoluteName} with step $step")
        pipelineContext.metadata.foreach {
          case (k, v) =>
            this.logger.info(s"-- M: $k -> ${v.value}")
        }

        // trace
        val tPipeline = trace.addPipelineTrace(pipeline)
        tPipeline

        // Run Pipelines
        (pipeline, step) match {

          // If Step is java -> Final and run
          //----------
          case (pipeline, Some(step)) if (!pipeline.ignore && step.isJavaPipeline) =>

            // Run Step
            this.logger.info(s"- Java Class: ${step.id}")


            // Create
            val pImpl = pipeline.createWPipelineForStep(step).get

            // Set Metadata
            addMetadataFromStep(pipelineContext, pipeline, step)
            /*step.metadatasAsScala.foreach {
              case m if (m.value != null) =>
                pipelineContext.addMetadata(m.id, m.value)
              case other =>
            }*/

            // Run Pipeline
            val pRunTime = tPipeline.runPipeline {
              // this.logger.info(s"- MSG: ${msg}")
              if (!trace.dry_run) {
                pImpl.downP(currentMessage, pipelineContext)
              } else {
                this.logger.info("Not calling code, dry-run requested")
              }

            }

            topPipeline.runTimeOrCreate.total += pRunTime


            currentMessage = currentMessage.getTransformedMessage



          // Steps
          //---------
          case (stepsPipelines, sourceStep) if (!pipeline.ignore && stepsPipelines.stepsAsScala.size > 0) =>

            this.logger.info(s"- Steps found")

            stepsPipelines.stepsAsScala.reverse.foreach {
              step =>
                addMetadataFromStep(pipelineContext, pipeline, step)
                step match {
                  case jstep if (jstep.id.startsWith("java/")) =>
                    //this.logger.info(s"  -> JStep ${jstep.id}")
                    pipelinesStack.push((stepsPipelines, Some(jstep)))
                  case pstep =>
                    //this.logger.info(s"  -> PStep ${pstep.id}")
                    pipelinesStack.push((pipeline.resolvePipeline(pstep.id).get, Some(pstep)))
                }
            }

          case (p, _) if (p.ignore) =>
            logger.info(s"Pipeline ${pipeline.getAbsoluteName} ignored")
          case other =>
            logger.info(s"Cannot process pipeline ${pipeline.id} with ${step}, unknown setup")

          //-- JPipeline
          //------------
          /*case javaPipeline if (javaPipeline.isJavaPipeline) =>

            this.logger.info(s"- Running Pipeline Java Class: ${pipeline.id}")

            // Reset Metadata to avoid sharing betwwen pipelines
            // Inject in message
            //msg.metadata = Map()

            pipeline.getImplentationJava match {
              case Some(p) =>

                this.logger.info(s"- Java Class: ${p}")

                // Run Pipeline
                // this.logger.info(s"- MSG: ${msg}")
                p.downP(currentMessage, pipelineContext)

                currentMessage = currentMessage.getTransformedMessage

              case None =>
                msg
            }

          //-- Imports
          //-------------
          case importedPipeline if (importedPipeline.isImportedPipeline) =>

            logger.info("- Found imported Pipeline")
            val nextPipelines = importedPipeline.getImportedPipelines
            nextPipelines.foreach {
              p =>
                logger.info(s"-- ${p.id}")
            }

            pipelinesStack ++= (importedPipeline.getImportedPipelines)


          //-- No Impl
          //-------------
          case other =>
            logger.info("- Found Pipeline, without implementation")

        }


        // Post
        //---------------------
        currentMessage.hasErrors match {
          case true =>
            currentMessage.consumeErrors {
              e =>
                this.logger.error("- Error during run: " + e.getLocalizedMessage)
            }
          case false =>
            // Get Post
            pipeline.postsAsScala.foreach {
              post =>

                this.logger.info(s"- Found Post Pipeline: ${post.id}")
                val (targetPackage, targetPipeline) = this.resolveAbsolutePipeline(post.id)

                // Inject metadata from pipeline and post definition into context
                targetPipeline.parametersAsScala.foreach {
                  case p if (p.default != null) =>
                    pipelineContext.addMetadata(p.id, targetPipeline.wpackage.resolveValue(p.default))
                  case p =>
                }
                post.metadatasAsScala.foreach {
                  m =>
                    pipelineContext.addMetadata(m.id, targetPipeline.wpackage.resolveValue(m.value))
                }


                pipelinesStack += (targetPipeline)
              /*targetPipeline.getImplentationJava match {
              case Some(postPipeline) =>

                this.logger.info(s"- Java Class: ${postPipeline}")

                // Inject metadata
                targetPipeline.parametersAsScala.foreach {
                  case p if (p.default != null) =>
                    pipelineContext.addMetadata(p.id, pipeline.wpackage.resolveValue(p.default))
                  case p =>
                }
                post.metadatasAsScala.foreach {
                  m =>
                    pipelineContext.addMetadata(m.id, pipeline.wpackage.resolveValue(m.value))
                }
                /*post.metadatasAsScala.foreach {
                  m =>
                    postMsg.addMetadata(m.id, m.value)
                }*/

                pipelineContext.metadata.foreach {
                  case (k, v) =>
                    this.logger.debug(s"Metadata bedore run: $k -> ${v.value}")
                }

                // Down
                postPipeline.downP(currentMessage, pipelineContext)


                // Errors
                currentMessage.consumeErrors {
                  e =>
                    this.logger.error("Error during post-run (msg): " + e.getLocalizedMessage)
                }

                postPipeline.consumeErrors {
                  e =>
                    this.logger.error("Error during post-run (msg): " + e.getLocalizedMessage)
                }

              case None =>
                this.logger.warn(s"Could not run post pipeline ${post.id}, no implementation")
            }
*/
            }*/
        }

      }

    }
    catch {
      case e: Throwable =>
        e.printStackTrace()
    }

    trace


  }

  def runTrigger(iname: String, msg: DataMessage): Unit = {

    val name = ("/" + iname).replaceAll("//+", "/")
    println("Rtrigger: " + logger)
    logger.info(s"runTrigger: $name with $msg")

    // Search for Package
    //-------------
    findPackageAndActionQuery(name) match {
      case Some((wpackage, action, params)) =>

        this.logger.info(s"Found source package for action $name ($params)")

        // Set Metadata of action to message
        addMetadataFromAction(msg, params)

        // Run on all packages
        //--------------------
        this.wiskImpl.getAllPackages.foreach {
          currentPackage =>

            this.logger.info(s"Calling $name (short=$action) on package: ${currentPackage.getPackageAbsolutePath}")


            // Run Pipelines
            //----------------
            val pipelines = currentPackage.gatherPipelinesForTrigger(name, action)
            if (pipelines.size > 0) {


            }
            pipelines.foreach {
              case pipeline =>

                // Run Pipelines
                //---------------------------
                this.logger.info(s"Found Pipeline 2: ${pipeline.id} // ${msg.metadata}")
                try {
                  val pipelineContext = new ProcessingContext

                  pipeline.parametersAsScala.foreach {
                    case p if (p.default != null) =>
                      pipelineContext.addMetadata(p.id, p.default.toString)
                    case p =>
                  }

                  msg.metadata.foreach {
                    case (k, v) =>
                      pipelineContext.addMetadata(k, v)
                  }

                  runPipeline(pipeline, msg, pipelineContext)
                } catch {
                  case e: Throwable =>
                    e.printStackTrace()
                }


            }
        }
        // EOF Run on all package

        // Collect
        //----------------
        wpackage.getAction(action) match {
          case Some(sourceAction) =>
            sourceAction.collectsAsScala.foreach {
              collectAction =>
                this.logger.info(s"Collecting: ${collectAction.id}")

                val collectable = this.wiskImpl.getPendingTriggers(collectAction.id)
                this.logger.info(s"- Found ${collectable.size} requests")

                collectable.foreach {
                  c =>
                    try {
                      runTrigger(c, msg)
                    } catch {
                      case e: Throwable =>
                        this.logger.error("Error while running collected action: " + e.getLocalizedMessage)
                    }
                }


            }
          case None =>
        }


      case None =>
        println("NP")
        this.logger.error(s"No package for $name")
        sys.error(s"Cannot find package for $name")
    }

  }


  // Utils
  //------------
  def instanciatePipeline(cl: Class[_]) = this.wiskImpl.instantiator.newInstance(cl)

  def addMetadataFromStep(context: MetadataContainer, pipeline: wpackageTraitpipeline, step: wpackageTraitpipelineTraitstep) = {
    step.metadatasAsScala.foreach {
      case m if (m.value != null) =>
        context.addMetadata(m.id, pipeline.wpackage.resolveValue(m.value))
      case other =>
        context.addMetadata(other.id, true)

    }
  }

  def addMetadataFromAction(msg: DataMessage, params: Map[String, Any]) = {

    params.foreach { case (k, v) => msg.addMetadata(k, v) }
  }

}

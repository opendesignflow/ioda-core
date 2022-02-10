package org.odfi.ioda.uwisk

import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.ioda.data.protocols.{PMetadataContainer, ProcessingContext}
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
          this.logger.debug("Action parameters: " + p)
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
   *
   * @param ns
   * @param p
   * @return
   */
  def importPackage(ns: String, p: wpackage) = {
    registerPackage(ns, p.duplicate)
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
  def startPipeline(name: String, msg: DataMessage = EmptyMessage(), trace: utrace = new utrace()) = {

    this.findPackageAndPipelineQuery(name) match {
      case Some((pack, Some(pipeline), params)) =>

        // Create context
        val context = new ProcessingContext
        params.foreach {
          case (k, v) =>
            context.addMetadataFromValue(k, v)
        }

        // Run
        this.runPipeline(pipeline, msg, context, trace)

      case other =>
        sys.error(s"Could not start pipeline $name")
    }

  }

  def runPipeline(spipeline: wpackageTraitpipeline, msg: DataMessage, pipelineContext: ProcessingContext, trace: utrace = new utrace()) = {

    logger.info(s"Starting Pipeline ${spipeline.getAbsoluteName}")
    logger.debug(s"DD Starting Pipeline ${spipeline.getAbsoluteName}")
    // Current Pipelines for recusrive stack
    var pipelinesStack = scala.collection.mutable.Stack[(wpackageTraitpipeline, Option[wpackageTraitpipelineTraitstep])]()
    pipelinesStack.addOne((spipeline, None))
    var currentMessage: DataMessage = msg

    // Create top trace
    val topPipeline = trace.addPipelineTrace(spipeline)
    topPipeline.top = true
    topPipeline.runTimeOrCreate.total = 0

    try {
      while (pipelinesStack.nonEmpty) {

        val (pipeline, step) = pipelinesStack.pop()

        this.logger.debug(s"- Processing Pipeline ${pipeline.getAbsoluteName} with step $step")
        if (this.logger.isDebugEnabled) {

          pipelineContext.metadatasAsScala.foreach {
            case (m) =>
              this.logger.debug(s"-- M: ${m.id} -> ${m.value}")
          }
        }

        // trace
        val tPipeline = trace.addPipelineTrace(pipeline)

        // Run Pipelines
        (pipeline, step) match {

          // If Step is java -> Final and run
          //----------
          case (pipeline, Some(step)) if (!pipeline.ignore && step.isJavaPipeline) =>

            // Run Step
            this.logger.debug(s"- Java Class: ${step.id}")


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
                this.logger.debug("Not calling code, dry-run requested")
              }

            }

            topPipeline.runTimeOrCreate.total += pRunTime


            currentMessage = currentMessage.getTransformedMessage



          // Steps
          //---------
          case (stepsPipelines, sourceStep) if (!pipeline.ignore && stepsPipelines.stepsAsScala.size > 0) =>

            this.logger.debug(s"- Steps found")

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
            logger.debug(s"Pipeline ${pipeline.getAbsoluteName} ignored")
          case other =>
            logger.error(s"Cannot process pipeline ${pipeline.id} with ${step}, unknown setup")

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

        this.logger.debug(s"Found source package for action $name ($params)")

        // Set Metadata of action to message
        addMetadataFromAction(msg, params)

        // Run on all packages
        //--------------------
        this.wiskImpl.getAllPackages.foreach {
          currentPackage =>

            this.logger.debug(s"Calling $name (short=$action) on package: ${currentPackage.getPackageAbsolutePath}")


            // Run Pipelines
            //----------------
            val pipelines = currentPackage.gatherPipelinesForTrigger(name, action)
            if (pipelines.size > 0) {


            }
            pipelines.foreach {
              case pipeline =>

                // Run Pipelines
                //---------------------------
                this.logger.debug(s"Found Pipeline 2: ${pipeline.id} // ${msg.metadatasAsScala}")
                try {
                  val pipelineContext = new ProcessingContext

                  pipeline.parametersAsScala.foreach {
                    case p if (p.default != null) =>
                      pipelineContext.addMetadataFromValue(p.id, p.default)
                    case p =>
                  }

                  msg.metadatasAsScala.foreach {
                    case m =>
                      pipelineContext.addMetadataFromValue(m.id, m.value)
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
                this.logger.debug(s"Collecting: ${collectAction.id}")

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

  def addMetadataFromStep(context: PMetadataContainer, pipeline: wpackageTraitpipeline, step: wpackageTraitpipelineTraitstep) = {
    step.metadatasAsScala.foreach {
      case m if (m.value != null) =>
        context.addMetadataFromValue(m.id, pipeline.wpackage.resolveValue(context,m.toString))
      case other =>
        context.addMetadataFromValue(other.id, true)

    }
  }

  def addMetadataFromAction(msg: DataMessage, params: Map[String, Any]) = {

    params.foreach { case (k, v) => msg.addMetadataFromValue(k, v) }
  }

}

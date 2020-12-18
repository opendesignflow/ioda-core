package org.odfi.ioda.uwisk
import org.odfi.ioda.pipelines.DefaultPipeline
class wpackageTraitpipeline extends wpackageTraitpipelineTrait {

  var wpackage : wpackage = _

  var overrideMetadata = Map[String,String]()

  def isJavaPipeline = this.implementationOption match {
    case Some(i) if (i.javaClass!=null) => true
    case other => false
  }

  def isImportedPipeline = this.implementationOption match {
    case Some(im) if im.importsAsScala.nonEmpty => true
    case other => false
  }

  def getImportedPipelines = this.implementation.importsAsScala.map {
    case global  if (global.id.startsWith("/")) =>
      sys.error("TODO")
    case local =>

        this.wpackage.getPipeline(local.id) match {
          case Some(p) =>

            // Set Local Metadata
            p
          case None =>
            sys.error(s"Cannot find local pipeline ${local.id}")
        }


      // Resolve

  }

  def getImplentationJava = isJavaPipeline match {
    case false => None
    case true =>
      Some(wpackage.uwisk.wiskImpl.instantiator.newInstanceFromString(this.implementation.javaClass))
      //Some(IWisk.instanciatePipeline(Thread.currentThread().getContextClassLoader.loadClass(this.implementation.javaClass)))

  }

  def createWPipelineForStep(step:wpackageTraitpipelineTraitstep) = step.isJavaPipeline match {
    case false => None
    case true =>
      Some(wpackage.uwisk.wiskImpl.instantiator.newInstanceFromString(step.getJavaClass))
  }

  def resolvePipeline(id:String) = {
    this.wpackage.getPipeline(id) match {
      case Some(p) => Some(p)
      case None =>
        Some(this.wpackage.uwisk.resolveAbsolutePipeline(id)._2)
    }
  }



}

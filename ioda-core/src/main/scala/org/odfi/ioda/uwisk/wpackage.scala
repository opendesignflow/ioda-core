package org.odfi.ioda.uwisk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import java.io.{File, FileInputStream, InputStream, InputStreamReader}
import java.net.URL


class wpackage extends wpackageTrait {


  var uwisk: UWisk = _

  def check = {


    // Check id
    assert(id != null, "ID must be defined")
    assert(namespace != null, "Namespacee must be defined")



    // Check Pipelines
    this.pipelinesAsScala.foreach {
      p =>
        // Propagate to pipeline instance
        p.wpackage = this

        // Check Pipelines instance
        p.isJavaPipeline match {
          case true =>
            p.getImplentationJava
          case false =>
        }

    }


  }

  def gatherPipelinesForTrigger(fullName:String,action: String) = {

    this.pipelinesAsScala.filter {
      p =>
        p.triggersAsScala.find {
          ta =>
            //println(s"testing against $ta")
            fullName.startsWith(ta) || action.startsWith(ta)
        }.isDefined
    }.toList

  }

  def hasPipeline(id: String) = getPipeline(id).isDefined

  def getPipeline(id: String) = this.pipelinesAsScala.find(p => p.id != null && p.id == id)

  def getAction(id: String) = this.actionsAsScala.find(a => a.id != null && a.id == id)

  def isJavaImplementationPipeline(p: wpackageTraitpipeline) = {
    p.implementationOption match {

      case Some(impl) if (impl.javaClass != null) =>
        true
      case other =>
        false

    }
  }

  def getPipelineJavaImplementation(p: wpackageTraitpipeline) = {
    p.implementationOption match {

      case Some(impl) if (impl.javaClass != null) =>
        Some(Thread.currentThread().getContextClassLoader.loadClass(impl.javaClass).getDeclaredConstructor().newInstance())
      case other => None

    }
  }

  def getNamespaceOrEmpty = namespace match {
    case null => ""
    case other => other
  }

  /**
   * Get Package Name (NS and id)
   *
   * @return
   */
  def getPackageName = (getNamespaceOrEmpty + "/#" + id).replaceAll("/+", "/")

  /**
   * The NS this package is placed into
   */
  var packageNamespace = "/"

  def getPackageAbsolutePath = (packageNamespace + "/" + getNamespaceOrEmpty + "/#" + id).replaceAll("/+", "/")


  // Value resolution
  //------------------
  val valRegexp = """\$\{?([\w:_\-\.]+)\}?""".r

  def resolveValue(str: String): String = {

    var resStr = str

    valRegexp.findAllMatchIn(str)
      .foreach {
        matchRes =>

          // Resolve
          val (envName, varName) = matchRes.group(1).split(":") match {
            case envName if (envName.size == 2) =>
              (envName(0), envName(1))
            case other => sys.error(s"Variable definition incorrect: ${matchRes.matched}")
          }
          val resolved = findEnvironmentValue(envName, varName)

          // Replace
          resStr = resStr.replace(matchRes.matched, resolved)


      }

    resStr

  }

  def findEnvironmentValue(env: String, varName: String): String = {

    this.environmentsAsScala.find(_.id == env) match {
      case Some(renv) =>
        renv.metadatasAsScala.find(_.id == varName) match {
          case Some(rvar) if (rvar.value != null && rvar.value.contains(varName)) =>
            sys.error("Variable value $env:$varName would recurse")
          case Some(rvar) if (rvar.value == null) =>
            sys.error(s"Variable value $env:$varName not defined")
          case Some(rvar) =>
            resolveValue(rvar.value)
          case None =>
            sys.error(s"Value $env:$varName not found in environment $env")
        }
      case None => sys.error(s"Could not find environment $env for $env:$varName")
    }

  }
}

object wpackage {

  def apply(f: File): wpackage = {
    val is = new FileInputStream(f)
    try {
      return apply(is)
    } finally {
      is.close()
    }
  }

  def apply(u: URL): wpackage = {
    val is = u.openStream()
    try {
      return apply(is)
    } catch {
      case e: Throwable =>

        println("Faileed loading: " + u.toExternalForm)
        throw e
    }
    finally {
      is.close()
    }
  }

  def apply(is: InputStream): wpackage = {
    val mapper = new ObjectMapper(new YAMLFactory)
    mapper.findAndRegisterModules()

    mapper.readValue(new InputStreamReader(is), classOf[wpackage])
  }
}

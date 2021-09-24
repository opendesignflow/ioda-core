package org.odfi.ioda.uwisk

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationContext, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.idyria.osi.ooxoo.lib.json.model.JSONHelper
import org.odfi.ioda.data.protocols.PMetadataContainer

import java.io.{File, FileInputStream, InputStream, InputStreamReader}
import java.net.URL
import javax.json.{JsonString, JsonValue}


class wpackage extends wpackageTrait {

  var uwisk: UWisk = _

  var sourceYAML: Option[URL] = None

  def duplicate = {


    val p = sourceYAML match {
      case Some(src) =>
        wpackage(src)
      case None =>
        sys.error("Cannot duplicate package without source URL")
    }

    p
  }

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

  def gatherPipelinesForTrigger(fullName: String, action: String) = {

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

  def resolveValue(context: PMetadataContainer, str: String): String = {

    var resStr = str

    // Match variable name against pattern to check correctness
    valRegexp.findAllMatchIn(str)
      .foreach {
        matchRes =>

          // Resolve
          //----------
          val (envName, varName) = matchRes.group(1).split(":") match {

            // If Environment is provided; use it
            case envName if (envName.size == 2) =>
              (envName(0), envName(1))

            // If only variable name, lookup environment
            case other if (other.size == 1) =>

              //  Use currently defined environment
              (uwisk.selectedEnvironment, other(0))


            case other =>
              sys.error(s"Variable definition incorrect: ${matchRes.matched}")
          }
          val resolved = findEnvironmentValue(context, envName, varName)

          // Replace
          resStr = resStr.replace(matchRes.matched, resolved)


      }

    resStr

  }

  /**
   * Search ${ENV:VARNAME} formats in environemnt or fallback to metadata
   *
   * @param env
   * @param varName
   * @return
   */
  def findEnvironmentValue(context: PMetadataContainer, env: String, varName: String): String = {

    this.environmentsAsScala.find(_.id == env) match {
      // Found env
      case Some(renv) =>
        renv.metadatasAsScala.find(_.id == varName) match {
          case Some(rvar) if (rvar.value != null && rvar.toString.contains(varName)) =>
            sys.error("Variable value $env:$varName would recurse")
          case Some(rvar) if (rvar.value == null) =>
            sys.error(s"Variable value $env:$varName not defined")
          case Some(rvar) =>
            resolveValue(context, rvar.toString)
          case None =>
            sys.error(s"Value $env:$varName not found in environment $env")
        }
      // NoEenv -> Metadata
      case None =>
        context.getMetadata(varName) match {
          case Some(metadata) =>
            metadata.asString
          case None =>
            sys.error(s"Could not find environment $env for $env:$varName, or metadata for $varName")
        }

    }

  }
}

object wpackage {

  def apply(f: File): wpackage = {
    val is = new FileInputStream(f)
    try {
      return apply(f.toURI.toURL)
    } finally {
      is.close()
    }
  }

  def apply(u: URL): wpackage = {
    val is = u.openStream()
    try {
      val p = apply(is)
      p.sourceYAML = Some(u)
      p
    } catch {
      case e: Throwable =>

        println("Failed loading: " + u.toExternalForm)
        throw e
    }
    finally {
      is.close()
    }
  }

  def apply(is: InputStream): wpackage = {

    JSONHelper.fromYAML[wpackage](new InputStreamReader(is))
    /*// Create Mapper
    val mapper = new ObjectMapper(new YAMLFactory)
    mapper.findAndRegisterModules()

    val m = new SimpleModule()
    m.addDeserializer(classOf[JsonValue], new JsonValueDesierualiser)
    mapper.registerModule(m)

    mapper.readValue(new InputStreamReader(is), classOf[wpackage])*/
  }

  class JsonValueDesierualiser extends com.fasterxml.jackson.databind.JsonDeserializer[JsonValue] {
    override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsonValue = {
      println("IN DESER")
      javax.json.Json.createValue("OK")
      // new JsonString("OK")
    }
  }
}

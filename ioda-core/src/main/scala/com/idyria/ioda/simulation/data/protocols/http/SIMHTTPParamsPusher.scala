package com.idyria.ioda.simulation.data.protocols.http

import org.odfi.wsb.fwapp.Site
import com.idyria.ioda.simulation.data.phy.SIMPhysicalPushInterface
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import scala.util.Random
import java.net.URL
import java.net.HttpURLConnection

/**
 * Site should be a IODA HTTP base site
 */
class SIMHTTPParamsPusher extends SIMPhysicalPushInterface {

  def configFromSite(targetSite: Site) = {

    //-- Get Top Site
    val topSite = targetSite.findTopMostIntermediaryOfType[Site] match {
      case None => targetSite
      case Some(s) => s
    }

    //-- Update Port
    topSite.engine.network.connectors.collectFirst {
      case http: HTTPConnector => http.port
    } match {
      case Some(port) =>
        this.config.get.setInt("port", port)
      case None =>
    }

    //-- Update base path
    config.get.setString("basePath", targetSite.fullURLPath)

  }

  this.onConfigModelUpdated {

  }

  def close = {
    true
  }

  def open = {
    true
  }

  def triggerPush = {

    //-- Check port and base path
    val (basePath, port) = (this.config.get.getString("basePath"), this.config.get.getInt("port"))

    assert(basePath.isDefined, "Base Path must be defined to trigger push")
    assert(port.isDefined, "Port must be defined to trigger push")

    //-- Create params
    this.config.get.getKey("parameters", "list") match {
      case Some(parameters) if (parameters.values.size == 0) =>

        logWarn[SIMHTTPParamsPusher]("Parameters config is empty, no parameters to generate")

      case None =>
        logWarn[SIMHTTPParamsPusher]("No parameters config of type list defined to generate parameters")

      case Some(parameters) =>

        val generatedParameters = parameters.values.map {
          parameter =>

            logFine[SIMHTTPParamsPusher]("Generating parameter: " + parameter)

            this.config.get.getKey(parameter) match {
              case None =>
                logWarn[SIMHTTPParamsPusher](s"Parameter $parameter has no config key, don't know how to generate")
                None
              case Some(parameterConfig) if (parameterConfig.keyType.toString == "value") =>

                Some(parameter.toString -> parameterConfig.values(0).toString())

              case Some(parameterConfig) if (parameterConfig.keyType.toString == "generate") =>

                //--
                var count = parameterConfig.values.find(_.toString().startsWith("count")).get.split("=").last.toInt
                var vtype = parameterConfig.values.find(_.toString().startsWith("type")).get.split("=").last
                var min = parameterConfig.values.find(_.toString().startsWith("min")).get.split("=").last.toInt
                var max = parameterConfig.values.find(_.toString().startsWith("max")).get.split("=").last.toInt
                var stripes = parameterConfig.values.find(_.toString().startsWith("stripes")) match {
                  case Some(stripes) => Some(stripes.split("=").last.toInt)
                  case other => None
                }

                //-- Generate Values
                //-- FIXME support other types than int
                val values = stripes match {
                  case Some(s) =>

                    
                    (0 until count).toList.grouped(s).zipWithIndex.map {
                      case (g,i) if ((i%2) == 0) => g.map(_ => min)
                      case (g,i) => g.map(_ => max)
                        
                        
                    }.flatten
                    
                  case other =>

                    val values = (0 until count).map {
                      i =>
                        Math.abs(Random.nextGaussian() * max).toInt

                    }
                    values
                }

                //-- Make string
                Some(parameter.toString -> values.mkString(","))

              case Some(parameterConfig) =>

                logWarn[SIMHTTPParamsPusher](s"Parameter $parameter has no config key of type ${parameterConfig.keyType}, don't know how to generate this")
                None
            }

        }.collect { case p if (p.isDefined) => p.get }.toList

        //-- Turn in parameter=value and make string with "&&
        val urlString = generatedParameters.map {
          case (name, values) =>
            s"$name=$values"
        }.mkString("&")

        logFine[SIMHTTPParamsPusher]("Generated URL Parameters: " + urlString)

        //-- Make push
        val finalUrlString = "http://localhost:" + port.get + "/" + basePath.get + "/incoming/data/params"
        val finalURL = new URL(finalUrlString)

        logFine[SIMHTTPParamsPusher]("Pushing to URL String: " + finalUrlString)

        val connection = finalURL.openConnection().asInstanceOf[HttpURLConnection]

        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        
        //-- Set Request properties
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", urlString.length().toString());
        connection.setUseCaches(false);
        
        this.config.get.getString("ioda.uid") match {
          case Some(uid) =>
            connection.setRequestProperty("X-IODA-UID", uid)
          case None => 
        }
        
        //-- Write and finish
        connection.getOutputStream().write(urlString.getBytes("UTF-8"))
        connection.getOutputStream.flush()
        connection.getResponseCode

    }

  }

}
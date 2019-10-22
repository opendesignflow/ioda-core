package com.idyria.platforms.std

import org.odfi.wsb.fwapp.DefaultSite
import java.net.URL
import java.net.HttpURLConnection
import com.idyria.ioda.env.EnvironmentTraitDataPath
import com.idyria.ioda.data.types.StringValuesMessage
import com.idyria.ioda.data.types.StringValueMessage
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.ioda.data.types.DataMessage
import com.idyria.ioda.data.types.DataMapMessage
import org.odfi.wsb.fwapp.Site
import com.idyria.ioda.data.protocols.params.ParamsMessage
import com.idyria.ioda.data.protocols.params.ParamValue
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

class StdPlatformHTTPInterface {

}
object StdPlatformHTTPInterface extends Site("/ioda") {

  /**
   * VCID to datapath mapping
   */
  var datapathMapping = Map[String, EnvironmentTraitDataPath]()

  def mapDatapathToVCID(p: (String, EnvironmentTraitDataPath)) = {
    this.datapathMapping = this.datapathMapping + p
  }

  // Statistics
  //-------------

  var statVirtualChannels = Map[String, Long]()

  def statVirtualChannelsSeen(dm: DataMessage) = this.synchronized {
    dm.virtualChannel match {
      case Some(vc) =>

        this.statVirtualChannels = this.statVirtualChannels + (vc -> System.currentTimeMillis())

      case None =>
    }
  }

  // tree
  //----------------
  "/incoming" is {

    "/data" is {

      onPOST {

        req =>

          logFine[StdPlatformHTTPInterface]("Got Data request, checking VCID")

          req.ensureURLParameters(List("vcid"))

      }

      "/value" post {

        req =>

          logFine[StdPlatformHTTPInterface]("Checking value parameter")

          req.ensureURLParameters(List("value"))

          //-- Create Data Message
          val stringValueMessage = new StringValueMessage
          stringValueMessage.value = Some(req.getURLParameter("value").get)
          stringValueMessage.virtualChannel = req.getURLParameter("vcid")

          logFine[StdPlatformHTTPInterface]("Got Value request for VCID: " + stringValueMessage.virtualChannel)

          //-- Map to datapath
          this.datapathMapping.foreach {
            case (m, dp) if (stringValueMessage.virtualChannel.get.toString.matches(m)) =>

              println("Found DP")
              dp.pushMessage(stringValueMessage)

            case other =>
          }

          response(HTTPResponse.c200, req)

      }
      // EOF Value

      "/params" post {

        req =>

          //-- Take all parameters
          req.parseURLParameters

          var valueParameters = req.urlParameters.filter { case (k, v) => k != "vcid" }

          // println("URL parameters raw= "+valueParameters)
          //println("IOT ID:" + req.getParameter("X-IODA-UID"))
          
          //-- Create Parameters Message
          val dataMap = new ParamsMessage
          dataMap.virtualChannel = Some(req.getURLParameter("vcid").get)

          valueParameters.foreach {
            case (k, v) =>
              dataMap.addParameter(k, v)
          }

          //-- Metadata
          populateMetadata(dataMap, req)
          
          
          //-- Send
          pushMessageToDatapah(dataMap)

          //-- Throttle and response
          Thread.sleep(50)
          response(HTTPResponse.c200, req)

      }

    }

    "/diagnostic" is {

    }

  }
  // EOF Incoming
  
  "/data" is {
    
    "/query" is {
      
    }
    
  }

  //-- Metadata
  def populateMetadata(m:DataMessage,req:HTTPRequest) = {
    
    req.getParameter("X-IODA-UID") match {
      case Some(uid) =>
        m.addUID(uid)
      case None => 
    }
    
  }
  
  //-- Datapath mapping

  def pushMessageToDatapah(dm: DataMessage) = {

    statVirtualChannelsSeen(dm)

    this.datapathMapping.foreach {
      case (m, dp) if (dm.virtualChannel.get.toString.matches(m)) =>

        logFine[StdPlatformHTTPInterface]("Push DM to Path on VC=" + dm.virtualChannel.get)

        dp.pushMessage(dm)

      case other =>
    }
  }

  //-- Helpers

  /**
   * Post Data Using simple HTTP connection
   */
  def postValueData(d: Map[String, String]) = {

    var fullurl = StdPlatformHTTPInterface.fullURLPath
    var host = "localhost"
    var sitePort = getSite.get.getHTTPListenPort.get

    var urlString = s"http://localhost:${sitePort}/$fullurl/incoming/data/params"

    println("Std Platform post  to: " + urlString)

    var url = new URL(urlString)
    var httpUrlConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    httpUrlConnection.setDoOutput(true);
    httpUrlConnection.setInstanceFollowRedirects(false);
    httpUrlConnection.setRequestMethod("POST");

    var parameters = d.map { case (k, v) => s"$k=$v" }.mkString("&")

    httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    httpUrlConnection.setRequestProperty("charset", "utf-8");
    httpUrlConnection.setRequestProperty("Content-Length", parameters.length().toString());
    httpUrlConnection.setUseCaches(false);
    httpUrlConnection.getOutputStream().write(parameters.getBytes("UTF-8"))
    httpUrlConnection.getOutputStream.flush()
    httpUrlConnection.getResponseCode

  }

}
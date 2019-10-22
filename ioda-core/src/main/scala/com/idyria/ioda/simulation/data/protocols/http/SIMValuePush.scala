package com.idyria.ioda.simulation.data.protocols.http

import com.idyria.ioda.simulation.data.phy.SIMPhysicalInterface
import com.idyria.ioda.simulation.data.phy.SIMPhysicalPushInterface
import java.net.URL
import java.net.HttpURLConnection
import scala.util.Random

class SIMValuePush extends SIMPhysicalPushInterface {
  
  this.onConfigModelUpdated {
    config.get.supportStringKey("path", "/", "Target URL Path")
    config.get.supportIntKey("port",8889, "Target Server Port")
    config.get.supportStringKey("host","localhost", "Target Server host")
  }
  
  def open = {
    true
  }
  
  def close = {
    true
  }
  
  def triggerPush : Unit = {
    println("Triggering PUSH")
    var conn = new URL(s"http://${config.get.supportGetString("host").get}:${config.get.supportGetInt("port").get}/${config.get.supportGetString("path").get}?test=${Random.nextInt()}")
    var httpConnection = conn.openConnection().asInstanceOf[HttpURLConnection]
    httpConnection.setConnectTimeout(1000)
    httpConnection.setReadTimeout(1000)
    httpConnection.connect()
    httpConnection.getResponseCode match {
      case 200 => 
      case other => 
        addError("Error ")
    }
  }
  
}
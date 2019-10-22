package com.idyria.ioda.data.phy.network.http

import com.idyria.ioda.data.phy.PhysicalInterface
import com.idyria.ioda.data.phy.ManagedOpenClosePhy
import com.idyria.ioda.data.phy.PushPhysicalInterface
import com.idyria.osi.wsb.core.WSBEngine
import com.idyria.osi.wsb.core.network.dispatch.Dispatch
import com.idyria.osi.wsb.core.broker.MessageBroker
import com.idyria.osi.wsb.core.message.Message
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import com.idyria.osi.wsb.webapp.http.message.HTTPResponse
import com.idyria.ioda.data.types.DataMapMessage
import com.idyria.osi.wsb.webapp.http.message.HTTPMessage
import com.idyria.osi.wsb.core.network.dispatch.TypedDispatch
import scala.reflect._
import com.idyria.osi.wsb.webapp.http.message.HTTPRequest

class HTTPPush extends PushPhysicalInterface with ManagedOpenClosePhy with TypedDispatch[HTTPRequest] {
  
  
  val ttag = classTag[HTTPRequest]
  
  this.onConfigModelUpdated {
    config.get.supportIntKey("port", 8889, "Port for this PUSH interface")
  }
  
  val localEngine = new WSBEngine
  localEngine.network.dispatchImplementation = Some(this)
  
  def lstop = {
    
  }
  
  def deliverMessage(req:HTTPRequest,b:MessageBroker)  : Unit = {
    logInfo[HTTPPush]("Delivering Message")
    
    //-- Accept
    val resp = HTTPResponse.c200
    resp.networkContext = req.networkContext
    localEngine.network.send(resp)
 
    //-- Create Message
    val dm = new DataMapMessage
    dm.dataMap = req.urlParameters.toMap
    
    findDataPaths.foreach {
      dp => 
        logInfo[HTTPPush](s"Delivering to: "+dp.getId)
        dp.pushMessage(dm)
    }
  }
  
  def doOpen = {
    
    logInfo[HTTPPush]("Opening PUSH Receiver")
    // Add Connector
    localEngine.network.connectors.collectFirst {
      case c if ( classOf[HTTPConnector].isInstance(c)) => c.asInstanceOf[HTTPConnector]
    } match {
      case Some(c) =>
        c.port = config.get.supportGetInt("port").get
      case None => 
        localEngine.network.addConnector(new HTTPConnector(config.get.supportGetInt("port").get))
    }
    
    
    localEngine.lInit
    localEngine.lStart
  }
  
  def doClose = {
    
    localEngine.lStop
    localEngine.cycleToInit
    
    
  }
  
  
}
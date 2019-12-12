package org.odfi.ioda.data.phy.network.http

import org.odfi.ioda.data.phy.PhysicalInterface
import org.odfi.ioda.data.phy.ManagedOpenClosePhy
import org.odfi.ioda.data.phy.PushPhysicalInterface
import org.odfi.ubroker.core.WSBEngine
import org.odfi.ubroker.core.network.dispatch.Dispatch
import org.odfi.ubroker.core.broker.MessageBroker
import org.odfi.ubroker.core.message.Message
import org.odfi.ubroker.app.http.connector.HTTPConnector
import org.odfi.ubroker.app.http.message.HTTPResponse
import org.odfi.ioda.data.types.DataMapMessage
import org.odfi.ubroker.app.http.message.HTTPMessage
import org.odfi.ubroker.core.network.dispatch.TypedDispatch
import scala.reflect._
import org.odfi.ubroker.app.http.message.HTTPRequest

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
package com.idyria.platforms.std.protocols
/*
import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.wsb.fwapp.Site

class DataPathToSiteProtocol extends ProtocolWithId {
  
  var targetSite : Option[Site] = None
  
  this.onDownMessage {
    dm => 
        
        logFine[DataPathToSiteProtocol]("Dispatching to site")
        
      this.config.get.supportGetInt("delay") match {
        case Some(d) =>
          Thread.sleep(d)
        case None => 
      }
      //Thread.sleep(
      
      //println(s"Redispatching message on VC=${dm.virtualChannel} ->  "+dm)
      this.targetSite.get.engine.network.dispatch(dm)
      //println("TPB: "+this.targetSite.get.engine.broker.brokeringTree)
     // this.targetSite.get.engine.broker.brokeringTree.down(dm)
  }
  
}*/
package org.odfi.ioda.uwisk.local


import org.odfi.ioda.uwisk.{IWiskInstanciator, wpackage}

class LWisk extends LWiskTrait {


  var instantiator : IWiskInstanciator = _

  def getInstantiator = instantiator match {
    case null =>
      sys.error("No instantiator defined")
    case other =>
      other
  }

  /**
   * Saves pending trigger with a counter
   */
  val pendingTriggers = scala.collection.mutable.HashMap[String,Int]()

  /**
   * Saves pending trigger with a counter
   */
  val packages = scala.collection.mutable.HashMap[String,wpackage]()

  var requestedTriggerHandler : Option[(String,String,String)=> Unit]  = None

  def requestTrigger(namespace:String,action:String) : String  = {

    val cleanNS = (namespace+"/"+action).trim.replaceAll("//+","/")
    pendingTriggers.synchronized {
      pendingTriggers.put(cleanNS,pendingTriggers.getOrElse(cleanNS,0)+1)
    }

    // Call Handler if set
    requestedTriggerHandler match {
      case Some(handler) => handler(cleanNS,namespace,action)
      case None =>
    }

    cleanNS

  }

  def onTriggerRequest(handler: (String,String,String)=> Unit) = {
    requestedTriggerHandler = Some(handler)
  }

  def getPendingTriggers(actionName:String) = {
    this.pendingTriggers.synchronized {

      val foundKeys = this.pendingTriggers.keys.filter {
        pendingAction =>
          pendingAction.startsWith(actionName)
      }
      foundKeys.foreach(pendingTriggers.remove)
      foundKeys
    }
  }
  def clearRequestedTrigger(fullName:String) = {
    this.pendingTriggers.synchronized {

      pendingTriggers.get(fullName) match {
        case None =>
        case Some(n) => pendingTriggers.remove(fullName)
      }
    }
  }



  // Packages
  //--------------

  def listPackages = this.packages.keys.toList.sorted

  def getAllPackages = this.packages.values.toList

  def getPackage(packageNS:String) = this.packages.get(packageNS)

  def registerPackage(ns:String,p:wpackage) = {
    this.packages.put(ns,p)
  }

}

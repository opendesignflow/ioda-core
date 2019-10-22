package com.idyria.ioda.data.phy.drivers

trait LineDriver extends Driver {
  
  
  def sendLineReceiveLine(cmd:String) : String
  
}
package com.idyria.ioda.data.phy

import java.io.OutputStream
import java.io.InputStream

trait BytesInterfacePhy extends PhysicalInterface {
  
   // Bytes Interface
  //--------------
  
  def phyWrite(b:Array[Byte]) 
  
  def phyRead(count:Int) : Array[Byte] 
  
  
  def phyGetOutputStream : OutputStream
  def phyGetInputStream : InputStream
}
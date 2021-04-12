package org.odfi.ioda.instruments.compress

import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import java.io.File

trait XMLCompressOutput extends STAXSyncTrait {
  
  
  override def toFile(f:File, prefixes: Map[String, String] = Map[String, String]()) = {
    
    toOutputStream(CompressModule.getFileCompressOutputStream(f),prefixes)
    
    this.staxPreviousFile = Some(f)
    
    this
  }
  
  
  
  override def fromFile(f: File) = {
    
    this.fromInputStream(CompressModule.getFileCompressInputStream((f)))
    
    this.staxPreviousFile = Some(f)
    
    this
    
  }
  
}
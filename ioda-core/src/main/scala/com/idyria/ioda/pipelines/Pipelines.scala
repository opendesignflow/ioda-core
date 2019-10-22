package com.idyria.ioda.pipelines

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import com.idyria.osi.ooxoo.core.buffers.structural.XList

/**
 * This is a wrapper to gather different pipelines from XML
 */
@xelement(name="pipelines",ns="http:///www.opendesignflow.org/ioda/pipelines/1.0")
class Pipelines extends ElementBuffer with STAXSyncTrait {
  
  
  @xelement(name="pipeline",ns="http:///www.opendesignflow.org/ioda/pipelines/1.0")
  var pipelines = XList { new DefaultPipeline }
  
}
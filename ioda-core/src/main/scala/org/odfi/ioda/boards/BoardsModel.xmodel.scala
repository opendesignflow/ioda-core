package org.odfi.ioda.boards

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import org.odfi.indesign.core.harvest.HarvestedResource


@producers(Array(
  new producer(value = classOf[ScalaProducer])))
class BoardsModel extends ModelBuilder {
  
  "Board" is {
    isTrait
    withTrait(classOf[HarvestedResource])
    
    
  }
  
}
package org.odfi.ioda.boards

import org.odfi.ooxoo.model.ModelBuilder
import org.odfi.ooxoo.model.producers
import org.odfi.ooxoo.model.producer
import org.odfi.ooxoo.model.out.markdown.MDProducer
import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import org.odfi.indesign.core.harvest.HarvestedResource


@producers(Array(
  new producer(value = classOf[ScalaProducer])))
class BoardsModel extends ModelBuilder {
  
  "Board" is {
    isTrait
    withTrait(classOf[HarvestedResource])
    
    
  }
  
}
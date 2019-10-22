package com.idyria.ioda.data.types

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.STAXSyncTrait
import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId
import org.odfi.indesign.core.config.Config
import org.odfi.indesign.core.config.model.CommonConfig
import com.idyria.osi.tea.errors.ErrorSupport
import com.idyria.osi.tea.errors.ErrorRepairSupport
import org.odfi.wsb.fwapp.lib.ooxoo.EntityBindBuffer
import org.odfi.indesign.core.config.ConfigModelWithImpl
import com.idyria.osi.wsb.core.message.Message
import com.idyria.osi.wsb.core.message.XMLDataMessage
import com.idyria.osi.wsb.core.broker.tree.single.SingleMessage

@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object DataTypesModel extends ModelBuilder {

  val dm = "DataMessage" is {
    withTrait[XMLDataMessage]
    withTrait[SingleMessage]
    
    makeTraitAndUseCustomImplementation

   // "vChannel" ofType "string"

  }
  

  
  "ValueMessage" is {
    withTrait(dm)
    makeTraitAndUseCustomImplementation
  }

  "ValuesListMessage" is {
    withTrait(dm)
    makeTraitAndUseCustomImplementation

  }

  "MatrixValueMessage" is {
    withTrait(dm)
    makeTraitAndUseCustomImplementation

    attribute("n") ofType ("integer")
    attribute("m") ofType ("integer")

  }

}
package org.odfi.ioda.data.types

import org.odfi.ooxoo.model.ModelBuilder
import org.odfi.ooxoo.model.producers
import org.odfi.ooxoo.model.producer
import org.odfi.ooxoo.model.out.markdown.MDProducer
import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ubroker.core.message.XMLDataMessage
import org.odfi.ubroker.core.broker.tree.single.SingleMessage

@producers(Array(
  new producer(value = classOf[ScalaProducer])))
class DataTypesModel extends ModelBuilder {

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
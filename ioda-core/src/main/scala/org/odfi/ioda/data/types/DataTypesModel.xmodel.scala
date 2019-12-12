package org.odfi.ioda.data.types

import com.idyria.osi.ooxoo.model.ModelBuilder
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.producer
import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ubroker.core.message.XMLDataMessage
import org.odfi.ubroker.core.broker.tree.single.SingleMessage

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
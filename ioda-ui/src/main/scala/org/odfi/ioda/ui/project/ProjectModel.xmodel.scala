package org.odfi.ioda.ui.project

import org.odfi.ooxoo.model.out.scala.JSONBProducer
import org.odfi.ooxoo.model.{ModelBuilder, producer, producers}

@producers(Array(
  new producer(value = classOf[JSONBProducer])))
class ProjectModel extends ModelBuilder {

  val uwiskPipeline = "org.odfi.ioda.uwisk.wpackageTraitpipeline"

  "Project" is {
    withTrait("org.odfi.ooxoo.lib.json.model.ToJsonObjectTrait")

    // Settings
    //------------
    "Settings" is {
      withTrait("org.odfi.ioda.data.metadata.SMetadataContainer")
      withTrait("org.odfi.ooxoo.lib.json.model.ToJsonObjectTrait")
    }


    // Pipelines
    //----------------
    val pipeline = importElement(uwiskPipeline)
    pipeline.name = "pipeline"
    pipeline.setMultiple
    /*"Pipeline" multiple {
      withTrait(uwiskPipeline)
      //this.elementsStack.head.
      /*
      // description
      attribute("id")

      // steps
      "step" multiple {
        withTrait("org.odfi.ioda.data.metadata.SMetadataContainer")
        attribute("id")

      }
*/

    }*/

  }


}

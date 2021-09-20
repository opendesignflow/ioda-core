package org.odfi.ioda.uwisk

import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.JSONBProducer
import com.idyria.osi.ooxoo.model.{Element, ModelBuilder, producer, producers}

@producers(Array(
  new producer(value = classOf[JSONBProducer])))
class WiskPackageModel extends ModelBuilder {

  val wiskMetadata = "MetadataContainer" is {
    makeTraitAndUseCustomImplementation

    "metadata" multiple {
      makeTraitAndUseCustomImplementation
      attribute("id")
      attribute("value") ofType ("jsonvalue")
      attribute("type")
      attribute("unit")
      attribute("json") ofType ("json")
    }
  }

  // Common Pipeline ref
  val idAndMetadata = "MetadataAndId" is {
    isTrait
    attribute("id")
    withTrait(wiskMetadata)

  }

  val pipelineRef = "PipelineRefTrait" is {
    isTrait
    attribute("id")
    "metadata" multiple {
      attribute("id")
      attribute("value")
    }
  }

  // Package
  "wpackage" is {
    makeTraitAndUseCustomImplementation
    withTrait(idAndMetadata)

    attribute("namespace")
    attribute("version")
    attribute("name")


    "environment" multiple {
      withTrait(idAndMetadata)
    }

    "action" multiple {
      makeTraitAndUseCustomImplementation
      attribute("id")
      attribute("graceTime")
      "collect" multiple {
        attribute("id")
      }
    }

    "pipeline" multiple {
      makeTraitAndUseCustomImplementation

      attribute("id")
      attribute("env")
      attribute("ignore") ofType ("boolean") default ("false")

      "pre" multiple {
        withTrait(pipelineRef)
      }

      "posts" multiple {
        withTrait(pipelineRef)
      }

      "trigger" ofType ("string") setMultiple (true)

      "implementation" is {
        "javaClass" ofType ("string")
        "import" multiple {
          withTrait(pipelineRef)
        }
      }

      "parameter" multiple {
        attribute("id")
        attribute("type")
        attribute("default")
      }

      "step" multiple {
        withTrait(idAndMetadata)
        makeTraitAndUseCustomImplementation
      }


    }

  }

  // Trace
  //------------
  val commonTrace = "commonTrace" is {
    makeTraitAndUseCustomImplementation

    // Action id
    attribute("action")

    // Run pipeline
    "pipeline" multiple {
      makeTraitAndUseCustomImplementation
      attribute("id")
      attribute("top") ofType ("boolean")

      "parameter" multiple {
        attribute("name")
        attribute("value")
        attribute("type")
        attribute("unit")
      }

      "runTime" is {
        "startTS" ofType ("datetime")
        "endTS" ofType ("datetime")
        "total" ofType ("long")
      }
      "error" is {
        "message" ofType("string")
      }
    }

    "collected" multiple {
      withTrait("commonTrace")
      // ofType()
    }

  }
  "utrace" is {
    withTrait(commonTrace)
    attribute("dry-run") ofType("boolean") default("false")
  }

}

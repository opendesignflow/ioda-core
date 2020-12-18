package org.odfi.ioda.uwisk

import com.idyria.osi.ooxoo.model.out.markdown.MDProducer
import com.idyria.osi.ooxoo.model.out.scala.JSONBProducer
import com.idyria.osi.ooxoo.model.{Element, ModelBuilder, producer, producers}

@producers(Array(
  new producer(value = classOf[JSONBProducer]),
  new producer(value = classOf[MDProducer])))
object WiskPackageModel extends ModelBuilder {

  // Common Pipeline ref
  val idAndMetadata = "MetadataAndId" is {
    isTrait
    attribute("id")
    "metadata" multiple {
      attribute("id")
      attribute("value")
    }
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

    attribute("namespace")
    attribute("version")
    attribute("name")
    attribute("id")

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
      attribute("ignore") ofType ("boolean") default ("true")

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
      attribute("id")
      "time" is {
        "startTS" ofType ("datetime")
        "endTS" ofType ("datetime")
        "total" ofType ("long")
      }
    }

    "collected" multiple {
      withTrait("commonTrace")
      // ofType()
    }

  }
  "utrace" is {
    withTrait(commonTrace)
  }

}

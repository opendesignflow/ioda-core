package com.idyria.ioda.env

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
import com.idyria.osi.ooxoo.core.buffers.id.ElementWithID
import com.idyria.osi.ooxoo.core.buffers.id.IdAndRefIdModelBuilder
import com.idyria.osi.ooxoo.lib.json.JSonUtilTrait

@producers(Array(
  new producer(value = classOf[ScalaProducer]),
  new producer(value = classOf[MDProducer])))
object IODAEnvironmentModel extends IdAndRefIdModelBuilder {

  this.defaultElementBufferClass = classOf[EntityBindBuffer]

  // Common
  //----------
  val nameAndId = "NameAndID" is {
    isTrait
    withElementID
    withTrait(classOf[JSonUtilTrait])
    attribute("name")

  }

  // Env
  //--------
  "Environment" is {
    makeTraitAndUseCustomImplementation
    withTrait(classOf[HarvestedResource])
    withTrait(nameAndId)

    "Description" ofType ("cdata")

    // Data paths
    //--------------
    "DataPath" multiple {

      makeTraitAndUseCustomImplementation
      requestContainerReference

      withTrait(nameAndId)
      withTrait(classOf[HarvestedResourceDefaultId])
      withTrait(classOf[ErrorRepairSupport])

      "Description" ofType ("cdata")

      "DataSourceReference" multiple {
        referenceElementID("EnvironmentTraitDataSource")
      }

      "ProtocolStack" is {
        makeTraitAndUseCustomImplementation
        withTrait(classOf[ErrorRepairSupport])

        "Protocol" multiple {
          makeTraitAndUseCustomImplementation
          withTrait(classOf[ConfigModelWithImpl[_]].getCanonicalName + "[com.idyria.ioda.data.protocols.Protocol]")
          withTrait(classOf[CommonConfig])
          withTrait(classOf[ErrorRepairSupport])

          //"ClassType" ofType ("class")

        }

      }

      // Runs
      //----------

      /**
       * Runs is the basic model to gather data and process it
       * It can be executed on demand or periodically
       * Executions sumup
       */
      "Run" multiple {
        
        makeTraitAndUseCustomImplementation
        withElementID
        withTrait(classOf[ErrorRepairSupport])
        requestContainerReference

        // Periodic run
        
        // Cleanup of runs
        "Conservation" is {
          "ExecutionCount" ofType("integer") default "-1"
        }
        
        // Execution on datas
        "Execution" multiple {
          
          makeTraitAndUseCustomImplementation
          withTrait(classOf[ErrorSupport])
          requestContainerReference
          
          attribute("start") ofType ("datetime")
          attribute("stop") ofType ("datetime")

          "Statistics" is {
            "ValuesCount" ofType ("integer")
          }

          "Dump" is {
            attribute("fileId")
          }
        }

      }
    }

    // Boards
    //-----------------
    "DataSource" multiple {
      makeTraitAndUseCustomImplementation
      requestContainerReference

      withTrait(classOf[HarvestedResourceDefaultId])
      withTrait(nameAndId)

      //attribute("providesId") ofType ("boolean") default ("false") is ("If true, Board is supposed to be delivering a unique ID using the Physical Interface")

      // Physical
      //-------------
      "Physicals" is {

        makeTraitAndUseCustomImplementation
        withTrait(classOf[ErrorRepairSupport])

        "GlobalPhysical" multiple {
          referenceElementID("EnvironmentTraitGlobalPhysical")
        }

        "SimulationPhysical" multiple {
          withElementID
          withTrait(classOf[ErrorRepairSupport])
          withTrait(classOf[ConfigModelWithImpl[_]].getCanonicalName + "[com.idyria.ioda.simulation.data.phy.SIMPhysicalInterface]")

          // Default fallback in case no other Physicals Are Available
          attribute("fallback") ofType ("boolean") default ("true")
        }

        "LocalPhysical" multiple {
          withTrait(classOf[ConfigModelWithImpl[_]].getCanonicalName + "[com.idyria.ioda.data.phy.PhysicalInterface]")
        }

      }
      // Simulation Physical Reference
      // Used when no Real Physical is available
      //"SimulationPhysical" is {
      //  attribute("virtualId")
      //}
    }

    // Simulation
    //---------------
    "Simulation" is {
      makeTraitAndUseCustomImplementation

      "Physical" multiple {
        makeTraitAndUseCustomImplementation
        withTrait(classOf[CommonConfig])
        withTrait(classOf[ErrorRepairSupport])

        withTrait(classOf[ConfigModelWithImpl[_]].getCanonicalName + "[com.idyria.ioda.simulation.data.phy.SIMPhysicalInterface]")

        attribute("virtualId")

      }

    }

    "GlobalPhysical" multiple {
      requestContainerReference
      makeTraitAndUseCustomImplementation
      withElementID
      withTrait(classOf[ConfigModelWithImpl[_]].getCanonicalName + "[com.idyria.ioda.data.phy.PhysicalInterface]")
    }

  }

}
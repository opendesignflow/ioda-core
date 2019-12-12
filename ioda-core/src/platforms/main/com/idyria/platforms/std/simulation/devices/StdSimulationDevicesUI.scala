package com.idyria.platforms.std.simulation.devices

import com.idyria.platforms.std.StdPlatformBaseView
import com.idyria.platforms.std.environment.EnvironmentUIUtils
import com.idyria.platforms.std.ui.utils.ErrorUIUtils
import org.odfi.ioda.IODA
import org.odfi.wsb.fwapp.framework.FWAppTempBufferView
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.env.EnvironmentTraitSimulationTraitPhysical
import org.odfi.wsb.fwapp.module.semantic.indesign.SemanticConfigUtil

class StdSimulationDevicesUI extends StdPlatformBaseView with EnvironmentUIUtils with ErrorUIUtils with FWAppTempBufferView with SemanticConfigUtil {

  definePage {

    withEnvironmentsTabsOtherwiseWarning {

      (index, name, env) =>

        h2("Simulation Physical Devices") {

        }

        semanticDefinitionTable("Virtual ID", "Type", "Errors") {

          tbodyTrLoopWithDefaultLine("No Defined Simulation devices")(env.getSimulationPhysicals) {
            simPhysical =>

              simPhysical.checkImmediateErrors
              rtd {
                repairReloadIcon(simPhysical)

                "ui button" :: buttonClick("Test config") {
                  simPhysical.getImplementation.config.get.setBoolean("physical", true)
                  IODA.saveConfig
                }

                "ui edit icon" :: iconClickReload { putToTempBuffer("interface", simPhysical) }
              }

              rtd {
                inputToBufferAfter500MSAnd(simPhysical.eid) {
                  //IODA.saveConfig
                }

              }
              td(simPhysical.getImplementation.toString()) {

              }
              //-- Error
              lastErrorColumn(simPhysical)
          }
        }
        // EOF Physical Table

        // Edit Physical
        //----------------
        withTempBufferValue[EnvironmentTraitSimulationTraitPhysical]("interface") {
          interface =>
            h2("Edit interface: " + interface.virtualId) {

            }

            // Configurations
            //--------------------
            configSupportTable(interface)
            

        }

    }

  }
}
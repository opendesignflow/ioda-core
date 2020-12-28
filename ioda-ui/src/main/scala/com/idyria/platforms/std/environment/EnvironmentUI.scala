package com.idyria.platforms.std.environment

/*
import com.idyria.platforms.std.StdPlatformBaseView
import com.idyria.platforms.std.StdPlatform
import com.idyria.platforms.std.StdPlatformUI
import org.odfi.wsb.fwapp.module.jquery.JQueryFormValidationClientUtils
import org.odfi.ioda.env.EnvironmentHarvester
import org.odfi.ioda.env.Environment
import org.odfi.ioda.data.phy.PhysicalInterfaceHarvester
import org.odfi.ioda.simulation.SimulationHarvester
import org.odfi.ioda.IODA
import org.odfi.ioda.simulation.data.phy.SIMPhysicalInterface
import org.odfi.ioda.data.phy.PhysicalInterface
import com.idyria.platforms.std.ui.utils.ErrorUIUtils

class EnvironmentUI extends StdPlatformBaseView with JQueryFormValidationClientUtils with ErrorUIUtils with EnvironmentUIUtils {

  definePage {

    "ui segment" :: ribbonHeaderDiv("blue", "Environments") {

      form {
        jqueryFormValidatorEnable

        semanticFormField("name", "Environment Name") {
          input {
            jqueryFormValidatorRequire
          }
        }

        semanticOnSubmitButton("Add Environment") {

          // var env = new Environment
          EnvironmentHarvester.addEnvironment(withRequiredRequestParameter("name"))

        }

      }
    }

    withEnvironmentsTabsOtherwiseWarning {
      case (index, name, env) =>

        h1("Environment: "+name) {
          
        }
        
        // Boards
        //----------------
        /*"ui segment" :: ribbonHeaderDiv("blue", "Boards") {

              h2("Defined Boards") {
              }
              semanticDivider

              semanticDefinitionTable("Board ID", "Board Name", "Physical Interface","Errors") {
                tbodyTrLoopWithDefaultLine("No Boards Defined")(env.boards) {
                  board =>
                    rtd {
                      "ui delete icon" :: iconClickReload {
                        
                      }
                    }
                    td(board.getId) {

                    }
                    td(board.name) {

                    }
                    rtd {
                      if (board.physicalOption.isDefined && board.physical.refIdOption.isDefined) {
                        "ui info message" :: board.physical.refId.toString
                      }
                      if (board.physicalOption.isDefined && board.physical.simulationRefIdOption.isDefined) {
                        "ui info message" :: board.physical.simulationRefId.toString
                      }
                      
                    }
                    lastErrorColumn(board)
                }
              }

              // Add Board
              //-----------------

              h2("Add Board") {
              }
              semanticDivider

              form {
                jqueryFormValidatorEnable

                semanticFormField("id", "Board ID") {
                  input {

                  }
                }
                semanticFormField("name", "Board Name") {
                  input {
                    jqueryFormValidatorRequire
                  }
                }

                val allAvailableInterfaces = env.getAllAvailablePhysicalInterfaces

                allAvailableInterfaces match {

                  case none if (none.size == 0) =>

                    "ui warning message " :: div {
                      cssForceBlockDisplay
                      text("No Free Physical Interface is available, cannot add a physical interface for the board right now")
                    }

                  case interfaces =>
                    semanticFormField("physical", "Physical") {
                      "ui dropdown selection" :: div {
                        hiddenInput("selectedPhysical") {
                          jqueryFormValidatorRequire
                        }

                        "default text" :: "Select Physical Interface"
                        "icon dropdown" :: i {}
                        "menu" :: div {

                          // Globally Available Physical interfaces
                          semanticMenuIconHeader("tags", "Physical Interfaces")
                          semanticDivider
                          semanticDropDownItems(PhysicalInterfaceHarvester.listUnusedPhysicalInterfacesInfos)

                          // Simulated from environment
                          semanticMenuIconHeader("tags", "Simulation Interfaces")
                          semanticDivider
                          semanticDropDownItems(env.getUnusedSimulationPhysical.map(p => (p.virtualId, p.virtualId)).toList)

                        }

                      }
                    }
                }
                // EOF Physical Selection

                semanticOnSubmitButton("Add") {

                  env.boards.addRollbackOnError {
                    board =>
                      board.boardId = withRequiredRequestParameter("id")
                      board.name = withRequiredRequestParameter("name")
                      withNonEmptyRequestParameter("physical") match {
                        case Some(physicalId) =>
                           env.getAvailablePhysicalInterfaceById(physicalId) match {
                             case Some(interface : SIMPhysicalInterface ) => 
                               board.physical.simulationRefId = interface.getId
                               case Some(interface : PhysicalInterface ) =>
                               board.physical.refId = interface.getId
                             case None =>
                               sys.error(s"Selected Physical $physicalId is not available")
                           }
                        case None => 
                      }
                      IODA.saveConfig
                  }

                }
              }
              // EOF Form
            }*/
        // EOF Boards

        // Simulation
        //----------------
        "ui segment" :: ribbonHeaderDiv("blue", "Simulation") {
          h2("Simulation") {

          }

          // Defined Interfaces and available table
          //-------------------------
          semanticDefinitionTable("Virtual ID", "Type", "Errors") {
            trtd("Defined Interfaces") {
              colspan(tableColumnsCount)
            }
            trLoop(env.simulation.physicals) {
              interface =>

                interface.checkImmediateErrors

                rtd {
                  if (interface.hasErrors) {
                    //@data-tooltip='Repair'
                    "ui treatment icon " :: iconClickReload(interface.repairErrors)
                  }

                  "ui delete icon" :: iconClickReload {
                    env.simulation.removePhysical(interface)
                  }
                }
                rtd {
                  "ui input" :: inputToBufferAfter500MSAnd(interface.virtualId) {
                    IODA.saveConfig
                  }

                }
                td(interface.getImplementation.toString()) {

                }
                rtd {

                  interface.getLastError match {
                    case Some(err) =>
                      classes("negative")
                      text(err.getLocalizedMessage)
                    case None =>
                      classes("Positive")
                      "ui icon check" :: i {

                      }
                      text("None")
                  }

                }

            }

            trtd("Available Interfaces") {
              colspan(tableColumnsCount)
            }
            trLoop(SimulationHarvester.getProvidedPhysicalInterfaces) {
              case (id, name, implementationClass) =>
                rtd {
                  "ui add icon" :: iconClickReload {
                    val physical = env.simulation.physicals.add
                    physical.virtualId = implementationClass.getSimpleName + physical.hashCode()
                    physical.implementationType = implementationClass
                    IODA.saveConfig
                  }
                }
                td(name) {

                }
                td(implementationClass.getCanonicalName) {

                }
                td("") {

                }
            }

          }
        }

    }

    //StdPlatformUI.config.get.custom.content.getAllOfType

  }

}*/
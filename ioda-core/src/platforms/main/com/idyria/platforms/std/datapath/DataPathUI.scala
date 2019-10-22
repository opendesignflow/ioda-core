package com.idyria.platforms.std.datapath

import com.idyria.platforms.std.StdPlatformBaseView
import com.idyria.platforms.std.environment.EnvironmentUIUtils
import org.odfi.wsb.fwapp.module.jquery.JQueryFormValidation
import org.odfi.wsb.fwapp.lib.ooxoo.OOXOOEntityBindView
import com.idyria.ioda.env.EnvironmentTraitDataPath
import com.idyria.ioda.IODA
import com.idyria.platforms.std.ui.utils.IODAUIUtils
import com.idyria.platforms.std.ui.utils.ErrorUIUtils
import org.odfi.indesign.ide.core.module.d3.D3View

class DataPathUI extends StdPlatformBaseView with EnvironmentUIUtils 
                                              with JQueryFormValidation 
                                              with OOXOOEntityBindView 
                                              with IODAUIUtils 
                                              with ErrorUIUtils
                                              with D3View {

  definePage {
    

    withEnvironmentsTabsOtherwiseWarning {
      case (i, name, env) =>

        h1("Data paths") {

        }

        // Add Datapath
        //---------------------
        semanticContentThenDivider(h2("Add Datapath") {})
        /*entityBindFormXList(env.dataPaths) {
          dp =>
            jqueryFormValidatorEnable

            semanticFormField("Datapath ID") {
              semanticFormFieldInput {
                jqueryFormValidatorRequire
                entityBindInput("id")
              }
            }

            semanticFormField("Datapath Name") {
              semanticFormFieldInput {
                jqueryFormValidatorRequire
                entityBindInput("name")
              }
            }

            semanticSubmitButton("Add")

            dp.onEntitySubmit {
              println("Entity Submitted: " + dp.name)
            }
            iodaEntitySubmitSaveConfig

        }*/

        // Table
        //--------------
        semanticDefinitionTable("ID", "Name", "Data Sources","Stack Count", "Errors") {

          tbodyTrLoopWithDefaultLine("No Datapaths")(env.dataPaths) {
            datapath =>

              rtd {
                entityEditReloadIcon(datapath)
              }
              td(datapath.eid) {

              }
              td(datapath.name) {

              }
              
              td(datapath.dataSourceReferences.size.toString) {
                
              }

              td(datapath.protocolStack.protocols.size.toString) {

              }
              lastErrorColumn(datapath)

          }
        }
        // EOF Datapath table

        // Entity Edit
        //--------------
        withListEntityEdit(env.dataPaths) {
          datapath =>
            h2("Editing: " + datapath.eid) {

            }

            // Errors
            div {
              immidiateErrorAndRepairStatus(datapath)
            }
            
            // Visual
           /* "#dpg .ioda-std-datapath-graph" :: div {
              
            }
            script(s"""
              $$(function() {
                console.log("Making datapath graphs");
                ioda.std.datapath("dpg",$$.parseJSON('{${datapath.toJSONString}}'));
              
              });
              
              
              """)*/
            
            // Form
            semanticH3Divider("Data Source")
            semanticFormField("Data Source") {

             // semanticWarningIfEmptyString(datapath.dataSourceReference.refId)("No Data Source Connected to data path")

              // Board selection
              /*selectFromObjects(env.boards.map(b => (b.boardId.toString, b)), datapath.boardRef.boardId) {
                selectedBoard =>
                  datapath.boardRef.boardId = selectedBoard.boardId
              }*/
            }

        }
      // withEntityEdit[Data { x => ??? }
      // EOF Entity Edit

    }
  }
}
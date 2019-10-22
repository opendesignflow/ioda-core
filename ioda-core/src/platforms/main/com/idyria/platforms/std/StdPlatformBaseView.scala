package com.idyria.platforms.std

import com.idyria.ioda.ui.IODABaseView
import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.indesign.core.harvest.Harvest
import org.odfi.wsb.fwapp.framework.FWAppValueBufferView
import org.odfi.wsb.fwapp.lib.indesign.FWappResourceValueBindingView

trait StdPlatformBaseView extends IODABaseView with SemanticView with FWappResourceValueBindingView {

  this.addLibrary("ioda-std") {
    case (_,targetNode) => 
      onNode(targetNode) {
        script(createDefaultAssetsResolverURI("/ioda-std.js")) {
          
        }
      }
  }
  
  def definePage(cl: => Any) = {
    this.definePart("page") {
      div {
        cl
      }
    }
  }

  this.viewContent {
    html {

      head {
        placeLibraries
        stylesheet(createDefaultAssetsResolverURI("/stdplatform.css")) {

        }
        title("IODA :: Std :: " + getDisplayName) {

        }
      }

      "ui page container" :: body {

        "#header" :: div {

          "ui menu" :: div {

            "ui item" :: a("#") {
              onClickReload {
                Harvest.run
              }
              "ui refresh icon" :: i {

              }
            }

            "ui dropdown item" :: div {
              //a("/environments") {

              text("Environments")
              "dropdown icon" :: i {}
              "menu" :: div {

                "ui item" :: a("/environments/")(text("Setup"))
                "ui item" :: a("/environments/boards")(text("Boards"))
                "ui item" :: a("/environments/datapaths")(text("Data Paths"))
                "ui item " :: div {
                  text("Simulation")
                  "dropdown icon" :: i {}

                  "menu" :: div {
                    "ui item" :: a("/environments/simulation/devices")(text("Devices"))
                  }
                }

              }
            }
            "ui item" :: a("/devices")(text("Devices"))
            "ui item" :: a("/simulation")(text("Simulation"))

          }

        }
        div {
          "#page" :: div {
            placePart("page")
          }
        }

        "#footer" :: div {

        }

      }

    }
  }

}
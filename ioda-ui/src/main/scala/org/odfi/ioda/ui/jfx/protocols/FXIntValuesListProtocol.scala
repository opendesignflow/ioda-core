package org.odfi.ioda.ui.jfx.protocols

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import org.odfi.ioda.data.phy.LineSupportPhy
import org.odfi.ioda.data.phy.state.{PhyClosedMessage, PhyOpenedMessage}
import org.odfi.ioda.data.protocols.text.IntValuesListProtocol
import org.odfi.ioda.ui.jfx.WPipelineWithShortConfigUI

class FXIntValuesListProtocol extends IntValuesListProtocol with WPipelineWithShortConfigUI {

  // Top UI
  //------
  val top = new VBox
  top.setFillWidth(true)


  // Open/Close Phy
  //--------
  val phyAvailable = new CheckBox("PHY Available")
  phyAvailable.setSelected(false)
  phyAvailable.setDisable(true)


  // Add all to Top
  //------------
  top.getChildren.add(phyAvailable)

  override def loadShortConfigUI: Node = top

  // State
  //------------
  this.onWiskMessage {
    case ( phyOpen: PhyOpenedMessage,_) if (phyOpen.phyIsLineSupport) =>

      onJFXThreadLater {
        phyAvailable.setSelected(true)
      }

      this.connect(phyOpen.phy.asInstanceOf[LineSupportPhy])

    case ( phyOpen: PhyClosedMessage,_) =>

      onJFXThreadLater {
        phyAvailable.setSelected(false)
      }

      this.disconnectPhy


  }
}

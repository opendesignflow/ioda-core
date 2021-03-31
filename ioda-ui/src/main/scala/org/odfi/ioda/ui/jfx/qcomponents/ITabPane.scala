package org.odfi.ioda.ui.jfx.qcomponents

import javafx.scene.Node
import javafx.scene.control.{ScrollPane, Tab, TabPane}

class ITabPane extends TabPane {

  // Utils
  //-------------
  def addTab(text:String,node:Node) = {

    // Wrap in Scrollpane
    val sp = new ScrollPane()
    sp.setContent(node)
    sp.setFitToHeight(true)
    sp.setFitToWidth(true)

    // Add Tab
    val tab = new Tab
    tab.setContent(sp)
    tab.setText(text)
    getTabs.add(tab)

    tab

  }
}

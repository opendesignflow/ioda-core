package org.odfi.ioda.ui.jfx.qcomponents

import javafx.scene.Node
import javafx.scene.control.{ScrollPane, Tab, TabPane}
import javafx.scene.layout.BorderPane

class BorderPaneWithTabs extends BorderPane {

  // Add Tab Pane
  //----------
  val tp = new TabPane()
  this.setCenter(tp)



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
    tp.getTabs.add(tab)

    tab

  }

}

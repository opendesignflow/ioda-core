package org.odfi.ioda.ui.dev

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.{SplitPane, TextArea}

class JSPipelineUI extends SplitPane{


  // Init
  //----------------


  // LEFT: Text Editor
  val jsScriptString = new SimpleStringProperty()
  val jsTextView = new TextArea()

  this.getChildren.add(jsTextView)

  // Right: Pipeline Overview Table
  //--------

}

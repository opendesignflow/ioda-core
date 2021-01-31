package org.odfi.ioda.ui.dev

import javafx.scene.control.Button
import javafx.scene.layout.{BorderPane, Priority, VBox}
import org.odfi.ioda.ui.jfx.{JFXRun, JavaFXUtilsTrait}

import java.io.{File, FileReader}
import javax.script.{ScriptEngine, ScriptEngineManager, SimpleScriptContext}

object IODADevTry extends App with JavaFXUtilsTrait {
  println("Hello")



  val graalEngine = new ScriptEngineManager().getEngineByName("graal.js")

  //graalEngine.eval("""console.log('Hello from Graal')""")

  val ctx = new SimpleScriptContext()

  graalEngine.eval(new FileReader(new File("test_pipeline.js")),ctx)

  sys.exit()
  JFXRun.onJavaFX {
    JFXRun.setImplicitExit

    val bp = new BorderPane
    val ui = new VBox
    ui.fillWidthProperty().setValue(true)
    ui.setFillWidth(true)

    bp.setCenter(ui)

    println("IN Main ui")

    JFXRun.onStageUI(ui) {
      case (stage,bp) =>

        println("IN Stage ui")
        val (devUI,controller) = JSPipelineController.load

        // Button
        val b = new Button
        ui.getChildren.add(b)
        b.setText("Load JS")

        onJFXClickThread(b) {
          println("Controller: "+controller)
          controller.loadJSFile(new File("test_pipeline.js").toURI.toURL)
        }



        // ADd Dev U
        ui.getChildren.add(devUI)
        VBox.setVgrow(devUI,Priority.ALWAYS)

        stage.setWidth(1024)
        stage.setHeight(768)
        stage.centerOnScreen()
        stage.showAndWait()

    }
  }

}

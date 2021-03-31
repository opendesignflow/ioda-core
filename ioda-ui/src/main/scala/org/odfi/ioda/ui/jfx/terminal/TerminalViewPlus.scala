package org.odfi.ioda.ui.jfx.terminal

import com.kodedu.terminalfx.TerminalView
import javafx.scene.paint.Color
import org.odfi.indesign.core.module.jfx.JavaFXUtilsTrait

import java.io.{OutputStream, PrintStream}

class TerminalViewPlus extends TerminalView with JavaFXUtilsTrait {


  var terminalStreamConnector = new PipedWritersToPipedReader
  var terminalErrorStreamConnector = new PipedWritersToPipedReader

  // Create Piped Interfaces
  //--------------
  this.onTerminalFxReady(new Runnable {
    override def run(): Unit = {
      setInputReader(terminalStreamConnector.getReader)
      setErrorReader(terminalErrorStreamConnector.getReader)
    }
  })

  // General Utils
  //----------------

  def cleanTerminal = {
    onJFXThread {
      this.getTerminal.call("reset")
      //this.getTerminalVT.call("reset")
    }

  }

  // Pipe Utils
  //-------------

  def grabStdIO = {
    System.setOut(getPrintStreamToTerminalStream)
    System.setErr(getPrintStreamToErrorStream)
  }

  def getPrintStreamToTerminalStream  = {
    new PrintStream(terminalStreamConnector.connectNewOutputStream)
  }

  def getOutputStreamToTerminalStream : OutputStream = {
    terminalStreamConnector.connectNewOutputStream
  }


  def getPrintStreamToErrorStream = {
    new PrintStream(terminalErrorStreamConnector.connectNewOutputStream)
  }

  def getOutputStreamToErrorStream : OutputStream = {
    terminalErrorStreamConnector.connectNewOutputStream
  }

}
object TerminalViewPlus {

  def createDefaultTerminal = {
    // Terminal
    //----------------
    val terminalView = new TerminalViewPlus
    terminalView.getTerminalConfig.setBackgroundColor(Color.ALICEBLUE)
    terminalView.getTerminalConfig.setFontFamily("sans-serif")
    terminalView.getTerminalConfig.setCopyOnSelect(true)
    terminalView.getTerminalConfig.setEnableClipboardNotice(true)


    terminalView
  }

}

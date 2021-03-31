package console

import org.odfi.ioda.ui.jfx.terminal.PipedWritersToPipedReader

import java.io.{OutputStream, PrintStream}

class SConsoleView extends ConsoleView{

  var terminalStreamConnector = new PipedWritersToPipedReader
  var terminalErrorStreamConnector = new PipedWritersToPipedReader

  // Pipe Utils
  //-------------

  def grabStdIO = {
    System.setOut(getOut)
    //System.setOut(getPrintStreamToTerminalStream)
   // System.setErr(getPrintStreamToErrorStream)
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

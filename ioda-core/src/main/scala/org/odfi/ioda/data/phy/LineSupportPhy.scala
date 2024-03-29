package org.odfi.ioda.data.phy

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}


trait LineSupportPhy extends TextSupportPhy with ManagedOpenClosePhy {

  // Line Ignore on Open, convienient for Boards sending data on reset
  var ignoreLineOnOpen = false
  this.onOpened {
    if (ignoreLineOnOpen) {
      receiveLine(forceReceive = true)
    }

  }
  var lineIgnorePrefix: Option[String] = None

  def pollValue  : Unit

  /**
   * If LineIgnore is set, ignore all lines starting with prefix
   * This is used so that info/debugging lines and can be prefixed with "#" for example, but the software will still react on relevant lines
   */
  def setLineIgnorePrefix(prefix: String) = {
    this.lineIgnorePrefix = Some(prefix)
  }

  def clearReceivedLineInBuffer  = {

    try {
      withOpenedAndNotBusy {
        if (this.phyGetInputStream.available()>0) {
          this.receiveLine()
        }
      }

    } catch {
      case e: Throwable =>
        ""
    }
  }

  /*def isExceptionRemoved(e: SerialException) = {
    e.getLocalizedMessage match {
      case m if (m.contains("The device does not recognize the command")) =>
        try {
          this.comPort.close()
        } catch {
          case e: Throwable =>
        }
        true
      case m => false
    }

  }*/

  def sendLine(command: String): Unit = {
    withOpenedAndNotBusy {
      try {

        var writer = new PrintWriter(new OutputStreamWriter(this.phyGetOutputStream))
        writer.println(s"${command}")
        writer.flush()
        this.phyGetOutputStream.flush
      } catch {

        // Board has been removed!
        // case e: SerialException if (isExceptionRemoved(e)) =>
        //  e.printStackTrace()
        case e: Throwable => throw e
      }
    }
  }

  def sendLineReceiveLine(command: String, localPrefix: String = ""): String = {
    withOpenedAndNotBusy {
      try {
        sendLine(command)
        this.receiveLine(localPrefix).trim
      } catch {
        // Board has been removed!
        //case e: SerialException if (isExceptionRemoved(e)) =>
        //   ""

        case e: Throwable => throw e
      }
    }
  }

  def send1ZeroByte = {
    withOpenedAndNotBusy {
      try {
        this.phyGetOutputStream.write(0)
        this.phyGetOutputStream.flush
      } catch {
        // Board has been removed!
        //case e: SerialException if (isExceptionRemoved(e)) =>
        //   ""

        case e: Throwable => throw e
      }
    }
  }

  def send1LineReturn = {
    withOpenedAndNotBusy {
      try {
        this.phyGetOutputStream.write('\n')
        this.phyGetOutputStream.flush
      } catch {
        // Board has been removed!
        //case e: SerialException if (isExceptionRemoved(e)) =>
        //   ""

        case e: Throwable => throw e
      }
    }
  }

  def receiveLine(localPrefix: String = "", forceReceive: Boolean = false) = {
    withOpenedAndNotBusy {
      try {
        var ignorePrefix = localPrefix.trim match {
          case any if (forceReceive) => None
          case "" => this.lineIgnorePrefix
          case _ => Some(localPrefix)
        }

        // println("Waiting for line: ")
        var br = new BufferedReader(new InputStreamReader(this.phyGetInputStream, "US-ASCII"), 4096 * 4)
        var buf = new Array[Byte](5)
        var eofLoop = false
        var line = ""
        while (!eofLoop) {

          line = br.readLine() match {
            case null =>
              println("Got null line")

              ""
            case line =>

              ignorePrefix match {

                // Ignore line if starting with prefix
                case Some(prefix) if (line != null && line.startsWith(prefix)) =>
                  //println("Ignoring Line: "+line)
                  logInfo[LineSupportPhy]("Ignoring Line: "+line)
                // br.reset
                case _ =>
                  // Finished
                  logInfo[LineSupportPhy]("Got Line: "+println("Got null line"))
                  eofLoop = true
              }

              line
          }

        }


        line

      } catch {
        // Board has been removed!
        // case e: SerialException if (isExceptionRemoved(e)) =>
        //   ""

        case e: Throwable => throw e
      }
    }
  }

}
package com.idyria.ioda.data.phy

import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.BufferedReader
import scala.tools.jline_embedded.internal.InputStreamReader

trait LineSupportPhy extends TextSupportPhy with ManagedOpenClosePhy {

  // Line Ignore on Open, convienient for Boards sending data on reset
  var ignoreLineOnOpen = false
  this.onOpened {
    if (ignoreLineOnOpen) {
      receiveLine(forceReceive = true)
    }

  }
  var lineIgnorePrefix: Option[String] = None

  /**
   * If LineIgnore is set, ignore all lines starting with prefix
   * This is used so that info/debugging lines and can be prefixed with "#" for example, but the software will still react on relevant lines
   */
  def setLineIgnorePrefix(prefix: String) = {
    this.lineIgnorePrefix = Some(prefix)
  }

  def clearReceivedLineInBuffer: String = {

    try {
      this.receiveLine()
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
    this.synchronized {
      try {
        open
        //comPort.getOutputStream.flush()
        //clearReceivedLineInBuffer
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
    this.synchronized {
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
    this.synchronized {
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

  def receiveLine(localPrefix: String = "", forceReceive: Boolean = false) = {
    this.synchronized {
      try {
        open
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
            case null => ""
            case other =>

              ignorePrefix match {

                // Ignore line if starting with prefix
                case Some(prefix) if (other != null && other.startsWith(prefix)) =>
                // br.reset
                case _ =>
                  eofLoop = true
              }

              other
          }

        }
        /* while (!eofLoop) {

          var readCount = this.phyGetInputStream.read(buf)
          println("Read: " + readCount)
          if (readCount > 0) {
            line = line + new String(buf.slice(0, readCount - 1))

            println("Now line: " + line)
            ignorePrefix match {

              // Ignore line if starting with prefix
              case Some(prefix) if (line.startsWith(prefix)) =>
              // br.reset
              case _ =>
             //   eofLoop = true
            }

            // Detect EOF Line
            if (line.last=='\n') {
              println("EOF Line")
              eofLoop = true
              line=line.trim
            }
          }

        }*/

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
package org.odfi.ioda.uwisk.local

import org.odfi.tea.io.TeaIOUtils

import java.nio.ByteBuffer
import java.nio.charset.Charset

trait LWiskTrait {


  def requestTrigger(namespace:String,action:String) : String

  /**
   * Uses current Thread Class Loader
   * @param resourcePath
   */
  def loadResourceAsString(resourcePath:String, encoding: Charset = Charset.forName("UTF8")) = {
    Thread.currentThread().getContextClassLoader.getResourceAsStream(resourcePath) match {
      case null => None
      case in =>
        val bytes = TeaIOUtils.swallow(in)
        Some(encoding.decode(ByteBuffer.wrap(bytes)).toString)

    }
  }

}

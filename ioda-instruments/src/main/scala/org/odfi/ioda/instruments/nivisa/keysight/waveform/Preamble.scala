package org.odfi.ioda.instruments.nivisa.keysight.waveform

/**
  * Created by Tristran on 01.02.2017.
  */
class Preamble(val prString: String) {

  var fields = prString.split(",")

  var format = fields(0).toInt
  var wtype = fields(1).toInt
  var points = fields(2).toLong
  var lngCount = fields(3).toLong
  var dblXIncrement = fields(4).toDouble
  var dblXOrigin = fields(5).toDouble
  var lngXReference = fields(6).toLong
  var sngYIncrement = fields(7).toDouble
  var sngYOrigin = fields(8).toDouble
  var lngYReference = fields(9).toLong

  // Utilities
  def isFormatWord = format == Preamble.FORMAT_WORD

  def isFormatByte = format == Preamble.FORMAT_BYTE

  def isFormatASCII = format == Preamble.FORMAT_ASCII
}


object Preamble {

  val FORMAT_WORD = 1
  val FORMAT_BYTE = 0
  val FORMAT_ASCII = 4
}

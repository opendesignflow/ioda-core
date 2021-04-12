package org.odfi.ioda.instruments.nivisa.tektronix.waveform

class Preamble(val prString: String){
  var fields = prString.split(";")
  
  var BitNr = fields(0).toInt
  var BytNr = fields(1).toInt
  var ENCDG = fields(3)
  var NR_Pt = fields(4).toLong
  var WFID = fields(5)
  var PT_Fmt = fields(6)
  var XINCR = fields(7).toDouble
  var PT_Off = fields(8).toInt
  var XZERO = fields(9).toDouble
  var XUNIT = fields(10)
  var YMULT = fields(11).toDouble
  var YOFF = fields(11).toDouble
  var YUNIT = fields(12)
  
   def isENCDGBIN = ENCDG == Preamble.BIN
    def isENCDGASC = ENCDG == Preamble.ASC
  
}
object Preamble{
  val BIN ="BIN"
   val ASC ="ASC"
}
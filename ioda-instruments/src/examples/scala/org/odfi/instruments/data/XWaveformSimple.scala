package org.odfi.ioda.instruments.data

import scala.util.Random
import java.io.File

object XWaveformSimple extends App {

  // Waveform with Embedded Data
  //------------------

  var waveform = new XWaveform
  waveform.points = 50000L
 
 // waveform.data.set((0 until waveform.points).map { i => Random.nextInt() }.toArray)
  waveform.data = (0L until waveform.points).map { i => Random.nextInt() }.toArray

  //-- Save to File
  var out = new File("target/test-out")
  out.mkdirs

  waveform.toFile(new File(out, "waveform-50000-simple.xml"))
  //waveform.toFile(new File(out, "waveform-50000-simple.xml.bz2"))
  //(0 until

  
  //-- REad back
  var waveformRead = new XWaveform 
  waveformRead.fromFile(new File(out, "waveform-50000-simple.xml"))

  /*
>>>>>>> f21e7d93476326860247b3fb7df17c5c71f9ef3d
  // Wavefor with external file
  //---------------
  var waveformExt = new XWaveform
  waveformExt.points = 50000
  
  var smallWaveform = new SmallWaveform
  smallWaveform.setValues((0 until waveformExt.points).map { i => Random.nextInt() }.toArray)
  
  waveformExt.toFile(new File(out, "waveform-50000-external.xml"))
  waveformExt.toFile(new File(out, "waveform-50000-external.xml.bz2"))
  
  smallWaveform.toBinaryFile(new File(out,"waveform-50000-external.data"))
  smallWaveform.toBinaryFile(new File(out,"waveform-50000-external.data.bz2"))
  * */
  

}
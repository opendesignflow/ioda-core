package org.odfi.ioda.instruments.data

import org.odfi.ioda.instruments.compress.CompressModule
import org.odfi.ioda.instruments.nivisa.keysight.waveform.{Preamble, Waveform}

import java.io.{DataInputStream, DataOutputStream, File}

class SmallWaveform {
  
  var values: Option[Array[Int]] = None

  

  def getValues = values match {
    case Some(data) => data
    case None => throw new RuntimeException("Waveform has not been initialised with data")
  }
  def setValues(arr: Array[Int]) = {
    this.values = Some(arr)
  }

  def getPointsCount = {
    this.getValues.size
  }

  def toBinaryFile(f: File) = {

    //-- Check compression format
    var fileOutStream = CompressModule.getFileCompressOutputStream(f)

    if (f.exists()) {
      f.delete()
    }

    // Open Binary Stream
    var os = new DataOutputStream(fileOutStream)


    // Write next line with bytes
    this.values.get.foreach(os.writeInt(_))

    os.flush
    os.close

  }
}


object SmallWaveform {

  def fromBinaryFile(f: File) = {

    //-- Open
    
    var is = new DataInputStream(CompressModule.getFileCompressInputStream(f))


    //-- Prepare
    var waveform = new SmallWaveform

    //-- Get Data
    var data = Vector[Int]()
    while(is.available()>0) {
      data = data :+ is.readInt()
    }

    
    waveform.setValues(data.toArray)

    is.close()
    waveform

  }

}
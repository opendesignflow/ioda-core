package org.odfi.ioda.instruments.ieee

class IEEE4882BinaryBlock(var array : Option[Array[Byte]]) {
  
  //-- Check validity
  array match {
    case Some(data) =>
      
      if(data.length==0 || data(0).toChar != '#') {
        throw new RuntimeException("Cannot init IEEE4882BinaryBlock from data, must not be empty and start with # ")
      }
      
   
    case None => 
  }
  
  def getArray = array match {
    case Some(data) => data
    case None => throw new RuntimeException("Cannot work on non defined array")
  }
  
  /**
   * First char is #
   */
  def getDataLength = {

    // Second byte is a char that needs to be converted to Int
    var lengthDefinitionCharCount = this.getArray(1).toChar.toString.toInt

    //println("Lengtj definition char count: "+lengthDefinitionCharCount)

    // Legnth definition number of chars tells us how many chars to make a String from to get the number of points
    var lengthString = new String(this.getArray.drop(2).take(lengthDefinitionCharCount))
    //println("Lengtj string: "+lengthString)
    //var length = this.getArray.drop(2).take(lengthDefinitionCharCount).mkString.toInt
    lengthString.toInt
  }
  
  /**
   * Get the actual Data
   */
  def getData = {
    this.getArray.takeRight(this.getDataLength)
  }
  
  
}
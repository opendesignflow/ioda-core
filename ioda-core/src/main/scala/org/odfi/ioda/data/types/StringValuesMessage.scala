package org.odfi.ioda.data.types

class StringValuesMessage extends DataMessageValuesListMessage[String] {


  def cloneMessage : DataMessageValuesListMessage[String] = {
    val v = new StringValuesMessage
    v.values = this.values
    v
  }
  
}

class IntValuesMessage extends DataMessageValuesListMessage[Int] {

  def cloneMessage : DataMessageValuesListMessage[Int] = {
    val v = new IntValuesMessage
    v.values = this.values
    v
  }
}
class DoubleValuesMessage extends DataMessageValuesListMessage[Double] {

  def cloneMessage : DataMessageValuesListMessage[Double] = {
    val v = new DoubleValuesMessage
    v.values = this.values
    v
  }
}
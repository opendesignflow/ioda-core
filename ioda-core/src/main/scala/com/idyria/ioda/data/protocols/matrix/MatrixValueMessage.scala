package com.idyria.ioda.data.protocols.matrix


import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.ioda.data.types.MatrixValueMessageTrait

@xelement(name = "MatrixValueMessage")
class MatrixValueMessage[DT] extends MatrixValueMessageTrait {

  /**
   * List of Rows
   * Each row contains the columns
   */
  var data: Option[List[List[DT]]] = None

  def getRows = data.get.length
  def getColumns = data.get(0).length
  
  /**
   * Map each rows values to a reverse -> Mirror columns
   */
  def mirrorColumns = this.data match {
    case Some(d) =>
      this.data = Some(data.get.map { row => row.reverse} )
    case None => 
  }
  
  /**
   * Just reverse the rows
   */
  def mirrorRows = this.data match {
    case Some(d) =>
      this.data = Some(data.get.reverse )
    case None => 
  }
  
  /**
   * Returns the rows in series
   */
  def flatten = data match {
    case Some(data) => data.flatten
    case None => List()
  }
  
}

class NumericMatrixValueMessage[DT] extends MatrixValueMessage[DT]

class IntMatrixValueMessage extends NumericMatrixValueMessage[Int] {

}
class ShortMatrixValueMessage extends NumericMatrixValueMessage[Short] {

}
class LongMatrixValueMessage extends NumericMatrixValueMessage[Long] {

}

class DoubleMatrixValueMessage extends NumericMatrixValueMessage[Double] {

}
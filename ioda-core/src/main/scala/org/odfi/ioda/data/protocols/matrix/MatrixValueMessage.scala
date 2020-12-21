package org.odfi.ioda.data.protocols.matrix


import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import org.odfi.ioda.data.types.MatrixValueMessageTrait

import scala.util.Random

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

  def initRandom(rows : Int, columns : Int,min:Double,max:Double) = {

    val rand = new Random()

    val res = (0 until rows).map {
      r =>
        (0 until columns).map  {
          c =>
            rand.between(min,max)

        }.toList
    }.toList

    this.data = Some(res)

  }

}
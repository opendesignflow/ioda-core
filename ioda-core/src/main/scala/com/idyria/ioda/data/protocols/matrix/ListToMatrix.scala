package com.idyria.ioda.data.protocols.matrix

import com.idyria.ioda.data.protocols.Protocol
import com.idyria.ioda.data.protocols.ProtocolDescriptor
import com.idyria.ioda.data.protocols.ProtocolWithId
import com.idyria.ioda.data.types.IntValuesMessage
import com.idyria.ioda.data.types.DataMessageValuesListMessage
import com.idyria.ioda.data.types.DoubleValuesMessage
import com.idyria.ioda.data.protocols.params.ParamsMessage

class ListToMatrix extends ProtocolWithId {

  //this.configModel

  this.onConfigModelUpdated {

    config.get.supportBooleanKey("mirrorX", false, "Mirror Rows")
    config.get.supportBooleanKey("mirrorY", false, "Mirror Columns")
    config.get.supportIntKey("rows", -1, "Number of Columns")
    config.get.supportIntKey("columns", -1, "Number of Columns")
    config.get.supportBooleanKey("permuteRowsBy2", false, "Rows Permutation")
    
    
  }

  this.onDownMessage {

    case m: ParamsMessage if (config.get.hasString("parameter")) =>

      //-- Check values count
      val (cols, rows) = config.get.supportGetIntsOrError("columns", "rows")
     
      //println("On List message to matrix: " + cols + " -> " + rows)

      

      //-- Check parameter is a list
      m.parameters.get(config.get.getString("parameter").get) match {

        case Some(parameter) if (parameter.isIntList) =>

          //-- Check values count
          val intList = parameter.asIntList
          assert(intList.size == (cols * rows), "Not enough values to create matrix")
          
          //-- Convert
          val rowsData = intList.grouped(cols).toList
          val matrix = new IntMatrixValueMessage
          matrix.data = Some(rowsData)
          m.nextMessage = matrix
          messageMirroring(matrix)
          
          //-- Add permutations
           this.config.get.supportGetBoolean("permuteRowsBy2") match {
            case Some(true) => 
              
             // println("Permutting")
              matrix.data = Some(rowsData.zipWithIndex.map {
                case (vals,i) if ((i%2) ==0) => 
                  rowsData(i+1)
                case (vals,i) => 
                  rowsData(i-1)
              })
              
              
              
              
            case other => 
          }
          
          
        case Some(parameter) =>
          m.addError("Cannot make a Matrix out of non supported type for value ")

        case None =>

          m.addError("Cannot make a Matrix out of non existen parameter: " + config.get.getString("parameter"))
      }

    case m: DataMessageValuesListMessage[_] =>

      println("On List message to matrix: ")
      //-- Check values count
      val (cols, rows) = config.get.supportGetIntsOrError("columns", "rows")
     
      //println("On List message to matrix: " + cols + " -> " + rows)

      assert(m.values.size == (cols * rows), "Not enough values to create matrix")

      //-- Convert
      val nextMessage = m match {
        case im: IntValuesMessage =>
          val matrix = new IntMatrixValueMessage
          matrix.data = Some(im.values.grouped(cols).toList)
          im.nextMessage = matrix
          matrix
        case im: DoubleValuesMessage =>

          //println("Converting")

          val matrix = new DoubleMatrixValueMessage
          matrix.data = Some(im.values.grouped(cols).toList)
          im.nextMessage = matrix
          matrix
        case other =>
          sys.error("Message Type not supported")
      }

      //-- Add mirroring
      this.config.get.supportGetBoolean("mirrorX") match {
        case Some(true) =>
        //nextMessage.mirrorRows
        case other =>
      }
      this.config.get.supportGetBoolean("mirrorY") match {
        case Some(true) =>
          nextMessage.mirrorColumns
        case other =>
      }

  }

  def messageMirroring(m: MatrixValueMessage[_]) = {
    
     //-- Add mirroring
      this.config.get.supportGetBoolean("mirrorX") match {
        case Some(true) =>
        m.mirrorRows
        case other =>
      }
      this.config.get.supportGetBoolean("mirrorY") match {
        case Some(true) =>
          m.mirrorColumns
        case other =>
      }
    
  }
  
}

object ListToMatrix extends ProtocolDescriptor {

}
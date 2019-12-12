package com.idyria.platforms.std.valueslist

import org.odfi.indesign.core.main.IndesignPlatorm
import com.idyria.platforms.std.StdPlatform
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import org.odfi.indesign.core.config.Config
import org.odfi.ioda.IODA
import org.odfi.ioda.env.Environment
import org.odfi.ioda.simulation.data.protocols.text.SIMValuesListPhy
import org.odfi.ioda.data.protocols.text.ValuesListProtocol
import org.odfi.ioda.data.protocols.display.PrintMessageProtocol
import org.odfi.ioda.data.protocols.display.SeriesGUIPlotter
import org.odfi.ioda.data.protocols.text.IntValuesListProtocol
import org.odfi.ioda.data.protocols.text.DoubleValuesListProtocol
import org.odfi.ioda.data.protocols.matrix.ListToMatrix
import java.io.File
import com.idyria.platforms.std.StdPlatformUI
//import org.odfi.ioda.data.protocols.matrix.jfx.JFXMatrixStage

object StdValuesListExample extends App {

  /*Config.onImplementationSet {
    i =>
      i.swithToCleanRealm("examples.std-values-list")
  }*/
  OOXOOConfigModule.setCleanConfigFolder(new File("indesign-config/examples.std-values-list"))

  IndesignPlatorm use StdPlatform
  IndesignPlatorm use StdPlatformUI
  //StdPlatformUI.listenWithJMXClose(8888)
  IndesignPlatorm start

  // GUI Variables
  //----------------

  // Create Environment
  //---------------
  val env = IODA.addEnvironment("test")

  //-- Add Simulation physical
  /*env.simulation.addPhysical("sim-values") {
    phy =>
      phy.implementationType = classOf[SIMValuesListPhy]
      phy.setInt("valuesCount", 25)
      phy.repairErrors
  }*/

  //-- Add Data Source
  val datasource = env.onDataSource("test-datasource") {
    ds =>

      //-- Add Simulation 
      ds.physicals.simulationPhysicals.addRollbackOnError {
        sp => 
          sp.implementationType = classOf[SIMValuesListPhy]
          sp.setInt("valuesCount", 25)
          sp.ensureInstance
      }

      ds

  }

  //-- Add Datapath 
  val datapath = env.onDataPath("test-datapath") {
    datapath =>

      // Reference Data Source
      datapath.dataSourceReferences.add.reference(datasource)

      // datapath.boardRef.boardId = "test-board"

      datapath.pushProtocol[DoubleValuesListProtocol]
      datapath.pushProtocol[PrintMessageProtocol]
      datapath.pushProtocol[SeriesGUIPlotter]
      datapath.pushProtocolAnd[ListToMatrix] {
        p =>
          p.setInt("rows", 5)
          p.setInt("columns", 5)
      }
      datapath.pushProtocol[PrintMessageProtocol]
      //datapath.pushProtocol[JFXMatrixStage]

      datapath.repairErrors
      datapath

  }

  //-- Trigger a Datapath read
  datapath.runStatic {
    execution =>

      execution.collectData

    /*println(s"Collecting data")
      run.collectData
      Thread.sleep(1000)
       run.collectData
      Thread.sleep(1000) 
      run.collectData
      Thread.sleep(1000)*/

  }

  //IndesignPlatorm stop

}
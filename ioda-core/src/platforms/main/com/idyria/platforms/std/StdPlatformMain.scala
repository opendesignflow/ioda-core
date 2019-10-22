package com.idyria.platforms.std

import org.odfi.indesign.core.main.IndesignPlatorm
import org.odfi.indesign.core.config.ooxoo.OOXOOConfigModule
import java.io.File
import com.idyria.osi.tea.logging.TLogSource
import org.odfi.wsb.fwapp.views.FWAppViewIntermediary

object StdPlatformMain extends App with TLogSource {

  tlogEnableFull[FWAppViewIntermediary]
  
  // Parameters
  //-----------------
  IndesignPlatorm use OOXOOConfigModule
  OOXOOConfigModule.setConfigFolder(new File("ioda-std"))

  // Platform
  //--------------
  IndesignPlatorm use StdPlatform
  
  // Gui
  //--------------------
  IndesignPlatorm use StdPlatformUI
  //StdPlatformUI.listenWithJMXClose(8888)

  // Start
  //------------
  IndesignPlatorm.start

}
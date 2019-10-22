package com.idyria.ioda.env

import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions

class EnvironmentTraitDataPathTraitProtocolStackTraitProtocol extends EnvironmentTraitDataPathTraitProtocolStackTraitProtocolTrait {

  def onlyOnVCID(vcid: String) = {
    
    this.setString("vcidFilters",vcid)
  }

}

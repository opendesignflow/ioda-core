package org.odfi.ioda.env

import org.odfi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions

class EnvironmentTraitDataPathTraitProtocolStackTraitProtocol extends EnvironmentTraitDataPathTraitProtocolStackTraitProtocolTrait {

  def onlyOnVCID(vcid: String) = {
    
    this.setString("vcidFilters",vcid)
  }

}

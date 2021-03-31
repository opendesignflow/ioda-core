package org.odfi.ioda.data.phy.state

import org.odfi.ioda.data.phy.{LineSupportPhy, ManagedOpenClosePhy, PhysicalInterface}
import org.odfi.ioda.data.types.DataMessage

class PhyMessage[T <: PhysicalInterface] (val phy: T)extends DataMessage{



  def phyIsLineSupport = phy.isInstanceOf[LineSupportPhy]

}

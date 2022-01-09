package org.odfi.ioda.data.phy

import org.odfi.indesign.core.harvest.HarvestedResource

trait Device extends HarvestedResource {


  def open : Unit
  def close : Unit
}
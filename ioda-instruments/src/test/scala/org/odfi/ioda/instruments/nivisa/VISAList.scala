package org.odfi.ioda.instruments.nivisa

object VISAList extends App {
  
   var h = VISAHarvester
  h.harvest
  
  h.getResourcesOfType[VISADevice].foreach {
     case r => 
       println("Found Resource: "+r.getId)
   }
  
  
  
}
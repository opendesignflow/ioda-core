package org.odfi.ioda.data.types

import scala.reflect.ClassTag

class ValueMessage[BT : ClassTag](implicit valueTag : ClassTag[BT]) extends ValueMessageTrait {
  
  var value : Option[BT] = None
  
  def isOfType[TT: ClassTag](implicit tag:ClassTag[TT]) = {
    
    tag.getClass.isAssignableFrom(valueTag.getClass)
    
  }
  
}
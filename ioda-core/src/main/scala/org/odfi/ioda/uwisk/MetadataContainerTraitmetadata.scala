package org.odfi.ioda.uwisk

import javax.json.{JsonString, JsonValue}

class MetadataContainerTraitmetadata extends MetadataContainerTraitmetadataTrait {


  def asString : Option[String] = {
    this.value match {
      case v if (v!=null && v.getValueType==JsonValue.ValueType.STRING) =>
        Some(v.asInstanceOf[JsonString].getString)
      case other =>
        None
    }
  }

  override def toString = asString match {
    case None => value.toString
    case Some(str) => str
  }

}

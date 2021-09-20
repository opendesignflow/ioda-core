package org.odfi.ioda.uwisk

trait MetadataContainer extends MetadataContainerTrait {

  def getMetadataOption(name:String) = this.metadatasAsScala.find(_.id==name)

}

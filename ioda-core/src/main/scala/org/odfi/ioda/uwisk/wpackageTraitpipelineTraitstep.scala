package org.odfi.ioda.uwisk

class wpackageTraitpipelineTraitstep extends wpackageTraitpipelineTraitstepTrait{

  def isJavaPipeline = this.id.startsWith("java/")
  def isPipelineRegistryReference = this.id.startsWith("@")
  def getJavaClass = this.id.stripPrefix("java/")
}

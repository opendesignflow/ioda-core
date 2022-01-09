package com.idyria.platforms.std.ui.utils
/*
import org.odfi.wsb.fwapp.lib.ooxoo.OOXOOEntityBindView
import org.odfi.ioda.IODA
import org.odfi.wsb.fwapp.framework.FWAppTempBufferView
import org.odfi.wsb.fwapp.lib.ooxoo.EntityBindBuffer
import scala.reflect.ClassTag
import org.odfi.ooxoo.core.buffers.structural.XList

trait IODAUIUtils extends OOXOOEntityBindView with FWAppTempBufferView {
 
  def iodaEntitySubmitSaveConfig = {
    entityOnSubmit {
      println("Saving IODA entity")
      IODA.saveConfig
    }
  }
  
  /**
   * Creates an Edit icon that saves the entity to TEmp Buffer for editing
   * TO be used in combination with "withEntityEdit"
   */
  def entityEditReloadIcon[T<:EntityBindBuffer](entity:T) = {
    "ui edit icon" :: iconClickReload {
      this.putToTempBuffer[T]("entity.edit."+entity.getClass.getCanonicalName, entity)
    }
  }
  
  /**
   * If the temp buffer holds an entity to edit with same canonical name as the provided class, then the closure is called
   */
  def withEntityEdit[T<:EntityBindBuffer](category:String)(cl: T => Any)(implicit tag : ClassTag[T]) = {
    
    this.getTempBufferValue[T](s"entity.edit.${category}."+tag.runtimeClass.getCanonicalName) match {
      case Some(entity) => 
        cl(entity) 
      case None =>
    }
    
  }
  
  /**
   * If the temp buffer holds an entity to edit with same canonical name as the provided class, and contained in the provided list, then the closure is called
   */
  def withListEntityEdit[T<:EntityBindBuffer](ls:XList[T])(cl: T => Any)(implicit tag : ClassTag[T]) = {
    withEntityEdit[T](ls.hashCode().toString) {
      entity => 
        if (ls.contains(entity)) {
          cl(entity)
        }
    }
  }
  
}*/
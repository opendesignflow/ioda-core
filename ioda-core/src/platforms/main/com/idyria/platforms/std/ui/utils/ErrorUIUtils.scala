package com.idyria.platforms.std.ui.utils

import org.odfi.wsb.fwapp.module.semantic.SemanticView
import com.idyria.osi.tea.errors.ErrorSupport
import com.idyria.osi.tea.errors.ErrorRepairSupport

trait ErrorUIUtils extends SemanticView {

  def repairReloadIcon(err: ErrorRepairSupport) = {
    if (err.hasErrors) {
      //@data-tooltip='Repair'
      "ui treatment icon " :: iconClickReload(err.repairErrors)
    }
  }

  def lastErrorColumn(err: ErrorSupport) = {
    rtd {
      err.getLastError match {
        case Some(err) =>
          classes("negative")
          text(err.getLocalizedMessage)
        case None =>
          classes("Positive")
          "ui icon check" :: i {

          }
          text("None")
      }
    }
  }
  
  def immidiateErrorAndRepairStatus(err: ErrorRepairSupport) = {
    div {
      err.hasImmediateErrors match {
        case true => 
          "ui error message " :: div {
            +@("style" -> "display:inline-block")
            text(s"${getImmediateErrors.size} errors to be repaired")
          }
          "ui success button " :: buttonClickReload("Repair") {
           // +@("style" -> "display:inline-block")
            err.repairErrors
          }
        case false => 
          "ui success message " :: "No Repairable Errors found"
          
      }
    }
  }

}
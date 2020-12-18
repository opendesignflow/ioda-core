package com.idyria.platforms.std.environment

import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.ioda.env.EnvironmentHarvester
import org.odfi.ioda.env.Environment
import com.idyria.osi.ooxoo.core.buffers.structural.xelement

trait EnvironmentUIUtils extends SemanticView {

  def withEnvironmentsOtherwiseWarning(cl: Iterable[Environment] => Any) = {
    EnvironmentHarvester.getResourcesOfType[Environment] match {
      case none if (none.size == 0) =>

        "ui warning message" :: "Please add at least one environment"

      case environments =>
        cl(environments)
    }
  }

  /**
   * Shows warning if no environment, otherwise creates tabs and call closure to create tab content
   */
  def withEnvironmentsTabsOtherwiseWarning(cl: (Integer, String, Environment) => Any) = {

    withEnvironmentsOtherwiseWarning {
      envs =>
        semanticObjectsBottomTab("environments",envs.map(env => (env.name.toString, env))) {
          case (index, name, env) =>
            cl(index,name,env)
        }
    }
 
  }

}
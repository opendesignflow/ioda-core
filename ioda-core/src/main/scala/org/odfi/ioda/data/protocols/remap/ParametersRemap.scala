package org.odfi.ioda.data.protocols.remap

import org.odfi.ioda.data.protocols.ProtocolWithId
import org.odfi.ioda.data.protocols.params.ParamsMessage

/**
 * Remap parameter names to another name by exact match
 */
class ParametersRemap extends ProtocolWithId {

  this.onDownMessage {
    
    case dm: ParamsMessage =>

      // Search Remaps in config support
      //------------------
      this.config.get.supportGetKeysOfType("remap").foreach {
        remapSupportKey =>
          logInfo[ParametersRemap]("FOUND KEY For REmap: " + remapSupportKey.name+" -> "+remapSupportKey.default)

          //-- Get Actual value
          try {
            val remapName = remapSupportKey.name.toString
            val remapTargetName = this.config.get.supportGetValue(remapSupportKey.name, "remap").get
            dm.renameParameter(remapName, remapTargetName)
          } catch {
            case e: Throwable =>
              e.printStackTrace()
          }

      }

    /*
      this.configGetDouble(name, default)

      this.config.get.getKeysByType("remap").foreach {
        remapConfig =>

          dm.renameParameter(remapConfig.name.toString(), remapConfig.values.head.toString())

      }*/
    case other =>

  }

}
package com.idyria.ioda.data.protocols.params

import com.idyria.ioda.data.protocols.ProtocolWithId

/**
 * TODO: Support config based mapping
 */
class ParamsMapper extends ProtocolWithId {

    // Monitoring
    // Map[VCID,[Name,LastSeen]]
    var monitoringMap = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Any]]()

    this.onConfigModelUpdated {

    }

    this.onDownMessage {
        case dm: ParamsMessage =>

            try {
                this.config.get.getKeysByType("transform").foreach {
                    transformKey =>

                        val nameMatcher = transformKey.name.toString.r

                        // Got through message parameters and look for appropriate transform
                        dm.parameters.foreach {

                            // Param Name matches transformation key regexp
                            case (parameterName, parameterValue) if (nameMatcher.findFirstIn(parameterName).isDefined) =>

                                logInfo[ParamsMapper]("Transforming parameter " + parameterName)

                                // Init with value from map, maybe be updated
                                //var parameterValue = originalParameterValue

                                //-- Got through transforms
                                transformKey.values.map(_.toString()).foreach {

                                    case "toInt" =>

                                        parameterValue.toInt
                                    // parameterValue = dm.updateParameter(parameterName -> parameterValue.toString().toInt)

                                    case "toBoolean" =>

                                        parameterValue.toBoolean
                                    //parameterValue = dm.updateParameter(parameterName -> booleanValue)

                                    //-- Pedestal
                                    case pedestal if (pedestal.startsWith("pedestal") && parameterValue.isInt) =>

                                        // TH
                                        val pValue = pedestal.split('=')(1).toInt

                                        // Calculate
                                        if (parameterValue.asInt > pValue) {
                                            dm.updateParameter(parameterName -> (parameterValue.asInt - pValue))
                                        } else {
                                            dm.updateParameter(parameterName -> 0)
                                        }
                                    case pedestal if (pedestal.startsWith("pedestal") && parameterValue.isIntList) =>

                                        // TH
                                        val pValue = pedestal.split('=')(1).toInt

                                        // Calculate
                                        parameterValue.value = parameterValue.asIntList.map {
                                            case v if (v > pValue) => ((v - pValue)).toInt
                                            case v                 => 0
                                        }

                                    //-- Gain
                                    case gain if (gain.startsWith("gain") && parameterValue.isIntList) =>

                                        // TH
                                        val gValue = gain.split('=')(1).toDouble

                                        // Calculate
                                        parameterValue.value = parameterValue.asIntList.map {
                                            case v => (v*gValue).toInt
                                        }

                                    //-- Threshold
                                    case threshold if (threshold.startsWith("threshold") && parameterValue.isInt) =>

                                        // TH
                                        val thValue = threshold.split('=')(1).toInt

                                        // Calculate
                                        if (parameterValue.asInt > thValue) {
                                            dm.updateParameter(parameterName -> true)
                                        } else {
                                            dm.updateParameter(parameterName -> false)
                                        }

                                    //-- Monitoring
                                    case "monitorChanged" if (dm.virtualChannel.isDefined && dm.parameters.contains("forceChanged")) =>
                                        parameterValue.changed = true
                                    case "monitorChanged" if (dm.virtualChannel.isDefined) =>

                                        //-- Get VCID and monitored values
                                        val vcid = dm.virtualChannel.get
                                        val monitoringValues = this.monitoringMap.getOrElseUpdate(vcid, scala.collection.mutable.Map[String, Any]())

                                        //-- Update Changed in current message value
                                        monitoringValues.get(parameterName) match {
                                            case Some(oldValue) =>
                                                parameterValue.updateChangedState(oldValue)
                                            case None =>
                                        }

                                        //-- Save actual in monitor
                                        monitoringValues.update(parameterName, parameterValue.value)

                                    case "monitorChanged" if (dm.virtualChannel.isEmpty) =>

                                        logWarn[ParamsMapper]("monitorChanged can only be used on a message with virtual channel defined")

                                    //-- toIntList
                                    case "toIntList" =>

                                        logInfo[ParamsMapper]("To Int list on value:" + parameterValue.value.toString.trim)
                                        parameterValue.value = parameterValue.value.toString().trim.split(',').map(_.toInt).toList

                                    //-- Unsupported
                                    case other =>
                                        logWarn[ParamsMapper]("Unsupported Transform: " + other)
                                }

                            case other =>

                        }

                }
            } catch {
                case e: Throwable =>
                    logError[ParamsMapper]("Error while transforming parameter: " + e.getLocalizedMessage)
                    if (isLogFine[ParamsMapper]) {
                        e.printStackTrace()
                    }
                    throw e
            }
        //this.configGetDouble(name, default)

        case other =>

    }

}
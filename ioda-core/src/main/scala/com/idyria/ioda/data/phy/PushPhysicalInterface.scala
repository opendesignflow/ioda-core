package com.idyria.ioda.data.phy

import com.idyria.ioda.env.EnvironmentTraitGlobalPhysical
import com.idyria.ioda.env.EnvironmentTraitDataSourceTraitPhysicalsTraitLocalPhysical
import com.idyria.ioda.env.EnvironmentTraitDataSource

trait PushPhysicalInterface extends PhysicalInterface {
  
  
  def findDataPaths = {
    this.configModel match {
      case Some( globalphy : EnvironmentTraitGlobalPhysical) => 
        //println(s"Global Physical context")
        globalphy.parentReference match {
          case Some(env) => 
           // println(s"--> Env")
            // Filter datapaths with a data source reference which references this physical
            env.dataPaths.filter {
              dp => 
              //  println("--> Testing DP")
                dp.dataSourceReferences.find {
                  dsref => 
                  //  println(s"---> Datasource ref")
                    //ds.physicals.
                    env.dataSources.findByEId[EnvironmentTraitDataSource](dsref.refId.toString) match {
                      case Some(datasource) => 
                        
                      //  println(s"-----> Found datasource")
                        datasource.physicals.globalPhysicals.find {
                          globalRef =>
                          //  println(s"-------> Testing Global Ref ${globalRef.refId} against ${globalphy.eid}")
                            globalphy.eid !=null && globalRef.refId.toString ==  globalphy.eid.toString
                        }.isDefined
                        
                        //datasource.physicals
                      case other => false
                    }
                }.isDefined
            }.toList
          case None => List()
        }
      case Some(localphy:EnvironmentTraitDataSourceTraitPhysicalsTraitLocalPhysical) => 
        List()
      case other =>  List()
    }
  }
  
}
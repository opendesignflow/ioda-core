package org.odfi.ioda.discover.mdns

import org.odfi.indesign.core.module.IndesignModule
import java.net.NetworkInterface

import org.odfi.ubroker.app.http.connector.HTTPConnector
import org.odfi.indesign.core.harvest.HarvestedResource
import org.odfi.indesign.core.harvest.HarvestedResourceDefaultId
import javax.jmdns.JmDNS
import java.net.InetAddress
import javax.jmdns.ServiceInfo
import org.odfi.tea.thread.ThreadLanguage
import java.net.Inet4Address

/*
class MDNSSiteDiscovery(val site: Site) extends IndesignModule with DecorateAsScala with ThreadLanguage {

    //val mDNSService = new MulticastDNSService();
    var jmDNS = List[JmDNS]()
    val externIps = NetworkInterface.getNetworkInterfaces.asScala.filter(n => !n.isLoopback() && n.isUp()).map(n => n.getInetAddresses.asScala).flatten.toArray

    
    def reload = {
        
        println(s"UNregistereing")
        jmDNS.foreach {
            jmd => 
                
                jmd.unregisterAllServices()
                
        }
        
        println(s"Registering") 
        
        var externIps = NetworkInterface.getNetworkInterfaces.asScala.filter(n => !n.isLoopback() && n.isUp()).map(n => n.getInetAddresses.asScala).flatten.toArray
        jmDNS = externIps.collect {
            case ip: Inet4Address =>

                

                println("Creating JMDSN on: " + ip.getHostAddress)
                var mdns = JmDNS.create(ip)

                mdns
           

        }.toList
        
        //-- Start
        val topSite = site.findTopMostIntermediaryOfType[Site] match { case Some(p) => p; case None => site }
        
         jmDNS.foreach {
            jmdns =>
                println("Registered Service")
                var serviceInfo = ServiceInfo.create("_ioda._tcp.local.", "concentrator", topSite.engine.network.connectors.collectFirst { case c: HTTPConnector => c }.get.port, 0, 0, true, s"${site.fullURLPath}/incoming/data/params");

                jmdns.registerService(serviceInfo)
        }
        
    }
    
    // Services
    //-------------

    this.onInit {

    }

    this.onStart {

        //-- Start
        val topSite = site.findTopMostIntermediaryOfType[Site] match { case Some(p) => p; case None => site }

        // Create a JmDNS instance
        var externIps = NetworkInterface.getNetworkInterfaces.asScala.filter(n => !n.isLoopback() && n.isUp()).map(n => n.getInetAddresses.asScala).flatten.toArray
        jmDNS = externIps.collect {
            case ip: Inet4Address =>

                

                println("Creating JMDSN on: " + ip.getHostAddress)
                var mdns = JmDNS.create(ip)

                mdns
           

        }.toList

        //-- Support Concentrator Params Service
        /*val service = new ServiceInstance(new ServiceName("concentrator._ioda._tcp.local."), 0, 0, site.engine.network.connectors.collectFirst { case c: HTTPConnector => c }.get.port, Name.fromString("n550g."), externIps, s"${site.basePath}/incoming/params");
    val registeredService = mDNSService.register(service);

    this.addDerivedResource(new MDNSServiceResource(registeredService))*/

        //fork {
        jmDNS.foreach {
            jmdns =>
                println("Registered Service")
                var serviceInfo = ServiceInfo.create("_ioda._tcp.local.", "concentrator", topSite.engine.network.connectors.collectFirst { case c: HTTPConnector => c }.get.port, 0, 0, true, s"${site.fullURLPath}/incoming/data/params");

                jmdns.registerService(serviceInfo)
        }
        // }

        //jmDNS.foreach(_.registerService(serviceInfo))

    }
    this.onStop {

        //fork {
        /*this.getDerivedResources[MDNSServiceResource].foreach {
        r =>
         // mDNSService.unregister(r.instance)
      }*/

        this.jmDNS.foreach {
            s =>
                s.unregisterAllServices()
                s.close()
        }
        // }
        /*mDNSService.getNames.foreach {
      case sn : ServiceName => mDNSService.unregister(sn)
      case other =>
    }*/

    }

}
*/
//class MDNSServiceResource(val instance: ServiceInstance) extends HarvestedResourceDefaultId
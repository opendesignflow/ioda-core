package com.idyria.platforms.discovery
/*
import org.odfi.wsb.fwapp.Site
import org.odfi.wsb.fwapp.swing.SwingPanelSite
import net.posick.mDNS.DNSSDListener
import net.posick.mDNS.ServiceInstance
import org.xbill.DNS.ResolverListener
import net.posick.mDNS.Lookup
import org.xbill.DNS.Type
import org.xbill.DNS.DClass
import org.odfi.wsb.fwapp.views.InlineView
import org.odfi.wsb.fwapp.module.semantic.SemanticView
import org.odfi.wsb.fwapp.DefaultSiteApp
import net.posick.mDNS.MulticastDNSService
import java.net.InetAddress
import org.xbill.DNS.Name
import net.posick.mDNS.ServiceName
import java.net.NetworkInterface
import scala.collection.convert.DecorateAsScala
import net.posick.mDNS.Lookup.RecordListener
import net.posick.mDNS.Browse
import org.xbill.DNS.Message
import com.idyria.osi.wsb.webapp.http.message.HTTPPathIntermediary
import com.idyria.osi.wsb.webapp.http.connector.HTTPProtocolHandler
import com.idyria.osi.wsb.core.network.connectors.tcp.TCPProtocolHandlerConnector
import com.idyria.osi.wsb.core.network.connectors.tcp.TCPConnector
import com.idyria.osi.wsb.webapp.http.connector.HTTPConnector
import org.odfi.wsb.fwapp.DefaultSite

object MDNSDiscoveryExample extends App with DecorateAsScala {

  val externIps = NetworkInterface.getNetworkInterfaces.asScala.filter(n => !n.isLoopback() && n.isUp()).map(n => n.getInetAddresses.asScala).flatten.toArray

  externIps.foreach {
    a =>
      println(s"Address: " + a.getHostAddress)
  }

  val lookup = new Lookup("ioda.device._http._tcp.local.", Type.ANY, DClass.IN);
  val records = lookup.lookupRecords();
  lookup.lookupRecordsAsync(new RecordListener {
    def handleException(a: Any, b: Exception): Unit = {
      println("LU Error: " + b.getLocalizedMessage)
    }
    def receiveRecord(a: Any, b: org.xbill.DNS.Record): Unit = {
      println("A: " + a)
      println("Found Device: " + b.getName.getLabelString(0))
      println("Found IP: " + b.rdataToString())
    }
  })
  records.foreach {
    r =>
      println(s"Record: " + r)
  }

  /*val serviceTypes = Array(

    "_http._tcp.", // Web pages

    );

  //ResolverListener
  val browse = new Browse("_http._tcp.");
  browse.start(new DNSSDListener() {
    def serviceDiscovered( id : Object,  service : ServiceInstance): Unit =  {
      System.out.println("Service Discovered - " + service);
    }

    def serviceRemoved(id : Object,  service : ServiceInstance): Unit =  {
      System.out.println("Service Removed - " + service);
    }

    def handleException(id : Object ,  e : Exception) : Unit =  {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  });

  while (true) {
    Thread.sleep(100);
    if (System.in.read() == 'q') {
      sys.exit
    }
  }
  browse.close();*/

  val mDNSService = new MulticastDNSService();

  //-- Support Concentrator Params Service
  val service = new ServiceInstance(new ServiceName("ioda.concentrator.params._http._tcp.local."), 0, 0, 8589, Name.fromString("n550g."), externIps, "/data/incoming/params");

  val registeredService = mDNSService.register(service);

  var browse = new Browse(
    "ioda.device._http._tcp.local.",
    "_http._tcp.", // Web pages
    "_printer._sub._http._tcp", // Printer configuration web pages
    "_org.smpte.st2071.device:device_v1.0._sub._mdc._tcp", // SMPTE ST2071 Devices
    "_org.smpte.st2071.service:service_v1.0._sub._mdc._tcp");

  val listener = new DNSSDListener() {

    def serviceDiscovered(id: Any, service: ServiceInstance): Unit = {
      System.out.println("Service Discovered - " + service);
    }

    def serviceRemoved(id: Any, service: ServiceInstance): Unit = {
      System.out.println("Service Removed - " + service);
    }

    def handleException(id: Any, e: Exception): Unit = {
      System.err.println("Exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }

    def receiveMessage(id: Any, m: Message) = {

    }
  }
  mDNSService.startServiceDiscovery(browse, listener)

  // deviceLookup.start
 //tlogEnableFull[HTTPProtocolHandler]
  // tlogEnableFull[TCPProtocolHandlerConnector[_]]
  
 

  //tlogEnableFull[TCPConnector]
  //tlogEnableFull[HTTPConnector]
  val site = new DefaultSite("/data") {

     tlogEnableFull[HTTPPathIntermediary]
    
    this.onPOST {
      req =>
      //  println(s"Received post: " + req.nextParts.head.bytes)
        println(s"Received post: ")
    }

  }

  site.listen(8589)
  site.start
/*

  val int = "/data/incoming/params" is {

  }

  int.fwappIntermediary.onPOST {
    p =>

      println("Received Data: " + new String(p.nextParts.head.bytes))
  }*/

  site.onShutdown {
    println("Stopping services")

    mDNSService.unregister(service)
    mDNSService.close()
  }

  /*"/boards" view new InlineView with SemanticView {
    html {
      head {
        placeLibraries
      }

      body {

        //val deviceLookup = new Lookup("ioda.device._http._tcp.local.", Type.ANY, DClass.IN);

        /* var lookup = new Lookup("ioda.device._http._tcp.local.", Type.ANY, DClass.IN);
        val records = lookup.lookupRecords();
        var services = lookup.lookupServices()
        services.foreach {
          si =>
            println("Found Service: " + si.getName.toString())
        }
        lookup.close()*/

        "ui table " :: table {
          thead("Board Name", "IP Address")

          trLoop(records.filter(_.getType == Type.A)) {
            case record =>

              td(record.getName.getLabelString(0)) {

              }

              td(record.rdataToString()) {

              }
          }
        }

      }
    }
  }*/

  

  var serviceTypes = Array[String](

    "_http._tcp.", // Web pages
    "_printer._sub._http._tcp", // Printer configuration web pages
    "_org.smpte.st2071.device:device_v1.0._sub._mdc._tcp", // SMPTE ST2071 Devices
    "_org.smpte.st2071.service:service_v1.0._sub._mdc._tcp" // SMPTE ST2071 Services
  );

  /*
var browse = new Browse("_http._tcp.",              // Web pages
    "_printer._sub._http._tcp", // Printer configuration web pages
    "_org.smpte.st2071.device:device_v1.0._sub._mdc._tcp",  // SMPTE ST2071 Devices
    "_org.smpte.st2071.service:service_v1.0._sub._mdc._tcp" );

browse.start(new DNSSDListener() {

   def serviceDiscovered( id: Any,  service : ServiceInstance) : Unit {
        System.out.println("Service Discovered - " + service);
    }

    def serviceRemoved(id : Any,  service : ServiceInstance) : Unit{
        System.out.println("Service Removed - " + service);
    }

   def handleException( id : Any,  e : Exception) : Unit {
        System.err.println("Exception: " + e.getMessage());
        e.printStackTrace(System.err);
    }
});
while (true)
{
    Thread.sleep(10);
    if (System.in.read() == 'q')
    {
       // break;
    }
}
browse.close();*/

}*/
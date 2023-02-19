package org.odfi.ioda.uwisk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import java.io.{File, FileInputStream, InputStream, InputStreamReader}
import java.net.URL

class Packages {

  var wpackage = new java.util.LinkedList[wpackage]()

  def loadFromYAML(f: File): Packages = {

    val is = new FileInputStream(f)
    try return loadFromYAML(is)
    finally is.close()
  }
  def loadFromYAML(is: InputStream): Packages = {
    val mapper = new ObjectMapper(new YAMLFactory)
    mapper.findAndRegisterModules()

    mapper.readValue(new InputStreamReader(is), classOf[Packages])

  }
}

object Packages {

  def loadWPackage(f: File): wpackage = {
    val is = new FileInputStream(f)
    try return loadWPackage(is)
    finally is.close()
  }

  def loadWPackage(u: URL): wpackage = {
    val is = u.openStream()
    try return loadWPackage(is)
    finally is.close()
  }

  def loadWPackage(is: InputStream): wpackage = {
    val mapper = new ObjectMapper(new YAMLFactory)
    mapper.findAndRegisterModules()

    mapper.readValue(new InputStreamReader(is), classOf[wpackage])
  }

}

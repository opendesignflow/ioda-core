package org.odfi.ioda.instruments.compress

import org.odfi.indesign.core.module.IndesignModule
import java.io.File
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.util.zip.GZIPOutputStream
import java.io.FileOutputStream
import java.util.zip.ZipOutputStream
import java.util.zip.GZIPInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry

object CompressModule extends IndesignModule {

  def isArchive(f:File) = {
    
    f.getName match {
      case arch if(arch.endsWith(".zip")) => true
      case arch if(arch.endsWith(".tar")) => true
      case arch if(arch.endsWith(".tar.gz")) => true
      case arch if(arch.endsWith(".tar.bz2")) => true
      case other => false
    }
  }
  
  def getFileCompressOutputStream(f: File) = {
    f.getName match {
      case name if (name.endsWith("bz2")) =>

        new BZip2CompressorOutputStream(new FileOutputStream(f))

      case name if (name.endsWith("zip")) =>

        var zo = new ZipOutputStream(new FileOutputStream(f))
        zo.putNextEntry(new ZipEntry(f.getName))
        zo

      case name if (name.endsWith("gzip")) =>

        new GZIPOutputStream(new FileOutputStream(f))

      case other => new FileOutputStream(f)
    }
  }
  
  def getFileCompressInputStream(f: File) = {
    f.getName match {
      case name if (name.endsWith("bz2")) =>

        new BZip2CompressorInputStream(new FileInputStream(f))

      case name if (name.endsWith("zip")) =>

        var is = new ZipInputStream(new FileInputStream(f))
        is.getNextEntry
        is

      case name if (name.endsWith("gzip")) =>

        new GZIPInputStream(new FileInputStream(f))

      case other => new FileInputStream(f)
    }
  }

}
package org.odfi.ioda.instruments.compress

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import java.lang.ref.SoftReference
import org.odfi.tea.io.TeaIOUtils

trait MemoryArchiveEntry {

  var data: Option[SoftReference[Array[Byte]]] = None

  def getName : String
  
  def getData = data match {
    case Some(data) if (data.get() != null) => data.get
    case other =>
      data = Some(new SoftReference(fetchData))
      data.get.get
  }

  def fetchData: Array[Byte]

  def clear = data = None
  
}

class ZipMemoryArchiveEntry(val zipFile: ZipFile, val archiveEntry: ZipArchiveEntry) extends MemoryArchiveEntry {

  def getName = archiveEntry.getName

  def fetchData = {
    var is = zipFile.getInputStream(archiveEntry)
    var data = TeaIOUtils.swallowBytes(is, archiveEntry.getSize.toInt)
    is.close()
    data
  }

}
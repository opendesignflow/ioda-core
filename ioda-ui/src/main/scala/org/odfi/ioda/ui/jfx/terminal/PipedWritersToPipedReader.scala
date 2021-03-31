package org.odfi.ioda.ui.jfx.terminal

import java.io._
import java.util.concurrent.{Callable, Executors, ThreadFactory}
import scala.jdk.CollectionConverters._

class PipedWritersToPipedReader {

  // Piped Writer and readers

  val pipedWriter = new PipedWriter()
  val ber = new BufferedWriter(pipedWriter)

  val pipedReader = new PipedReader(pipedWriter)
  val bufferedReader = new BufferedReader(pipedReader)

  // Print Writer to write to the pipe
  val targetPrintWriter = new PrintWriter(pipedWriter)

  val streamExecutorTHFactory = new ThreadFactory {

    val created = false
    var th : Option[Thread] = None

    override def newThread(r: Runnable): Thread =  {
      created.synchronized {
        th match {
          case Some(th) => th
          case None =>
            val nth = new Thread
            nth.setDaemon(true)
            th = Some(nth)
            nth
        }

      }
    }
  }
  val streamExecutorTHFactory2 = new ThreadFactory {



    override def newThread(r: Runnable): Thread =  {
      val nth = new Thread
      nth.setDaemon(true)
      nth
    }
  }
  val streamExecutor = Executors.newCachedThreadPool(streamExecutorTHFactory2)


  def runOnPipeThread(cl : => Unit) = {
    this.streamExecutor.invokeAll(List(new Callable[Unit] {
      override def call(): Unit =  {
        cl
      }
    }).asJavaCollection)
  }



  class InternalOS extends OutputStream {
    override def write(b: Int): Unit = {
      runOnPipeThread {
        pipedWriter.write(b)
      }
      // pipedWriter.synchronized {

      //pipedWriter.flush()
      //}

    }

    override def write(b: Array[Byte]): Unit =  {

      runOnPipeThread {
        pipedWriter.write(b.map(_.toChar))
      }
      //pipedWriter.synchronized {

      //pipedWriter.flush()
      //}
    }

    override def write(b: Array[Byte], off: Int, len: Int): Unit =  {
      runOnPipeThread {
        pipedWriter.write(b.map(_.toChar),off,len)
      }
      //pipedWriter.synchronized {

      //pipedWriter.flush()
      //}
    }

    override def flush(): Unit =  {
      runOnPipeThread {
        pipedWriter.flush()
      }
      // pipedWriter.synchronized {
      //
      // }
    }
  }

  /*class InternalOS extends OutputStream {
    override def write(b: Int): Unit = {
     // pipedWriter.synchronized {
        pipedWriter.write(b)
        //pipedWriter.flush()
      //}

    }

    override def write(b: Array[Byte]): Unit =  {
      //pipedWriter.synchronized {
        pipedWriter.write(b.map(_.toChar))
        //pipedWriter.flush()
      //}
    }

    override def write(b: Array[Byte], off: Int, len: Int): Unit =  {
      //pipedWriter.synchronized {
        pipedWriter.write(b.map(_.toChar),off,len)
        //pipedWriter.flush()
      //}
    }

    override def flush(): Unit =  {
     // pipedWriter.synchronized {
        //pipedWriter.flush()
     // }
    }
  }*/

  def connectNewOutputStream = {

    //new BufferedOutputStream(new InternalOS)
    new InternalOS

  }


  def getReader = pipedReader


}
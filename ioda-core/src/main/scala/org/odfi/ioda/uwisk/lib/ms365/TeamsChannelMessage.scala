package org.odfi.ioda.uwisk.lib.ms365

import org.apache.http.client.fluent.{Executor, Request}
import org.apache.http.entity.ContentType
import org.markdown4j.Markdown4jProcessor
import org.odfi.ioda.logging.WithLogger
import org.odfi.ioda.pipelines.{DefaultPipeline, PipelineWithId}
import org.odfi.ioda.uwisk.pipeline.{WPipeline, WPipelineWithId}

import java.net.URI

class TeamsChannelMessage extends WPipelineWithId with WithLogger {

  this.onWiskMessage {
    case (msg, context) =>

      // LOGGER.info(s"Message: ${msg.getMetadataString("message")}")
      // LOGGER.info(s"Connector: ${msg.getMetadataString("connector")}")
      try {
        println("In P Teams message")
        val tMessage = context.getMetadataString("teams.message").getOrElse(sys.error("Teams Channel requires message parameter"))
        val tURL = context.getMetadataString("teams.connector.url").getOrElse(sys.error("Teams Channel requires target URL"))
        println("In P Teams message")

        // Text
        //---------------
        val mp = new Markdown4jProcessor



        // Send
        //--------------
        val exec = Executor.newInstance()
        val request = Request.Post(new URI(tURL))
          .bodyString(s"""{text: "${mp.process(tMessage)}"}""",
            ContentType.APPLICATION_JSON)


        val response = exec.execute(request).returnResponse()
        val status = response.getStatusLine
        logger.info("Res: " + status.getReasonPhrase)
      } catch {
        case e: Throwable =>
          e.printStackTrace()
      }

  }

}

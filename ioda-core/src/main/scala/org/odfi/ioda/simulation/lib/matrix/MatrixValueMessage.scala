package org.odfi.ioda.simulation.lib.matrix
/*
import java.io.File
import java.nio.ByteBuffer
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator
import org.openimaj.image.ImageUtilities
import org.openimaj.image.processing.resize.BicubicInterpolation
import com.innovationlab.board.model.MatrixValue
import org.odfi.ioda.data.types.DataMessage

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import scala.util.Random

class MatrixValueMessage extends DataMessage {

  var doubleValues: Option[Array[Array[Double]]] = None

  
  def initRandom(width:Int,height:Int,maxValue:Double) = {
    
    this.width = width
    this.height = height
    
    
   
    for (x <- 0 until this.width) {
   
      for (y <- 0 until this.height) {
        
        var p = this.pixels.add
        p.x = x 
        p.y = y
        p.value = Random.nextDouble()*maxValue
        
      }
    }
 
    
    
  }
  
  def toImage(f: File, scale: Int = 20) = {

    var img = new WritableImage(width, height)

    //-- reate Image
    //var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    pixels.foreach {
      pixel =>

        var hue = pixel.value.data
        var color = Color.hsb(hue, 1.0, 0.8);
        img.getPixelWriter.setColor(pixel.x, pixel.y, color)
   

    }

    var renderedImage = SwingFXUtils.fromFXImage(img, null);

    //var fimag =  

    // OIMG
    //----------------------------
    var interpolation = new BicubicInterpolation(width * scale, height * scale, scale.toFloat)
    var resized = ImageUtilities.createFImage(renderedImage)
    interpolation.processImage(resized)

    ImageUtilities.write(resized, new File("resized.png"))

    // Normal resize
    var resizedImgBCB = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
    var g2 = resizedImgBCB.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.drawImage(renderedImage, 0, 0, width * scale, height * scale, null);
    g2.dispose();
    ImageIO.write(
      resizedImgBCB,
      "png",
      new File("ResizedBCB.png"));

    var resizedImgNB = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
    g2 = resizedImgNB.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g2.drawImage(renderedImage, 0, 0, width * scale, height * scale, null);
    g2.dispose();
    ImageIO.write(
      resizedImgNB,
      "png",
      new File("ResizedNB.png"));

    var resizedImgBL = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
    g2 = resizedImgBL.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(renderedImage, 0, 0, width * scale, height * scale, null);
    g2.dispose();
    ImageIO.write(
      resizedImgBL,
      "png",
      new File("ResizedBL.png"));

    // Write

    ImageIO.write(
      renderedImage,
      "png",
      f);

  }

  override def clone = {

    var newValues = new MatrixValueMessage()
    newValues.virtualChannel = virtualChannel
    newValues.width = width
    newValues.height = height

    for (x <- 0 until this.width) {
      for (y <- 0 until this.height) {

        var pixel = newValues.pixels.add
        pixel.x = x
        pixel.y = y
        pixel.value = this.pixels.find { p => p.x.data == x && p.y.data == y }.get.value
        //array(x)(y) = this.pixels.find { p => p.x.data == x && p.y.data == y }.get.value.toDouble
      }
    }

    newValues
  }

  def getDoubleValue(x:Int,y:Int) = doubleValues.get(x)(y)
  
  def createDoubleValues = {

    var array = new Array[Array[Double]](this.width)
    for (x <- 0 until this.width) {
      array(x) = new Array[Double](this.height)
      for (y <- 0 until this.height) {
        array(x)(y) = this.pixels.find { p => p.x.data == x && p.y.data == y }.get.value.toDouble
      }
    }
    doubleValues = Some(array)

  }

  def interpolateBicubic = doubleValues match {
    case Some(values) =>

      var interpolator = new BicubicInterpolator()
      var interpolated = interpolator.interpolate((0 until width).map(_.toDouble).toArray, (0 until height).map(_.toDouble).toArray, doubleValues.get)

      var newValues = new MatrixValueMessage()
      newValues.width = width
      newValues.height = height

      for (x <- 0 until this.width) {
        for (y <- 0 until this.height) {

          var pixel = newValues.pixels.add
          pixel.x = x
          pixel.y = y
          pixel.value = interpolated.value(x.toDouble, y.toDouble).toInt
          //array(x)(y) = this.pixels.find { p => p.x.data == x && p.y.data == y }.get.value.toDouble
        }
      }

      //interpolated.value(x, y)

      newValues

    case None =>
      sys.error("Cannot interpolate if no double values where created")
  }

  def toBytes = {
    ByteBuffer.wrap(this.toString.getBytes)
  }

}*/
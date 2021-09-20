package org.odfi.ioda.ui.color

import javafx.scene.paint.Color
import net.mahdilamb.colormap.Colormaps

/**
 * https://github.com/mahdilamb/colormap
 */
class ColorMapper {

  // Threshold
  //-----------
  var minValue = 0.0
  var maxValue = 4096.0
  var thresholds = List[Double]()

  /**
   * Returns the value if no Threshold is defined, otherwise the next lowvalue if under Threhsold
   * @param v
   */
  def adjustForThreshold(v:Double)  = {

    thresholds.size match {
      case 0 => v
      case other =>
        (List(minValue) ::: thresholds ::: List(maxValue)).filter(_ < v).last
    }

  }

  // Opacity
  //------------
  var useOpacity = true
  var minOpacity = 0.1
  var maxOpacity = 1.0

  // Mappers
  //-----------
  //def selectedMap = "sequential.reds"
  var selectedMap = Colormaps.get("sequential.turbo")


  /**
   * Get Heat Color, use Viridis per default
   *
   */
  def mapHeat(v: Double) = {


    // Map
    //-------------
    val realV = adjustForThreshold(v)
    val valueRatioToMax = realV /  maxValue

    val color = selectedMap.get(valueRatioToMax)

    // Opacity
   // var opacity = if (valueRatioToMax>minOpacity) valueRatioToMax else minOpacity

    // Map
    val c = new Color(color.getRed,color.getGreen,color.getBlue,color.getAlpha)

    c


  }


}

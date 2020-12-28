package org.odfi.ioda.data.types

import scala.util.Random

class StringValueMessage extends ValueMessage[String] {

}


class IntValueMessage extends ValueMessage[Int] {

  def setRandomValue(min: Int, max: Int) = {
    val rand = new Random()
    this.value = Some(rand.between(min,max))

  }

}


class DoubleValueMessage extends ValueMessage[Double] {

}
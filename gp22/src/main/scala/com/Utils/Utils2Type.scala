package com.Utils

object Utils2Type {
  def toInt(str:String):Int={
    try {
      str.toInt
    }catch{
      case _=>0
    }
  }
  def toDouble(str:String):Double={
    try {
      str.toDouble
    }catch{
      case _=>0.0
    }
  }
}

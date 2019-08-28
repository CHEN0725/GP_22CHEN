package com.Tags

import com.Utils.Tag
import org.apache.spark.sql.Row

object TagCanal extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] ={
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]
    val adplatformproviderid = row.getAs[Int]("adplatformproviderid")
    list:+=("CN"+adplatformproviderid,1)
    list
  }
}

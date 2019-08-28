package com.Tags

import com.Utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row


object TagAd extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]
    val adtype=row.getAs[Int]("adspacetype")
    val adname = row.getAs[String]("adspacetypename")
    adtype match {
      case v if v>9=> list :+= ("LC"+v,1)
      case v if v<=9&&v>0=>list:+=("LC0"+v,1)
    }
    if (StringUtils.isNotBlank(adname)){
      list:+=("LN"+adname,1)
    }
    list
  }
}

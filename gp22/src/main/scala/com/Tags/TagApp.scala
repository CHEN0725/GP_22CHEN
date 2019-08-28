package com.Tags

import com.Utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row

object TagApp extends Tag{

  override def makeTags(args: Any*): List[(String, Int)] ={
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]
    val appInfo=args(1).asInstanceOf[Broadcast[Map[String,String]]]
    val appid=row.getAs[String]("appid")
    val appname=row.getAs[String]("appname")
    if (StringUtils.isNotBlank(appname)){
      list:+=("APP "+appname,1)
    }else if(StringUtils.isNoneBlank(appid)){
      list:+=("APP "+appInfo.value.getOrElse(appid,appid),1)
    }
    list
  }
}

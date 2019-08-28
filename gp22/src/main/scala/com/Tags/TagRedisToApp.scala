package com.Tags

import com.Utils.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row
import redis.clients.jedis.Jedis

object TagRedisToApp extends Tag{

  override def makeTags(args: Any*): List[(String, Int)] ={
    val jedis=new Jedis("192.128.25.132",6379)
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]

    val appid=row.getAs[String]("appid")
    val appname=row.getAs[String]("appname")
    if (StringUtils.isNotBlank(appname)){
      list:+=("APP "+appname,1)
    }else if(StringUtils.isNoneBlank(appid)){
      list:+=("APP "+jedis.get(appid),1)
    }
    list
  }
}

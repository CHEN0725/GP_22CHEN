package com.Tags

import ch.hsr.geohash.GeoHash
import com.Utils.{AmupUtils, RedisUtil, Tag, Utils2Type}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagBusiness extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]
    val long=row.getAs[String]("long")
    val lat=row.getAs[String]("lat")
    if(Utils2Type.toDouble(long)>=73.0&&
      Utils2Type.toDouble(long)<=135.0&&
      Utils2Type.toDouble(lat)>=3.0&&
      Utils2Type.toDouble(lat)<=54.0){
      val business=getBusiness(long.toDouble,lat.toDouble)
      if(StringUtils.isNotBlank(business)){
        val businessArr = business.split(",")
        businessArr.foreach(business=>list:+=(business,1))
      }
    }
  list
  }
//  def getBusiness(long:Double,lat:Double):String={
//    val geohash = GeoHash.geoHashStringWithCharacterPrecision(lat,long,8)
//    //先在redis数据库中查询是否有商圈信息
//    var business=redisQueryBusiness(geohash)
//    if (business==null || business.length==0){
//      business=AmupUtils.getBusinessFromAmap(long.toDouble,lat.toDouble)
//      redisInsertBusiness(geohash,business)
//    }
//    business
//  }
//  def redisQueryBusiness(geoHash: String):String={
//    val jedis=RedisUtil.getJedis()
//    val business = jedis.get(geoHash)
//    jedis.close()
//    business
//  }
//  def redisInsertBusiness(geoHash: String,business:String)={
//    val jedis=RedisUtil.getJedis()
//    jedis.set(geoHash,business)
//    jedis.close()
//
//  }
def getBusiness(long: Double, lat: Double): String = {

  //  转换GeoHash字符串
  val geohash: String = GeoHash
    .geoHashStringWithCharacterPrecision(lat, long, 8)

  //  去数据库查询
  var business: String = redis_queryBusiness(geohash)

  //  判断商圈是否为空
  if(business == null || business.length == 0){

    //  通过经纬度获取商圈
    val business: String = AmupUtils.getBusinessFromAmap(long.toDouble, lat.toDouble)

    //  如果调用高德地图解析商圈，那么需要将此次商圈存入redis
    redis_insertBusiness(geohash, business)

  }
  business
}

  /**
    *  获取商圈信息
    */

  def redis_queryBusiness(geohash:String):String={
    val jedis = RedisUtil.getJedis()
    val business = jedis.get(geohash)
    jedis.close()
    business
  }

  /**
    * 存储商圈到redis
    */

  def redis_insertBusiness(geoHash:String,business:String): Unit ={
    val jedis = RedisUtil.getJedis()
    jedis.set(geoHash,business)
    jedis.close()
  }
}

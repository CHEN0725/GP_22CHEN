package com.Utils

import com.alibaba.fastjson.{JSON, JSONObject}

import scala.collection.mutable.ListBuffer


/**
  * 高德解析工具类
  */
object AmupUtils {

  //获取高德地图商圈信息
  def getBusinessFromAmap(long:Double,lat:Double):String={
    val location=long+","+lat
    val urlStr="https://restapi.amap.com/v3/geocode/regeo?key=ac807ae142f784fb4ab664889fe9ffb2&location="+location
    //调用请求
    val jsonStr=HttpUtil.get(urlStr)

    //解析json串
    val jsonparse = JSON.parseObject(jsonStr)
    val status = jsonparse.getIntValue("status")
    if(status==0) return ""
    val regeocodeJson = jsonparse.getJSONObject("regeocode")
    if(regeocodeJson==null || regeocodeJson.keySet().isEmpty) return ""
    val addresscomponent = regeocodeJson.getJSONObject("addressComponent")
    if (addresscomponent==null || addresscomponent.keySet().isEmpty) return null
    val busArr = addresscomponent.getJSONArray("businessAreas")

    val list=ListBuffer[String]()
    for (item <- busArr.toArray()){
      if(item.isInstanceOf[JSONObject]){
        val json=item.asInstanceOf[JSONObject]
        list.append(json.getString("name"))
      }
    }
    list.mkString(",")
  }
}

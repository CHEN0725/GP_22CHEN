package com.Test

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object KaoshiDemo1 {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val sc=new SparkContext(conf)
    val lines=sc.textFile("dir/json.txt")
    //第一题
    val str=getBusinessCount(lines)
    val res=str.flatMap(x=>x.split(",")).map((_,1))
      .groupByKey().mapValues(_.toList.size)
        .filter(_._1!="[]")

    res.foreach(println)
    println("------------------------------------")
    val typeStr=getTypeCount(lines)
    val resTwo=typeStr.flatMap(x=>x.split(";"))
      .map(x=>(x,1)).groupByKey().mapValues(_.toList.size)
    resTwo.foreach(println)

  }
  def getBusinessCount(lines:RDD[String]):RDD[String]= {
    val busInfo = lines.map(line => {
      val jsonparse = JSON.parseObject(line)

      val status = jsonparse.getIntValue("status")
      //if (status == 0) return null
      val regeocodeJson = jsonparse.getJSONObject("regeocode")
      //if (regeocodeJson == null || regeocodeJson.keySet().isEmpty) return null
      val poisArr = regeocodeJson.getJSONArray("pois")

      val list = ListBuffer[String]()
      for (item <- poisArr.toArray()) {
        if (item.isInstanceOf[JSONObject]) {
          val json = item.asInstanceOf[JSONObject]
          val bus = json.getString("businessarea")
          list.append(bus)
        }
      }
      list.mkString(",")
    })
    busInfo
  }

  def getTypeCount(lines:RDD[String]):RDD[String]= {
    val typeInfo = lines.map(line => {
      val jsonparse = JSON.parseObject(line)

      val status = jsonparse.getIntValue("status")
      //if (status == 0) return null
      val regeocodeJson = jsonparse.getJSONObject("regeocode")
      //if (regeocodeJson == null || regeocodeJson.keySet().isEmpty) return null
      val poisArr = regeocodeJson.getJSONArray("pois")

      val list = ListBuffer[String]()
      for (item <- poisArr.toArray()) {
        if (item.isInstanceOf[JSONObject]) {
          val json = item.asInstanceOf[JSONObject]
          val typeNmae = json.getString("type")
          list.append(typeNmae)
        }
      }
      list.mkString(";")
    })
    typeInfo
  }



}

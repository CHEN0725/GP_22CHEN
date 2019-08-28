package com.Test

import com.Utils.AmupUtils
import org.apache.spark.{SparkConf, SparkContext}

object AmupTest {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
    val sc=new SparkContext(conf)
    val list=List("116.310003,39.991957")
    val rdd=sc.makeRDD(list)
    val bs = rdd.map(t => {
      val arr = t.split(",")
      AmupUtils.getBusinessFromAmap(arr(0).toDouble, arr(1).toDouble)
    })
    bs.foreach(println)
  }
}

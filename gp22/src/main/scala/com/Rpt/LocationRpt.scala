package com.Rpt

import com.Utils.RptUtis
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * 地域分布指标
  */
object LocationRpt {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val sc=new SparkContext(conf)
    val spark=SparkSession.builder().config(conf)
      .getOrCreate()
    val lines=spark.read.parquet("F:/outputPath")
    import spark.implicits._
    val res = lines.rdd.map(row => {
      val requestmode = row.getAs[Int]("requestmode")
      val processnode = row.getAs[Int]("processnode")
      val iseffective = row.getAs[Int]("iseffective")
      val isbilling = row.getAs[Int]("isbilling")
      val isbid = row.getAs[Int]("isbid")
      val iswin = row.getAs[Int]("iswin")
      val adorderid = row.getAs[Int]("adorderid")
      val WinPrice = row.getAs[Double]("winprice")
      val adpayment = row.getAs[Double]("adpayment")
      // key 值  是地域的省市
      val pro = row.getAs[String]("provincename")
      val city = row.getAs[String]("cityname")
      // 创建三个对应的方法处理九个指标
      val reqList = RptUtis.request(requestmode, processnode)
      val clickList = RptUtis.click(requestmode, iseffective)
      val adList = RptUtis.Ad(iseffective, isbilling, isbid, iswin, adorderid, WinPrice, adpayment)
      ((pro, city), reqList ++ clickList ++ adList)
    }).reduceByKey((list1, list2) => {
      list1.zip(list2).map(x => x._1 + x._2)
    })
    //println(res.collect.toList)
  }
}

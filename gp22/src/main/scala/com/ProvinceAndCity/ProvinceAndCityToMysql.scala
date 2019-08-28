package com.ProvinceAndCity

import java.util.Properties

import com.typesafe.config.ConfigFactory

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object ProvinceAndCityToMysql {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")

    val sc=new SparkContext(conf)
    val spark=SparkSession.builder().config(conf)
      .getOrCreate()
    val lines=spark.read.parquet("F:/outputPath")
    import spark.implicits._

    val tups = lines.rdd.map(line => {
      val province = line(24).toString
      val city = line(25).toString
      ((province, city), 1)
    })
    val res = tups.groupByKey().mapValues(x => x.reduce(_ + _))
      .map(x => {
        (x._2, x._1._1, x._1._2)
      })
    //println(res.collect.toList)
    //df.write.json("F:/outputJson")
    val df = res.toDF("ct","provincename","cityname")
    val load=ConfigFactory.load()
    val prop=new Properties()
    prop.setProperty("user",load.getString("jdbc.user"))
    prop.setProperty("password",load.getString("jdbc.password"))
    df.write.jdbc(load.getString("jdbc.url"),load.getString("jdbc.TableName"),prop)
//    prop.put("user","root")
//    prop.put("password","root")
//    val url="jdbc:mysql://localhost:3306/11?characterEncoding=utf8"
    //df.write.jdbc(url,"provinceCount",prop)
    //df.write.partitionBy("provincename","cityname").save("hdfs://hadoop1:8020/ProvinceAndCity")
    sc.stop()
    spark.stop()
  }
}

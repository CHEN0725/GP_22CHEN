package com.Test

import com.Tags.TagBusiness
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object BusinessTest {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
    //val sc=new SparkContext(conf)
    val spark=SparkSession.builder().config(conf).getOrCreate()
    val df = spark.read.parquet("F:/outputPath")
    df.rdd.map(row => {
      val business = TagBusiness.makeTags(row)
      business
    }).foreach(println)

  }

}

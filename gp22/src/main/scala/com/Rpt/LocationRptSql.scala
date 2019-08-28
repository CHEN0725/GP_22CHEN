package com.Rpt

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object LocationRptSql {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val spark=SparkSession.builder().config(conf)
      .getOrCreate()
    val lines=spark.read.parquet("F:/outputPath")
    lines.createOrReplaceTempView("t_locationRpt")
    val res=spark.sql("select " +
      "provincename,cityname," +
      "sum(case when requestmode=1 and processnode>=1 then 1 else 0 end) primitiveAdQuest," +
      "sum(case when requestmode=1 and processnode>=2 then 1 else 0 end) adAvailQuest," +
      "sum(case when requestmode=1 and processnode=3 then 1 else 0 end) adQuest," +
      "sum(case when iseffective=1 and isbilling=1 and isbid=1 then 1 else 0 end) partakeBidding," +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 and adorderid!=0 then 1 else 0 end) biddingSucess," +
      "sum(case when requestmode=2 and iseffective=1 then 1 else 0 end) show," +
      "sum(case when requestmode=3 and iseffective=1 then 1 else 0 end) click," +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 then winprice/1000 else 0 end) DSPConsume," +
      "sum(case when iseffective=1 and isbilling=1 and iswin=1 then adpayment/1000 else 0 end) DSPPrime " +
      "from t_locationRpt group by provincename,cityname")
    res.show()
    spark.stop()
  }
}

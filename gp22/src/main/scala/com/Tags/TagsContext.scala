package com.Tags

import com.Utils.TagUtils
import org.apache.spark.sql.SparkSession

/**
  * 上下文标签
  */
object TagsContext {
  def main(args: Array[String]): Unit = {
    if(args.length!=3){
      println("目录不匹配，退出程序")
      sys.exit()
    }
    val Array(inputPath,dictPath,stopwordPath)=args
    val spark=SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .getOrCreate()
    val df=spark.read.parquet(inputPath)
    //app字典文件处理
    val appInfo=spark.sparkContext.textFile(dictPath).map(_.split("\\s",-1)).filter(_.length>=5)
      .map(x=>(x(4),x(1))).collectAsMap()
    val broadApp = spark.sparkContext.broadcast(appInfo)
    //
    val stopWord=spark.sparkContext.textFile(stopwordPath).map((_,0)).collectAsMap()
    val broadStopWord=spark.sparkContext.broadcast(stopWord)

    //过滤符合id的数据
    val tup=df.filter(TagUtils.OneUserId)
      .rdd.map(row=>{
      //取出id
      val userid = TagUtils.getOneUserId(row)
      //广告标签
      val adList=TagAd.makeTags(row)
      //app标签
      val appList = TagApp.makeTags(row,broadApp)
      //渠道标签
      val canalList = TagCanal.makeTags(row)
      //设备标签
      val equipmentList = TagEquipment.makeTags(row)
      //关键字标签
      val keywordList = TagKeyWord.makeTags(row,broadStopWord)
      //地域标签
      val locationList = TagLocation.makeTags(row)
      //商圈标签
      val businessList=TagBusiness.makeTags(row)
      (userid,adList++appList++canalList++equipmentList++keywordList++locationList)
    }).reduceByKey((list1,list2)=>{
      (list1:::list2).groupBy(_._1)
        .mapValues(_.foldLeft[Int](0)(_+_._2)).toList
    }).foreach(println)



  }
}

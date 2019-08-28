package com.Tags

import com.Utils.TagUtilsGraphx
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 上下文标签
  */
object TagsContextGraphx_ToHbase {
  def main(args: Array[String]): Unit = {
    if(args.length!=4){
      println("目录不匹配，退出程序")
      sys.exit()
    }
    val Array(inputPath,dictPath,stopwordPath,days)=args
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val spark=SparkSession.builder()
      .config(conf)
      .getOrCreate()
    //加载配置文件
    val load = ConfigFactory.load()
    val hbaseTableName = load.getString("hbase.TableName")

    //创建Hadoop任务
    val configuration = spark.sparkContext.hadoopConfiguration
    configuration.set("hbase.zookeeper.quorum",load.getString("hbase.host"))
    //创建hbaseConnection
    val hbconn = ConnectionFactory.createConnection(configuration)
    val hbadmin = hbconn.getAdmin
    //判断表是否可用
    if(!hbadmin.tableExists(TableName.valueOf(hbaseTableName))){
      //创建表操作
      val tableDescriptor = new HTableDescriptor(TableName.valueOf(hbaseTableName))
      val descriptor = new HColumnDescriptor("tags")
      tableDescriptor.addFamily(descriptor)
      hbadmin.createTable(tableDescriptor)
      hbadmin.close()
      hbconn.close()
    }

    //创建JobConf
    val jobConf = new JobConf(configuration)
    //指定输出类型和类
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE,hbaseTableName)

    val df=spark.read.parquet(inputPath)
    //app字典文件处理
    val appInfo=spark.sparkContext.textFile(dictPath).map(_.split("\\s",-1)).filter(_.length>=5)
      .map(x=>(x(4),x(1))).collectAsMap()
    val broadApp = spark.sparkContext.broadcast(appInfo)
    //
    val stopWord=spark.sparkContext.textFile(stopwordPath).map((_,0)).collectAsMap()
    val broadStopWord=spark.sparkContext.broadcast(stopWord)

    //过滤符合id的数据
    val tup=df.filter(TagUtilsGraphx.OneUserId)
      .rdd.map(row=> {
      val userList = TagUtilsGraphx.getAllUserId(row)
      (userList,row)
    })
//      //取出id
//      val userid = TagUtils.getOneUserId(row)
//      //广告标签
//      val adList=TagAd.makeTags(row)
//      //app标签
//      val appList = TagApp.makeTags(row,broadApp)
//      //渠道标签
//      val canalList = TagCanal.makeTags(row)
//      //设备标签
//      val equipmentList = TagEquipment.makeTags(row)
//      //关键字标签
//      val keywordList = TagKeyWord.makeTags(row,broadStopWord)
//      //地域标签
//      val locationList = TagLocation.makeTags(row)
//      //商圈标签
//      val businessList = TagBusiness.makeTags(row)
//      (userid,adList++appList++canalList++equipmentList++keywordList++locationList++businessList)
//    }).reduceByKey((list1,list2)=>{
//      (list1:::list2).groupBy(_._1)
//        .mapValues(_.foldLeft[Int](0)(_+_._2)).toList
//    }).map{
//      case (userid,userTag)=>{
//        val put=new Put(Bytes.toBytes(userid))
//        //处理标签
//        val tags=userTag.map(t=>t._1+","+t._2).mkString(",")
//        put.addImmutable(Bytes.toBytes("tags")
//          ,Bytes.toBytes(s"$days"),Bytes.toBytes(tags))
//        (new ImmutableBytesWritable(),put)
//      }
//    }.saveAsHadoopDataset(jobConf)
    //构建点集合
    val vertiesRDD = tup.flatMap(tp => {
      val row = tp._2
      //广告标签
      val adList = TagAd.makeTags(row)
      //app标签
      val appList = TagApp.makeTags(row, broadApp)
      //渠道标签
      val canalList = TagCanal.makeTags(row)
      //设备标签
      val equipmentList = TagEquipment.makeTags(row)
      //关键字标签
      val keywordList = TagKeyWord.makeTags(row, broadStopWord)
      //地域标签
      val locationList = TagLocation.makeTags(row)
      //商圈标签
      val businessList = TagBusiness.makeTags(row)
      val AllTag = adList ++ appList ++ canalList ++ equipmentList ++ keywordList ++ locationList ++ businessList

      //保证其中一个点携带所有标签，同时也保留所有userid
      val VD = tp._1.map((_, 0)) ++ AllTag
      tp._1.map(uid => {
        if (tp._1.head.equals(uid)) {
          (uid.hashCode.toLong, VD)
        } else {
          (uid.hashCode.toLong, List.empty)
        }
      })
    })

    //构建边的集合
    val edge: RDD[Edge[Int]] = tup.flatMap(tp => {
      tp._1.map(uid => Edge(tp._1.head.hashCode, uid.hashCode, 0))
    })
    //构建图
    val graph=Graph(vertiesRDD,edge)
    //取出最大顶点
    val vertices = graph.connectedComponents().vertices
    vertices.join(vertiesRDD).map{
      case (uid,(conId,tagsAll))=>(conId,tagsAll)
    }.reduceByKey((list1,list2)=>{
      list1++list2.groupBy(_._1).mapValues(_.map(_._2).sum).toList
    }).map{
      case (userid,userTag)=>{
        val put=new Put(Bytes.toBytes(userid))
        //处理标签
        val tags=userTag.map(t=>t._1+","+t._2).mkString(",")
        put.addImmutable(Bytes.toBytes("tags")
          ,Bytes.toBytes(s"$days"),Bytes.toBytes(tags))
        (new ImmutableBytesWritable(),put)
      }
    }.saveAsHadoopDataset(jobConf)
    spark.stop()

  }
}

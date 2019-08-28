package com.Utils

import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.Row


/**
  * 标签工具类
  */
object TagUtilsGraphx {

  val OneUserId=
    """
      | imei !='' or mac !='' or openudid !='' or androidid !='' or idfa !='' or
      | imeimd5 !='' or macmd5 !='' or openudidmd5 !='' or androididmd5 !='' or idfamd5 !='' or
      | imeisha1 !='' or macsha1 !='' or openudidsha1 !='' or androididsha1 !='' or idfasha1 !=''
    """.stripMargin
    //用户唯一不为空id
    def getAllUserId(row:Row):List[String]={
      var list=List[String]()
      row match{
        case v if StringUtils.isNotBlank(v.getAs[String]("imei"))=>list:+=(v.getAs[String]("imei"))
        case v if StringUtils.isNotBlank(v.getAs[String]("mac"))=>list:+=(v.getAs[String]("mac"))
        case v if StringUtils.isNotBlank(v.getAs[String]("openudid"))=>list:+=(v.getAs[String]("openudid"))
        case v if StringUtils.isNotBlank(v.getAs[String]("androidid"))=>list:+=(v.getAs[String]("androidid"))
        case v if StringUtils.isNotBlank(v.getAs[String]("idfa"))=>list:+=(v.getAs[String]("idfa"))

        case v if StringUtils.isNotBlank(v.getAs[String]("imeimd5"))=>list:+=(v.getAs[String]("imeimd5"))
        case v if StringUtils.isNotBlank(v.getAs[String]("macmd5"))=>list:+=(v.getAs[String]("macmd5"))
        case v if StringUtils.isNotBlank(v.getAs[String]("openudidmd5"))=>list:+=(v.getAs[String]("openudidmd5"))
        case v if StringUtils.isNotBlank(v.getAs[String]("androididmd5"))=>list:+=(v.getAs[String]("androididmd5"))
        case v if StringUtils.isNotBlank(v.getAs[String]("idfamd5"))=>list:+=(v.getAs[String]("idfamd5"))

        case v if StringUtils.isNotBlank(v.getAs[String]("imeisha1"))=>list:+=(v.getAs[String]("imeisha1"))
        case v if StringUtils.isNotBlank(v.getAs[String]("macsha1"))=>list:+=(v.getAs[String]("macsha1"))
        case v if StringUtils.isNotBlank(v.getAs[String]("openudidsha1"))=>list:+=(v.getAs[String]("openudidsha1"))
        case v if StringUtils.isNotBlank(v.getAs[String]("androididsha1"))=>list:+=(v.getAs[String]("androididsha1"))
        case v if StringUtils.isNotBlank(v.getAs[String]("idfasha1"))=>list:+=(v.getAs[String]("idfasha1"))
      }
      list
    }
}

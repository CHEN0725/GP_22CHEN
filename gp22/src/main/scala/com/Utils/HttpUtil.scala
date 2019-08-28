package com.Utils

import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils


/**
  * http请求协议
  */
object HttpUtil {
  //get请求
  def get(url:String):String={
    val client=HttpClients.createDefault()
    val get=new HttpGet(url)
    val response: CloseableHttpResponse = client.execute(get)
    EntityUtils.toString(response.getEntity,"utf-8")

  }
}

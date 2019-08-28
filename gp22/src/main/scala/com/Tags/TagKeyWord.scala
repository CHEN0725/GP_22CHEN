package com.Tags

import com.Utils.Tag
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Row

object TagKeyWord extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list=List[(String,Int)]()
    val row=args(0).asInstanceOf[Row]
    val stopWord=args(1).asInstanceOf[Broadcast[Map[String,Int]]]
    val keyword = row.getAs[String]("keywords")
    keyword.split("\\|").filter(word=>{
      word.length>=3 && word.length<=8 && !stopWord.value.contains(word)
    }).foreach(x=>list:+=("K"+x,1))
    list
  }
}

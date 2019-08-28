//package com.Utils
///**
//  * JDBC 连接池
//  * 配置信息从 config/db.properties 中读取
//  * @author
//  */
//object JDBCUtils {
//    private val propertiesUtil = new PropertiesUtil("config/db.properties")
//
//    // 数据库驱动类
//    private val driverClass: String = propertiesUtil.readPropertyByKey("driverClass")
//    // 数据库连接地址
//    private val url: String = propertiesUtil.readPropertyByKey("url")
//    // 数据库连接用户名
//    private val username: String = propertiesUtil.readPropertyByKey("userName")
//    // 数据库连接密码
//    private val password: String = propertiesUtil.readPropertyByKey("password")
//
//    // 加载数据库驱动
//    Class.forName(driverClass)
//
//    // 连接池大小
//    val poolSize: Int = propertiesUtil.readPropertyByKey("poolSize").toInt
//
//    // 连接池 - 同步队列
//    private val pool: BlockingQueue[Connection]  = new LinkedBlockingQueue[Connection]()
//
//    /**
//      * 初始化连接池
//      */
//    for(i <- 1 to poolSize) {
//      DBUtil.pool.put(DriverManager.getConnection(url, username, password))
//    }
//
//    /**
//      * 从连接池中获取一个Connection
//      * @return
//      */
//    private def getConnection: Connection = {
//      pool.take()
//    }
//
//
//    /**
//      * 向连接池归还一个Connection
//      * @param conn
//      */
//    private def returnConnection(conn: Connection): Unit = {
//      DBUtil.pool.put(conn)
//    }
//
//    /**
//      * 启动守护线程释放资源
//      */
//    def releaseResource() = {
//      val thread = new Thread(new CloseRunnable)
//      thread.setDaemon(true)
//      thread.start()
//    }
//
//    /**
//      * 关闭连接池连接资源类
//      */
//    class CloseRunnable extends Runnable{
//      override def run(): Unit = {
//        while(DBUtil.pool.size > 0) {
//          try {
//            //          println(s"当前连接池大小: ${DBUtil.pool.size}")
//            DBUtil.pool.take().close()
//          } catch {
//            case e: Exception => e.printStackTrace()
//          }
//        }
//      }
//    }
//
//}

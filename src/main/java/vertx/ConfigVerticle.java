package vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import vertx.Utils.VertxHttpUtils;

import java.util.ArrayList;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class ConfigVerticle extends AbstractVerticle {

  // 连接配置
  MySQLConnectOptions connectOptions ;
  //pool options   连接池配置
  PoolOptions poolOptions   =new PoolOptions()
    .setMaxSize(10);
  //Create  the  mysql  connection pool
  MySQLPool client  ;
  Router router;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever retriver =  ConfigRetriever.create(vertx);
     retriver.getConfig(ar -> {
       if(ar.succeeded()){
          JsonObject  config  =  ar.result();
          connectOptions  = new MySQLConnectOptions()
           .setPort(Integer.parseInt(config.getString("port")))
           .setHost(config.getString("host"))
           .setDatabase(config.getString("database"))
           .setUser(config.getString("user"))
           .setPassword(config.getString("password"));

         router  = Router.router(vertx);
         client= MySQLPool.pool(vertx,connectOptions,poolOptions);
         // select
         router.route("/test/list").handler(req -> {
           client.getConnection(ac -> {
             if(ac.succeeded()){
               System.out.println("Connected");
               // Obtain  our connection
               SqlConnection conn   = ac.result();
               conn.query("select id,name,password from sys_user ")
                 .execute(rs->{
                   var list =  new ArrayList<JsonObject>();
                   rs.result().forEach(item->{
                     var json  = new JsonObject();
                     json.put("id",item.getValue("id"));
                     json.put("name",item.getValue("name"));
                     json.put("password",item.getValue("password"));
                     list.add(json);
                   });
                   VertxHttpUtils.WriterJson(req.request(),list);
                   conn.close();
                 });
             }
           });
         });
         // 传参数
         router.route("/test/paramlist").handler(req -> {
           client.getConnection(ac -> {
             if(ac.succeeded()){
               System.out.println("Connected");
               // Obtain  our connection
               Integer  id  =  Integer.parseInt(req.request().getParam("id"));
               String name  = req.request().getParam("name");
               SqlConnection conn   = ac.result();
               conn.preparedQuery("select id,name,password from sys_user  where id = ? and name = ?")
                 .execute(Tuple.of(id,name), rs->{
                   var list =  new ArrayList<JsonObject>();
                   rs.result().forEach(item->{
                     var json  = new JsonObject();
                     json.put("id",item.getValue("id"));
                     json.put("name",item.getValue("name"));
                     json.put("password",item.getValue("password"));
                     list.add(json);
                   });
                   req.response().putHeader("connect-type","application/json").putHeader("charset","utf-8").end(list.toString());
                   conn.close();
                 });
             }
           });
         });

         vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
           if (http.succeeded()) {
             startPromise.complete();
             System.out.println("HTTP server started on port 8888");
           } else {
             startPromise.fail(http.cause());
           }
         });

       }else {
         System.out.println("出错了");
       }
     });
  }
}

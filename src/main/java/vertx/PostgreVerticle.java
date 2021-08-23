package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class PostgreVerticle extends AbstractVerticle {
  // 连接配置
  PgConnectOptions connectOptions  = new PgConnectOptions()
    .setPort(5432)
    .setHost("159.75.23.131")
    .setDatabase("test_db")
    .setUser("root")
    .setPassword("123456");
  //pool options   连接池配置
  PoolOptions poolOptions   =new PoolOptions()
    .setMaxSize(10);
  //Create  the  mysql  connection pool
  PgPool client;
  Router router;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router  = Router.router(vertx);
    client= PgPool.pool(vertx,connectOptions,poolOptions);
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
              req.response().putHeader("connect-type","application/json").putHeader("charset","utf-8").end(list.toString());
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
          //Postgre  传参和mysql  不同mysql是?  pg 是$加参数顺序  $1 $2 $3 $4
          conn.preparedQuery("select id,name,password from  sys_user where id = $1 and name =$2")
            .execute(Tuple.of(id,name),rs->{
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
  }
}

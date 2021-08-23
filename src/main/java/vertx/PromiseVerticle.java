package vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

import java.util.ArrayList;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class PromiseVerticle extends AbstractVerticle {

  // 连接配置
  MySQLConnectOptions connectOptions;
  //pool options   连接池配置
  PoolOptions poolOptions = new PoolOptions()
    .setMaxSize(10);
  //Create  the  mysql  connection pool
  MySQLPool client;
  Router router;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever retriver = ConfigRetriever.create(vertx);
    retriver.getConfig(ar -> {
      if (ar.succeeded()) {
        JsonObject config = ar.result();
        connectOptions = new MySQLConnectOptions()
          .setPort(Integer.parseInt(config.getString("port")))
          .setHost(config.getString("host"))
          .setDatabase(config.getString("database"))
          .setUser(config.getString("user"))
          .setPassword(config.getString("password"));

        router = Router.router(vertx);
        client = MySQLPool.pool(vertx, connectOptions, poolOptions);
        //select
        router.route("/test/list").handler(req -> {
        this.getConn()
          .compose(con-> this.getRows(con,req.request().getParam("id"),req.request().getParam("name"))
        ).onSuccess(rows -> {
          var list = new ArrayList<JsonObject>();
          rows.forEach(item -> {
            var json = new JsonObject();
            json.put("id", item.getValue("id"));
            json.put("name", item.getValue("name"));
            json.put("password", item.getValue("password"));
            list.add(json);
          });
          req.response().putHeader("connect-type", "application/json").putHeader("charset", "utf-8").end(list.toString());
        })
         .onFailure(throwable -> {
           req.response().putHeader("content","application/json").end(throwable.toString()); });
        });
        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8888");
          } else {
            startPromise.fail(http.cause());
          }
        });

      } else {
        System.out.println("出错了");
      }
    });
  }

  //第一步获取数据库连接
  private Future<SqlConnection> getConn() {
    Promise<SqlConnection> promise = Promise.promise();
    client.getConnection(ac -> {
      if (ac.succeeded()) {
        System.out.println("Connected");
        // Obtain  our connection
        SqlConnection conn = ac.result();

        promise.complete(conn);
      } else {
        promise.fail(ac.cause());
      }
    });
    return promise.future(); // promis生成future返回
  }

  private Future<RowSet<Row>> getRows(SqlConnection conn,String id,String name ){
      Promise<RowSet<Row>> promise  = Promise.promise();
      conn.query("select id,name,password from sys_user ")
      .execute(rs -> {
        if(rs.succeeded()){
          promise.complete(rs.result());
          conn.close();
        } else {
          conn.close();
          promise.fail(rs.cause());
        }
    }
      );
      return  promise.future();
  }

}

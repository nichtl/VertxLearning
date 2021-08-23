package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class JsonVerticle extends AbstractVerticle {
// vertx 中 自定义的 JsonObject和JsonArray 用于代替mvc中序列化框架
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "application/json")
        .end(new JsonObject().put("name","zhangsan").toString());
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}

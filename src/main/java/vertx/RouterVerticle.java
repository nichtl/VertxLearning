package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class RouterVerticle extends AbstractVerticle {
  Router  router;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //  vertx  AbstractVerticle 中的成员变量
    router  = Router.router(vertx);
    // 不区分 请求类型
    router.route("/").handler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    });
    // 只接受get 请求
    router.get("/").handler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    });
    // 只接受post 请求
    router.post("/").handler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
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

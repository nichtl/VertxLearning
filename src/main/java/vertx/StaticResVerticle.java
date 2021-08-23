package vertx;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class StaticResVerticle extends AbstractVerticle {
  Router router;
  ThymeleafTemplateEngine templateEngine;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final InternalLogger logger  = Log4JLoggerFactory.getInstance(StaticResVerticle.class);
    //  vertx  AbstractVerticle 中的成员变量
    router  = Router.router(vertx);
    // 初始化模板引擎
    templateEngine  = ThymeleafTemplateEngine.create(vertx);
   //整合静态资源
      //自定义webrobot 下资源访问路径
    //router.route("/static/*/").handler(StaticHandler.create());
    router.route("/*").handler(StaticHandler.create());
    // 不区分 请求类型
    router.route("/").handler(req -> {
      logger.error("出错了");
      JsonObject data = new JsonObject();
      data.put("name","Hello,World");
      templateEngine.render(data,"templates/index.html",bufferAsyncResult -> {
        if(bufferAsyncResult.succeeded()){
          req.response()
            .putHeader("content-type", "text/html")
            .end(bufferAsyncResult.result());
        }else {
          req.response()
            .putHeader("content-type", "text/plain")
            .end("Thymeleaf编译出错");
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

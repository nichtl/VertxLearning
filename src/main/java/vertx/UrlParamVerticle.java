package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/1
 * @Link
 */
public class UrlParamVerticle extends AbstractVerticle {
  Router router;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //  vertx  AbstractVerticle 中的成员变量
    router  = Router.router(vertx);
    router.route().handler(BodyHandler.create());//  获取body参数需要声明 body处理器
    // 不区分 请求类型
    router.route("/").handler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    });
    // 只接受get 请求  获取get参数
    // test?page=1&age=10&pagenum=10
    router.get("/test").handler(req -> {
      var page  = req.request().getParam("page");
      String pagenum = req.request().getParam("pagenum");
      var age = req.request().getParam("age");

      req.response()
        .putHeader("content-type", "text/plain")
        .end("page="+page+"pagenum="+pagenum+"age="+age);
    });

    //rest参数获取   // test/1/10/10
    // 纯粹以 / 分割  这个需要我们在后台url匹配中使用:name的格式指定
    router.route("/test/:page/:pagenum/:age").handler(req -> {
      var page  = req.request().getParam("page");
      var pagenum  = req.request().getParam("pagenum");
      var age = req.request().getParam("age");

      req.response()
        .putHeader("content-type", "text/plain")
        .end("page="+page+"pagenum="+pagenum+"age="+age);
    });

    //只接受post 请求   body 参数获取
    //获取body参数需要在router初始化后添加这句
    //获取 form-data content-type: application/ x-www-form-urlencoded // form表单格式数据
    router.route("/test/form").handler(req -> {
      var page  = req.request().getFormAttribute("page");
      String pagenum = req.request().getFormAttribute("pagenum");
      var age = req.request().getFormAttribute("age");

      req.response()
        .putHeader("content-type", "text/plain")
        .end("formpage="+page+"pagenum="+pagenum+"age="+age);
    });
    //获取Json格式数据
    //获取 form-data content-type: application/ json // json格式数据
    router.route("/test/json").handler(req -> {
      var page = req.getBodyAsJson();
      String  pagenum  = page.getValue("pagenum").toString();
      req.response()
        .putHeader("content-type", "text/plain")
        .end("jsonpage="+page.toString());
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

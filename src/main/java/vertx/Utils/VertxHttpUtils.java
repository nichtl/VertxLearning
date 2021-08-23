package vertx.Utils;

import com.google.gson.Gson;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.impl.Http2ServerRequestImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.buffer.Buffer;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;

/**
 * @Author Nicht
 * @Description
 * @Time 2021/7/27
 * @Link
 */
public class VertxHttpUtils {
  /**
   * @param request
   * @param text 字符文本
   */
  public static  void WriterText(HttpServerRequest request,String text) {
     request.response().
       putHeader(HeaderEnum.Content_TEXT.key,HeaderEnum.Content_TEXT.value).
       putHeader(HeaderEnum.Charset_UTF8.key,HeaderEnum.Charset_UTF8.value).
       end(text);
    }

  /**
   * @param request
   * @param object 对象
   */
  public static  void WriterJson(HttpServerRequest request,Object object) {
    request.response().
      putHeader(HeaderEnum.Content_JSON.key,HeaderEnum.Content_JSON.value).
      putHeader(HeaderEnum.Charset_UTF8.key,HeaderEnum.Charset_UTF8.value).
      end(new Gson().toJson(object));
  }

  /**
   *
   * @param request
   * @param buffer  html模板编译后的缓存 buffer
   */
  public static  void WriterHtml(HttpServerRequest request, Buffer buffer) {
    request.response().
      putHeader(HeaderEnum.Content_HTML.key,HeaderEnum.Content_HTML.value).
      putHeader(HeaderEnum.Charset_UTF8.key,HeaderEnum.Charset_UTF8.value).
      end(buffer);
  }







  private   enum  HeaderEnum{
    /** 文本*/
    Content_TEXT("content-type","text/plain"),
    /** JSON*/
    Content_JSON("content-type","application/json"),
    /** HTML*/
    Content_HTML("content-type","text/html"),
    /**
     * UTF-8
     */
    Charset_UTF8("charset", "UTF-8"),
    ;
    private String key;
    private String value;
    HeaderEnum(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
}

package data;

import annotation.RequestMapping.RequestMethod;

import java.util.Arrays;

import static annotation.RequestMapping.RequestMethod.*;

public enum Uri {

    INDEX(GET, "/index.html", "webapp/index.html", "text/html"),
    STYLES(GET,"/css/styles.css", "webapp/css/styles.css", "text/css"),
    BOOTSTRAP_CSS(GET,"/css/bootstrap.min.css", "webapp/css/bootstrap.min.css", "text/css"),
    BOOTSTRAP_JS(GET,"/js/bootstrap.min.js", "webapp/js/bootstrap.min.js", "text/javascript"),
    JQUERY(GET,"/js/jquery-2.2.0.min.js", "webapp/js/jquery-2.2.0.min.js", "text/javascript"),
    SCRIPT(GET,"/js/scripts.js", "webapp/js/scripts.js", "text/javascript"),
    FAVICON(GET,"/favicon.ico", "webapp/favicon.ico", "image/x-icon"),
    FORM(GET,"/user/form.html","webapp/user/form.html", "text/html"),

    JOIN(GET,"/user/create", null, "text/html"),
    DEFAULT(GET,"/", null, "text/html"),
    ;

    RequestMethod method;
    String uri;
    String path;
    String contentType;

    Uri(RequestMethod method, String uri, String path, String contentType) {
        this.method = method;
        this.uri = uri;
        this.path = path;
        this.contentType = contentType;
    }

    public String getUri(){
        return this.uri;
    }

    public RequestMethod getMethod(){
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getContentType(){
        return this.contentType;
    }

    public static Uri findResponseInfo(String method, String uri){
        return Arrays.stream(values())
                .filter(value -> value.uri.contains(uri)
                        && value.getMethod() == findMethod(method))
                .findFirst().orElse(DEFAULT);
    }

}

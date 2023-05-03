package data;

import java.util.Arrays;

public enum Uri {

    INDEX("/index.html", "webapp/index.html", "text/html"),
    STYLES("/css/styles.css", "webapp/css/styles.css", "text/css"),
    BOOTSTRAP_CSS("/css/bootstrap.min.css", "webapp/css/bootstrap.min.css", "text/css"),
    BOOTSTRAP_JS("/js/bootstrap.min.js", "webapp/js/bootstrap.min.js", "text/javascript"),
    JQUERY("/js/jquery-2.2.0.min.js", "webapp/js/jquery-2.2.0.min.js", "text/javascript"),
    SCRIPT("/js/scripts.js", "webapp/js/scripts.js", "text/javascript"),
    FAVICON("/favicon.ico", "webapp/favicon.ico", "image/x-icon"),
    FORM("/user/form.html","webapp/user/form.html", "text/html"),
    DEFAULT("/", null, "text/html")
    ;

    String uri;
    String path;
    String contentType;

    Uri(String uri, String path, String contentType) {
        this.uri = uri;
        this.path = path;
        this.contentType = contentType;
    }

    public String getUri(){
        return this.uri;
    }

    public String getPath() {
        return this.path;
    }

    public String getContentType(){
        return this.contentType;
    }

    public static Uri findResponseInfo(String uri){
        return Arrays.stream(values())
                .filter(value -> value.uri.contains(uri))
                .findFirst().orElse(DEFAULT);
    }

}

package data;

import util.HttpRequestUtils;
import util.IOUtils;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static webserver.RequestHandler.*;

public class HttpRequest {

    private String method;
    private String url;
    private int contentLength;
    private boolean logined;
    private Map<String, String> params = new HashMap<>();


    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public int getContentLength() {
        return contentLength;
    }

    public boolean isLogined() {
        return logined;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpRequest(BufferedReader br){
        try{
            String line = br.readLine();
            String[] tokens = line.split(" ");
            method = tokens[0];

            while (!line.equals("")) {
                log.debug("header : {}", line);
                line = br.readLine();

                if (line.contains("Content-Length"))
                    contentLength = RequestHandler.getContentLength(line);

                if (line.contains("Cookie"))
                    logined = isLogin(line);
            }

            url = tokens[1];
            String body = IOUtils.readData(br, contentLength);
            params = HttpRequestUtils.parseQueryString(body);


        } catch (IOException e) {
            log.error(e.getMessage());
        }


    }
}

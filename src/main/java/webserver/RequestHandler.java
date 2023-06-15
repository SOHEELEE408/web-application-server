package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import config.RequestMapping;
import controller.Controller;
import data.HttpRequest;
import data.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;


public class RequestHandler extends Thread {
    public static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            Controller controller = RequestMapping.getController(request.getPath());

            if(controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else
               controller.service(request, response);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path){
        if(path.equals("/"))
            return "/index.html";

        return path;
    }

    private String getSessionId(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);

        return cookies.get("JESSIONID");
    }
}

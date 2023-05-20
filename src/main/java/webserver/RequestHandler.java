package webserver;

import java.io.*;
import java.net.Socket;

import config.WebConfiguration;
import data.HttpRequest;
import data.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

            if(request.getPath().contains("."))
                WebConfiguration.controller().get("static").service(request, response);
            else
                WebConfiguration.controller().get(request.getPath()).service(request, response);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

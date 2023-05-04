package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import data.Uri;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static data.Uri.JOIN;
import static data.Uri.findResponseInfo;
import static util.HttpRequestUtils.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            String[] headerInfo = br.readLine().split(" ");
            String[] splitUri = parseUri(headerInfo[1]);
            Uri URI = findResponseInfo(headerInfo[0], splitUri[0]);

            if(URI == JOIN){

                Map<String, String> request = parseQueryString(splitUri[1]);
                User user = new User(request.get("userId"), request.get("password"), request.get("name"), request.get("email"));

                log.info(user.toString());
            }

            FileInputStream responseFile = URI.getPath() != null? new FileInputStream(URI.getPath()) : null;
            byte[] body = responseFile != null? responseFile.readAllBytes() : "Hello World".getBytes();

            response200Header(dos, body.length, URI.getContentType());
            responseBody(dos, body);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType+";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}

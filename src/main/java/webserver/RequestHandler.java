package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import data.Uri;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import static data.Uri.*;
import static db.DataBase.*;
import static util.HttpRequestUtils.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    public static final String HOST = "http://localhost:8080";

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String[] headerInfo = br.readLine().split(" ");
            String[] splitUri = parseUri(headerInfo[1]);
            Uri URI = findResponseInfo(headerInfo[0], splitUri[0]);

            int contentLength = 0;

            if(URI == JOIN){

                Map<String, String> request = parseQueryString(splitUri[1]);
                User user = new User(request.get("userId"), request.get("password"), request.get("name"), request.get("email"));

                log.info(user.toString());
            }

            if(URI == JOIN_POST) {

                boolean next = true;
                while (next){
                    String current = br.readLine();
                    if(current.contains("Content-Length")){
                        String[] tmps = current.split(" ");
                        contentLength = Integer.parseInt(tmps[1]);
                    }
                    if(current == null || current.equals(""))
                        next = false;
                }

                Map<String, String> request = parseQueryString(IOUtils.readData(br, contentLength));
                User user = new User(request.get("userId"), request.get("password"), request.get("name"), request.get("email"));
                addUser(user);

                log.info(user.toString());

                response302Header(dos, HOST+INDEX.getUri(), null, null);
                dos.flush();

                return;
            }

            if(URI == LOGIN){

                boolean next = true;
                while (next){
                    String current = br.readLine();
                    if(current.contains("Content-Length")){
                        String[] tmps = current.split(" ");
                        contentLength = Integer.parseInt(tmps[1]);
                    }
                    if(current == null || current.equals(""))
                        next = false;
                }

                Map<String, String> request = parseQueryString(IOUtils.readData(br, contentLength));
                User user = findUserById(request.get("userId"));

                if(user == null || !user.getPassword().equals(request.get("password"))) {
                    response302Header(dos, HOST+LOGIN_FAILED.getUri(), "logined", "false");
                    dos.flush();
                    return;
                }

                response302Header(dos, HOST+INDEX.getUri(), "logined", "true");
                dos.flush();
                return;
            }

            if(URI == USER_LIST){

                Map<String, String> request = new HashMap<>();
                boolean next = true;

                while (next){
                    String current = br.readLine();
                    if(current.contains("Content-Length")){
                        String[] tmps = current.split(" ");
                        contentLength = Integer.parseInt(tmps[1]);
                    }

                    if(current.contains("Cookie")) {
                        String[] tmps = current.split(" ");
                        request = parseCookies(tmps[1]);
                        next = false;
                    }

                    if(current == null || current.equals(""))
                        next = false;
                }

                if(Boolean.parseBoolean(request.get("logined")) == true){
                    Collection<User> users = findAll();

                    FileReader responsePage = new FileReader(URI.getPath());
                    BufferedReader pageReader = new BufferedReader(responsePage);
                    StringBuilder sb = new StringBuilder();

                    while(pageReader.ready()){
                        String tmp = pageReader.readLine();
                        sb.append(tmp);

                        if(tmp.contains("<tbody>")){
                            AtomicInteger row = new AtomicInteger();
                            users.forEach(user -> {
                                sb.append("<tr>\n" + "<th scope=\"row\">"+(row.incrementAndGet())+"</th> <td>"
                                        +user.getUserId()+"</td> <td>"
                                        +user.getName()+"</td> <td>"
                                        +user.getEmail()+"</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n"
                                        +"</tr>\n");
                            });
                        }
                    }

                    response200Header(dos, sb.length(), URI.getContentType());
                    responseBody(dos, sb.toString().getBytes());
                    return;
                }

                if(request == null || Boolean.parseBoolean(request.get("logined")) == false) {
                    response302Header(dos, HOST + LOGIN_PAGE.getUri(), null, null);
                    dos.flush();
                    return;
                }
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

    private void response302Header(DataOutputStream dos, String redirectUrl, String cookieName, String cookieValue){
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: "+redirectUrl+"\r\n");

            if(cookieName != null) dos.writeBytes("Set-Cookie: "+ cookieName + "="+ cookieValue+"; Domain: localhost:8080; Path: /"+"\r\n");

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

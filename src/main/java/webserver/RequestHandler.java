package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import data.HttpRequest;
import data.Uri;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import static data.Uri.findResponseInfo;

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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            HttpRequest request = new HttpRequest(br);

            if ("/user/create".equals(request.getUrl())) {

                User user = new User(request.getParams().get("userId"), request.getParams().get("password"),
                        request.getParams().get("name"), request.getParams().get("email"));
                DataBase.addUser(user);

                response302Header(dos, null,"/index.html");

            } else if(request.getUrl().endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("./webapp" + request.getUrl()).toPath());
                response200Header(dos, "text/css", body.length);
                responseBody(dos, body);

            } else if ("/user/login".equals(request.getUrl())) {
                User user = DataBase.findUserById(request.getParams().get("userId"));

                if (user == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if (user.getPassword().equals(request.getParams().get("password"))) {
                    response302Header(dos, "logined=true", "/index.html");
                } else {
                    responseResource(out, "/user/login_failed.html");
                }

            } else if("/user/list".equals(request.getUrl())) {
                if(!request.isLogined()) {
                    responseResource(out, "/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border = '1'>");
                for (User user : users){
                    sb.append("<tr>");
                    sb.append("<td>"+ user.getUserId()+"/<td>");
                    sb.append("<td>"+ user.getName()+"/<td>");
                    sb.append("<td>"+ user.getEmail()+"/<td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                response200Header(dos, "text/html;charset=utf-8", body.length);
                responseBody(dos, body);

            }else {
                responseResource(out, request.getUrl());
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static boolean isLogin(String line){
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");

        if(value == null)
            return false;

        return Boolean.parseBoolean(value);
    }

    public static int getContentLength(String line){
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private void responseResource(OutputStream out, String url) throws IOException{
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File(("./webapp"+url)).toPath());

        response200Header(dos, "text/html;charset=utf-8", body.length);
        responseBody(dos, body);
    }

    private void response302Header(DataOutputStream dos, String cookie, String url){
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            if(cookie != null) dos.writeBytes("Set-Cookie: "+ cookie+" \r\n");
            dos.writeBytes("Location: "+url+" \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, String contentType, int lengthOfBodyContent){
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType+" \r\n");
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

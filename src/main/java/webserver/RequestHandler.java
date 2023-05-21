package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

import data.HttpRequest;
import data.HttpResponse;
import db.DataBase;
import model.User;
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
            String path = getDefaultPath(request.getPath());
            HttpResponse response = new HttpResponse(out);

            if ("/user/create".equals(path)) {

                User user = new User(request.getParameter("userId"), request.getParameter("password"),
                        request.getParameter("name"), request.getParameter("email"));
                DataBase.addUser(user);

                response.sendRedirect("/index.html");

            } else if ("/user/login".equals(request.getPath())) {
                User user = DataBase.findUserById(request.getParameter("userId"));

                if (user != null) {
                    if (user.getPassword().equals(request.getParameter("password"))) {

                        response.addHeader("Set-Cookie", "logined=true");
                        response.sendRedirect("/index.html");

                    } else {
                        response.forward("/user/login_failed.html");
                    }
                } else {
                    response.sendRedirect("/user/login_failed.html");
                }

            } else if("/user/list".equals(path)) {

                if(!request.isLogin()) {
                    response.forward("/user/login.html");
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
                response.forwardBody(sb.toString());

            } else {
                response.forward(request.getPath());
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path){
        if(path.equals("/"))
            return "/index.html";

        return path;
    }
}

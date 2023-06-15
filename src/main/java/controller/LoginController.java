package controller;

import data.HttpRequest;
import data.HttpResponse;
import data.HttpSession;
import data.HttpSessions;
import db.DataBase;
import model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static data.HttpSessions.httpSession;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {

        User user = DataBase.findUserById(request.getParameter("userId"));
        StringBuilder cookieValue = new StringBuilder();

        if (user == null) {
            response.forward("/user/login_failed.html");
            return;
        }

        if (user.getPassword().equals(request.getParameter("password"))) {

            HttpSession session = new HttpSession();
            Map<String, String> cookies = new LinkedHashMap<>();

            cookies.put("JESSIONID", session.getId());
            session.setAttributes("cookie", cookies);
            httpSession.put(session.getId(), session);

            Object[] keys = cookies.keySet().toArray();
            for(int i=0; i< keys.length; i++){
                cookieValue.append(keys[i]);
                cookieValue.append("=");
                cookieValue.append(cookies.get(keys[i]));

                if(i < keys.length-1)
                    cookieValue.append("; ");
            }

            response.addHeader("Set-Cookie", cookieValue.toString());
            response.sendRedirect("/index.html");

        } else {
            response.forward("/user/login_failed.html");
        }

    }
}

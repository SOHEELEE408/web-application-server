package controller;

import data.HttpRequest;
import data.HttpResponse;
import data.HttpSession;
import data.HttpSessions;
import db.DataBase;
import model.User;

import java.util.*;

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

            HttpSession session = request.getSession();
            session.setAttributes("user", user);

            response.sendRedirect("/index.html");

        } else {
            response.forward("/user/login_failed.html");
        }

    }
}

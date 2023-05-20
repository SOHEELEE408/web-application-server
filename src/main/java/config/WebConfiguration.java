package config;

import controller.*;

import java.util.HashMap;
import java.util.Map;

public class WebConfiguration {

    public static Map<String, Controller> controller() {
        Map<String, Controller> controllers = new HashMap<>();

        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListUserController());
        controllers.put("static", new StaticController());

        return controllers;
    }
}

package webserver;

import annotation.RequestMapping;
import model.User;

import java.util.Map;

public class Controller {

    public Controller(){}

    @RequestMapping(value = "/user/create", method = RequestMapping.RequestMethod.GET)
    public User createUser(Map<String, String> request){
        User user = new User(request.get("userId"), request.get("password"), request.get("name"), request.get("email"));

        return user;
    }
}

package util;

import data.Uri;
import webserver.Controller;

import java.util.Map;

import static data.Uri.JOIN;

public class HandlerAdapter {

    private Uri uri;


    public HandlerAdapter(Uri uri){
        this.uri = uri;
    }

    public Object handle(Object ... request) {
        Controller controller = new Controller();
        Object response = null;

        if (uri == JOIN)
            response = controller.createUser((Map<String, String>) request[0]);

        return response;
    }
}

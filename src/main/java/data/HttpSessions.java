package data;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpSessions {

    public static Map<String, HttpSession> httpSession = new LinkedHashMap<>();

    public static HttpSession getSession(String id){
        HttpSession session = httpSession.get(id);

        if(session == null){
            session = new HttpSession(id);
            httpSession.put(session.getId(), session);
        }

        return session;
    }

    static void remove(String id){
        httpSession.remove(id);
    }
}

package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static data.HttpSessions.httpSession;


public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private Map<String, String> parameter = new HashMap<>();
    private Map<String, String> header = new HashMap<>();
    private Map<String, String> cookie = new HashMap<>();


    public String getHeader(String key){
        return header.get(key);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String key){
        return parameter.get(key);
    }

    public HttpRequest(InputStream in){

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if(line == null) return;

            requestLine = new RequestLine(line);

            line = br.readLine(); // 요청 라인과 header 사이의 공백
            while (line != null && !line.equals("")) {
                log.debug("header: {}", line);

                String[] tokens = line.split(":");

                if(tokens[0].trim().equals("Cookie"))
                    cookie = HttpRequestUtils.parseCookies(tokens[1].trim());
                else
                    header.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if(getMethod().isPost()){
                String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                parameter = HttpRequestUtils.parseQueryString(body);
            } else
                parameter = requestLine.getParameter();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public HttpCookie getCookies(){
        return new HttpCookie(getHeader("Cookie"));
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
    }

    public boolean isLogin(){
        String value = cookie.get("JSESSIONID");

        HttpSession session = httpSession.get(value);

        if(session == null)
            return false;

        return true;
    }
}

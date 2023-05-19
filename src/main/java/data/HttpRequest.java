package data;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static webserver.RequestHandler.*;

public class HttpRequest {

    private Map<String, String> header = new HashMap<>();
    private String method;
    private String path;
    private Map<String, String> parameter = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();


    public String getHeader(String key){
        return header.get(key);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String key){
        return parameter.get(key);
    }

    public String getCookies(String key){ return cookies.get(key); }

    public HttpRequest(InputStream in){

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            String[] tokens = line.split(" ");
            method = tokens[0];

            while (line != null && !line.equals("")) {

                if(line == null || line.equals("")) break;

                String[] headers = line.split(" ");
                headers[0] = headers[0].replace(":","");

                if(headers[0].equals("Cookie"))
                    cookies = HttpRequestUtils.parseCookies(headers[1].trim());

                 else
                     header.put(headers[0], headers[1].trim());
            }

            String body = null;
            if(tokens[1].contains("\\?")){
                String[] paths = tokens[1].split("\\?");
                path = paths[0];

                body = paths[1];

            } else {
                path = tokens[1];
            }

            if(method.equals("POST"))
                body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));

            parameter = HttpRequestUtils.parseQueryString(body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }


    }
}

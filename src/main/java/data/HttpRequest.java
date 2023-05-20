package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private Map<String, String> parameter = new HashMap<>();
    private Map<String, String> header = new HashMap<>();


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

    public HttpRequest(InputStream in){

        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if(line == null) return;

            processRequestLine(line);

            line = br.readLine(); // 요청 라인과 header 사이의 공백
            while (line != null && !line.equals("")) {
                log.debug("header: {}", line);

                String[] tokens = line.split(":");
                header.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if("POST".equals(method)){
                String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                parameter = HttpRequestUtils.parseQueryString(body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processRequestLine(String requestLine) {
        log.debug("request line: {}", requestLine);

        String[] tokens = requestLine.split(" ");
        method = tokens[0];

        if ("POST".equals(method)){
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if(index == -1) path = tokens[1];
        else {
            path = tokens[1].substring(0, index);
            parameter = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
        }

    }
}

package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    public static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private Map<String, String> header = new LinkedHashMap<>();
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
         this.dos = new DataOutputStream(out);
    }

    public void forward(String path) {

        try{
            byte[] body = Files.readAllBytes(new File(("./webapp" + path)).toPath());

            addHeader("statusCode", "HTTP/1.1 200 OK");

            if(path.contains(".html"))
                addHeader("Content-Type", "text/"+path.substring(path.lastIndexOf(".")+1)+";charset=utf-8");

            else if (path.contains(".js"))
                addHeader("Content-Type", "text/javascript");

            else
                addHeader("Content-Type", "text/"+path.substring(path.lastIndexOf(".")+1));

            addHeader("Content-Length", String.valueOf(body.length));

            response(dos, body);

        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String path){

        try {
            addHeader("statusCode", "HTTP/1.1 302 Redirect");
            addHeader("Location", path);

            response(dos, null);

        } catch (IOException e){
            log.error(e.getMessage());
        }

    }

    public void addHeader(String key, String value){
        header.put(key, value);
    }

    public void forwardBody(String body) throws IOException {
        byte[] contents = body.getBytes();
        header.put("Content-Type", "text/html;charset=utf-8");
        header.put("Content-Length", contents.length+"");

        response(dos, contents);
    }

    public void response(DataOutputStream dos, byte[] body) throws IOException {

        dos.writeBytes(header.get("statusCode") + " \r\n");

        for(String key : header.keySet()){
            if(key.equals("statusCode")) continue;
            dos.writeBytes(key + ": " + header.get(key) + " \r\n");
        }

        dos.writeBytes("\r\n");
        if(body != null) dos.write(body, 0, body.length);
        dos.flush();
    }

}

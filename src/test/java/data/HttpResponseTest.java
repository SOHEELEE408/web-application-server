package data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    @DisplayName("")
    void responseForward() throws IOException {
        // given
        HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));

        // when
        response.sendRedirect("/index.html");

        // then
    }

    @Test
    @DisplayName("")
    void responseCookies() throws IOException {
        // given
        HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));

        // when
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");

        // then
    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(testDirectory+filename);
    }

}
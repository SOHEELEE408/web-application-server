package data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    @DisplayName("")
    void request_GET() throws Exception {
        // given
        InputStream in = new FileInputStream(testDirectory+"Http_GET.txt");
        HttpRequest request = new HttpRequest(in);

        // when
        assertEquals("GET", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));

        // then

    }

    @Test
    @DisplayName("")
    void request_POST() throws Exception {
        // given
        InputStream in = new FileInputStream(testDirectory+"Http_POST.txt");
        HttpRequest request = new HttpRequest(in);

        // when
        assertEquals("POST", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));

        // then
    }

    @DisplayName("")
    @Test
    void request_GET_List() throws Exception {
        // given
        InputStream in = new FileInputStream(testDirectory+"Http_list.txt");
        HttpRequest request = new HttpRequest(in);

        // when
        assertEquals(true, request.isLogin());

        // then

    }
}
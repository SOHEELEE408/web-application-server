package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @DisplayName("")
    @Test
    void create_method() {
        // given
        RequestLine getLine = new RequestLine("GET /index.html HTTP/1.1");
        RequestLine postLine = new RequestLine("POST /index.html HTTP/1.1");

        // when
        assertEquals("GET", getLine.getMethod());
        assertEquals("/index.html", getLine.getPath());
        assertEquals("/index.html", postLine.getPath());

        // then

    }

    @DisplayName("")
    @Test
    void create_path_and_params() {
        // given
        RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1");

        // when
        assertEquals("GET", line.getMethod());
        assertEquals("/user/create", line.getPath());
        assertEquals(3, line.getParameter().size());

        // then

    }

}
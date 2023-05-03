package util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import util.HttpRequestUtils.Pair;

import java.io.IOException;
import java.util.Map;

public class HttpRequestUtilsTest {

    @Test
    @DisplayName("URI와 QueryString을 분리하여 배열로 반환한다.")
    void parseUri_with_queryString() throws IOException {
        // given
        String uriWithQueryString = "/user/create?userId=guest&password=password&name=Sohee&email=guest@email.com";

        // when
        String[] request = HttpRequestUtils.parseUri(uriWithQueryString);

        // then
        assertEquals(true, request[1].contains("&"));
    }

    @Test
    @DisplayName("QueryString 없으면 URI만 담은 배열을 반환한다.")
    void parseUri() throws IOException {
        // given
        String uriWithoutQueryString = "/user/create";

        // when
        String[] request = HttpRequestUtils.parseUri(uriWithoutQueryString);

        // then
        assertEquals(request[0], uriWithoutQueryString);
        assertEquals(request[1], null);
    }

    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals(parameters.get("userId"), "javajigi");
        assertNull(parameters.get("password"));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals(parameters.get("userId"), "javajigi");
        assertEquals(parameters.get("password"), "password2");
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertEquals(parameters.isEmpty(), true);

        parameters = HttpRequestUtils.parseQueryString("");
        assertEquals(parameters.isEmpty(), true);

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertEquals(parameters.isEmpty(), true);
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertEquals(parameters.get("userId"), "javajigi");
        assertNull(parameters.get("password"));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertEquals(parameters.get("logined"), "true");
        assertEquals(parameters.get("JSessionId"), "1234");
        assertNull(parameters.get("session"));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertEquals(pair, new Pair("userId", "javajigi"));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertNull(pair);
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertEquals(pair, new Pair("Content-Length", "59"));
    }
}

package controller;

import data.HttpRequest;
import data.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}

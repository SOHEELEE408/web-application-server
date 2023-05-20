package controller;

import data.HttpRequest;
import data.HttpResponse;

import java.io.IOException;

public class StaticController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {

        doGet(request, response);
    }

    public void doGet(HttpRequest request, HttpResponse response) {

        response.forward(request.getPath());
    }
}

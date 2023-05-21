package controller;

import data.HttpMethod;
import data.HttpRequest;
import data.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {

        HttpMethod method = request.getMethod();

        if(method.isPost()) doPost(request, response);

        else doGet(request, response);
    }

    public void doGet(HttpRequest request, HttpResponse response) throws IOException {

    }

    public void doPost(HttpRequest request, HttpResponse response){

    }
}

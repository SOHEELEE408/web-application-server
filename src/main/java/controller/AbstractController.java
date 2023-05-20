package controller;

import data.HttpRequest;
import data.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {

        if(request.getMethod().equals("GET")) doGet(request, response);

        if(request.getMethod().equals("POST")) doPost(request, response);
    }

    public void doGet(HttpRequest request, HttpResponse response) throws IOException {

    }

    public void doPost(HttpRequest request, HttpResponse response){

    }
}

package ru.aslastin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceInfo {
    private final String serverUrl;
    private final HttpClient client;

    public ServiceInfo(String serverUrl, HttpClient client) {
        this.serverUrl = serverUrl;
        this.client = client;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public HttpResponse<byte[]> addProduct(String name, long price) throws Exception {
        return sendRequest(request("/add-product?name=" + name + "&price=" + price));
    }

    public HttpResponse<byte[]> getProducts() throws Exception {
        return sendRequest(request("/get-products"));
    }

    public HttpResponse<byte[]> max() throws Exception {
        return sendCommand("max");
    }

    public HttpResponse<byte[]> min() throws Exception {
        return sendCommand("min");
    }

    public HttpResponse<byte[]> sum() throws Exception {
        return sendCommand("sum");
    }

    public HttpResponse<byte[]> count() throws Exception {
        return sendCommand("count");
    }

    private HttpResponse<byte[]> sendCommand(String command) throws Exception {
        return sendRequest(request("/query?command=" + command));
    }

    private HttpResponse<byte[]> sendRequest(HttpRequest.Builder requestBuilder) throws Exception {
        return client.send(requestBuilder.GET().build(), HttpResponse.BodyHandlers.ofByteArray());
    }

    HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(getServerUrl() + path));
    }
}

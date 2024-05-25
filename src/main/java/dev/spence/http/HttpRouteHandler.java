package dev.spence.http;

import dev.spence.http.request.HttpRequest;
import dev.spence.http.response.HttpResponse;

@FunctionalInterface
public interface HttpRouteHandler {

    HttpResponse handle(HttpRequest request, Object... params);

}

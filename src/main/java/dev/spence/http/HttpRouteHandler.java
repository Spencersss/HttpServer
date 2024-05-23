package dev.spence.http;

import dev.spence.http.response.HttpResponse;

public interface HttpRouteHandler<T> {

    HttpResponse handle(T request, Object... params);

}

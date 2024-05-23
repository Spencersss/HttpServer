package dev.spence.http.response;

public interface HttpResponseBody {

    byte[] toBytes();
    String toString();

}

package dev.spence.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpResponseWriter {

    /**
     * Write a provided list of bytes to a provided output stream.
     *
     * @param outputStream Stream to write to
     * @param responseBytes List of byte data to write to the output stream
     */
    public static void writeResponseBytesToOutput(OutputStream outputStream, List<Byte> responseBytes) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        for (byte responseByte : responseBytes) {
            writer.write(responseByte);
        }

        writer.flush();
    }

}

package dev.spence.http;

import dev.spence.http.response.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpEncoder {

    // Constants
    private static final byte NEWLINE_BYTE = (byte) 0x0a;
    private static final byte CARRIAGE_RETURN_BYTE = (byte) 0x0d;
    private static final byte SPACE_BYTE = (byte) 0x20;
    private static final byte COLON_BYTE = (byte) 0x3a;

    private static final byte[] CARRIAGE_AND_NEWLINE_BYTES = new byte[]{CARRIAGE_RETURN_BYTE, NEWLINE_BYTE};
    private static final byte[] COLON_AND_SPACE_BYTES = new byte[]{COLON_BYTE, SPACE_BYTE};

    // Encodes a provided http response into a byte array to be sent across a OutputStream
    public static List<Byte> encode(HttpResponse response) {
        List<Byte> byteBuffer = new ArrayList<>();
        byte[] bodyBytes = response.getBody().toBytes();

        // Encode start line
        byte[] protocolBytes = response.getProtocol().getBytes(StandardCharsets.UTF_8);
        byte[] statusCodeBytes = Integer.toString(response.getStatusCode()).getBytes(StandardCharsets.UTF_8);
        byte[] statusTextBytes = response.getStatusText().getBytes(StandardCharsets.UTF_8);

        appendByteArraysToList(byteBuffer, SPACE_BYTE, protocolBytes, statusCodeBytes, statusTextBytes);

        // Encode headers
        if (response.getHeaders() != null && !response.getHeaders().isEmpty()) {
            response.getHeaders().put("content-length", String.valueOf(bodyBytes.length));
            appendHeaderBytesToList(byteBuffer, response.getHeaders());
        } else {
            byteBuffer.addAll(Arrays.asList(CARRIAGE_RETURN_BYTE, NEWLINE_BYTE));
        }

        // Encode body
        appendByteArrayToList(byteBuffer, bodyBytes);

        return byteBuffer;
    }

    /**
     * Given an existing list of bytes, append the byte data in each array provided in-order.
     *
     * @param list List of bytes to populate.
     * @param separator Byte value of character to separate byte data groups.
     * @param dataGroups Array containing byte arrays of data.
     */
    private static void appendByteArraysToList(List<Byte> list, Byte separator, byte[]... dataGroups) {
        int totalGroups = dataGroups.length;
        for (int groupIdx = 0; groupIdx < totalGroups; groupIdx++) {
            byte[] dataGroup = dataGroups[groupIdx];
            appendByteArrayToList(list, dataGroup);

            if (separator != null && groupIdx != totalGroups - 1) {
                list.add(separator);
            }
        }
        appendByteArrayToList(list, CARRIAGE_AND_NEWLINE_BYTES);
    }

    /**
     * Given an existing list of bytes, append the byte data in the array provided in-order.
     *
     * @param list List of bytes to populate.
     * @param data Array containing byte data.
     */
    private static void appendByteArrayToList(List<Byte> list, byte[] data) {
        for (byte dataPoint : data) {
            list.add(dataPoint);
        }
    }

    /**
     * Given an existing list of bytes, append the byte data in the array provided in-order.
     *
     * @param list List of bytes to populate.
     * @param headers Map containing header key-value combinations.
     */
    private static void appendHeaderBytesToList(List<Byte> list, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((headerName, headerVal) -> appendByteArraysToList(list,
                    null,
                    headerName.getBytes(StandardCharsets.UTF_8),
                    COLON_AND_SPACE_BYTES,
                    headerVal.getBytes(StandardCharsets.UTF_8)));
            appendByteArrayToList(list, CARRIAGE_AND_NEWLINE_BYTES);
        }
    }

}

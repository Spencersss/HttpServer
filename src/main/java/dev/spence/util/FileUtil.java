package dev.spence.util;

import dev.spence.server.Logging;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    /**
     * Retrieves the byte data from the provided filename and returns.
     *
     * @param filename String containing full path and filename combination
     * @return Byte array of file data
     */
    public byte[] getFileBytes(String filename) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            int fileByteSize = (int) inputStream.getChannel().size();
            byte[] byteData = new byte[fileByteSize];
            int bytesRead = inputStream.read(byteData);

            if (bytesRead != fileByteSize) {
                Logging.LOGGER.info(String.format("Unable to fully read file %s", filename));
            }
            return byteData;
        } catch (IOException e) {
            Logging.LOGGER.info(String.format("Unable to find file %s", filename));
            return new byte[]{};
        }
    }

}

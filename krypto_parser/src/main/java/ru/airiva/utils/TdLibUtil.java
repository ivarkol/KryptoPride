package ru.airiva.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.client.TlgClient;

import java.io.*;

import static java.lang.System.getProperty;

public class TdLibUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TdLibUtil.class);

    /**
     * Загрузка файла libtdjni
     */
    public static void loadLibTd() {
        LOGGER.debug("Start loading a libtdjni");
        String path;
        try {
            File file;
            InputStream input;
            String libtdjniPath;
            String os = getProperty("os.name").toLowerCase();
            if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                libtdjniPath = "/libtdjni.so";
                file = File.createTempFile("libtdjni", ".so");
            } else if (os.contains("mac")) {
                libtdjniPath = "/libtdjni.dylib";
                file = File.createTempFile("libtdjni", ".dylib");
            } else {
                throw new RuntimeException("OS not defined: " + os);
            }

            input = TlgClient.class.getResourceAsStream(libtdjniPath);
            if (input.available() == 0) {
                throw new RuntimeException("File libtdjni not found");
            }

            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            file.deleteOnExit();
            path = file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Error across loading libtdjni", e);
        }
        System.load(path);
        LOGGER.debug("Finish loading a libtdjni");
    }

}

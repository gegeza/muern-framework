package com.muern.framework.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static byte[] file2Bytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void bytes2File(byte[] bytes, File file) {
        try {
            Files.write(file.toPath(), bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static String file2Base64(File file) {
        return Base64.getEncoder().encodeToString(file2Bytes(file));
    }


}
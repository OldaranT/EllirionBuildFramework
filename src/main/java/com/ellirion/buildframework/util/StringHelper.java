package com.ellirion.buildframework.util;

public class StringHelper {

    /**
     * Checks if the given string is a valid filename.
     * @param filename the filename to check
     * @return whether the given filename is valid
     */
    public static boolean invalidFileName(String filename) {
        return filename.matches("^[a-zA-Z0-9-_]+$") && filename.length() < 240;
    }
}

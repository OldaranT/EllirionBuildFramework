package com.ellirion.buildframework.util;

import com.ellirion.buildframework.BuildFramework;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;

public class JsonWriter {

    /**
     *
     * @param json json string.
     * @param name name of the file.
     * @return Returns a boolean if the file has been made.
     */
    public static boolean writeJsonToFile(String json, String name) {
        try {
            String path = "plugins/Ellirion/BuildFramework/templates/" + name + ".json";
            File f = new File(path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileWriter writer = new FileWriter(path);
            writer.write(json);
            writer.close();
            return true;
        } catch (Exception  e) {
            BuildFramework.getInstance().getLogger().log(Level.WARNING, e.getMessage().toString());
            return false;
        }
    }
}

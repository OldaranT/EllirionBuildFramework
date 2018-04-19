package com.ellirion.buildframework.util;

import com.ellirion.buildframework.BuildFramework;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonWriter {
    /**
     *
     * @param path the path of the file to read
     * @param <T> the type of object
     * @return the object read from the file
     */
    public static <T> T readObjectFromFile(final String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(in);

            T t = (T) ois.readObject();
            return t;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

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

    /**
     *
     * @param path the path to the file
     * @return the contents of the file as string
     */
    public static String readJsonFromFile(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param object the object to write to a file
     * @param path the path of the file to write to
     * @return whether the object was written to the file
     */
    public static boolean writeObjectToFile(Object object, String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            ObjectOutputStream oout = new ObjectOutputStream(out);

            oout.writeObject(object);

            oout.close();
            return true;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }
}

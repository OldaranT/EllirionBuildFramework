package com.ellirion.buildframework.Util;

import com.ellirion.buildframework.BuildFramework;
import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter {

    public static boolean WriteJsonToFile(String json, String name) {
        try{
            FileWriter writer = new FileWriter("/templates/" + name +".json");
            writer.write(json.toString());
            writer.close();
            return true;
        }catch (IOException  e){
            BuildFramework.getInstance().getLogger().warning(e.getStackTrace().toString());
            e.printStackTrace();
            return false;
        }
    }
}

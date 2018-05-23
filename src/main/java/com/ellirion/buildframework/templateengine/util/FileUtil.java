package com.ellirion.buildframework.templateengine.util;

import com.ellirion.buildframework.BuildFramework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * Get a list of all the template file names.
     * @return list of file names.
     */
    public static List<String> getListOfNBTFileNames() {

        String path = BuildFramework.getInstance().getConfig().getString("TemplateEngine.Path");
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        List<String> listOfFileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile() && f.getName().contains(".nbt")) {
                    listOfFileNames.add(f.getName().substring(0, f.getName().length() - 4).toUpperCase());
                }
            }
        }

        return listOfFileNames;
    }
}

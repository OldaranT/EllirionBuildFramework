package com.ellirion.buildframework.templateengine.util;

import org.apache.commons.io.FilenameUtils;
import com.ellirion.buildframework.BuildFramework;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

    private static final String PATH = BuildFramework.getInstance().getConfig().getString("TemplateEngine.Path");

    /**
     * Get a list of all the template file names.
     * @return list of file names.
     */
    public static List<String> getListOfNBTFileNames() {
        String path = PATH;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        List<String> listOfFileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile() && FilenameUtils.getExtension(f.getName()).equals("nbt")) {
                    listOfFileNames.add(f.getName().substring(0, f.getName().length() - 4).toUpperCase());
                }
            }
        }

        return listOfFileNames;
    }

    /**
     * Get a list of all the template file names that start with a certain prefix.
     * @param prefix the prefix for the file names
     * @return list of file names.
     */
    public static List<String> getListOfNBTFileNamesWithPrefix(String prefix) {
        String path = PATH;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        List<String> listOfFileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile() && FilenameUtils.getExtension(f.getName()).equals("nbt") &&
                    f.getName().startsWith(prefix)) {
                    listOfFileNames.add(f.getName().substring(0, f.getName().length() - 4).toUpperCase());
                }
            }
        }

        listOfFileNames.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return listOfFileNames;
    }

    /**
     * Get the file of a template.
     * @param name the name of the template
     * @return the file of the template
     */
    public static File getTemplate(String name) {
        String path = PATH;

        return new File(path + "\\" + name + ".nbt");
    }

    /**
     * Get the number of files in a directory that start with a certain prefix.
     * @param prefix the prefix of the files to count
     * @return the amount of files that start with {@code prefix}
     */
    public static int getNumberOfTemplatesWithPrefix(String prefix) {
        String path = PATH;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        int count = 0;
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                String filename = f.getName();
                if (filename.startsWith(prefix)) {
                    count++;
                }
            }
        }

        return count;
    }
}

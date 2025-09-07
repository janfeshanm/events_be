package com.sprocms.jprj.events;
//import java.nio.charset.StandardCharsets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

public class Utils {
    public static Long GN(String uniStr) {
        long l = 0;
        //byte[] oB = uniStr.trim().getBytes(StandardCharsets.UTF_8);

        try {
            l = Long.parseLong(uniStr);
        } catch (Exception e) {
            l = 0;
        }

        return l;
    }

    public static String getFilesFolderPath() {
        URL cl = EventsApp.class.getClassLoader().getResource(".");
        String resourcesPath = cl.getPath();
        resourcesPath = resourcesPath.replaceAll("/C:/", "C:/");
        File cd = new File(resourcesPath);
        String filesPath = cd.getParentFile().getParentFile().getParentFile().getParentFile().getPath()
                + "/files";
        File directory = new File(filesPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return filesPath;
    }

    public static void unzip(String filePath) throws IOException {
        URL sqlScriptUrl = Utils.class.getClassLoader().getResource(".");
        String resourcesPath = sqlScriptUrl.getPath();
        resourcesPath = resourcesPath.replaceAll("/C:/", "C:/") + "static/";
        File directory = new File(resourcesPath);
        FileUtils.deleteDirectory(directory);
        directory.mkdir();

        File copied = new File(resourcesPath + "fe.zip");
        try (
                InputStream in = new BufferedInputStream(
                        new FileInputStream(filePath));
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(copied))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }

        File destDir = new File(resourcesPath);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(copied))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        }
        copied.delete();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}

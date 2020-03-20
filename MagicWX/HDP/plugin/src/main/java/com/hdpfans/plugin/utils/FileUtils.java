package com.hdpfans.plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    public static void unzipFile(File zipFile, String outPath) throws IOException {
        if (zipFile == null || !zipFile.exists()) {
            throw new FileNotFoundException();
        }

        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry;
        String szName;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPath + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPath + File.separator + szName);
                file.getParentFile().mkdirs();
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

}

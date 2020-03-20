package com.hdpfans.app.data.manager;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.hdpfans.app.model.annotation.DiskCacheName;

@Singleton
public class FileManager {

    @Inject
    Context mContext;

    @Inject
    public FileManager() {
    }

    public File getDiskCacheDir(@DiskCacheName String diskCacheName) {
        String cachePath;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cachePath = mContext.getExternalCacheDir().getPath();
//        } else {
        cachePath = mContext.getCacheDir().getPath();
//        }

        return new File(cachePath + File.separator + diskCacheName);
    }

    public File getSystemDexDir() {
        return mContext.getDir("dex", Context.MODE_PRIVATE);
    }

    public File getSystemLibsDir() {
        return mContext.getDir("libs", Context.MODE_PRIVATE);
//        return new File(mContext.getApplicationContext().getApplicationInfo().nativeLibraryDir);
    }

    public File getPluginsDir() {
        File pluginsDir = new File(mContext.getFilesDir(), "plugins");
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs();
        }
        return pluginsDir;
    }

    public void unzipFile(File zipFile, String outPath) throws IOException {
        if (zipFile == null || !zipFile.exists()) {
            throw new FileNotFoundException();
        }

        unzipFile(new FileInputStream(zipFile), outPath);
    }

    public void unzipFile(InputStream zipInputStream, String outPath) throws IOException {
        if (zipInputStream == null) {
            throw new IOException();
        }

        ZipInputStream inZip = new ZipInputStream(zipInputStream);
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


    /**
     * 删除指定目录下文件及目录
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);

                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (File tmpFile : files) {
                        deleteFolderFile(tmpFile.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel(); FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}

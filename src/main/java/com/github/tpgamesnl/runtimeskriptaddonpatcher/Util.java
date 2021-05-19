package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;

public class Util {

    /**
     * Does not close given {@link OutputStream}
     */
    public static void transferStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, length);
        }
    }

    public static JarEntry newJarEntry(JarEntry oldEntry) {
        JarEntry newEntry = new JarEntry(oldEntry.getName());
        if (oldEntry.getComment() != null)
            newEntry.setComment(oldEntry.getComment());
        if (oldEntry.getCreationTime() != null)
            newEntry.setCreationTime(oldEntry.getCreationTime());
        if (oldEntry.getLastAccessTime() != null)
            newEntry.setLastAccessTime(oldEntry.getLastAccessTime());
        if (oldEntry.getLastModifiedTime() != null)
            newEntry.setLastModifiedTime(oldEntry.getLastModifiedTime());
        if (oldEntry.getTime() != -1)
            newEntry.setTime(oldEntry.getTime());
        return newEntry;
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transferStreams(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void exitError(Throwable t, String error) {
        System.err.println(error);
        t.printStackTrace();
        System.exit(-1);
    }

}

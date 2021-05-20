package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarFile;

public class RuntimeSkriptAddonPatcher extends JavaPlugin {

    static {
        //noinspection ConstantConditions
        for (File file : new File("plugins").listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try {
                    JarFile jarFile = new JarFile(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    boolean changed = SkriptAddonPatcher.convertJar(jarFile, baos);
                    if (changed) {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(baos.toByteArray());
                        fileOutputStream.close();
                        System.out.println("[RuntimeSkriptAddonPatcher] File " + file + " changed!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

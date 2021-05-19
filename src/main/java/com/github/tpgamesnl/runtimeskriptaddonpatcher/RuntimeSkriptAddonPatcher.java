package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.management.ManagementFactory;

public class RuntimeSkriptAddonPatcher extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            startAgent();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("Attached");
    }

    public void startAgent() throws IOException, InterruptedException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.substring(0, name.indexOf('@'));
        getLogger().info("Attaching to pid=" + pid);
        String path = getFile().getCanonicalPath();

        Process process = new ProcessBuilder("java", "-jar", path, path, pid)
                .redirectError(Redirect.INHERIT)
                .redirectOutput(Redirect.INHERIT)
                .start();
        process.waitFor();

        int exitValue;
        if ((exitValue = process.exitValue()) != 0)
            throw new IllegalStateException("Exit value non-zero: " + exitValue);
    }

}

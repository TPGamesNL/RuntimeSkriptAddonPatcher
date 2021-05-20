package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class SkriptAddonPatcher {

    public static boolean convertJar(JarFile jarFile, OutputStream outputStream) throws IOException {
        JarOutputStream jarOutputStream = new JarOutputStream(outputStream);

        Enumeration<JarEntry> enumeration = jarFile.entries();
        boolean changed = false;
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            try {
                changed |= handleJarEntry(jarFile, jarOutputStream, jarEntry);
            } catch (Exception ignored) { }
        }
        jarOutputStream.close();

        return changed;
    }

    public static boolean handleJarEntry(JarFile jarFile, JarOutputStream jarOutputStream, JarEntry jarEntry) throws IOException {
        JarEntry newJarEntry = Util.newJarEntry(jarEntry);

        InputStream inputStream = jarFile.getInputStream(jarEntry);
        if (!newJarEntry.getName().endsWith(".class")) {
            jarOutputStream.putNextEntry(newJarEntry);
            Util.transferStreams(inputStream, jarOutputStream);
            return false;
        }

        byte[] oldClassBytes = Util.readAll(inputStream);

        AtomicBoolean used = new AtomicBoolean();
        byte[] newBytes = replaceClass(oldClassBytes, used);

        if (!used.get()) { // Class didn't have anything replaced
            jarOutputStream.putNextEntry(newJarEntry);
            jarOutputStream.write(oldClassBytes);
            return false;
        } else { // The entry needs replacing
            newJarEntry.setComment("Converted with SkriptAddonPatcher");
            newJarEntry.setLastModifiedTime(FileTime.from(Instant.now()));
            newJarEntry.setSize(newBytes.length);

            jarOutputStream.putNextEntry(newJarEntry);
            jarOutputStream.write(newBytes);
            return true;
        }
    }

    public static byte[] replaceClass(byte[] classBytes, AtomicBoolean used) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(0);

        ClassVisitor currentScriptReplacer = new MethodWrappingVisitor(classWriter, mv -> new FieldEncapsulatingMethodVisitor(
                mv,
                "ch/njol/skript/ScriptLoader",
                "currentScript",
                "Lch/njol/skript/config/Config;",
                "getCurrentScript",
                "setCurrentScript",
                used
        ));

        ClassVisitor currentSectionsReplacer = new MethodWrappingVisitor(currentScriptReplacer, mv -> new FieldEncapsulatingMethodVisitor(
                mv,
                "ch/njol/skript/ScriptLoader",
                "currentSections",
                "Ljava/util/List;",
                "getCurrentSections",
                "setCurrentSections",
                used
        ));

        ClassVisitor currentLoopsReplacer = new MethodWrappingVisitor(currentSectionsReplacer, mv -> new FieldEncapsulatingMethodVisitor(
                mv,
                "ch/njol/skript/ScriptLoader",
                "currentLoops",
                "Ljava/util/List;",
                "getCurrentLoops",
                "setCurrentLoops",
                used
        ));

        ClassVisitor hasDelayBeforeReplacer = new MethodWrappingVisitor(currentLoopsReplacer, mv -> new FieldEncapsulatingMethodVisitor(
                mv,
                "ch/njol/skript/ScriptLoader",
                "hasDelayBefore",
                "Lch/njol/util/Kleenean;",
                "getHasDelayBefore",
                "setHasDelayBefore",
                used
        ));

        classReader.accept(hasDelayBeforeReplacer, 0);

        return classWriter.toByteArray();
    }

}

package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.concurrent.atomic.AtomicBoolean;

public class SkriptAddonPatcher {

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

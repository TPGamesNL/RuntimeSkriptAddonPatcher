package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Function;

public class MethodWrappingVisitor extends ClassVisitor {

    private final Function<MethodVisitor, MethodVisitor> function;

    public MethodWrappingVisitor(ClassVisitor classVisitor, Function<MethodVisitor, MethodVisitor> function) {
        super(Opcodes.ASM9, classVisitor);
        this.function = function;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return function.apply(mv);
    }

}

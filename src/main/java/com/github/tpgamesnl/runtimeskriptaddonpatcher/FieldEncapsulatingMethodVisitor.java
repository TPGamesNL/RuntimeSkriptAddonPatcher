package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Only supports static fields
 */
public class FieldEncapsulatingMethodVisitor extends MethodVisitor {

    private final String fieldOwner;
    private final String fieldName;
    private final String fieldType;
    private final String getterName;
    private final String setterName;
    private final AtomicBoolean used;

    public FieldEncapsulatingMethodVisitor(MethodVisitor methodVisitor,
                                           String fieldOwner,
                                           String fieldName,
                                           String fieldType,
                                           String getterName,
                                           String setterName,
                                           AtomicBoolean used) {
        super(Opcodes.ASM9, methodVisitor);
        this.fieldOwner = fieldOwner;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.getterName = getterName;
        this.setterName = setterName;
        this.used = used;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (owner.equals(fieldOwner) && name.equals(fieldName)) {
            if (opcode == Opcodes.GETSTATIC) {
                visitMethodInsn(Opcodes.INVOKESTATIC, fieldOwner, getterName, "()" + fieldType, false);
                used.set(true);
                return;
            } else if (opcode == Opcodes.PUTSTATIC) {
                visitMethodInsn(Opcodes.INVOKESTATIC, fieldOwner, setterName, "(" + fieldType + ")V", false);
                used.set(true);
                return;
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

}

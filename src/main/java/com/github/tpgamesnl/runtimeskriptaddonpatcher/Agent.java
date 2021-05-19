package com.github.tpgamesnl.runtimeskriptaddonpatcher;

import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicBoolean;

public class Agent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MyClassFileTransformer());
    }

    public static void main(String[] args) throws Exception {
        String path = args[0];
        String pid = args[1];
        VirtualMachine.attach(pid).loadAgent(path);
    }

    public static class MyClassFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader,
                                String className,
                                Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain,
                                byte[] classfileBuffer) {
            AtomicBoolean used = new AtomicBoolean();
            byte[] bytes = SkriptAddonPatcher.replaceClass(classfileBuffer, used);
            if (used.get()) {
                System.out.println("Modified " + className);
                return bytes;
            }
            return null;
        }
    }

}

package com.test;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class StaticFieldClassVisitor extends ClassVisitor {


    public void Record(String data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(this.resultFile),true);
            fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String resultFile ;
    private boolean isStatic = false;
    private boolean isContext = false;
    private String className ;
    private List<String> classNames ;
    private String field;
    public StaticFieldClassVisitor(String file, List<String> classNames) {
        super(ASM6);
        this.resultFile = file;
        this.classNames = classNames;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        this.isContext = false;
        this.isStatic = false;
        // 忽略非静态字段
        if (access >= ACC_STATIC && (access ^ ACC_STATIC) < access) {
            isStatic = true;
        }

        //field = name;

        for (String className : classNames) {
            if (descriptor.contains(className) || (signature!=null && signature.contains(className)) ) {
                isContext = true;
                break;
            }
        }

        if (isContext && isStatic) {
            Record(String.format("%30s field: %s",className,name));
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        //super.visit(version, access, name, signature, superName, interfaces);
    }

//    @Override
//    public void visitEnd() {
//        super.visitEnd();
//    }
}

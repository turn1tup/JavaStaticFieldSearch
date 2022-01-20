package com.test;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM6;

public class SubClassClassVisitor extends ClassVisitor {


//    public void Record(String data) {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(new File(this.resultFile),true);
//            fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
//            fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
  //  private String resultFile ;
    private String father ;
    public Set<String> classNames = new HashSet<String>();

    public SubClassClassVisitor(String father) {
        super(ASM6);
        this.father = father;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        if (name.equals(father)) {
//            return;
//        }
        if (superName!=null &&superName.equals(father) ) {
            classNames.add(name);
        }else{
            for(String i :interfaces){
                if (i.equals(father)) {
                    classNames.add(name);
                }
                break;
            }
        }
    }

}

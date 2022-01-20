package com.test;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) throws Exception {
        String father;
        String staticFieldFile;
        //通过 mvn 命令复制依赖jar
        String jarPath;

//        // spring-boot
        jarPath = "spring-boot-libs";
        father = "org/springframework/context/ApplicationContext";
        staticFieldFile = "ApplicationContext.txt";

        // tomcat web
  //      jarPath = "tomcat-libs";
//        father = "javax.servlet.ServletRequest";
//        staticFieldFile = "ServletRequest.txt";
//        father= "org.apache.catalina.Container";
//        staticFieldFile = "Container.txt";

        father = father.replaceAll("\\.", "/");
        List<String> classNames = getClassNames(jarPath, father);

        String classNamesFile = "classNames.txt";
        classNames.stream().forEach(name -> {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(classNamesFile),true);
                fileOutputStream.write(name.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //由于使用的contains判断类型是否为目标类，所以存在一些误报
        getStaticField(jarPath, staticFieldFile, classNames);

    }


    /**
     * 找出该类的所有子类
     * @param jarPath
     * @param father
     * @return
     * @throws Exception
     */
    public static List<String> getClassNames(String jarPath,String father) throws Exception{
        List<String> classNames = new ArrayList<String>(){{add(father);}};
        for(int index=0;index<classNames.size();index++){
            String currentName = classNames.get(index);
            for (String jarFileName : getFiles(jarPath)) {
               // try {
                JarFile jarFile = new JarFile(jarFileName);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (!jarEntry.getName().endsWith(".class")) {
                        continue;
                    }
                    SubClassClassVisitor classVisitor = new SubClassClassVisitor(
                            currentName);
                    ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                    classReader.accept(classVisitor, 0);
                    for (String name : classVisitor.classNames) {
                        if (!classNames.contains(name)) {
                            classNames.add(name);
                        }
                    }
                }

//                } catch (Exception e) {
//                    throw new Exception("error: " + jarFileName);
//                }
            }

        }
        return classNames;
    }


    public static void getStaticField(String jarPath,String resultFile,List<String> classNames) throws Exception{

        for (String jarFileName : getFiles(jarPath)) {
            // try {
            JarFile jarFile = new JarFile(jarFileName);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                StaticFieldClassVisitor classVisitor = new StaticFieldClassVisitor(resultFile, classNames);
                ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                classReader.accept(classVisitor, 0);
            }
        }
    }

    private static Set<String> getFiles(String file){
        Set<String> files = new HashSet<String>();
        File[] fs = new File(file).listFiles();
        for(File f:fs){
            if(f.isDirectory())
                files.addAll(getFiles(f.getPath()));
            if(f.isFile())
                files.add(f.getPath());
        }
        return files;
    }
}

package com.yuhengx.IocAssistance.processor;

import com.yuhengx.IocAssistance.annotation.AliothConfiguration;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author white
 */
public class AnnotationParser {
    private static final String META_INF_PATH = "META-INF/alioth.configurations";
    private static final Set<String> SET = new HashSet<>();

    private static final List<String> LIST = new ArrayList<>();
    /**
     * 它其实是一个工具类，只是用来处理TypeMirror. 也就是一个类的父类。
     * TypeMirror superClassType = currentClass.getSuperclass();
     */
    private static Types typeUtils;

    /**
     * 我们可以通过这个类来创建新的文件
     */
    private static Filer filer;

    /**
     * 在注解处理器处理注解生成新的源代码过程中，我们可用Messager来将一些错误信息打印到控制台上
     */
    private static Messager messager;

    /**
     * 它其实是一个工具类，用来处理所有的Element 元素，
     * 而我们可以把生成代码的类中所有的元素都可以成为Element 元素，
     * 如包就是PackageElement,
     * 类／接口为TypeElement,
     * 变量为VariableElement,
     * 方法为ExecutableElement
     */
    private static Elements elementUtils;

    protected static void init(ProcessingEnvironment processingEnv) {
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    protected static void addMain(Element element) {
        // TODO 如果依赖com.sun.tools.JavacTree，还不如不写!
    }

    protected static void start(Set<? extends Element> rootElements) {
        try {
            resourceCheck();
            rootElements.forEach(AnnotationParser::doParse);
            LIST.forEach(SET::remove);
            if (!SET.isEmpty()) {
                doOps();
            }
        }catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING, e.getLocalizedMessage());
        }

    }

    private static void doParse(Element element) {
        // 得到类
        TypeElement classElement = (TypeElement) element;
        // 获取类全限定名
        String classTypeName = classElement.getQualifiedName().toString();
        AliothConfiguration annotation = element.getAnnotation(AliothConfiguration.class);
        if (annotation != null) {
            // 拿到类的别名
            String beanAliasName = annotation.value();
            // 获取注解全限定名
            String annotationCanonicalName = annotation.annotationType().getCanonicalName();
            String info;
            if ("".equals(beanAliasName)) {
                doRemove(classTypeName, null, 0);
                info = classTypeName + "=" + annotationCanonicalName;
            } else {
                doRemove(classTypeName, beanAliasName, 1);
                info = classTypeName + "=" + annotationCanonicalName + "=" + beanAliasName;
            }
            SET.add(info);
        } else {
            doRemove(classTypeName, null, -1);
        }
    }

    protected static void doOps() {
        try {
            OutputStream wrapOut = Base64.getEncoder().wrap(creatResource().openOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(wrapOut, StandardCharsets.UTF_8));
            boolean flag = false;
            for (String s : SET) {
                if (flag) {
                    bw.newLine();
                    bw.write(s);
                } else {
                    bw.write(s);
                    flag = true;
                }
            }
            bw.close();
            wrapOut.close();
            SET.clear();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.WARNING, e.getLocalizedMessage());
        }
    }

    protected static void resourceCheck() {
        try {
            FileObject resource = getResource();
            if (resource != null) {
                InputStream wrapIn = Base64.getDecoder().wrap(resource.openInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(wrapIn, StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    SET.add(line);
                }
                br.close();
                wrapIn.close();
                resource.delete();
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.WARNING, e.getLocalizedMessage());
        }
    }

    private static void doRemove(String classTypeName, String beanAliasName, int num) {
        for (String s : SET) {
            String[] names = s.split("=");
            switch (num) {
                case 0:
                    if (names.length == 3 && classTypeName.equals(names[0])) {
                        LIST.add(s);
                    }
                    break;
                case 1:
                    if (names.length == 2 && classTypeName.equals(names[0])) {
                        LIST.add(s);
                    } else if (names.length == 3 && classTypeName.equals(names[0]) && !Objects.equals(names[2], beanAliasName)) {
                        LIST.add(s);
                    }
                    break;
                case -1:
                    if (classTypeName.equals(names[0])) {
                        LIST.add(s);
                    }
                    break;
                default:break;
            }
        }
    }

    private static FileObject creatResource() throws IOException {
        return filer.createResource(StandardLocation.CLASS_OUTPUT, "", META_INF_PATH);
    }

    private static FileObject getResource() throws IOException {
        File file = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath() + META_INF_PATH);
        if (!file.exists()) {
            return null;
        }
        return filer.getResource(StandardLocation.CLASS_OUTPUT, "", META_INF_PATH);
    }
}

package com.yuhengx.IocAssistance.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;

/**
 * @author white
 */
public class AliothProcessor extends AbstractProcessor {
    private static final Set<String> SET = new HashSet<>();

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    /**
     * 用于指定该自定义注解处理器(Annotation Processor)是注册给哪些注解的(Annotation)
     *
     * @return * or 完整的包名+类名(eg:com.example.MyAnnotation)
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    /**
     * 用于指定所使用的java版本
     *
     * @return java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 可以获取到很多有用的工具类: Elements , Types , Filer, Local, Messager
     *
     * @param processingEnv environment for facilities the tool framework
     *                      provides to the processor
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        AnnotationParser.init(processingEnv);
    }

    /**
     * Annotation Processor扫描出的结果会存储进roundEnv中，可以在这里获取到注解内容，编写你的操作逻辑。
     * Tips：process()函数中不能直接进行异常抛出,否则的话,运行Annotation Processor的进程会异常崩溃,然后弹出一大堆让人捉摸不清的堆栈调用日志显示。
     *
     * @param annotations the annotation interfaces requested to be processed
     * @param roundEnv    environment for information about the current and prior round
     * @return boolean
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
//        Set<? extends Element> bootAnnotated = roundEnv.getElementsAnnotatedWith(AliothBoot.class);
        Set<? extends Element> rootElements = roundEnv.getRootElements();
//        bootAnnotated.forEach(this::addMain);
        AnnotationParser.start(rootElements);
        return false;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }
}

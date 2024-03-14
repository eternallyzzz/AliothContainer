package com.yuhengx.IocAssistance.factory;

import com.yuhengx.IocAssistance.annotation.AliothBean;
import com.yuhengx.IocAssistance.annotation.AliothResource;
import com.yuhengx.IocAssistance.annotation.AliothValue;
import com.yuhengx.IocAssistance.context.AliothContainer;
import com.yuhengx.IocAssistance.context.AliothContainerContext;
import com.yuhengx.IocAssistance.exception.AliothBeanFactoryException;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author white
 */
public class AliothBeanFactory {
    /**
     * 存八大类型和String类型
     */
    private static final Map<Object, Integer> THM = new HashMap<>();
    /**
     * 配置文件路径
     */
    private static final String CONFIGURATION_PATH = ClassLoader.getSystemResource("").getPath() + "META-INF/alioth.configurations";

    /**
     * bean容器
     */
    private static final ConcurrentMap<String, Object> CHM = new ConcurrentHashMap<>();

    /**
     * 处理初始化的参数
     */
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static final Logger LOGGER = Logger.getLogger(AliothBeanFactory.class.toString());

    /**
     * 存有bean原材料
     */
    private static final Map<String, String> HM = new HashMap<>();

    private static final List<String> LIST = new ArrayList<>();


    static {
        THM.put(byte.class.getTypeName(), 0);
        THM.put(Byte.class.getTypeName(), 0);
        THM.put(short.class.getTypeName(), 1);
        THM.put(Short.class.getTypeName(), 1);
        THM.put(int.class.getTypeName(), 2);
        THM.put(Integer.class.getTypeName(), 2);
        THM.put(long.class.getTypeName(), 3);
        THM.put(Long.class.getTypeName(), 3);
        THM.put(float.class.getTypeName(), 4);
        THM.put(Float.class.getTypeName(), 4);
        THM.put(double.class.getTypeName(), 5);
        THM.put(Double.class.getTypeName(), 5);
        THM.put(boolean.class.getTypeName(), 6);
        THM.put(Boolean.class.getTypeName(), 6);
        THM.put(char.class.getTypeName(), 7);
        THM.put(Character.class.getTypeName(), 7);
        THM.put(String.class.getTypeName(), 8);
    }

    protected static void init() {
        if (INITIALIZED.compareAndSet(false, true)) {
            try {
                doLoad();
                creatBeanInit();
                doSet();
                System.out.println(CHM);
            } catch (Exception e) {
                LOGGER.warning(e.getLocalizedMessage());
            }
        }
    }

    private static void doLoad() throws IOException {
        InputStream wrapIn = Base64.getDecoder().wrap(new FileInputStream(CONFIGURATION_PATH));
        BufferedReader br = new BufferedReader(new InputStreamReader(wrapIn, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            String[] names = line.split("=");
            if (names.length != 0) {
                if (names.length == 2) {
                    HM.put(names[0], names[0]);
                } else {
                    HM.put(names[2], names[0]);
                }
            }
        }
    }

    protected static List<Object> getBeans() {
        return List.of(CHM.values().toArray());
    }
    protected static Object getBean(Class<?> clazz, String beanName) {
        try {
            init();
            if ((beanName == null || "".equals(beanName)) && clazz == null) {
                return null;
            } else if (beanName != null && !"".equals(beanName) && !CHM.containsKey(beanName) && !HM.containsKey(beanName)) {
                return null;
            } else if (clazz != null && !CHM.containsKey(clazz.getTypeName()) && !HM.containsKey(clazz.getTypeName())) {
                return null;
            }
            if (beanName != null && CHM.containsKey(beanName)) {
                return CHM.get(beanName);
            } else if (clazz != null && CHM.containsKey(clazz.getTypeName())) {
                return CHM.get(clazz.getTypeName());
            }
            creatBean(clazz, beanName);
        } catch (Exception e) {
            LOGGER.warning(e.getLocalizedMessage());
        }
        return CHM.get(Objects.requireNonNullElse((beanName == null || "".equals(beanName)) ? null : beanName, clazz == null ? null : clazz.getTypeName()));
    }

    private static void creatBean(Class<?> clazz, String beanName) throws AliothBeanFactoryException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String fullyQualifiedName;
        String typeName = null;
        if (beanName != null && HM.containsKey(beanName)) {
            fullyQualifiedName = HM.get(beanName);
        } else {
            if (clazz == null || !HM.containsKey(clazz.getTypeName())) {
                throw new AliothBeanFactoryException("At least one of Class<?> type and beanName is required.");
            }
            fullyQualifiedName = HM.get(clazz.getTypeName());
            typeName = clazz.getTypeName();
        }

        Class<?> aClass = Class.forName(fullyQualifiedName);
        Object o = aClass.getDeclaredConstructor().newInstance();
        CHM.putIfAbsent(Objects.requireNonNullElse(typeName, beanName), o);
    }

    private static void creatBeanInit() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<String> keys = HM.keySet();
        for (String key : keys) {
            String fullyQualifiedName = HM.get(key);
            doReflex(key, fullyQualifiedName);
        }
    }

    private static void doReflex(String key, String fullyQualifiedName) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Object o = aClass.getDeclaredConstructor().newInstance();
        boolean assignableFrom = AliothContainerContext.class.isAssignableFrom(aClass);
        if (assignableFrom) {
            LIST.add(key);
        }
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            AliothValue valueAnnotation = field.getAnnotation(AliothValue.class);
            AliothResource sourceAnnotation = field.getAnnotation(AliothResource.class);
            field.setAccessible(true);
            if (valueAnnotation != null) {
                String[] values = valueAnnotation.value();
                List<Object> list = AliothConvertType.typeConversion(List.of(field.getType().getName()), values, THM);
                field.set(o, list.get(0));
            }
            if (sourceAnnotation != null) {
                if (CHM.containsKey(field.getType().getTypeName())) {
                    field.set(o, CHM.get(field.getType().getTypeName()));
                } else if (CHM.containsKey(field.getName())) {
                    Object bean = CHM.get(field.getName());
                    boolean equals = bean.getClass().getTypeName().equals(field.getType().getTypeName());
                    if (equals) {
                        field.set(o, CHM.get(field.getName()));
                    }
                } else if (HM.containsKey(field.getName())) {
                    String s = HM.get(field.getName());
                    boolean equals = s.equals(field.getType().getTypeName());
                    if (equals) {
                        doReflex(field.getName(), s);
                        field.set(o, CHM.get(field.getName()));
                    }
                } else if (HM.containsKey(field.getType().getTypeName())) {
                    doReflex(field.getType().getTypeName(), field.getType().getTypeName());
                    field.set(o, CHM.get(field.getType().getTypeName()));
                }
            }
        }
        Method[] methods = aClass.getMethods();
        Annotation annotation = null;
        for (Method method : methods) {
            AliothResource sourceAnnotation = method.getAnnotation(AliothResource.class);
            AliothValue valueAnnotation = method.getAnnotation(AliothValue.class);
            AliothBean beanAnnotation = method.getAnnotation(AliothBean.class);
            if (beanAnnotation != null) {
                annotation = beanAnnotation;
            }
            method.setAccessible(true);
            Class<?>[] parameterTypes = method.getParameterTypes();
            List<Object> typeList = new ArrayList<>();
            for (Class<?> type : parameterTypes) {
                typeList.add(type.getTypeName());
            }
            if (valueAnnotation != null) {
                String[] values = valueAnnotation.value();
                List<Object> list = AliothConvertType.typeConversion(typeList, values, THM);
                if (beanAnnotation != null) {
                    Object bean = method.invoke(o, list.toArray());
                    CHM.putIfAbsent(bean.getClass().getTypeName(), bean);
                } else {
                    method.invoke(o, list.toArray());
                }
            }
            if (sourceAnnotation != null) {
                doReSource(o, method, parameterTypes, beanAnnotation != null);
            }
            if (sourceAnnotation == null && valueAnnotation == null && beanAnnotation != null) {
                if (parameterTypes.length == 0) {
                    Object bean = method.invoke(o);
                    CHM.putIfAbsent(bean.getClass().getTypeName(), bean);
                } else {
                    doReSource(o, method, parameterTypes, true);
                }
            }
        }
        if (annotation == null) {
            CHM.putIfAbsent(key, o);
        }
    }

    private static void doReSource(Object o, Method method, Class<?>[] parameterTypes, boolean beanAnnotation) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Object> list = new ArrayList<>();
        for (Class<?> type : parameterTypes) {
            int star = list.size();
            for (Object bean : CHM.values()) {
                if (bean.getClass().getTypeName().equals(type.getTypeName())) {
                    list.add(bean);
                }
            }
            int end = list.size();
            if (star == end) {
                for (String beanKey : HM.keySet()) {
                    if (HM.get(beanKey).equals(type.getTypeName())) {
                        doReflex(beanKey, type.getTypeName());
                        list.add(CHM.get(beanKey));
                    }
                }
            }
        }
        if (list.size() < parameterTypes.length) {
            for (int i = list.size(), len = parameterTypes.length; i < len; i++) {
                list.add(i, null);
            }
        }
        if (beanAnnotation) {
            Object bean = method.invoke(o, list.toArray());
            CHM.putIfAbsent(bean.getClass().getTypeName(), bean);
        } else {
            method.invoke(o, list.toArray());
        }
    }


    private static void doSet() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (String key : LIST) {
            Object o = CHM.get(key);
            Method setAliothApplicationContext = o.getClass().getMethod("setAliothContainer", AliothContainer.class);
            setAliothApplicationContext.invoke(o, List.of(new AliothBeanContainer()).toArray());
            CHM.put(key, o);
        }
    }
}
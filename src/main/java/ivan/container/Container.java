package ivan.container;

import ivan.annotation.Autowired;
import ivan.annotation.Component;
import ivan.annotation.PostConstructor;
import ivan.annotation.Qualifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Container {
    private static final Container container = new Container();
    private static final Map<String, Class<?>> beans = new HashMap<>();
    private static final Map<Class<?>, Class<?>> interfaceImplementation = new HashMap<>();
    private static final Map<Class<?>, Object> instances = new HashMap<>();
    private static final String BASE_PACKAGE = "main";

    static {
        init(BASE_PACKAGE);
    }

    public static Container getContainer() {
        return container;
    }

    public static Object getInstance(Class<?> clazz) {
        Object instance = null;
        if (clazz.isInterface()) {
            clazz = interfaceImplementation.get(clazz);
        }
        if (instances.containsKey(clazz)) {
            return instances.get(clazz);
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> autowiredConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                autowiredConstructor = constructor;
                break;
            }
        }

        if (autowiredConstructor == null) {
            Constructor<?> constructor = constructors[0];
            if (constructor.getParameterCount() == 0) {
                try {
                    instance = constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException
                         | InvocationTargetException e) {
                    throw new RuntimeException("Can't create a new instance "
                            + "use constructor: " + constructor, e);
                }
            } else {
                autowiredConstructor = constructor;
            }
        }

        if (autowiredConstructor != null) {
            Class<?>[] parameterTypes = autowiredConstructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Parameter parameter = autowiredConstructor.getParameters()[i];
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    String qualifierValue = qualifier.value();
                    parameters[i] = getQualifiedInstance(parameterTypes[i], qualifierValue);
                } else {
                    parameters[i] = getInstance(parameterTypes[i]);
                }
            }
            try {
                instance = autowiredConstructor.newInstance(parameters);
            } catch (InstantiationException | IllegalAccessException
                     | InvocationTargetException e) {
                throw new RuntimeException("Can't create a new instance "
                        + "use constructor: " + autowiredConstructor
                        + " and parameters: " + Arrays.toString(parameters), e);
            }

            Method[] methods = instance.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstructor.class)) {
                    method.setAccessible(true);
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Can't invoke method " + method
                                + " with instance " + instance, e);
                    }
                }
            }
        }
        instances.put(clazz, instance);
        return instance;
    }

    private static <T> T getQualifiedInstance(Class<T> clazz, String qualifierValue) {
        Class<?> implementationClass = beans.get(qualifierValue);
        if (implementationClass != null) {
            if (implementationClass.getSimpleName().equalsIgnoreCase(qualifierValue)) {
                return (T) getInstance(implementationClass);
            }
        }
        throw new IllegalArgumentException("No qualified instance found for class "
                + clazz.getName() + " with qualifier value: " + qualifierValue);
    }

    private static void init(String basePackage) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                File file = new File(resource.toURI());
                for (File classFile : file.listFiles()) {
                    if (classFile.isDirectory()) {
                        init(basePackage + "/" + classFile.getName());
                    }
                    String fileName = classFile.getName();
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        Class<?> classObject = Class.forName(
                                basePackage.replace('/', '.') + "." + className);
                        if (classObject.isAnnotationPresent(Component.class)) {
                            beans.put(className, classObject);
                            if (classObject.getInterfaces().length != 0) {
                                Class<?>[] interfaces = classObject.getInterfaces();
                                interfaceImplementation.put(interfaces[0], classObject);
                            }
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

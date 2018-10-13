package com.viching.generate.db;

import static com.viching.generate.db.util.messages.Messages.getString;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.viching.generate.config.Engine;

/**
 * 创建对象
 * @author Administrator
 *
 */
public class ObjectFactory {

    private static ClassLoader loader;

    /**
     * Utility class. No instances allowed.
     */
    private ObjectFactory() {
        super();
    }

    /**
     * 重置
     */
    public static void reset() {
    	loader = null;
    }

    /**
     * 添加jdbc驱动
     * @param engine
     */
    public static synchronized void addExternalClassLoader(Engine engine) {
    	if(StringUtils.isBlank(engine.getJarpath())){
    		return;
    	}
    	ClassLoader classLoader = getCustomClassloader(engine.getJarpath());
        ObjectFactory.loader = classLoader;
    }
    
    public static ClassLoader getCustomClassloader(String classPathEntry) {
        List<URL> urls = new ArrayList<URL>();
        File file;

        file = new File(classPathEntry);
        if (!file.exists()) {
            throw new RuntimeException(getString("RuntimeError.9", classPathEntry));
        }

        try {
            urls.add(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(getString("RuntimeError.9", classPathEntry));
        }

        ClassLoader parent = Thread.currentThread().getContextClassLoader();

        URLClassLoader ucl = new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);

        return ucl;
    }

    /**
     * 根据雷鸣获取class
     * @param type
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> externalClassForName(String type)
            throws ClassNotFoundException {

        Class<?> clazz;

        try {
            clazz = Class.forName(type, true, loader);
            return clazz;
        } catch (Throwable e) {
            // ignore - fail safe below
        }

        return internalClassForName(type);
    }
    
    /**
     * 根据class名称创建实体
     * @param type
     * @return
     */
    public static Object createExternalObject(String type) {
        Object answer;

        try {
            Class<?> clazz = externalClassForName(type);
            answer = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getString(
                    "RuntimeError.6", type), e); //$NON-NLS-1$
        }

        return answer;
    }

    public static Class<?> internalClassForName(String type)
            throws ClassNotFoundException {
        Class<?> clazz = null;

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            clazz = Class.forName(type, true, cl);
        } catch (Exception e) {
            // ignore - failsafe below
        }

        if (clazz == null) {
            clazz = Class.forName(type, true, ObjectFactory.class.getClassLoader());
        }

        return clazz;
    }

    public static URL getResource(String resource) {
        URL url;

        url = loader.getResource(resource);
        if (url != null) {
            return url;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        url = cl.getResource(resource);

        if (url == null) {
            url = ObjectFactory.class.getClassLoader().getResource(resource);
        }

        return url;
    }

    public static Object createInternalObject(String type) {
        Object answer;

        try {
            Class<?> clazz = internalClassForName(type);

            answer = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getString(
                    "RuntimeError.6", type), e); //$NON-NLS-1$

        }
        return answer;
    }
}

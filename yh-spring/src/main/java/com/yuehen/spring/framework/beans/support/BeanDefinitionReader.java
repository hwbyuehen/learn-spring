package com.yuehen.spring.framework.beans.support;

import com.yuehen.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
public class BeanDefinitionReader {
    private List<String> registryClass = new ArrayList<String>();
    private Properties config = new Properties();

    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 扫描配置文件，得到需要扫描的包路径，扫描到所有类
     * @param configLocations
     */
    public BeanDefinitionReader(String... configLocations) {
        InputStream inputStream = 
                this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:" , ""));
        try {
            config.load(inputStream);
        } catch (IOException e) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {continue;}
                String className = scanPackage + "." + file.getName().replace(".class", "");
                registryClass.add(className);
            } 
        }
    }

    /**
     * 将扫描到的所有配置信息转换为BeanDefinition对象，方便后续操作
     * @return
     */
    public List<BeanDefinition> loadBeanDefinition() {
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        try {
            for (String className : registryClass) {
                Class<?> beanClass = Class.forName(className);
                //如果是接口，不能直接实例化，用它的实现类来实例化
                if (beanClass.isInterface()) {continue;}
                //beanName三种情况:
                //1.默认是类名首字母小写
                //2.自定义名称
                //3.接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return config;
    }
}

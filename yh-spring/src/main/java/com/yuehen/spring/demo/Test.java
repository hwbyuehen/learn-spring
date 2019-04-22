package com.yuehen.spring.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
public class Test {
    private final static String CONTEXT_CONFIG_LOCATION = "application.properties";
    
    public static void main(String[] args) {
//        IQueryService queryService = new QueryService();
//        queryService.query("张三");
//
//        ApplicationContext context = new ApplicationContext(CONTEXT_CONFIG_LOCATION);
//        System.out.println();

//            String line = "${data}";
//            String pat = "\\$\\{[^\\}]+\\}";
////            String pat = "\\$";
//            Pattern pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
//            Matcher matcher = pattern.matcher(line);
//            while (matcher.find()) {
//                String paramName = matcher.group();
//                paramName = paramName.replaceAll("\\$\\{|\\}", "");
//                System.out.println(paramName);
//            }

        String pointCut = "com\\.yuehen\\.spring\\.demo\\.service\\..*Service";
        Pattern pattern = Pattern.compile(pointCut);
        String methodString = "public java.lang.String com.yuehen.spring.demo.service.impl.QueryService.query(java.lang.String)";
        Matcher matcher = pattern.matcher(methodString);
        if (matcher.matches()) {
            System.out.println("pip");
        }
    }
}

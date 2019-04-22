package com.yuehen.spring.framework.webmvc.servlet;

import com.yuehen.spring.framework.annotation.Controller;
import com.yuehen.spring.framework.annotation.RequestMapping;
import com.yuehen.spring.framework.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DispatcherServlet extends HttpServlet {
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private ApplicationContext context;

    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();
    private Map<HandlerMapping, HandlerAdapter> handlerAdapterMap = new HashMap<HandlerMapping, HandlerAdapter>();
    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDespatch(req, resp);
        } catch(Exception e){
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
//            new GPModelAndView("500");

        }
    }

    private void doDespatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1.从request拿到uri去拿到对应HandlerMapping
        HandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new ModelAndView("404"));
            return;
        }

        //2.调用HandlerAdaptor获得调用的参数
        HandlerAdapter ha = getHandlerAdapter(handler);

        //3.调用方法返回模板
        ModelAndView mv = ha.handle(req, resp, handler);

        //4.输出
        processDispatchResult(req, resp, mv);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapterMap.isEmpty()) {return null;}
        HandlerAdapter ha = this.handlerAdapterMap.get(handler);
        if (ha.support(handler)) {
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if (mv == null) {return;}
        if (this.viewResolvers.isEmpty()) {return;}

        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(mv.getViewName(), null);
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) throws Exception {
        if (this.handlerMappings.isEmpty()) { return null;}

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handler : this.handlerMappings) {
            try {
                Matcher matcher = handler.getPattern().matcher(url);
                if (!matcher.matches()) {
                    continue;
                }
                return handler;
            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext容器
        context = new ApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化MVC 9大组件
        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        //1.多文件上传的组件
        initMultipartResolver(context);
        //2.初始化本地语言环境
        initLocaleResolver(context);
        //3.初始化模板处理器
        initThemeResolver(context);

        //4.handlerMapping，必须实现
        initHandlerMappings(context);
        //5.初始化参数适配器，针对每个HandlerMapping，必须实现
        initHandlerAdapters(context);
        //6.初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //7.初始化视图预处理器
        initRequestToViewNameTranslator(context);

        //8.初始化视图转换器，必须实现
        initViewResolvers(context);
        //9.参数缓存器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(ApplicationContext context) {
    }

    private void initLocaleResolver(ApplicationContext context) {
    }

    private void initThemeResolver(ApplicationContext context) {
    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)){continue;}

                //解析注解得到路径和method的对应关系
                String baseUrl = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {continue;}
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new HandlerMapping(pattern, controller, method));
                    log.info("Mapped " + regex + " , " + method);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            //HandlerAdapter内部解析方法参数等
            this.handlerAdapterMap.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    private void initViewResolvers(ApplicationContext context) {
        //1.拿到模板文件
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File dir = new File(templateRootPath);
        for (File file : dir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    private void initFlashMapManager(ApplicationContext context) {
    }
}

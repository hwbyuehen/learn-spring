package com.yuehen.spring.demo.action;

import com.yuehen.spring.demo.service.IModifyService;
import com.yuehen.spring.demo.service.IQueryService;
import com.yuehen.spring.framework.annotation.Autowired;
import com.yuehen.spring.framework.annotation.Controller;
import com.yuehen.spring.framework.annotation.RequestMapping;
import com.yuehen.spring.framework.annotation.RequestParam;
import com.yuehen.spring.framework.webmvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
@Controller
public class MyAction {
    @Autowired
    private IQueryService queryService;
    @Autowired
    private IModifyService modifyService;

    //http://localhost/query.json?name=Test
    @RequestMapping("/query.json")
    public ModelAndView query(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam("name") String name){
        String result = queryService.query(name);

        return out(response,result);
    }

    //http://localhost/first.html?data=jsdf
    @RequestMapping("/first.html")
    public ModelAndView first(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam("data") String data) {
        Map<String, String> model = new HashMap<String, String>();
        model.put("data", data);
        model.put("token", UUID.randomUUID().toString());
        return new ModelAndView("first", model);
    }

    @RequestMapping("/add*.json")
    public ModelAndView add(HttpServletRequest request,HttpServletResponse response,
                              @RequestParam("name") String name,@RequestParam("addr") String addr){
        String result = null;
        try {
            result = modifyService.add(name,addr);
            return out(response,result);
        } catch (Exception e) {
//			e.printStackTrace();
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("detail",e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            return new ModelAndView("500",model);
        }

    }

    @RequestMapping("/remove.json")
    public ModelAndView remove(HttpServletRequest request,HttpServletResponse response,
                                 @RequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(response,result);
    }

    @RequestMapping("/edit.json")
    public ModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                               @RequestParam("id") Integer id,
                               @RequestParam("name") String name){
        String result = modifyService.edit(id,name);
        return out(response,result);
    }



    private ModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

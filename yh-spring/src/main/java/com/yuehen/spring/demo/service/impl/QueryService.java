package com.yuehen.spring.demo.service.impl;

import com.yuehen.spring.demo.service.IQueryService;
import com.yuehen.spring.framework.annotation.Service;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 吃土的飞鱼
 * @date 2019/4/16
 */
@Slf4j
@Service
public class QueryService implements IQueryService {
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        String json = "{\"name\":\"" + name + "\",\"date\":\"" + date + "\"}";
        log.info("查询信息返回：{}", json);
        return json;
    }
}

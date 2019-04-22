package com.yuehen.orm.demo.dao;

import com.yuehen.orm.demo.entity.Member;
import com.yuehen.orm.framework.BaseDaoSupport;
import com.yuehen.orm.framework.QueryRule;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class MemberDao extends BaseDaoSupport<Member,Long> {
    @Override
    public String getPKColumn() {
        return "id";
    }

    @Resource
    public void setDataSource(DataSource dataSource) {
        super.dataSource(dataSource);
    }

    public List<Member> selectAll() throws  Exception{
        //1.组装sql规则类
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andLike("name","%%");

        //2.拼接sql并执行
        return super.select(queryRule);
    }
}

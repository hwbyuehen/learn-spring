package com.yuehen.orm.framework;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.core.common.utils.GenericsUtils;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> {
    private Logger log = Logger.getLogger(BaseDaoSupport.class);

    private String tableName = "";

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    private EntityOperation<T> op;

    protected BaseDaoSupport(){
        try{
            //1.获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager<Book>
            Class<T> entityClass = GenericsUtils.getSuperClassGenricType(getClass(), 0);
            //2.解析类，得到类字段，mapping，表等信息
            op = new EntityOperation<T>(entityClass,this.getPKColumn());
            this.setTableName(op.tableName);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private JdbcTemplate jdbcTemplate() {
        return this.jdbcTemplate;
    }


    protected String getTableName() {
        return tableName;
    }

    /**
     * 动态切换表名
     */
    protected void setTableName(String tableName) {
        if(StringUtils.isEmpty(tableName)){
            this.tableName = op.tableName;
        }else{
            this.tableName = tableName;
        }
    }
    /**
     * 获取主键列名称 建议子类重写
     * @return
     */
    protected abstract String getPKColumn();

    protected abstract void setDataSource(DataSource dataSource);

    /**
     * 查询函数，使用查询规
     * 例如以下代码查询条件为匹配的数据
     *
     * <pre>
     *		<code>
     * QueryRule queryRule = QueryRule.getInstance();
     * queryRule.addLike(&quot;username&quot;, user.getUsername());
     * queryRule.addLike(&quot;monicker&quot;, user.getMonicker());
     * queryRule.addBetween(&quot;id&quot;, lowerId, upperId);
     * queryRule.addDescOrder(&quot;id&quot;);
     * queryRule.addAscOrder(&quot;username&quot;);
     * list = userService.find(User.class, queryRule);
     * </code>
     * </pre>
     *
     * @param queryRule 查询规则
     * @return 查询出的结果List
     */
    protected List<T> select(QueryRule queryRule) throws Exception{
        QueryRuleSqlBuilder bulider = new QueryRuleSqlBuilder(queryRule);
        String ws = removeFirstAnd(bulider.getWhereSql());
        String whereSql = ("".equals(ws) ? ws : (" where " + ws));
        String sql = "select " + op.allColumn + " from " + getTableName() + whereSql;
        Object [] values = bulider.getValues();
        String orderSql = bulider.getOrderSql();
        orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
        sql += orderSql;
        log.debug(sql);
        return (List<T>) this.jdbcTemplate().query(sql, this.op.rowMapper, values);
    }

    /**
     * 插入一条记录
     * @param entity
     * @return
     */
    protected boolean insert(T entity) throws Exception{
        return this.doInsert(parse(entity));
    }

    /**
     * 将对象解析为Map
     * @param entity
     * @return
     */
    protected Map<String,Object> parse(T entity){
        return op.parse(entity);
    }

    /**
     * 插入
     * @param params
     * @return
     */
    private boolean doInsert(Map<String, Object> params) {
        String sql = this.makeSimpleInsertSql(this.getTableName(), params);
        int ret = this.jdbcTemplate().update(sql, params.values().toArray());
        return ret > 0;
    }

    /**
     * 生成对象INSERT语句，简化sql拼接
     * @param tableName
     * @param params
     * @return
     */
    private String makeSimpleInsertSql(String tableName, Map<String, Object> params){
        if(javax.core.common.utils.StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("insert into ").append(tableName);

        StringBuffer sbKey = new StringBuffer();
        StringBuffer sbValue = new StringBuffer();

        sbKey.append("(");
        sbValue.append("(");
        //添加参数
        Set<String> set = params.keySet();
        int index = 0;
        for (String key : set) {
            sbKey.append(key);
//			sbValue.append(" :").append(key);
            sbValue.append(" ?");
            if(index != set.size() - 1){
                sbKey.append(",");
                sbValue.append(",");
            }
            index++;
        }
        sbKey.append(")");
        sbValue.append(")");

        sb.append(sbKey).append("VALUES").append(sbValue);

        return sb.toString();
    }

    //删除第一个and
    private String removeFirstAnd(String sql){
        if(StringUtils.isEmpty(sql)){return sql;}
        return sql.trim().toLowerCase().replaceAll("^\\s*and", "") + " ";
    }
}

package com.yuehen.spring.demo.service.impl;

import com.yuehen.spring.demo.service.IModifyService;
import com.yuehen.spring.framework.annotation.Service;

/**
 * 增删改业务
 * @author Tom
 *
 */
@Service
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) throws Exception {
		throw new Exception("故意抛的异常！！");
		//return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}

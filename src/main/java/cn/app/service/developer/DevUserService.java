package cn.app.service.developer;

import cn.app.model.DevUser;

public interface DevUserService {
	/**
	 * 用户登录
	 */
	public DevUser login(String devCode, String devPassword) throws Exception;
}

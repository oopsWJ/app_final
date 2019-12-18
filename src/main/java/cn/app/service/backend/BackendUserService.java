package cn.app.service.backend;
import cn.app.model.BackendUser;

public interface BackendUserService {
	/**
	 * 用户登录
	 */
	public BackendUser login(String userCode, String userPassword) throws Exception;
}

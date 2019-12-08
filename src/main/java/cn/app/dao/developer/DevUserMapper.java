package cn.app.dao.developer;

import org.apache.ibatis.annotations.Param;
import cn.app.model.DevUser;

public interface DevUserMapper {
	/**
	 * 通过devCode获取User
	 * @param devCode
	 * @return
	 * @throws Exception
	 */
	public DevUser getLoginUser(@Param("devCode") String devCode)throws Exception;
}

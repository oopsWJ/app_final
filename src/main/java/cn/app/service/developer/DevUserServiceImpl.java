package cn.app.service.developer;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.app.dao.developer.DevUserMapper;
import cn.app.model.DevUser;

@Service
public class DevUserServiceImpl implements DevUserService {
	@Resource
	private DevUserMapper devUserMapper;
	@Override
	public DevUser login(String devCode, String devPassword) throws Exception {
		// TODO Auto-generated method stub
		DevUser user = null;
		user = devUserMapper.getLoginUser(devCode);
		//匹配密码
		if(null != user){
			if(!user.getDevPassword().equals(devPassword))
				user = null;
		}
		return user;
	}

}

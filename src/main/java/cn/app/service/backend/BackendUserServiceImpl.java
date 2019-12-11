package cn.app.service.backend;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.app.dao.backenduser.BackendUserMapper;
import cn.app.model.BackendUser;

@Service
public class BackendUserServiceImpl implements BackendUserService {
	@Resource
	private BackendUserMapper userMapper;
	
	@Override
	public BackendUser login(String userCode, String userPassword) throws Exception {
		// TODO Auto-generated method stub
		BackendUser user = null;
		user = userMapper.getLoginUser(userCode);
		//匹配密码
		if(null != user){
			if(!user.getUserPassword().equals(userPassword))
				user = null;
		}
		return user;
	}

}

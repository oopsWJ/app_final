package cn.app.controller.developer;

import cn.app.model.DevUser;
import cn.app.service.developer.DevUserService;
import cn.app.tools.SystemValues;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/dev")
public class DevLoginController {

	@Resource
	private DevUserService devUserService;
	
	@RequestMapping(value="/login")
	public String login(){
		return "devlogin";
	}
	
	@RequestMapping(value= "/doLogin",method=RequestMethod.POST)
	public String doLogin(@RequestParam String devCode,@RequestParam String devPassword,HttpServletRequest request,HttpSession session){
		//调用service方法，进行用户匹配
		DevUser user = null;
		try {
			user = devUserService.login(devCode,devPassword);
			System.out.println(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != user){//登录成功
			//放入session
			session.setAttribute(SystemValues.DEV_USER_SESSION, user);
			//页面跳转（main.jsp）
			return "redirect:/dev/appMgt/main";
		}else{
			//页面跳转（login.jsp）带出提示信息--转发
			request.setAttribute("error", "用户名或密码不正确");
			return "devlogin";
		}
	}
	
	@RequestMapping(value="/appMgt/main")
	public String main(HttpSession session){
		if(session.getAttribute(SystemValues.DEV_USER_SESSION) == null){
			return "redirect:/dev/login";
		}
		return "developer/main";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session){
		//清除session
		session.removeAttribute(SystemValues.DEV_USER_SESSION);
		return "devlogin";
	}
}


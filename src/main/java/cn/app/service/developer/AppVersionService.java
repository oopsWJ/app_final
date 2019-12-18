package cn.app.service.developer;

import java.util.List;
import cn.app.model.AppVersion;

public interface AppVersionService {
	/**
	 * 根据appId查询相应的app版本列表
	 */
	public List<AppVersion> getAppVersionList(Integer appId)throws Exception;
	/**
	 * 新增app版本信息，并更新app_info表的versionId字段
	 */
	public boolean appsysadd(AppVersion appVersion)throws Exception;
	/**
	 * 根据id获取AppVersion
	 */
	public AppVersion getAppVersionById(Integer id)throws Exception;
	
	/**
	 * 修改app版本信息
	 */
	public boolean modify(AppVersion appVersion)throws Exception;
	
	/**
	 * 删除apk文件
	 */
	public boolean deleteApkFile(Integer id)throws Exception;
}

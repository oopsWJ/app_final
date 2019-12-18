package cn.app.service.developer;

import java.util.List;
import cn.app.model.AppInfo;

public interface AppInfoService {
	
	/**
	 * 新增app
	 */
	public boolean add(AppInfo appInfo) throws Exception;
	/**
	 * 修改app信息
	 */
	public boolean modify(AppInfo appInfo)throws Exception;
	
	/**
	 * 根据appId删除app应用
	 */
	public boolean deleteAppInfoById(Integer delId)throws Exception;
	
	/**
	 * 根据条件查询出app列表
	 */
	public List<AppInfo> getAppInfoList(String querySoftwareName, Integer queryStatus,
                                        Integer queryCategoryLevel1, Integer queryCategoryLevel2,
                                        Integer queryCategoryLevel3, Integer queryFlatformId,
                                        Integer devId, Integer currentPageNo, Integer pageSize)throws Exception;
	
	/**
	 * 根据条件查询appInfo表记录数
	 */
	public int getAppInfoCount(String querySoftwareName, Integer queryStatus,
                               Integer queryCategoryLevel1, Integer queryCategoryLevel2,
                               Integer queryCategoryLevel3, Integer queryFlatformId, Integer devId)throws Exception;
	/**
	 * 根据id、apkName查找appInfo
	 */
	public AppInfo getAppInfo(Integer id, String APKName)throws Exception;
	
	/**
	 * 删除logo图片
	 */
	public boolean deleteAppLogo(Integer id)throws Exception;
	
	/**
	 * 通过appId删除app应用(app_info、app_version)
	 */
	public boolean appsysdeleteAppById(Integer id)throws Exception;
	
	
	/**
	 * update Sale Status By AppId and Operator
	 */
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo) throws Exception;
}

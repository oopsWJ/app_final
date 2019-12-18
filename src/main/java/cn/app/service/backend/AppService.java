package cn.app.service.backend;

import cn.app.model.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppService {
	
	/**
	 * 根据条件查询出待审核的APP列表并分页显示
	 */
	public List<AppInfo> getAppInfoList(@Param(value = "softwareName") String querySoftwareName,
                                        @Param(value = "categoryLevel1") Integer queryCategoryLevel1,
                                        @Param(value = "categoryLevel2") Integer queryCategoryLevel2,
                                        @Param(value = "categoryLevel3") Integer queryCategoryLevel3,
                                        @Param(value = "flatformId") Integer queryFlatformId,
                                        @Param(value = "from") Integer currentPageNo,
                                        @Param(value = "pageSize") Integer pageSize)throws Exception;
	/**
	 * 查询出待审核（status=1）的APP数量
	 */
	public int getAppInfoCount(@Param(value = "softwareName") String querySoftwareName,
                               @Param(value = "categoryLevel1") Integer queryCategoryLevel1,
                               @Param(value = "categoryLevel2") Integer queryCategoryLevel2,
                               @Param(value = "categoryLevel3") Integer queryCategoryLevel3,
                               @Param(value = "flatformId") Integer queryFlatformId)throws Exception;
	
	/**
	 * 根据id获取app详细信息
	 */
	public AppInfo getAppInfo(@Param(value = "id") Integer id)throws Exception;
	
	/**
	 * 根据id更新app的satus
	 */
	public boolean updateSatus(@Param(value = "status") Integer status, @Param(value = "id") Integer id)throws Exception;
	
}

package cn.app.service.developer;

import java.util.List;

import cn.app.model.DataDictionary;

public interface DataDictionaryService {
	
	/**
	 * 根据typeCode查询相应的数据字典列表
	 */
	public List<DataDictionary> getDataDictionaryList(String typeCode)throws Exception;
}

package cn.app.controller.developer;

import cn.app.model.*;
import cn.app.service.developer.AppCategoryService;
import cn.app.service.developer.AppInfoService;
import cn.app.service.developer.AppVersionService;
import cn.app.service.developer.DataDictionaryService;
import cn.app.tools.SystemValues;
import cn.app.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/dev/appMgt")
public class AppMgtController {
    @Resource
    private AppInfoService appInfoService;
    @Resource
    private DataDictionaryService dataDictionaryService;
    @Resource
    private AppCategoryService appCategoryService;
    @Resource
    private AppVersionService appVersionService;

    /**
     * yxq
     * 搜索数据
     */
    @RequestMapping(value = "/appInfoList")
    public String getAppInfoList(Model model, HttpSession session,
                                 @RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
                                 @RequestParam(value = "queryStatus", required = false) String _queryStatus,
                                 @RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
                                 @RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
                                 @RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
                                 @RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex) {
        //获取开发者ID
        Integer devId = ((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId();
        List<AppInfo> appInfoList = null;
        List<DataDictionary> statusList = null;
        List<DataDictionary> flatFormList = null;
        List<AppCategory> categoryLevel1List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<AppCategory> categoryLevel2List = null;
        List<AppCategory> categoryLevel3List = null;
        //页面容量
        int pageSize = SystemValues.pageSize;
        //当前页码
        Integer currentPageNo = 1;

        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Integer queryStatus = null;
        if (_queryStatus != null && !_queryStatus.equals("")) {
            queryStatus = Integer.parseInt(_queryStatus);
        }
        Integer queryCategoryLevel1 = null;
        if (_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")) {
            queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
        }
        Integer queryCategoryLevel2 = null;
        if (_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")) {
            queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
        }
        Integer queryCategoryLevel3 = null;
        if (_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")) {
            queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
        }
        Integer queryFlatformId = null;
        if (_queryFlatformId != null && !_queryFlatformId.equals("")) {
            queryFlatformId = Integer.parseInt(_queryFlatformId);
        }

        //表内信息总数量
        int totalCount = 0;
        try {
            totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        try {
            appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
            statusList = this.getDataDictionaryList("APP_STATUS");
            flatFormList = this.getDataDictionaryList("APP_FLATFORM");
            categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("queryStatus", queryStatus);
        model.addAttribute("querySoftwareName", querySoftwareName);
        model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
        model.addAttribute("queryFlatformId", queryFlatformId);

        //二级分类列表和三级分类列表---回显
        if (queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
            categoryLevel2List = getAppCategoryList(queryCategoryLevel1.toString());
            model.addAttribute("categoryLevel2List", categoryLevel2List);
        }
        if (queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")) {
            categoryLevel3List = getAppCategoryList(queryCategoryLevel2.toString());
            model.addAttribute("categoryLevel3List", categoryLevel3List);
        }
        return "developer/appinfolist";
    }

    //根据typeCode查询出相应的数据字典列表
    public List<DataDictionary> getDataDictionaryList(String typeCode) {
        List<DataDictionary> dataDictionaryList = null;
        try {
            dataDictionaryList = dataDictionaryService.getDataDictionaryList(typeCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataDictionaryList;
    }

    /**
     * 根据typeCode查询出相应的数据字典列表
     */
    @RequestMapping(value = "/dataDictionaryList.json", method = RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> getDataDicList(@RequestParam String tcode) {
        return this.getDataDictionaryList(tcode);
    }

    /**
     * yxq
     * 根据parentId查询出相应的分类级别列表
     */
    @RequestMapping(value = "/categoryLevelList.json", method = RequestMethod.GET)
    @ResponseBody
    public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
        if (pid.equals("")) pid = null;
        List<AppCategory> categoryLevelList = null;
        try {
            categoryLevelList = appCategoryService.getAppCategoryListByParentId(pid == null ? null : Integer.parseInt(pid));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryLevelList;
    }


    /**
     * yxq
     * 跳转到新增appinfo页面
     */
    @RequestMapping(value = "/appInfoAdd", method = RequestMethod.GET)
    public String appInfoAdd(@ModelAttribute("appInfo") AppInfo appInfo) {
        return "developer/appinfoadd";
    }

    /**
     * yxq
     * 新增并保存appInfo（主表）的数据
     */
    @RequestMapping(value = "/doAppInfoAdd", method = RequestMethod.POST)
    public String doAppInfoAdd(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                               @RequestParam(value = "a_logoPicPath", required = false) MultipartFile attach) {

        String logoPicPath = null;
        String logoLocPath = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String suffix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            int filesize = 500000;
            //判断文件的大小，上传大小不得超过 50k
            if (attach.getSize() > filesize) {
                //上传文件过大
                request.setAttribute("fileUploadError", SystemValues.FILEUPLOAD_ERROR_4);
                return "developer/appinfoadd";
            } else if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png")
                    || suffix.equalsIgnoreCase("jepg") || suffix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = appInfo.getAPKName() + ".jpg";//上传LOGO图片命名:apk名称.apk
                //绝对路径文件名
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    //使用绝对路径保存文件
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    //上传失败
                    request.setAttribute("fileUploadError", SystemValues.FILEUPLOAD_ERROR_2);
                    return "developer/appinfoadd";
                }
                //使用相对路径,存到数据库
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                //上传格式不正确
                request.setAttribute("fileUploadError", SystemValues.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
        appInfo.setCreatedBy(((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId());
        appInfo.setCreationDate(new Date());
        appInfo.setLogoPicPath(logoPicPath);
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setDevId(((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId());
        appInfo.setStatus(1);
        try {
            if (appInfoService.add(appInfo)) {
                return "redirect:/dev/appMgt/appInfoList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfoadd";
    }


    /**
     * yxq
     * 异步调用
     * 判断APKName是否唯一
     */
    @RequestMapping(value = "/apkExist.json", method = RequestMethod.GET)
    @ResponseBody
    public Object apkNameIsExist(@RequestParam String APKName) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        //如果APKName为空
        if (StringUtils.isNullOrEmpty(APKName)) {
            resultMap.put("APKName", "empty");
        } else {
            AppInfo appInfo = null;
            try {
                //通过id，APKName获取AppInfo
                appInfo = appInfoService.getAppInfo(null, APKName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != appInfo)
                resultMap.put("APKName", "exist");
            else
                resultMap.put("APKName", "noexist");
        }
        return JSONArray.toJSONString(resultMap);
    }



    /**
     * yxq
     * 跳转到修改appInfo页面
     */
    @RequestMapping(value = "/appInfoModify", method = RequestMethod.GET)
    public String appInfoModify(@RequestParam("id") String id,
                                @RequestParam(value = "error", required = false) String fileUploadError,
                                Model model) {
        AppInfo appInfo = null;
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_3;
        } else if (null != fileUploadError && fileUploadError.equals("error4")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_4;
        }
        try {
            appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute(appInfo);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appinfomodify";
    }

    /**
     * yxq
     * 修改appInfo并保存
     */
    @RequestMapping(value = "/doAppInfoModify", method = RequestMethod.POST)
    public String doAppInfoModify(AppInfo appInfo, HttpSession session, HttpServletRequest request,
                                  @RequestParam(value = "attach", required = false) MultipartFile attach) {
        String logoPicPath = null;
        String logoLocPath = null;
        String APKName = appInfo.getAPKName();
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            int filesize = 500000;
            if (attach.getSize() > filesize) {//上传大小不得超过 50k
                return "redirect:/dev/appMgt/appInfoModify?id=" + appInfo.getId()
                        + "&error=error4";
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = APKName + ".jpg";//上传LOGO图片命名:apk名称.apk
                //绝对路径文件名
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    //使用绝对路径保存文件
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    //上传失败
                    e.printStackTrace();
                    return "redirect:/dev/appMgt/appInfoModify?id=" + appInfo.getId()
                            + "&error=error2";
                }
                //使用相对路径,存到数据库
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                //上传格式错误
                return "redirect:/dev/appMgt/appInfoModify?id=" + appInfo.getId()
                        + "&error=error3";
            }
        }
        appInfo.setModifyBy(((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId());
        appInfo.setModifyDate(new Date());
        appInfo.setLogoLocPath(logoLocPath);
        appInfo.setLogoPicPath(logoPicPath);
        try {
            if (appInfoService.modify(appInfo)) {
                return "redirect:/dev/appMgt/appInfoList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appinfomodify";
    }

    /**
     * yxq
     * 删除app
     */
    @RequestMapping(value = "/delApp.json")
    @ResponseBody
    public Object delApp(@RequestParam String id) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(id)) {
            resultMap.put("delResult", "notexist");
        } else {
            try {
                if (appInfoService.appsysdeleteAppById(Integer.parseInt(id)))
                    resultMap.put("delResult", "true");
                else
                    resultMap.put("delResult", "false");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    /**
     * wj
     * 跳转到查看页面
     */
    @RequestMapping(value = "/appInfoView/{id}", method = RequestMethod.GET)
    public String appInfoView(@PathVariable String id, Model model) {
        AppInfo appInfo = null;
        List<AppVersion> appVersionList = null;
        try {
            appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appInfo);
        return "developer/appinfoview";
    }

    /**
     * wj
     * 跳转到新增app版本页面
     */
    @RequestMapping(value = "/appVersionAdd", method = RequestMethod.GET)
    public String appVersionAdd(@RequestParam(value = "id") String appId,
                                @RequestParam(value = "error", required = false) String fileUploadError,
                                AppVersion appVersion, Model model) {
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_3;
        }
        appVersion.setAppId(Integer.parseInt(appId));
        List<AppVersion> appVersionList = null;
        try {
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
            appVersion.setAppName((appInfoService.getAppInfo(Integer.parseInt(appId), null)).getSoftwareName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute(appVersion);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appversionadd";
    }

    /**
     * wj
     * 保存新增appversion数据（子表）-上传该版本的apk包
     */
    @RequestMapping(value = "/doAppVersionAdd", method = RequestMethod.POST)
    public String doAppVersionAdd(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                  @RequestParam(value = "a_downloadLink", required = false) MultipartFile attach) {
        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            if (prefix.equalsIgnoreCase("apk")) {//apk文件命名：apk名称+版本号+.apk
                String apkName = null;
                try {
                    apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/appMgt/appVersionAdd?id=" + appVersion.getAppId()
                            + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/dev/appMgt/appVersionAdd?id=" + appVersion.getAppId()
                            + "&error=error2";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            } else {
                return "redirect:/dev/appMgt/appVersionAdd?id=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }
        appVersion.setCreatedBy(((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId());
        appVersion.setCreationDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        try {
            if (appVersionService.appsysadd(appVersion)) {
                return "redirect:/dev/appMgt/appInfoList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/dev/appMgt/appVersionAdd?id=" + appVersion.getAppId();
    }

    /**
     * wj
     * 版本上下架切换
     */
    @RequestMapping(value = "/{appid}/sale", method = RequestMethod.PUT)
    @ResponseBody
    public Object sale(@PathVariable String appid, HttpSession session) {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        Integer appIdInteger = 0;
        try {
            appIdInteger = Integer.parseInt(appid);
        } catch (Exception e) {
            appIdInteger = 0;
        }
        resultMap.put("errorCode", "0");
        resultMap.put("appId", appid);
        if (appIdInteger > 0) {
            try {
                DevUser devUser = (DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION);
                AppInfo appInfo = new AppInfo();
                appInfo.setId(appIdInteger);
                appInfo.setModifyBy(devUser.getId());
                if (appInfoService.appsysUpdateSaleStatusByAppId(appInfo)) {
                    resultMap.put("resultMsg", "success");
                } else {
                    resultMap.put("resultMsg", "success");
                }
            } catch (Exception e) {
                resultMap.put("errorCode", "exception000001");
            }
        } else {
            //errorCode:0为正常
            resultMap.put("errorCode", "param000001");
        }
        return resultMap;
    }

    /**
     * wj
     * 跳转到修改appVersion页面
     */
    @RequestMapping(value = "/appVersionModify", method = RequestMethod.GET)
    public String appVersionModify(@RequestParam("vid") String versionId,
                                   @RequestParam("aid") String appId,
                                   @RequestParam(value = "error", required = false) String fileUploadError,
                                   Model model) {
        AppVersion appVersion = null;
        List<AppVersion> appVersionList = null;
        if (null != fileUploadError && fileUploadError.equals("error1")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_1;
        } else if (null != fileUploadError && fileUploadError.equals("error2")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_2;
        } else if (null != fileUploadError && fileUploadError.equals("error3")) {
            fileUploadError = SystemValues.FILEUPLOAD_ERROR_3;
        }
        try {
            appVersion = appVersionService.getAppVersionById(Integer.parseInt(versionId));
            appVersionList = appVersionService.getAppVersionList(Integer.parseInt(appId));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        model.addAttribute(appVersion);
        model.addAttribute("appVersionList", appVersionList);
        model.addAttribute("fileUploadError", fileUploadError);
        return "developer/appversionmodify";
    }

    /**
     * wj
     * 修改appVersion,并保存信息
     */
    @RequestMapping(value = "/doAppVersionModify", method = RequestMethod.POST)
    public String doAppVersionModify(AppVersion appVersion, HttpSession session, HttpServletRequest request,
                                     @RequestParam(value = "attach", required = false) MultipartFile attach) {

        String downloadLink = null;
        String apkLocPath = null;
        String apkFileName = null;
        if (!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            String oldFileName = attach.getOriginalFilename();//原文件名
            String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
            if (prefix.equalsIgnoreCase("apk")) {//apk文件命名：apk名称+版本号+.apk
                String apkName = null;
                try {
                    apkName = appInfoService.getAppInfo(appVersion.getAppId(), null).getAPKName();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (apkName == null || "".equals(apkName)) {
                    return "redirect:/dev/appMgt/appVersionModify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error1";
                }
                apkFileName = apkName + "-" + appVersion.getVersionNo() + ".apk";
                File targetFile = new File(path, apkFileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "redirect:/dev/appMgt/appVersionModify?vid=" + appVersion.getId()
                            + "&aid=" + appVersion.getAppId()
                            + "&error=error2";
                }
                downloadLink = request.getContextPath() + "/statics/uploadfiles/" + apkFileName;
                apkLocPath = path + File.separator + apkFileName;
            } else {
                return "redirect:/dev/appMgt/appVersionModify?vid=" + appVersion.getId()
                        + "&aid=" + appVersion.getAppId()
                        + "&error=error3";
            }
        }
        appVersion.setModifyBy(((DevUser) session.getAttribute(SystemValues.DEV_USER_SESSION)).getId());
        appVersion.setModifyDate(new Date());
        appVersion.setDownloadLink(downloadLink);
        appVersion.setApkLocPath(apkLocPath);
        appVersion.setApkFileName(apkFileName);
        try {
            if (appVersionService.modify(appVersion)) {
                return "redirect:/dev/appMgt/appInfoList";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "developer/appversionmodify";
    }

    /**
     * 修改操作时，删除文件（logo图片/apk文件），并更新数据库（app_info/app_version）
     */
    @RequestMapping(value = "/delFile", method = RequestMethod.GET)
    @ResponseBody
    public Object delfile(@RequestParam(value = "flag", required = false) String flag,
                          @RequestParam(value = "id", required = false) String id) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        String fileLocPath = null;
        if (flag == null || flag.equals("") ||
                id == null || id.equals("")) {
            resultMap.put("result", "failed");
        } else if (flag.equals("logo")) {//删除logo图片（操作app_info）
            try {
                fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id), null)).getLogoLocPath();
                File file = new File(fileLocPath);
                if (file.exists())
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appInfoService.deleteAppLogo(Integer.parseInt(id))) {//更新表
                            resultMap.put("result", "success");
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (flag.equals("apk")) {//删除apk文件（操作app_version）
            try {
                fileLocPath = (appVersionService.getAppVersionById(Integer.parseInt(id))).getApkLocPath();
                File file = new File(fileLocPath);
                if (file.exists())
                    if (file.delete()) {//删除服务器存储的物理文件
                        if (appVersionService.deleteApkFile(Integer.parseInt(id))) {//更新表
                            resultMap.put("result", "success");
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

}

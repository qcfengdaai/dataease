package io.dataease.system.server;

import io.dataease.api.system.SysParameterApi;
import io.dataease.api.system.request.OnlineMapEditor;
import io.dataease.api.system.request.SQLBotConfigCreator;
import io.dataease.api.system.vo.SQLBotConfigVO;
import io.dataease.api.system.vo.SettingItemVO;
import io.dataease.api.system.vo.ShareBaseVO;
import io.dataease.constant.StaticResourceConstants;
import io.dataease.constant.XpackSettingConstants;
import io.dataease.system.dao.auto.entity.CoreSysSetting;
import io.dataease.system.manage.SysParameterManage;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数服务控制器
 * <p>
 * 提供系统参数管理的 REST API 接口，包括：
 * <ul>
 * <li>基础参数配置查询和保存</li>
 * <li>在线地图配置管理</li>
 * <li>SQL Bot 配置管理</li>
 * <li>国际化配置查询</li>
 * <li>分享配置查询</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/sysParameter")
public class SysParameterServer implements SysParameterApi {

    @Resource
    private SysParameterManage sysParameterManage;

    /**
     * 获取单个参数值
     *
     * @param key 参数键
     * @return 参数值
     */
    @Override
    public String singleVal(String key) {
        return sysParameterManage.singleVal(key);
    }

    /**
     * 保存在线地图配置
     *
     * @param editor 在线地图编辑器配置对象
     */
    @Override
    public void saveOnlineMap(OnlineMapEditor editor) {
        sysParameterManage.saveOnlineMap(editor);
    }

    /**
     * 查询默认在线地图配置
     *
     * @return 在线地图配置对象
     */
    @Override
    public OnlineMapEditor queryOnlineMap() {
        return sysParameterManage.queryOnlineMap(null);
    }

    /**
     * 根据地图类型查询在线地图配置
     *
     * @param type 地图类型
     * @return 在线地图配置对象
     */
    @Override
    public OnlineMapEditor queryOnlineMapByMapType(String type) {
        return sysParameterManage.queryOnlineMap(type);
    }

    /**
     * 查询基础配置列表
     *
     * @return 基础配置项列表
     */
    @Override
    public List<SettingItemVO> queryBasicSetting() {
        String key = "basic.";
        List<CoreSysSetting> coreSysSettings = sysParameterManage.groupList(key);
        return sysParameterManage.convert(coreSysSettings);
    }

    /**
     * 保存基础配置
     *
     * @param settingItemVOS 配置项列表
     */
    @Override
    public void saveBasicSetting(List<SettingItemVO> settingItemVOS) {
        sysParameterManage.saveBasic(settingItemVOS);
    }

    /**
     * 获取请求超时时间
     *
     * @return 超时时间（秒）
     */
    @Override
    public Integer RequestTimeOut() {
        int frontTimeOut = 60;
        List<SettingItemVO> settingItemVOS = queryBasicSetting();
        for (SettingItemVO settingItemVO : settingItemVOS) {
            if (StringUtils.isNotBlank(settingItemVO.getPkey()) && settingItemVO.getPkey().equalsIgnoreCase(XpackSettingConstants.Front_Time_Out) && StringUtils.isNotBlank(settingItemVO.getPval())) {
                frontTimeOut = Integer.parseInt(settingItemVO.getPval());
            }
        }
        return frontTimeOut;
    }

    /**
     * 获取默认设置
     *
     * @return 默认设置 Map
     */
    @Override
    public Map<String, Object> defaultSettings() {
        Map<String, Object> map = new HashMap<>();
        map.put(XpackSettingConstants.DEFAULT_SORT, "1");

        List<SettingItemVO> settingItemVOS = queryBasicSetting();
        for (SettingItemVO settingItemVO : settingItemVOS) {
            if (StringUtils.isNotBlank(settingItemVO.getPkey()) && settingItemVO.getPkey().equalsIgnoreCase(XpackSettingConstants.DEFAULT_SORT) && StringUtils.isNotBlank(settingItemVO.getPval())) {
                map.put(XpackSettingConstants.DEFAULT_SORT, settingItemVO.getPval());
            }
            if (StringUtils.isNotBlank(settingItemVO.getPkey()) && settingItemVO.getPkey().equalsIgnoreCase(XpackSettingConstants.DEFAULT_OPEN) && StringUtils.isNotBlank(settingItemVO.getPval())) {
                map.put(XpackSettingConstants.DEFAULT_OPEN, settingItemVO.getPval());
            }
        }
        return map;
    }

    /**
     * 获取 UI 显示的配置列表
     *
     * @return 配置项列表
     */
    @Override
    public List<Object> ui() {
        return sysParameterManage.getUiList();
    }

    /**
     * 获取默认登录方式
     *
     * @return 默认登录方式
     */
    @Override
    public Integer defaultLogin() {
        return sysParameterManage.defaultLogin();
    }

    /**
     * 获取分享基础配置
     *
     * @return 分享基础配置对象
     */
    @Override
    public ShareBaseVO shareBase() {
        return sysParameterManage.shareBase();
    }

    /**
     * 获取国际化语言选项
     * <p>
     * 扫描国际化目录，返回可用的语言包列表
     * </p>
     *
     * @return 语言代码与语言名称的映射 Map，不存在时返回 null
     */
    @Override
    public Map<String, String> i18nOptions() {
        File dir = new File(StaticResourceConstants.I18N_DIR);
        File[] files = null;
        if (!dir.exists() || ObjectUtils.isEmpty(files = dir.listFiles())) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (File file : files) {
            String name = file.getName();
            int start = name.indexOf("custom_") + 7;
            int end = name.indexOf("front");
            String i18nName = name.substring(start, end - 1).replace("_", "-");
            String languageName = name.substring(end + 6, name.lastIndexOf("."));
            result.put(i18nName, languageName);
        }
        return result;
    }

    /**
     * 查询 SQL Bot 配置
     *
     * @return SQL Bot 配置对象，不存在时返回 null
     */
    @Override
    public SQLBotConfigVO sqlBotConfig() {
        String key = "sqlbot.";
        List<CoreSysSetting> coreSysSettings = sysParameterManage.groupList(key);
        if (CollectionUtils.isNotEmpty(coreSysSettings)) {
            SQLBotConfigVO vo = new SQLBotConfigVO();
            coreSysSettings.forEach(sysSetting -> {
                if (sysSetting.getPkey().equalsIgnoreCase(key + "domain")) {
                    vo.setDomain(sysSetting.getPval());
                } else if (sysSetting.getPkey().equalsIgnoreCase(key + "id")) {
                    vo.setId(sysSetting.getPval());
                } else if (sysSetting.getPkey().equalsIgnoreCase(key + "enabled")) {
                    vo.setEnabled(StringUtils.isNotBlank(sysSetting.getPval()) && StringUtils.equalsIgnoreCase(sysSetting.getPval(), "true"));
                } else if (sysSetting.getPkey().equalsIgnoreCase(key + "valid")) {
                    vo.setValid(StringUtils.isNotBlank(sysSetting.getPval()) && StringUtils.equalsIgnoreCase(sysSetting.getPval(), "true"));
                }
            });
            return vo;
        }
        return null;
    }

    /**
     * 保存 SQL Bot 配置
     *
     * @param creator SQL Bot 配置对象
     */
    @Override
    public void saveSqlBotConfig(SQLBotConfigCreator creator) {
        sysParameterManage.saveSqlBotConfig(creator);
    }
}

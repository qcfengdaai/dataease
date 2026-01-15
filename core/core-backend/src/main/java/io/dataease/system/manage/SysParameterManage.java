package io.dataease.system.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.system.request.OnlineMapEditor;
import io.dataease.api.system.request.SQLBotConfigCreator;
import io.dataease.api.system.vo.SettingItemVO;
import io.dataease.api.system.vo.ShareBaseVO;
import io.dataease.datasource.server.DatasourceServer;
import io.dataease.license.config.XpackInteract;
import io.dataease.system.dao.auto.entity.CoreSysSetting;
import io.dataease.system.dao.auto.mapper.CoreSysSettingMapper;
import io.dataease.system.dao.ext.mapper.ExtCoreSysSettingMapper;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.IDUtils;
import io.dataease.utils.SystemSettingUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统参数管理类
 * <p>
 * 提供系统参数的统一管理功能，包括：
 * <ul>
 * <li>基础参数配置（basic.*）</li>
 * <li>在线地图配置（map.*）</li>
 * <li>SQL Bot 配置（sqlbot.*）</li>
 * <li>演示提示配置</li>
 * </ul>
 * </p>
 * <p>
 * 支持 Xpack 扩展点，企业版可扩展更多参数类型
 * </p>
 */
@Component
public class SysParameterManage {

    /**
     * 是否显示演示提示
     */
    @Value("${dataease.show-demo-tips:false}")
    private boolean showDemoTips;

    /**
     * 演示提示内容
     */
    @Value("${dataease.demo-tips-content:#{null}}")
    private String demoTipsContent;

    /**
     * 地图配置键前缀
     */
    private static final String MAP_KEY_PREFIX = "map.";

    @Resource
    private CoreSysSettingMapper coreSysSettingMapper;

    @Resource
    private ExtCoreSysSettingMapper extCoreSysSettingMapper;
    @Resource
    private DatasourceServer datasourceServer;

    /**
     * 获取单个参数值
     * <p>
     * 根据参数键从数据库查询对应的参数值
     * </p>
     *
     * @param key 参数键
     * @return 参数值，不存在时返回 null
     */
    public String singleVal(String key) {
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pkey", key);
        CoreSysSetting sysSetting = coreSysSettingMapper.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(sysSetting)) {
            return sysSetting.getPval();
        }
        return null;
    }

    /**
     * 查询在线地图配置
     * <p>
     * 支持高德地图、百度地图等多种地图类型的配置查询
     * 如果未指定地图类型，则使用系统默认类型
     * </p>
     *
     * @param mapType 地图类型（如：gaode、baidu），为空则使用默认
     * @return 在线地图编辑器配置对象
     */
    public OnlineMapEditor queryOnlineMap(String mapType) {
        // 如果未指定地图类型，查询系统默认类型
        if (StringUtils.isBlank(mapType)) {
            List<CoreSysSetting> typeList = groupList(MAP_KEY_PREFIX + "mapType");
            mapType = "gaode";
            if (!CollectionUtils.isEmpty(typeList)) {
                mapType = typeList.getFirst().getPval();
            }
        }
        String prefix;
        if (!StringUtils.equals(mapType, "gaode")) {
            prefix = mapType + "." + MAP_KEY_PREFIX;
        } else {
            prefix = MAP_KEY_PREFIX;
        }
        // 创建编辑器对象
        var editor = new OnlineMapEditor();
        // 获取编辑器所有字段名称
        List<String> fields = BeanUtils.getFieldNames(OnlineMapEditor.class);
        // 根据前缀批量获取配置值
        Map<String, String> mapVal = groupVal(prefix);
        // 遍历字段并设置对应的配置值
        fields.forEach(field -> {
            String val = mapVal.get(prefix + field);
            if (StringUtils.isNotBlank(val)) {
                BeanUtils.setFieldValueByName(editor, field, val, String.class);
            }
        });

        // 设置地图类型
        editor.setMapType(mapType);

        return editor;
    }

    /**
     * 保存在线地图配置
     * <p>
     * 将地图配置信息保存到数据库，支持新增和更新操作
     * 配置键使用统一的格式：{mapType}.map.{fieldName}
     * </p>
     *
     * @param editor 在线地图编辑器配置对象
     */
    public void saveOnlineMap(OnlineMapEditor editor) {
        String mapType = editor.getMapType();
        // 如果未指定地图类型，查询系统默认类型
        if (StringUtils.isBlank(mapType)) {
            List<CoreSysSetting> typeList = groupList(MAP_KEY_PREFIX + "mapType");
            mapType = "gaode";
            if (!CollectionUtils.isEmpty(typeList)) {
                mapType = typeList.getFirst().getPval();
            }
        }

        // 获取编辑器所有字段名称
        List<String> fieldNames = BeanUtils.getFieldNames(OnlineMapEditor.class);
        String finalMapType = mapType;
        // 遍历所有字段并保存配置
        fieldNames.forEach(field -> {
            // 确定配置键前缀，非高德地图需要加上地图类型前缀
            String prefix = MAP_KEY_PREFIX;
            if (!(StringUtils.equals(field, "mapType") || StringUtils.equals(finalMapType, "gaode"))) {
                prefix = finalMapType + "." + MAP_KEY_PREFIX;
            }

            // 查询现有配置
            QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pkey", prefix + field);
            CoreSysSetting sysSetting = coreSysSettingMapper.selectOne(queryWrapper);
            // 获取字段值
            var val = (String) BeanUtils.getFieldValueByName(field, editor);
            // 如果配置不存在，则新增
            if (ObjectUtils.isEmpty(sysSetting)) {
                sysSetting = new CoreSysSetting();
                sysSetting.setId(IDUtils.snowID());
                sysSetting.setPkey(prefix + field);
                sysSetting.setPval(val == null ? "" : val);
                sysSetting.setType("text");
                sysSetting.setSort(1);
                coreSysSettingMapper.insert(sysSetting);
                return;
            }
            // 如果配置存在，则更新
            sysSetting.setPval(val);
            coreSysSettingMapper.updateById(sysSetting);
        });
    }


    /**
     * 批量获取参数组配置
     * <p>
     * 根据配置键前缀批量查询配置项，返回键值对 Map
     * </p>
     *
     * @param groupKey 配置组键前缀（如：basic.、map.）
     * @return 配置键值对 Map
     */
    public Map<String, String> groupVal(String groupKey) {
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        // 右模糊匹配，查询以指定前缀开头的所有配置
        queryWrapper.likeRight("pkey", groupKey);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        List<CoreSysSetting> sysSettings = coreSysSettingMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(sysSettings)) {
            // 转换为 Map 返回
            return sysSettings.stream().collect(Collectors.toMap(CoreSysSetting::getPkey, CoreSysSetting::getPval));
        }
        return new HashMap<>();
    }

    /**
     * 批量获取参数组配置列表
     * <p>
     * 根据配置键前缀批量查询配置项，返回完整的配置对象列表
     * </p>
     *
     * @param groupKey 配置组键前缀（如：basic.、map.）
     * @return 配置对象列表
     */
    public List<CoreSysSetting> groupList(String groupKey) {
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        // 右模糊匹配，查询以指定前缀开头的所有配置
        queryWrapper.likeRight("pkey", groupKey);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        return coreSysSettingMapper.selectList(queryWrapper);
    }

    /**
     * 转换系统设置为视图对象
     * <p>
     * 将数据库实体对象转换为前端展示用的视图对象
     * 支持 Xpack 扩展点，企业版可添加额外的转换逻辑
     * </p>
     *
     * @param sysSettings 系统设置实体列表
     * @return 设置项视图对象列表
     */
    @XpackInteract(value = "perSetting")
    public List<SettingItemVO> convert(List<CoreSysSetting> sysSettings) {
        // 按排序字段排序后转换为视图对象
        return sysSettings.stream().sorted(Comparator.comparing(CoreSysSetting::getSort)).map(item -> BeanUtils.copyBean(new SettingItemVO(), item)).toList();
    }

    /**
     * 获取UI显示的配置列表
     * <p>
     * 返回需要在界面上显示的系统配置项
     * 企业版可通过 Xpack 扩展点添加更多配置项
     * </p>
     *
     * @return 配置项列表
     */
    @XpackInteract(value = "perSetting", replace = true)
    public List<Object> getUiList() {
        List<Object> result = new ArrayList<>();
        // 添加社区版标识
        result.add(buildSettingItem("community", true));
        // 添加演示提示相关配置
        result.add(buildSettingItem("showDemoTips", showDemoTips));
        result.add(buildSettingItem("demoTipsContent", demoTipsContent));
        return result;
    }

    /**
     * 获取默认登录方式
     * <p>
     * 返回系统默认的登录方式
     * 企业版可通过 Xpack 扩展点修改默认登录方式
     * </p>
     *
     * @return 默认登录方式，社区版返回 0
     */
    @XpackInteract(value = "perSetting", replace = true)
    public Integer defaultLogin() {
        return 0;
    }

    /**
     * 构建设置项
     * <p>
     * 创建包含键值对的设置项 Map
     * </p>
     *
     * @param pkey 配置键
     * @param pval 配置值
     * @return 设置项 Map
     */
    private Map<String, Object> buildSettingItem(String pkey, Object pval) {
        Map<String, Object> item = new HashMap<>();
        item.put("pkey", pkey);
        item.put("pval", pval);
        return item;
    }


    /**
     * 批量保存参数组配置
     * <p>
     * 保存一组配置项，会先删除相同键的旧配置，然后批量插入新配置
     * 过滤掉企业版专属配置项，只保存社区版支持的配置
     * </p>
     *
     * @param vos 配置项视图对象列表
     * @param groupKey 配置组键前缀
     */
    @Transactional
    public void saveGroup(List<SettingItemVO> vos, String groupKey) {
        // 过滤掉企业版专属配置，并转换为实体对象
        List<CoreSysSetting> sysSettings = vos.stream().filter(vo -> !SystemSettingUtils.xpackSetting(vo.getPkey())).map(item -> {
            CoreSysSetting sysSetting = BeanUtils.copyBean(new CoreSysSetting(), item);
            sysSetting.setId(IDUtils.snowID());
            return sysSetting;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sysSettings)) {
            QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
            // 删除旧配置
            sysSettings.forEach(sysSetting -> {
                queryWrapper.clear();
                queryWrapper.eq("pkey", sysSetting.getPkey());
                coreSysSettingMapper.delete(queryWrapper);
            });
            // 批量插入新配置
            extCoreSysSettingMapper.saveBatch(sysSettings);
        }
        // 触发数据源相关任务
        datasourceServer.addJob(sysSettings);
    }

    /**
     * 保存 SQL Bot 配置
     * <p>
     * 保存 SQL Bot 的相关配置信息，包括：
     * <ul>
     * <li>domain: 服务域名</li>
     * <li>id: 服务ID</li>
     * <li>enabled: 是否启用</li>
     * <li>valid: 是否有效</li>
     * </ul>
     * </p>
     *
     * @param configVO SQL Bot 配置对象
     */
    public void saveSqlBotConfig(SQLBotConfigCreator configVO) {
        List<CoreSysSetting> configList = new ArrayList<>();
        String key = "sqlbot.";

        // 构建域名配置
        CoreSysSetting domainVo = new CoreSysSetting();
        domainVo.setPkey(key + "domain");
        domainVo.setPval(configVO.getDomain());
        domainVo.setType("text");
        domainVo.setSort(0);
        domainVo.setId(IDUtils.snowID());
        configList.add(domainVo);

        // 构建ID配置
        CoreSysSetting idVo = new CoreSysSetting();
        idVo.setPkey(key + "id");
        idVo.setPval(configVO.getId());
        idVo.setType("text");
        idVo.setSort(0);
        idVo.setId(IDUtils.snowID());
        configList.add(idVo);

        // 构建启用状态配置
        CoreSysSetting enabledVo = new CoreSysSetting();
        enabledVo.setPkey(key + "enabled");
        enabledVo.setPval(configVO.getEnabled().toString());
        enabledVo.setType("text");
        enabledVo.setSort(0);
        enabledVo.setId(IDUtils.snowID());
        configList.add(enabledVo);

        // 构建有效性配置
        CoreSysSetting validVo = new CoreSysSetting();
        validVo.setPkey(key + "valid");
        validVo.setPval(configVO.getValid().toString());
        validVo.setType("text");
        validVo.setSort(0);
        validVo.setId(IDUtils.snowID());
        configList.add(validVo);

        // 删除旧的 SQL Bot 配置
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("pkey", key);
        coreSysSettingMapper.delete(queryWrapper);

        // 批量保存新配置
        extCoreSysSettingMapper.saveBatch(configList);
    }

    /**
     * 保存基础配置
     * <p>
     * 保存系统基础配置参数，通过代理调用确保事务正确生效
     * 支持 Xpack 扩展点，企业版可添加额外的保存逻辑
     * </p>
     *
     * @param vos 配置项视图对象列表
     */
    @XpackInteract(value = "perSetting", before = false)
    @Transactional
    public void saveBasic(List<SettingItemVO> vos) {
        String key = "basic.";
        // 通过代理调用，确保事务和 Xpack 扩展点正常工作
        proxy().saveGroup(vos, key);
    }

    /**
     * 获取当前类的代理对象
     * <p>
     * 用于确保事务和 AOP 扩展点正常工作
     * </p>
     *
     * @return 当前类的代理对象
     */
    private SysParameterManage proxy() {
        return CommonBeanFactory.getBean(SysParameterManage.class);
    }

    /**
     * 获取分享基础配置
     * <p>
     * 查询分享相关的配置信息，包括是否禁用分享和是否需要密码
     * </p>
     *
     * @return 分享基础配置对象
     */
    public ShareBaseVO shareBase() {
        // 查询是否禁用分享
        String disableText = singleVal("basic.shareDisable");
        // 查询是否需要密码
        String requireText = singleVal("basic.sharePeRequire");
        ShareBaseVO vo = new ShareBaseVO();
        // 设置禁用状态
        if (StringUtils.isNotBlank(disableText) && StringUtils.equals("true", disableText)) {
            vo.setDisable(true);
        }
        // 设置密码要求状态
        if (StringUtils.isNotBlank(requireText) && StringUtils.equals("true", requireText)) {
            vo.setPeRequire(true);
        }
        return vo;
    }

    /**
     * 插入系统设置
     * <p>
     * 向数据库插入单条系统设置记录
     * </p>
     *
     * @param coreSysSetting 系统设置对象
     */
    public void insert(CoreSysSetting coreSysSetting) {
        coreSysSettingMapper.insert(coreSysSetting);
    }

}

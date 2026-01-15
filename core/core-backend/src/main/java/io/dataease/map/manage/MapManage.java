package io.dataease.map.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.map.dto.GeometryNodeCreator;
import io.dataease.api.map.vo.AreaNode;
import io.dataease.api.map.vo.CustomGeoArea;
import io.dataease.api.map.vo.CustomGeoSubArea;
import io.dataease.constant.StaticResourceConstants;
import io.dataease.exception.DEException;
import io.dataease.i18n.Translator;
import io.dataease.map.bo.AreaBO;
import io.dataease.map.dao.auto.entity.Area;
import io.dataease.map.dao.auto.entity.CoreCustomGeoArea;
import io.dataease.map.dao.auto.entity.CoreCustomGeoSubArea;
import io.dataease.map.dao.auto.mapper.AreaMapper;
import io.dataease.map.dao.auto.mapper.CoreCustomGeoAreaMapper;
import io.dataease.map.dao.auto.mapper.CoreCustomGeoSubAreaMapper;
import io.dataease.map.dao.ext.entity.CoreAreaCustom;
import io.dataease.map.dao.ext.mapper.CoreAreaCustomMapper;
import io.dataease.utils.*;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static io.dataease.constant.CacheConstant.CommonCacheConstant.CUSTOM_GEO_CACHE;
import static io.dataease.constant.CacheConstant.CommonCacheConstant.WORLD_MAP_CACHE;

/**
 * 地图管理类
 *
 * 功能描述：
 * 1. 管理世界地图区域树的构建和查询
 * 2. 支持自定义地理区域的上传、删除和查询
 * 3. 管理自定义地理区域及其子区域
 * 4. 提供地图数据的缓存管理
 *
 * 使用场景：
 * - BI系统中地图可视化组件的数据源管理
 * - 支持用户上传自定义地理区域进行数据分析
 * - 提供标准的世界地图层级结构（洲-国家-省-市-区）
 *
 * @author DataEase
 * @since 2023-07-09
 */
@Component
public class MapManage {
    private final static AreaNode WORLD;

    private static final String GEO_PREFIX = "geo_";

    static {
        WORLD = AreaNode.builder()
                .id("000")
                .level("world")
                .name("世界村")
                .build();
    }

    /**
     * 系统区域数据访问接口
     */
    @Resource
    private AreaMapper areaMapper;

    /**
     * 自定义地理区域数据访问接口
     */
    @Resource
    private CoreCustomGeoAreaMapper coreCustomGeoAreaMapper;

    /**
     * 自定义地理子区域数据访问接口
     */
    @Resource
    private CoreCustomGeoSubAreaMapper coreCustomGeoSubAreaMapper;

    /**
     * 自定义区域扩展数据访问接口
     */
    @Resource
    private CoreAreaCustomMapper coreAreaCustomMapper;

    /**
     * 获取所有默认区域列表
     * 从数据库查询系统内置的地理区域
     *
     * @return 所有默认区域列表
     */
    public List<Area> defaultArea() {
        return areaMapper.selectList(null);
    }

    /**
     * 获取当前类的代理对象
     * 用于解决Spring AOP代理导致的缓存失效问题
     *
     * @return 当前类的代理对象
     */
    private MapManage proxy() {
        return CommonBeanFactory.getBean(MapManage.class);
    }

    /**
     * 获取世界地图区域树
     * 构建包含系统区域和自定义区域的完整地图层级结构
     *
     * 实现逻辑：
     * 1. 查询所有系统内置区域
     * 2. 查询所有自定义区域并标记
     * 3. 将自定义区域合并到系统区域列表
     * 4. 构建父子关系的树形结构
     * 5. 返回以"世界村"为根节点的完整树
     *
     * @return 世界地图树根节点
     */
    @Cacheable(value = WORLD_MAP_CACHE, key = "'world_map'")
    public AreaNode getWorldTree() {
        // 1. 获取所有系统内置区域并转换为业务对象
        List<Area> areas = proxy().defaultArea();
        List<AreaBO> areaBOS = areas.stream().map(item -> BeanUtils.copyBean(new AreaBO(), item)).collect(Collectors.toList());

        // 2. 查询所有自定义区域并标记为自定义类型
        List<CoreAreaCustom> coreAreaCustoms = coreAreaCustomMapper.selectList(null);
        if (CollectionUtils.isNotEmpty(coreAreaCustoms)) {
            List<AreaBO> customBoList = coreAreaCustoms.stream().map(item -> {
                AreaBO areaBO = BeanUtils.copyBean(new AreaBO(), item);
                areaBO.setCustom(true);
                return areaBO;
            }).toList();
            areaBOS.addAll(customBoList);
        }

        // 3. 初始化世界根节点
        WORLD.setChildren(new ArrayList<>());

        // 4. 构建区域节点的映射表，key为区域ID，value为区域节点
        var areaNodeMap = new HashMap<String, AreaNode>();
        areaNodeMap.put(WORLD.getId(), WORLD);

        // 5. 遍历所有区域，构建树形结构
        areaBOS.forEach(area -> {
            // 获取或创建当前区域节点
            var node = areaNodeMap.get(area.getId());
            if (node == null) {
                node = AreaNode.builder().build();
                BeanUtils.copyBean(node, area);
                areaNodeMap.put(area.getId(), node);
            } else {
                BeanUtils.copyBean(node, area);
            }

            // 获取或创建父节点
            var pNode = areaNodeMap.get(area.getPid());
            if (pNode == null) {
                // 父节点不存在，创建新的父节点
                var child = new ArrayList<AreaNode>();
                child.add(node);
                pNode = AreaNode.builder()
                        .children(child)
                        .id(area.getPid())
                        .build();
                areaNodeMap.put(area.getPid(), pNode);
            } else {
                // 父节点存在，将当前节点添加到父节点的子节点列表
                if (pNode.getChildren() == null) {
                    pNode.setChildren(new ArrayList<>());
                }
                pNode.getChildren().add(node);
            }
        });

        return WORLD;
    }

    /**
     * 保存自定义地图地理区域
     * 支持上传GeoJSON格式的地理边界文件
     *
     * 业务流程：
     * 1. 验证区域编码的合法性
     * 2. 验证上传文件是否为JSON格式
     * 3. 检查区域编码是否已存在（系统区域和自定义区域）
     * 4. 保存区域信息到数据库
     * 5. 保存GeoJSON文件到文件系统
     * 6. 清除地图缓存
     *
     * @param request 地理区域创建请求，包含编码、名称、父节点ID等信息
     * @param file GeoJSON格式的地理边界文件
     * @throws DEException 当编码已存在、文件格式错误或保存失败时抛出异常
     */
    @CacheEvict(cacheNames = WORLD_MAP_CACHE, key = "'world_map'")
    @Transactional
    public void saveMapGeo(GeometryNodeCreator request, MultipartFile file) {
        // 1. 验证区域编码格式
        validateCode(request.getCode());

        // 2. 验证文件是否存在
        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            DEException.throwException("geometry file is require");
        }

        // 3. 验证文件格式是否为JSON
        String suffix = FileUtils.getExtensionName(file.getOriginalFilename());
        if (!StringUtils.equalsIgnoreCase("json", suffix)) {
            DEException.throwException("仅支持json格式文件");
        }

        // 4. 检查系统内置区域中是否已存在该编码
        List<Area> areas = proxy().defaultArea();
        String code = getBusiGeoCode(request.getCode());

        AtomicReference<String> atomicReference = new AtomicReference<>();
        if (areas.stream().anyMatch(area -> {
            boolean exist = area.getId().equals(code);
            if (exist) {
                atomicReference.set(area.getName());
            }
            return exist;
        })) {
            DEException.throwException(String.format("Area code [%s] is already exists for [%s]", code, atomicReference.get()));
        }

        // 5. 检查自定义区域中是否已存在该编码
        CoreAreaCustom originData = null;
        if (ObjectUtils.isNotEmpty(originData = coreAreaCustomMapper.selectById(getDaoGeoCode(code)))) {
            DEException.throwException(String.format("Area code [%s] is already exists for [%s]", code, originData.getName()));
        }

        // 6. 保存区域信息到数据库
        CoreAreaCustom coreAreaCustom = new CoreAreaCustom();
        coreAreaCustom.setId(getDaoGeoCode(code));
        coreAreaCustom.setPid(request.getPid());
        coreAreaCustom.setName(request.getName());
        coreAreaCustomMapper.insert(coreAreaCustom);

        // 7. 保存GeoJSON文件到文件系统
        File geoFile = buildGeoFile(code);
        try {
            file.transferTo(geoFile);
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
            DEException.throwException(e);
        }
    }

    /**
     * 删除自定义地理区域
     * 删除指定区域及其所有子区域的数据库记录和文件
     *
     * 业务流程：
     * 1. 验证区域编码
     * 2. 检查是否为系统内置区域（禁止删除）
     * 3. 查询区域是否存在
     * 4. 递归获取所有子区域ID
     * 5. 批量删除数据库记录
     * 6. 删除对应的GeoJSON文件
     * 7. 清除地图缓存
     *
     * @param code 要删除的区域编码
     * @throws DEException 当删除系统区域或区域不存在时抛出异常
     */
    @CacheEvict(cacheNames = WORLD_MAP_CACHE, key = "'world_map'")
    @Transactional
    public void deleteGeo(String code) {
        // 1. 验证区域编码格式
        validateCode(code);

        // 2. 禁止删除系统内置区域（以geo_开头的为自定义区域）
        if (!StringUtils.startsWith(code, GEO_PREFIX)) {
            DEException.throwException("内置Geometry，禁止删除");
        }

        // 3. 检查区域是否存在
        CoreAreaCustom coreAreaCustom = coreAreaCustomMapper.selectById(code);
        if (ObjectUtils.isEmpty(coreAreaCustom)) {
            DEException.throwException("Geometry code 不存在！");
        }

        // 4. 递归获取所有子区域ID列表
        List<String> codeResultList = new ArrayList<>();
        codeResultList.add(code);
        childTreeIdList(List.of(code), codeResultList);

        // 5. 批量删除数据库记录
        coreAreaCustomMapper.deleteBatchIds(codeResultList);

        // 6. 删除对应的GeoJSON文件
        codeResultList.forEach(id -> {
            File file = buildGeoFile(id);
            if (file.exists()) {
                file.delete();
            }
        });
    }

    /**
     * 查询所有自定义地理区域列表
     * 从数据库获取所有用户创建的地理区域
     *
     * @return 自定义地理区域列表，使用缓存提高性能
     */
    @Cacheable(value = CUSTOM_GEO_CACHE, key = "'custom_geo_area'")
    public List<CustomGeoArea> listCustomGeoArea() {
        return coreCustomGeoAreaMapper.selectList(null).stream().map(o -> BeanUtils.copyBean(new CustomGeoArea(), o)).toList();
    }

    /**
     * 根据区域ID查询该区域的所有子区域
     * 用于获取某个自定义地理区域下的详细分区信息
     *
     * @param areaId 自定义地理区域ID
     * @return 该区域的子区域列表
     */
    public List<CustomGeoSubArea> getCustomGeoArea(String areaId) {
        var query = new QueryWrapper<CoreCustomGeoSubArea>();
        query.eq("geo_area_id", areaId);
        return coreCustomGeoSubAreaMapper.selectList(query).stream().map(o -> BeanUtils.copyBean(new CustomGeoSubArea(), o)).toList();
    }

    /**
     * 删除自定义地理区域
     * 删除指定区域及其所有子区域
     *
     * @param areaId 要删除的区域ID
     */
    @CacheEvict(cacheNames = CUSTOM_GEO_CACHE, key = "'custom_geo_area'")
    @Transactional
    public void deleteCustomGeoArea(String areaId) {
        // 删除区域记录
        coreCustomGeoAreaMapper.deleteById(areaId);
        // 删除该区域下的所有子区域
        var q = new QueryWrapper<CoreCustomGeoSubArea>();
        q.eq("geo_area_id", areaId);
        coreCustomGeoSubAreaMapper.delete(q);
    }

    /**
     * 保存或更新自定义地理区域
     * 支持新增和编辑操作
     *
     * 业务逻辑：
     * 1. 检查区域名称是否已存在（同一名称不能重复）
     * 2. 如果是新增，生成新的区域ID
     * 3. 如果是更新，使用现有ID
     * 4. 保存或更新数据库记录
     * 5. 清除缓存
     *
     * @param geoArea 要保存的自定义地理区域对象
     * @throws DEException 当区域名称已存在时抛出异常
     */
    @CacheEvict(cacheNames = CUSTOM_GEO_CACHE, key = "'custom_geo_area'")
    @Transactional
    public void saveCustomGeoArea(CustomGeoArea geoArea) {
        var coreCustomGeoArea = new CoreCustomGeoArea();
        BeanUtils.copyBean(coreCustomGeoArea, geoArea);

        // 检查名称是否重复
        var q = new QueryWrapper<CoreCustomGeoArea>();
        q.eq("name", geoArea.getName());
        if (StringUtils.isNotBlank(coreCustomGeoArea.getId())) {
            q.ne("id", coreCustomGeoArea.getId());
        }
        var list = coreCustomGeoAreaMapper.selectList(q);
        if (CollectionUtils.isNotEmpty(list)) {
            DEException.throwException(Translator.get("i18n_geo_exists"));
            return;
        }

        // 新增或更新
        if (ObjectUtils.isEmpty(coreCustomGeoArea.getId())) {
            // 新增：生成自定义ID
            coreCustomGeoArea.setId("custom_" + IDUtils.snowID());
            coreCustomGeoAreaMapper.insert(coreCustomGeoArea);
        } else {
            // 更新：使用现有ID
            coreCustomGeoAreaMapper.updateById(coreCustomGeoArea);
        }
    }

    /**
     * 删除自定义地理子区域
     *
     * @param areaId 要删除的子区域ID
     */
    @Transactional
    public void deleteCustomGeoSubArea(long areaId) {
        coreCustomGeoSubAreaMapper.deleteById(areaId);
    }

    /**
     * 保存或更新自定义地理子区域
     * 支持新增和编辑操作
     *
     * 业务逻辑：
     * 1. 检查在同一父区域下，子区域名称是否已存在
     * 2. 如果是新增，生成新的子区域ID
     * 3. 如果是更新，使用现有ID
     * 4. 保存或更新数据库记录
     *
     * @param customGeoSubArea 要保存的自定义地理子区域对象
     * @throws DEException 当子区域名称在同一父区域下已存在时抛出异常
     */
    @Transactional
    public void saveCustomGeoSubArea(CustomGeoSubArea customGeoSubArea) {
        var geoSubArea = new CoreCustomGeoSubArea();
        BeanUtils.copyBean(geoSubArea, customGeoSubArea);

        // 检查在同一父区域下，名称是否重复
        var q = new QueryWrapper<CoreCustomGeoSubArea>();
        q.eq("name", customGeoSubArea.getName());
        q.eq("geo_area_id", customGeoSubArea.getGeoAreaId());
        if (ObjectUtils.isNotEmpty(customGeoSubArea.getId())) {
            q.ne("id", customGeoSubArea.getId());
        }
        var list = coreCustomGeoSubAreaMapper.selectList(q);
        if (CollectionUtils.isNotEmpty(list)) {
            DEException.throwException(Translator.get("i18n_geo_sub_exists"));
            return;
        }

        // 新增或更新
        if (ObjectUtils.isEmpty(geoSubArea.getId())) {
            // 新增：生成ID
            geoSubArea.setId(IDUtils.snowID());
            coreCustomGeoSubAreaMapper.insert(geoSubArea);
        } else {
            // 更新：使用现有ID
            coreCustomGeoSubAreaMapper.updateById(geoSubArea);
        }
    }

    /**
     * 获取自定义地理子区域的选项列表
     * 用于前端下拉选择框的数据源
     * 查询父节点ID为"156"（中国）的所有子区域
     *
     * @return 中国的省级区域节点列表
     */
    public List<AreaNode> getCustomGeoSubAreaOptions() {
        var q = new QueryWrapper<Area>();
        q.eq("pid", "156"); // 156是中国的ID
        return areaMapper.selectList(q).stream().map(a -> BeanUtils.copyBean(AreaNode.builder().build(), a)).toList();
    }

    /**
     * 递归获取子树的所有节点ID
     * 用于删除区域时获取所有子孙节点的ID列表
     *
     * @param pidList 父节点ID列表
     * @param resultList 结果列表，用于存储所有找到的节点ID（传入时已包含父节点）
     */
    public void childTreeIdList(List<String> pidList, List<String> resultList) {
        // 查询所有父节点在pidList中的子节点
        QueryWrapper<CoreAreaCustom> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pid", pidList);
        List<CoreAreaCustom> coreAreaCustoms = coreAreaCustomMapper.selectList(queryWrapper);

        // 如果存在子节点，递归查找更深层的子节点
        if (CollectionUtils.isNotEmpty(coreAreaCustoms)) {
            List<String> codeList = coreAreaCustoms.stream().map(CoreAreaCustom::getId).toList();
            resultList.addAll(codeList);
            childTreeIdList(codeList, resultList);
        }
    }

    /**
     * 获取数据库存储用的地理区域编码
     * 如果编码没有geo_前缀，则添加该前缀
     *
     * @param code 业务层使用的区域编码
     * @return 数据库层使用的区域编码（带geo_前缀）
     */
    private String getDaoGeoCode(String code) {
        return StringUtils.startsWith(code, GEO_PREFIX) ? code : (GEO_PREFIX + code);
    }

    /**
     * 获取业务层使用的地理区域编码
     * 如果编码有geo_前缀，则去掉该前缀
     *
     * @param code 数据库层使用的区域编码
     * @return 业务层使用的区域编码（不带geo_前缀）
     */
    private String getBusiGeoCode(String code) {
        return StringUtils.startsWith(code, GEO_PREFIX) ? code.substring(GEO_PREFIX.length()) : code;
    }

    /**
     * 构建GeoJSON文件的存储路径
     * 根据区域编码生成文件系统的完整路径
     *
     * 文件组织结构：CUSTOM_MAP_DIR/国家代码前3位/区域编码.json
     * 例如：/custom-map/156/156110000.json（北京市）
     *
     * @param code 区域编码
     * @return GeoJSON文件对象
     */
    private File buildGeoFile(String code) {
        // 获取不带前缀的区域编码
        String id = getBusiGeoCode(code);
        String customMapDir = StaticResourceConstants.CUSTOM_MAP_DIR;

        // 提取国家代码（前3位）用于创建子目录
        String countryCode = countryCode(id);
        String fileDirPath = customMapDir + "/" + countryCode + "/";

        // 如果目录不存在，创建目录
        File dir = new File(fileDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 构建完整的文件路径
        String filePath = fileDirPath + id + ".json";
        return new File(filePath);
    }

    /**
     * 从区域编码中提取国家代码
     * 取区域编码的前3位作为国家代码
     *
     * @param code 区域编码
     * @return 国家代码（前3位）
     */
    private String countryCode(String code) {
        return code.substring(0, 3);
    }

    /**
     * 验证区域编码的合法性
     * 检查编码是否为空且是否为纯数字
     *
     * @param code 要验证的区域编码
     * @throws DEException 当编码为空或不是数字时抛出异常
     */
    public void validateCode(String code) {
        if (StringUtils.isBlank(code)) DEException.throwException("区域编码不能为空");
        String busiGeoCode = getBusiGeoCode(code);
        if (!isNumeric(busiGeoCode)) {
            DEException.throwException("有效区域编码只能是数字");
        }
    }

    /**
     * 判断字符串是否为纯数字
     * 遍历字符串的每个字符，检查是否都在0-9之间
     *
     * @param str 要检查的字符串
     * @return 如果字符串为纯数字返回true，否则返回false
     */
    public boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) // ASCII码48-57对应数字0-9
                return false;
        }
        return true;
    }
}

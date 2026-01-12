package io.dataease.visualization.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.visualization.request.VisualizationStoreRequest;
import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.visualization.vo.VisualizationStoreVO;
import io.dataease.constant.BusiResourceEnum;
import io.dataease.exception.DEException;
import io.dataease.license.config.XpackInteract;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.CommonBeanFactory;
import io.dataease.utils.CommunityUtils;
import io.dataease.utils.IDUtils;
import io.dataease.visualization.dao.auto.entity.CoreStore;
import io.dataease.visualization.dao.auto.mapper.CoreStoreMapper;
import io.dataease.visualization.dao.ext.mapper.CoreStoreExtMapper;
import io.dataease.visualization.dao.ext.po.StorePO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VisualizationStoreManage {

    @Resource
    private CoreStoreMapper coreStoreMapper;

    @Resource
    private CoreStoreExtMapper coreStoreExtMapper;

    public void execute(VisualizationStoreRequest request) {
        Long resourceId = request.getId();
        Long uid = AuthUtils.getUser().getUserId();
        // 如果已经收藏，则取消收藏（删除收藏记录）
        if (favorited(resourceId)) {
            QueryWrapper<CoreStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resource_id", resourceId);
            queryWrapper.eq("uid", uid);
            coreStoreMapper.delete(queryWrapper);
            return;
        }
        // 如果未收藏，则添加收藏记录
        String type = request.getType();
        BusiResourceEnum busiResourceEnum = BusiResourceEnum.valueOf(type.toUpperCase());
        if (ObjectUtils.isEmpty(busiResourceEnum)) {
            DEException.throwException("type is invalid");
        }
        // 创建收藏记录
        CoreStore coreStore = new CoreStore();
        coreStore.setId(IDUtils.snowID());  // 生成唯一ID
        coreStore.setTime(System.currentTimeMillis());  // 设置收藏时间
        coreStore.setUid(uid);  // 设置收藏用户
        coreStore.setResourceId(resourceId);  // 设置资源ID
        coreStore.setResourceType(busiResourceEnum.getFlag());  // 设置资源类型
        coreStoreMapper.insert(coreStore);  // 插入收藏记录
    }

    public Boolean favorited(Long resourceId) {
        QueryWrapper<CoreStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        queryWrapper.eq("uid", AuthUtils.getUser().getUserId());
        return coreStoreMapper.exists(queryWrapper);
    }

    @XpackInteract(value = "perFilterManage", recursion = true, invalid = true)
    public IPage<VisualizationStoreVO> query(int pageNum, int pageSize, VisualizationWorkbranchQueryRequest request) {
        IPage<StorePO> storePOIPage = proxy().queryStorePage(pageNum, pageSize, request);
        if (ObjectUtils.isEmpty(storePOIPage)) return null;
        List<VisualizationStoreVO> vos = proxy().formatResult(storePOIPage.getRecords());
        IPage<VisualizationStoreVO> ipage = new Page<>();
        ipage.setCurrent(storePOIPage.getCurrent());
        ipage.setPages(storePOIPage.getPages());
        ipage.setSize(storePOIPage.getSize());
        ipage.setTotal(storePOIPage.getTotal());
        ipage.setRecords(vos);
        return ipage;
    }

    public VisualizationStoreManage proxy() {
        return CommonBeanFactory.getBean(this.getClass());
    }

    public List<VisualizationStoreVO> formatResult(List<StorePO> pos) {
        if (CollectionUtils.isEmpty(pos)) return new ArrayList<>();
        return pos.stream().map(po ->
                new VisualizationStoreVO(
                        po.getStoreId(), po.getResourceId(), po.getName(),
                        po.getType(), String.valueOf(po.getCreator()), ObjectUtils.isEmpty(po.getEditor()) ? null : String.valueOf(po.getEditor()),
                        po.getEditTime(), 9, po.getExtFlag(), po.getExtFlag1())).toList();
    }

    public IPage<StorePO> queryStorePage(int goPage, int pageSize, VisualizationWorkbranchQueryRequest request) {
        Long uid = AuthUtils.getUser().getUserId();
        // 构建查询条件
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("s.uid", uid);  // 只查询当前用户的收藏记录
        queryWrapper.isNotNull("s.resource_id");  // 资源ID不能为空
        // 根据资源类型筛选
        if (StringUtils.isNotBlank(request.getType())) {
            BusiResourceEnum busiResourceEnum = BusiResourceEnum.valueOf(request.getType().toUpperCase());
            if (ObjectUtils.isEmpty(busiResourceEnum)) {
                DEException.throwException("type is invalid");
            }
            queryWrapper.eq("s.resource_type", busiResourceEnum.getFlag());
        }
        // 根据关键词模糊查询资源名称（不区分大小写）
        if (StringUtils.isNotBlank(request.getKeyword())) {
            queryWrapper.apply("LOWER(v.name) LIKE LOWER(CONCAT('%', {0}, '%'))", request.getKeyword());
        }
        // 社区版需要过滤企业版专属资源
        String info = CommunityUtils.getInfo();
        if (StringUtils.isNotBlank(info)) {
            queryWrapper.notExists(String.format(info, "s.resource_id"));
        }
        // 按更新时间排序（支持升序和降序）
        queryWrapper.orderBy(true, request.isAsc(), "v.update_time");
        // 执行分页查询
        Page<StorePO> page = new Page<>(goPage, pageSize);
        return coreStoreExtMapper.query(page, queryWrapper);
    }
}

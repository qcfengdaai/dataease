package io.dataease.visualization.server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.api.visualization.VisualizationStoreApi;
import io.dataease.api.visualization.request.VisualizationStoreRequest;
import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.visualization.vo.VisualizationResourceVO;
import io.dataease.api.visualization.vo.VisualizationStoreVO;
import io.dataease.i18n.Translator;
import io.dataease.visualization.manage.VisualizationStoreManage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 可视化存储服务
 * <p>
 * 处理可视化资源的收藏、查询等操作
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RequestMapping("/store")
@RestController
public class VisualizationStoreServer implements VisualizationStoreApi {

    @Resource
    private VisualizationStoreManage visualizationStoreManage;

    /**
     * 执行存储操作
     *
     * @param request 存储请求
     */
    @Override
    public void execute(VisualizationStoreRequest request) {
        visualizationStoreManage.execute(request);
    }

    /**
     * 查询存储的可视化资源
     *
     * @param request 查询请求
     * @return 可视化资源列表
     */
    @Override
    public List<VisualizationStoreVO> query(VisualizationWorkbranchQueryRequest request) {
        IPage<VisualizationStoreVO> iPage = visualizationStoreManage.query(1, 20, request);
        List<VisualizationStoreVO> resourceVOS = iPage.getRecords();
        if (!CollectionUtils.isEmpty(resourceVOS)) {
            resourceVOS.forEach(item -> {
                item.setCreator(StringUtils.equals(item.getCreator(), "1") ? Translator.get("i18n_sys_admin") : item.getCreator());
                item.setLastEditor(StringUtils.equals(item.getLastEditor(), "1") ? Translator.get("i18n_sys_admin") : item.getCreator());
            });
        }
        return iPage.getRecords();
    }

    /**
     * 检查资源是否已收藏
     *
     * @param id 资源ID
     * @return 如果已收藏返回true，否则返回false
     */
    @Override
    public boolean favorited(Long id) {
        return visualizationStoreManage.favorited(id);
    }
}

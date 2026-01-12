package io.dataease.visualization.server;

import io.dataease.api.visualization.VisualizationWatermarkApi;
import io.dataease.api.visualization.request.VisualizationWatermarkRequest;
import io.dataease.api.visualization.vo.VisualizationWatermarkVO;
import io.dataease.utils.BeanUtils;
import io.dataease.visualization.dao.auto.entity.VisualizationWatermark;
import io.dataease.visualization.dao.auto.mapper.VisualizationWatermarkMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 可视化水印服务
 * <p>
 * 管理仪表板水印配置
 * <p>
 * 主要功能：
 * <ul>
 * <li>获取水印配置</li>
 * <li>保存水印配置</li>
 * </ul>
 * 水印配置为系统级配置，所有仪表板共享
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("/watermark")
public class VisualizationWatermarkService implements VisualizationWatermarkApi {

    private final static String DEFAULT_ID ="system_default";

    @Resource
    private VisualizationWatermarkMapper watermarkMapper;

    /**
     * 获取水印配置
     * <p>
     * 返回系统默认水印配置
     *
     * @return 水印配置信息
     */
    @Override
    public VisualizationWatermarkVO getWatermarkInfo() {
        VisualizationWatermark watermark =  watermarkMapper.selectById(DEFAULT_ID);
        VisualizationWatermarkVO watermarkVO = new VisualizationWatermarkVO();
        return BeanUtils.copyBean(watermarkVO,watermark);
    }

    /**
     * 保存水印配置
     * <p>
     * 更新系统默认水印配置
     *
     * @param watermarkRequest 水印配置请求
     */
    @Override
    public void saveWatermarkInfo(VisualizationWatermarkRequest watermarkRequest) {
        VisualizationWatermark watermark =  new VisualizationWatermark();
        BeanUtils.copyBean(watermark,watermarkRequest);
        watermark.setId(DEFAULT_ID);
        watermarkMapper.updateById(watermark);
    }
}

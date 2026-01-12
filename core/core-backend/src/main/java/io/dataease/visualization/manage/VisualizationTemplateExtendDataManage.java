package io.dataease.visualization.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.exception.DEException;
import io.dataease.template.dao.auto.entity.VisualizationTemplateExtendData;
import io.dataease.template.dao.auto.mapper.VisualizationTemplateExtendDataMapper;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.LogUtil;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 可视化模板扩展数据管理
 * <p>
 * 处理模板的内置数据（缓存数据）
 * <p>
 * 主要功能：
 * <ul>
 * <li>从模板缓存中获取图表数据</li>
 * <li>为从模板创建的仪表板提供初始数据</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Service
public class VisualizationTemplateExtendDataManage {

    @Resource
    private VisualizationTemplateExtendDataMapper extendDataMapper;

    /**
     * 从模板缓存中获取图表数据
     * <p>
     * 当从模板创建仪表板时，使用模板中内置的数据
     *
     * @param viewId 视图ID
     * @param view   视图对象
     * @return 填充了模板数据的视图对象
     */
    public ChartViewDTO getChartDataInfo(Long viewId, ChartViewDTO view) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("view_id",viewId);
        List<VisualizationTemplateExtendData> extendDataList = extendDataMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(extendDataList)) {
            try{
                ChartViewDTO chartViewTemplate = JsonUtil.parseObject(extendDataList.get(0).getViewDetails(),ChartViewDTO.class);
                if(chartViewTemplate != null){
                    view.setData(chartViewTemplate.getData());
                }
            }catch (Exception e){
                LogUtil.error("未获取内置数据："+viewId);
            }

        } else {
            DEException.throwException("模板缓存数据中未获取指定图表数据：" + viewId);
        }
        return view;
    }
}

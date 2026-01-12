package io.dataease.visualization.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.visualization.VisualizationBackgroundApi;
import io.dataease.api.visualization.vo.VisualizationBackgroundVO;
import io.dataease.i18n.Translator;
import io.dataease.utils.BeanUtils;
import io.dataease.visualization.dao.auto.entity.VisualizationBackground;
import io.dataease.visualization.dao.auto.mapper.VisualizationBackgroundMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 可视化背景服务
 * <p>
 * 管理仪表板背景样式
 * <p>
 * 主要功能：
 * <ul>
 * <li>查询所有背景样式</li>
 * <li>按分类返回背景样式</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("/visualizationBackground")
public class VisualizationBackgroundService implements VisualizationBackgroundApi {
    @Resource
    VisualizationBackgroundMapper mapper;

    /**
     * 查询所有背景样式
     * <p>
     * 背景按分类进行分组返回
     *
     * @return 分类到背景列表的映射
     */
    @Override
    public Map<String, List<VisualizationBackgroundVO>> findAll() {
        List<VisualizationBackground> result = mapper.selectList(new QueryWrapper<>());
        return result.stream().map(vb ->{
            VisualizationBackgroundVO vbVO = new VisualizationBackgroundVO();
            BeanUtils.copyBean(vbVO,vb);
            vbVO.setName(Translator.get("i18n_board")+vbVO.getName());
            return vbVO;
        }).collect(Collectors.groupingBy(VisualizationBackgroundVO::getClassification));
    }
}

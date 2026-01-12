package io.dataease.visualization.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.visualization.VisualizationSubjectApi;
import io.dataease.api.visualization.request.VisualizationSubjectRequest;
import io.dataease.api.visualization.vo.VisualizationSubjectVO;
import io.dataease.exception.DEException;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.IDUtils;
import io.dataease.visualization.dao.auto.entity.VisualizationSubject;
import io.dataease.visualization.dao.auto.mapper.VisualizationSubjectMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 可视化主题服务
 * <p>
 * 管理可视化主题（配色方案等）
 * <p>
 * 主要功能：
 * <ul>
 * <li>查询主题列表</li>
 * <li>创建或更新主题</li>
 * <li>删除主题</li>
 * <li>按分组查询主题</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@RestController
@RequestMapping("/visualizationSubject")
public class VisualizationSubjectService implements VisualizationSubjectApi {

    @Resource
    VisualizationSubjectMapper subjectMapper;
    /**
     * 查询主题列表
     *
     * @param request 查询请求
     * @return 主题列表
     */
    @Override
    public List<VisualizationSubjectVO> query(VisualizationSubjectRequest request) {
        QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_flag", 0);
        List<VisualizationSubject> result =subjectMapper.selectList(wrapper);
       return result.stream().map(subject ->{
           VisualizationSubjectVO subjectVO = new VisualizationSubjectVO();
           BeanUtils.copyBean(subject,subjectVO);
           return subjectVO;
       }).collect(Collectors.toList());
    }

    /**
     * 按分组查询主题
     * <p>
     * 每个分组最多返回4个主题
     *
     * @param request 查询请求
     * @return 分组主题列表
     */
    @Override
    public List querySubjectWithGroup(VisualizationSubjectRequest request) {
        List result = new ArrayList();
        int pageSize = 4;
        QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("create_time");
        List<VisualizationSubject> allInfo =subjectMapper.selectList(wrapper);
        for (int i = 0; i < allInfo.size(); i = i + pageSize) {
            List<VisualizationSubject> tmp = allInfo.subList(i, Math.min(i + pageSize, allInfo.size()));
            result.add(tmp);
        }
        return result;
    }
    /**
     * 创建或更新主题
     * <p>
     * 如果ID为空则创建新主题，否则更新现有主题
     *
     * @param request 主题请求
     */
    @Override
    public synchronized void update(VisualizationSubjectRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("name", request.getName());
            List<VisualizationSubject> subjectAll =subjectMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(subjectAll)) {
                request.setId(IDUtils.snowID().toString());
                request.setCreateTime(System.currentTimeMillis());
                request.setType("self");
                request.setName(request.getName());
                VisualizationSubject saveInfo = new VisualizationSubject();
                BeanUtils.copyBean(saveInfo,request);
                subjectMapper.insert(saveInfo);
            } else {
                DEException.throwException("名称已经存在");
            }
        } else {
            QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("name", request.getName());
            wrapper.ne("id",request.getId());
            List<VisualizationSubject> subjectAll =subjectMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(subjectAll)) {
                request.setUpdateTime(System.currentTimeMillis());
                VisualizationSubject updateInfo = new VisualizationSubject();
                BeanUtils.copyBean(updateInfo,request);
                subjectMapper.updateById(updateInfo);
            } else {
                DEException.throwException("名称已经存在");
            }
        }
    }

    /**
     * 删除主题
     *
     * @param id 主题ID
     */
    @Override
    public void delete(String id) {
        Assert.notNull(id, "subjectId should not be null");
        subjectMapper.deleteById(id);
    }

}

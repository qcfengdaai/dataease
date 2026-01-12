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
        // 1. 构建查询条件：查询未删除的主题
        QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_flag", 0);
        // 2. 执行查询
        List<VisualizationSubject> result =subjectMapper.selectList(wrapper);
        // 3. 将实体转换为VO对象
       return result.stream().map(subject ->{
           // 3.1 创建VO对象
           VisualizationSubjectVO subjectVO = new VisualizationSubjectVO();
           // 3.2 复制属性
           BeanUtils.copyBean(subject,subjectVO);
           // 3.3 返回VO对象
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
        // 1. 创建结果列表（二维列表）
        List result = new ArrayList();
        // 2. 定义每组的大小（每页显示4个主题）
        int pageSize = 4;
        // 3. 构建查询条件：按创建时间升序排序
        QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("create_time");
        // 4. 查询所有主题
        List<VisualizationSubject> allInfo =subjectMapper.selectList(wrapper);
        // 5. 将主题列表按每组pageSize个进行分组
        for (int i = 0; i < allInfo.size(); i = i + pageSize) {
            // 5.1 截取子列表（从i开始，最多取pageSize个）
            List<VisualizationSubject> tmp = allInfo.subList(i, Math.min(i + pageSize, allInfo.size()));
            // 5.2 添加到结果列表
            result.add(tmp);
        }
        // 6. 返回分组后的结果
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
        // ========== 第一阶段：判断是新增还是更新 ==========
        if (StringUtils.isEmpty(request.getId())) {
            // ========== 第二阶段：新增主题 ==========
            // 2.1 检查名称是否重复
            QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("name", request.getName());
            List<VisualizationSubject> subjectAll =subjectMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(subjectAll)) {
                // 2.2 名称不重复，创建新主题
                // 2.2.1 生成主题ID
                request.setId(IDUtils.snowID().toString());
                // 2.2.2 设置创建时间
                request.setCreateTime(System.currentTimeMillis());
                // 2.2.3 设置类型为自定义
                request.setType("self");
                // 2.2.4 确保名称已设置
                request.setName(request.getName());
                // 2.2.5 创建实体并保存
                VisualizationSubject saveInfo = new VisualizationSubject();
                BeanUtils.copyBean(saveInfo,request);
                subjectMapper.insert(saveInfo);
            } else {
                // 2.3 名称重复，抛出异常
                DEException.throwException("名称已经存在");
            }
        } else {
            // ========== 第三阶段：更新主题 ==========
            // 3.1 检查除自己外是否有同名主题
            QueryWrapper<VisualizationSubject> wrapper = new QueryWrapper<>();
            wrapper.eq("name", request.getName());
            wrapper.ne("id",request.getId());
            List<VisualizationSubject> subjectAll =subjectMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(subjectAll)) {
                // 3.2 没有重名，执行更新
                // 3.2.1 设置更新时间
                request.setUpdateTime(System.currentTimeMillis());
                // 3.2.2 创建实体并更新
                VisualizationSubject updateInfo = new VisualizationSubject();
                BeanUtils.copyBean(updateInfo,request);
                subjectMapper.updateById(updateInfo);
            } else {
                // 3.3 名称重复，抛出异常
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
        // 1. 校验主题ID不为空
        Assert.notNull(id, "subjectId should not be null");
        // 2. 执行删除操作
        subjectMapper.deleteById(id);
    }

}

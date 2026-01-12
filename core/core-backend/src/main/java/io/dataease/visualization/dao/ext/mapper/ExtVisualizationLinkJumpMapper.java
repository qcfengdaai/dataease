package io.dataease.visualization.dao.ext.mapper;

import io.dataease.api.visualization.dto.VisualizationLinkJumpDTO;
import io.dataease.api.visualization.request.VisualizationLinkJumpBaseRequest;
import io.dataease.api.visualization.vo.VisualizationLinkJumpInfoVO;
import io.dataease.api.visualization.vo.VisualizationLinkJumpVO;
import io.dataease.api.visualization.vo.VisualizationOutParamsJumpVO;
import io.dataease.api.visualization.vo.VisualizationViewTableVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 可视化跳转扩展Mapper
 * <p>
 * 提供可视化跳转功能的扩展查询操作
 * <p>
 * 主要功能：
 * <ul>
 * <li>查询跳转配置信息</li>
 * <li>管理跳转快照数据</li>
 * <li>复制跳转配置</li>
 * <li>删除跳转相关数据</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-01-12
 */
@Mapper
public interface ExtVisualizationLinkJumpMapper {

    /**
     * 根据仪表板ID查询跳转配置
     *
     * @param dvId 仪表板ID
     * @param uid 用户ID
     * @param isDesktop 是否桌面端
     * @return 跳转配置列表
     */
    List<VisualizationLinkJumpDTO> queryWithDvId(@Param("dvId") Long dvId,@Param("uid") Long uid,@Param("isDesktop") Boolean isDesktop);

    /**
     * 根据仪表板ID查询跳转快照配置
     *
     * @param dvId 仪表板ID
     * @param uid 用户ID
     * @param isDesktop 是否桌面端
     * @return 跳转配置列表
     */
    List<VisualizationLinkJumpDTO> queryWithDvIdSnapshot(@Param("dvId") Long dvId,@Param("uid") Long uid,@Param("isDesktop") Boolean isDesktop);

    /**
     * 根据视图ID查询跳转配置
     *
     * @param dvId 仪表板ID
     * @param viewId 视图ID
     * @param uid 用户ID
     * @param isDesktop 是否桌面端
     * @return 跳转配置
     */
    VisualizationLinkJumpDTO queryWithViewId(@Param("dvId") Long dvId,@Param("viewId") Long viewId,@Param("uid") Long uid,@Param("isDesktop") Boolean isDesktop);

    /**
     * 删除跳转目标视图信息快照
     *
     * @param dvId 仪表板ID
     * @param viewId 视图ID
     */
    void deleteJumpTargetViewInfoSnapshot(@Param("dvId") Long dvId,@Param("viewId") Long viewId);

    /**
     * 删除跳转信息快照
     *
     * @param dvId 仪表板ID
     * @param viewId 视图ID
     */
    void deleteJumpInfoSnapshot(@Param("dvId") Long dvId,@Param("viewId") Long viewId);

    /**
     * 删除跳转快照
     *
     * @param dvId 仪表板ID
     * @param viewId 视图ID
     */
    void deleteJumpSnapshot(@Param("dvId") Long dvId,@Param("viewId") Long viewId);

    /**
     * 删除仪表板的跳转目标视图信息
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpTargetViewInfoWithVisualization(@Param("dvId") Long dvId);

    /**
     * 删除仪表板的跳转信息
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpInfoWithVisualization(@Param("dvId") Long dvId);

    /**
     * 删除仪表板的跳转配置
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpWithVisualization(@Param("dvId") Long dvId);

    /**
     * 删除仪表板快照的跳转目标视图信息
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpTargetViewInfoWithVisualizationSnapshot(@Param("dvId") Long dvId);

    /**
     * 删除仪表板快照的跳转信息
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpInfoWithVisualizationSnapshot(@Param("dvId") Long dvId);

    /**
     * 删除仪表板快照的跳转配置
     *
     * @param dvId 仪表板ID
     */
    void deleteJumpWithVisualizationSnapshot(@Param("dvId") Long dvId);

    /**
     * 获取目标仪表板的跳转信息
     *
     * @param request 跳转请求
     * @return 跳转信息列表
     */
    List<VisualizationLinkJumpDTO> getTargetVisualizationJumpInfo(@Param("request") VisualizationLinkJumpBaseRequest request);

    /**
     * 获取目标仪表板的跳转快照信息
     *
     * @param request 跳转请求
     * @return 跳转信息列表
     */
    List<VisualizationLinkJumpDTO> getTargetVisualizationJumpInfoSnapshot(@Param("request") VisualizationLinkJumpBaseRequest request);

    /**
     * 复制跳转配置
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJump(@Param("copyId")Long copyId);

    /**
     * 复制跳转信息
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJumpInfo(@Param("copyId")Long copyId);

    /**
     * 复制跳转目标视图配置
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJumpTarget(@Param("copyId")Long copyId);

    /**
     * 查找仪表板的跳转配置
     *
     * @param dvId 仪表板ID
     * @return 跳转配置列表
     */
    List<VisualizationLinkJumpVO> findLinkJumpWithDvId(@Param("dvId")Long dvId);

    /**
     * 查找仪表板的跳转信息
     *
     * @param dvId 仪表板ID
     * @return 跳转信息列表
     */
    List<VisualizationLinkJumpInfoVO> findLinkJumpInfoWithDvId(@Param("dvId")Long dvId);

    /**
     * 获取视图表格详情
     *
     * @param dvId 仪表板ID
     * @return 视图表格详情列表
     */
    List<VisualizationViewTableVO> getViewTableDetails(@Param("dvId")Long dvId);

    /**
     * 查询仪表板的外部参数跳转目标
     *
     * @param dvId 仪表板ID
     * @return 外部参数跳转目标列表
     */
    List<VisualizationOutParamsJumpVO> queryOutParamsTargetWithDvId(@Param("dvId")Long dvId);
}

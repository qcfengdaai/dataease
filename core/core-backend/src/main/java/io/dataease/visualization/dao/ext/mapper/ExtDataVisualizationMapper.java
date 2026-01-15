package io.dataease.visualization.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.api.visualization.dto.VisualizationViewTableDTO;
import io.dataease.api.visualization.vo.DataVisualizationBaseVO;
import io.dataease.api.visualization.vo.DataVisualizationVO;
import io.dataease.api.visualization.vo.VisualizationReportFilterVO;
import io.dataease.api.visualization.vo.VisualizationResourceVO;
import io.dataease.chart.dao.auto.entity.CoreChartView;
import io.dataease.visualization.dao.ext.po.StorePO;
import io.dataease.visualization.dao.ext.po.VisualizationResourcePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据可视化扩展Mapper
 * <p>
 * 提供数据可视化的扩展查询功能
 * <p>
 * 主要功能：
 * <ul>
 * <li>查询仪表板基础信息</li>
 * <li>复制仪表板和图表</li>
 * <li>创建和恢复快照</li>
 * <li>查询视图详情</li>
 * </ul>
 *
 * @author DataEase
 * @since 2024-06-21
 */
@Mapper
public interface ExtDataVisualizationMapper {


    /**
     * 查询仪表板基础信息
     *
     * @param nodeType 节点类型（可选）
     * @param type     类型（可选）
     * @return 基础信息列表
     */
    @Select("<script> SELECT id, `name`, `name` as label, pid, org_id, node_type, mobile_layout, create_time, create_by, update_time, update_by \n" +
            "FROM\n" +
            "\tdata_visualization_info where delete_flag=0 <when test='nodeType !=null'> and node_type = #{nodeType} </when>  <when test='type !=null'> and type = #{type} </when> order by node_type desc </script>")
    List<DataVisualizationBaseVO> findBashInfo(@Param("nodeType") String nodeType, @Param("type") String type);

    /**
     * 查询仪表板类型
     *
     * @param dvId 仪表板ID
     * @return 类型（dashboard或dataV）
     */
    @Select("select type from data_visualization_info where id = #{dvId}")
    String findDvType(@Param("dvId") Long dvId);

    /**
     * 复制仪表板
     *
     * @param sourceDvId 源仪表板ID
     * @param newDvId    新仪表板ID
     * @param copyId     复制批次ID
     */
    void dvCopy(@Param("sourceDvId") Long sourceDvId,@Param("newDvId") Long newDvId,@Param("copyId") Long copyId);

    /**
     * 复制仪表板关联的图表
     *
     * @param sourceDvId    源仪表板ID
     * @param newDvId       新仪表板ID
     * @param copyId        复制批次ID
     * @param resourceTable 资源表
     */
    void viewCopyWithDv(@Param("sourceDvId") Long sourceDvId,@Param("newDvId") Long newDvId,@Param("copyId") Long copyId,@Param("resourceTable") String resourceTable);

    /**
     * 根据复制ID查找图表信息
     *
     * @param copyId 复制批次ID
     * @return 图表列表
     */
    List<CoreChartView> findViewInfoByCopyId(@Param("copyId") Long copyId);

    /**
     * 查询仪表板详细信息
     *
     * @param dvId          仪表板ID
     * @param dvType        仪表板类型（可选）
     * @param resourceTable 资源表（core或snapshot）
     * @return 仪表板详细信息
     */
    DataVisualizationVO findDvInfo(@Param("dvId") Long dvId,@Param("dvType") String dvType,@Param("resourceTable") String resourceTable);

    /**
     * 查询最近使用的资源
     *
     * @param page    分页对象
     * @param uid     用户ID
     * @param keyword 关键词（可选）
     * @param ew      查询条件
     * @return 资源列表
     */
    IPage<VisualizationResourcePO> findRecent(IPage<VisualizationResourcePO> page, @Param("uid") Long uid, @Param("keyword") String keyword, @Param("ew") Map ew);

    /**
     * 复制跳转配置
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJump(@Param("copyId") Long copyId);

    /**
     * 复制跳转信息
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJumpInfo(@Param("copyId") Long copyId);

    /**
     * 复制跳转目标视图信息
     *
     * @param copyId 复制批次ID
     */
    void copyLinkJumpTargetInfo(@Param("copyId") Long copyId);

    /**
     * 复制联动配置
     *
     * @param copyId 复制批次ID
     */
    void copyLinkage(@Param("copyId") Long copyId);

    /**
     * 复制联动字段配置
     *
     * @param copyId 复制批次ID
     */
    void copyLinkageField(@Param("copyId") Long copyId);

    /**
     * 获取仪表板的视图详情列表
     *
     * @param dvId 仪表板ID
     * @return 视图详情列表
     */
    List<VisualizationViewTableDTO> getVisualizationViewDetails(@Param("dvId") Long dvId);

    /**
     * 查询定时报告过滤组件信息
     *
     * @param dvId   仪表板ID
     * @param taskId 任务ID
     * @return 过滤组件列表
     */
    List<VisualizationReportFilterVO> queryReportFilter(@Param("dvId") Long dvId,@Param("taskId") Long taskId);

    /**
     * 批量删除仪表板
     *
     * @param ids           ID集合
     * @param resourceTable 资源表
     */
    void deleteDataVBatch(@Param("ids") Set<Long> ids,@Param("resourceTable") String resourceTable);

    /**
     * 批量删除图表
     *
     * @param ids           ID集合
     * @param resourceTable 资源表
     */
    void deleteViewsBatch(@Param("ids") Set<Long> ids,@Param("resourceTable") String resourceTable);

    /**
     * 删除镜像表中未使用的图表
     *
     * @param ids  有效图表ID列表
     * @param dvId 仪表板ID
     */
    void deleteUselessViewsBatchSnapshot(@Param("ids") List<Long> ids,@Param("dvId") Long dvId);

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserFormVO queryInnerUserInfo(@Param("id") Long id);

    /**
     * 创建仪表板快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotDataV(@Param("dvId") Long dvId);

    /**
     * 创建图表快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotViews(@Param("dvId") Long dvId);

    /**
     * 创建跳转目标视图快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotLinkJumpTargetViewInfo(@Param("dvId") Long dvId);

    /**
     * 创建跳转信息快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotLinkJumpInfo(@Param("dvId") Long dvId);

    /**
     * 创建跳转快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotLinkJump(@Param("dvId") Long dvId);

    /**
     * 创建联动字段快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotLinkageField(@Param("dvId") Long dvId);

    /**
     * 创建联动快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotLinkage(@Param("dvId") Long dvId);

    /**
     * 创建外部参数目标视图快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotOuterParamsTargetViewInfo(@Param("dvId") Long dvId);

    /**
     * 创建外部参数信息快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotOuterParamsInfo(@Param("dvId") Long dvId);

    /**
     * 创建外部参数快照
     *
     * @param dvId 仪表板ID
     */
    void snapshotOuterParams(@Param("dvId") Long dvId);

    /**
     * 恢复仪表板快照
     *
     * @param dvId 仪表板ID
     */
    void restoreDataV(@Param("dvId") Long dvId);

    /**
     * 恢复图表快照
     *
     * @param dvId 仪表板ID
     */
    void restoreViews(@Param("dvId") Long dvId);

    /**
     * 恢复跳转目标视图快照
     *
     * @param dvId 仪表板ID
     */
    void restoreLinkJumpTargetViewInfo(@Param("dvId") Long dvId);

    /**
     * 恢复跳转信息快照
     *
     * @param dvId 仪表板ID
     */
    void restoreLinkJumpInfo(@Param("dvId") Long dvId);

    /**
     * 恢复跳转快照
     *
     * @param dvId 仪表板ID
     */
    void restoreLinkJump(@Param("dvId") Long dvId);

    /**
     * 恢复联动字段快照
     *
     * @param dvId 仪表板ID
     */
    void restoreLinkageField(@Param("dvId") Long dvId);

    /**
     * 恢复联动快照
     *
     * @param dvId 仪表板ID
     */
    void restoreLinkage(@Param("dvId") Long dvId);

    /**
     * 恢复外部参数目标视图快照
     *
     * @param dvId 仪表板ID
     */
    void restoreOuterParamsTargetViewInfo(@Param("dvId") Long dvId);

    /**
     * 恢复外部参数信息快照
     *
     * @param dvId 仪表板ID
     */
    void restoreOuterParamsInfo(@Param("dvId") Long dvId);

    /**
     * 恢复外部参数快照
     *
     * @param dvId 仪表板ID
     */
    void restoreOuterParams(@Param("dvId") Long dvId);

    /**
     * 查询仪表板状态
     *
     * @param dvId 仪表板ID
     * @return 状态（0-未发布，1-已发布，2-已保存未发布）
     */
    @Select("select status from data_visualization_info where id = #{dvId}")
    Integer findDvInfoStats(@Param("dvId") Long dvId);
}

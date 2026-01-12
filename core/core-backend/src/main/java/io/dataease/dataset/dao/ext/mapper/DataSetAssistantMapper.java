package io.dataease.dataset.dao.ext.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据集助手映射接口
 * 提供数据集助手功能相关的复杂查询，包含权限控制和多表关联查询
 * 支持社区版和企业版的不同权限查询策略
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集、数据表、字段的完整信息查询</li>
 *   <li>基于用户权限的数据过滤</li>
 *   <li>支持社区版和企业版不同的权限模型</li>
 *   <li>用户角色信息查询</li>
 * </ul>
 *
 * @author Junjun
 */
@Mapper
public interface DataSetAssistantMapper {

    /**
     * 查询所有数据集完整信息
     * 获取数据源、数据集、数据表、字段的完整关联信息
     * 过滤掉跨数据源和状态异常的数据源，不进行权限控制
     *
     * @param queryWrapper 动态查询条件
     * @return 包含完整信息的数据集列表
     */
    @Select(
    """
            select
            cd.id as cd_id,
            cd.name as cd_name,
            cd.description as cd_description,
            cd.type as cd_type,
            cd.configuration as cd_configuration,
            
            cdg.id as cdg_id, 
            cdg.name as cdg_name,
            cdg.type as cdg_type,
            cdg.mode as cdg_model,
            cdg.info as cdg_info,
            cdg.union_sql as cdg_union_sql,
            cdg.is_cross as cdg_is_cross,
            
            cdt.id as cdt_id,
            cdt.table_name as cdt_table_name,
            cdt.type as cdt_type,
            cdt.info as cdt_info,
            cdt.sql_variable_details as cdt_sql_variable_details,
            
            cdtf.id as cdtf_id,
            cdtf.origin_name as cdtf_origin_name,
            cdtf.name as cdtf_name,
            cdtf.description as cdtf_description,
            cdtf.dataease_name as cdtf_dataease_name,
            cdtf.field_short_name as cdtf_field_short_name,
            cdtf.group_list as cdtf_group_list,
            cdtf.other_group as cdtf_other_group,
            cdtf.group_type as cdtf_group_type,
            cdtf.type as cdtf_type,
            cdtf.de_type as cdtf_de_type,
            cdtf.de_extract_type as cdtf_de_extract_type,
            cdtf.ext_field as cdtf_ext_field,
            cdtf.checked as cdtf_checked,
            cdtf.accuracy as cdtf_accuracy,
            cdtf.date_format as cdtf_date_format,
            cdtf.date_format_type as cdtf_date_format_type,
            cdtf.params as cdtf_params
            
            from `core_dataset_group` cdg
            left join `core_dataset_table` cdt on cdg.id =  cdt.dataset_group_id
            left join `core_dataset_table_field` cdtf on cdtf.dataset_group_id = cdg.id and (cdtf.dataset_table_id is NULL or cdtf.dataset_table_id = cdt.id)
            inner join `core_datasource` cd on cdt.datasource_id = cd.id
            where  cdg.is_cross != 1 and (cd.STATUS IS NULL OR cd.STATUS != 'Error')
            ${ew.customSqlSegment}
            """
    )
    List<Map<String, Object>> queryAll(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 社区版数据集信息查询
     * 获取用户有权限访问的数据集完整信息，使用简化的权限控制
     * 排除用户在业务资源表中被明确拒绝访问的数据源和数据集
     *
     * @param queryWrapper 动态查询条件
     * @return 用户可访问的数据集列表
     */
    @Select("""
    WITH user_ds_permissions AS (
        SELECT DISTINCT resource_id
        FROM (
            select id as resource_id from per_busi_resource where rt_id = 4 
        ) temp
    ),
    user_dg_permissions AS (
        SELECT DISTINCT resource_id
        FROM (
            select id as resource_id from per_busi_resource where rt_id = 3
        ) temp
    )
    SELECT
        cd.id as cd_id,
        cd.name as cd_name,
        cd.description as cd_description,
        cd.type as cd_type,
        cd.configuration as cd_configuration,
        
        cdg.id as cdg_id, 
        cdg.name as cdg_name,
        cdg.type as cdg_type,
        cdg.mode as cdg_model,
        cdg.info as cdg_info,
        cdg.union_sql as cdg_union_sql,
        cdg.is_cross as cdg_is_cross,
        
        cdt.id as cdt_id,
        cdt.table_name as cdt_table_name,
        cdt.type as cdt_type,
        cdt.info as cdt_info,
        cdt.sql_variable_details as cdt_sql_variable_details,
        
        cdtf.id as cdtf_id,
        cdtf.origin_name as cdtf_origin_name,
        cdtf.name as cdtf_name,
        cdtf.description as cdtf_description,
        cdtf.dataease_name as cdtf_dataease_name,
        cdtf.field_short_name as cdtf_field_short_name,
        cdtf.group_list as cdtf_group_list,
        cdtf.other_group as cdtf_other_group,
        cdtf.group_type as cdtf_group_type,
        cdtf.type as cdtf_type,
        cdtf.de_type as cdtf_de_type,
        cdtf.de_extract_type as cdtf_de_extract_type,
        cdtf.ext_field as cdtf_ext_field,
        cdtf.checked as cdtf_checked,
        cdtf.accuracy as cdtf_accuracy,
        cdtf.date_format as cdtf_date_format,
        cdtf.date_format_type as cdtf_date_format_type,
        cdtf.params as cdtf_params
    FROM `core_dataset_table` cdt
    INNER JOIN `core_datasource` cd ON cdt.datasource_id = cd.id and (cd.STATUS IS NULL OR cd.STATUS != 'Error')
    INNER JOIN `core_dataset_group` cdg ON cdg.id = cdt.dataset_group_id
        AND cdg.is_cross != 1
    INNER JOIN `core_dataset_table_field` cdtf ON cdtf.dataset_group_id = cdg.id and (cdtf.dataset_table_id is NULL or cdtf.dataset_table_id = cdt.id)
    where not exists( select 1 from user_ds_permissions ds_p where cd.id = ds_p.resource_id )
    and not exists( select 1 from user_dg_permissions dg_p where cdg.id = dg_p.resource_id )
    ${ew.customSqlSegment}
    """)
    List<Map<String, Object>> queryCommunity(@Param("ew") QueryWrapper queryWrapper);

    /**
     * 企业版数据集信息查询
     * 基于完整的权限体系查询用户可访问的数据集信息
     * 支持组织管理员和普通用户的差异化权限控制
     * 通过用户-角色-权限的复杂关联进行精确的权限过滤
     *
     * @param oid 组织ID
     * @param uid 用户ID
     * @param orgAdmin 是否为组织管理员
     * @param queryWrapper 动态查询条件
     * @return 用户可访问的数据集列表
     */
    @Select("""
    <script>
    WITH user_ds_permissions AS (
        <choose>
            <when test="orgAdmin">
                SELECT id as resource_id FROM per_busi_resource 
                WHERE org_id = #{oid} AND rt_id = 4
            </when>
            <otherwise>
                SELECT DISTINCT resource_id
                FROM (
                    SELECT resource_id FROM per_auth_busi_user
                    WHERE oid = #{oid} AND uid = #{uid} AND resource_type = 4
                    UNION ALL
                    SELECT a.resource_id FROM per_auth_busi_role a
                    INNER JOIN per_user_role b ON a.rid = b.rid
                    WHERE b.oid = #{oid} AND b.uid = #{uid} AND a.resource_type = 4
                ) temp
            </otherwise>
        </choose>
    ),
    user_dg_permissions AS (
        <choose>
            <when test="orgAdmin">
                SELECT id as resource_id FROM per_busi_resource 
                WHERE org_id = #{oid} AND rt_id = 3
            </when>
            <otherwise>
                SELECT DISTINCT resource_id
                FROM (
                    SELECT resource_id FROM per_auth_busi_user
                    WHERE oid = #{oid} AND uid = #{uid} AND resource_type = 3
                    UNION ALL
                    SELECT a.resource_id FROM per_auth_busi_role a
                    INNER JOIN per_user_role b ON a.rid = b.rid
                    WHERE b.oid = #{oid} AND b.uid = #{uid} AND a.resource_type = 3
                ) temp
            </otherwise>
        </choose>
    )
    SELECT
        cd.id as cd_id,
        cd.name as cd_name,
        cd.description as cd_description,
        cd.type as cd_type,
        cd.configuration as cd_configuration,
        
        cdg.id as cdg_id, 
        cdg.name as cdg_name,
        cdg.type as cdg_type,
        cdg.mode as cdg_model,
        cdg.info as cdg_info,
        cdg.union_sql as cdg_union_sql,
        cdg.is_cross as cdg_is_cross,
        
        cdt.id as cdt_id,
        cdt.table_name as cdt_table_name,
        cdt.type as cdt_type,
        cdt.info as cdt_info,
        cdt.sql_variable_details as cdt_sql_variable_details,
        
        cdtf.id as cdtf_id,
        cdtf.origin_name as cdtf_origin_name,
        cdtf.name as cdtf_name,
        cdtf.description as cdtf_description,
        cdtf.dataease_name as cdtf_dataease_name,
        cdtf.field_short_name as cdtf_field_short_name,
        cdtf.group_list as cdtf_group_list,
        cdtf.other_group as cdtf_other_group,
        cdtf.group_type as cdtf_group_type,
        cdtf.type as cdtf_type,
        cdtf.de_type as cdtf_de_type,
        cdtf.de_extract_type as cdtf_de_extract_type,
        cdtf.ext_field as cdtf_ext_field,
        cdtf.checked as cdtf_checked,
        cdtf.accuracy as cdtf_accuracy,
        cdtf.date_format as cdtf_date_format,
        cdtf.date_format_type as cdtf_date_format_type,
        cdtf.params as cdtf_params
    FROM `core_dataset_table` cdt
    INNER JOIN `core_datasource` cd ON cdt.datasource_id = cd.id 
        AND (cd.STATUS IS NULL OR cd.STATUS != 'Error')
    INNER JOIN `core_dataset_group` cdg ON cdg.id = cdt.dataset_group_id
        AND cdg.is_cross != 1
    INNER JOIN `core_dataset_table_field` cdtf ON cdtf.dataset_group_id = cdg.id and (cdtf.dataset_table_id is NULL or cdtf.dataset_table_id = cdt.id)
    INNER JOIN user_ds_permissions ds_p ON cd.id = ds_p.resource_id
    INNER JOIN user_dg_permissions dg_p ON cdg.id = dg_p.resource_id
    ${ew.customSqlSegment}
    </script>
""")
    List<Map<String, Object>> queryEnterprise(@Param("oid") Long oid, @Param("uid") Long uid, @Param("orgAdmin") boolean orgAdmin, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 根据用户ID查询角色信息
     * 获取指定用户在指定组织中的角色详细信息
     * 包含角色ID、只读权限标识和父角色ID等信息
     *
     * @param uid 用户ID
     * @param oid 组织ID
     * @return 用户角色信息列表
     */
    @Select("select pr.id, pr.readonly, pr.pid from per_user_role pur left join per_role pr on pur.rid = pr.id where pur.uid = #{uid} and pur.oid = #{oid} ")
    List<Map<String, Object>> roleInfoByUid(@Param("uid") Long uid, @Param("oid") Long oid);
}

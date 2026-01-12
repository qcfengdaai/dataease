package io.dataease.dataset.manage;

import io.dataease.api.chart.dto.DeSortField;
import io.dataease.api.dataset.dto.*;
import io.dataease.api.dataset.union.DatasetGroupInfoDTO;
import io.dataease.api.dataset.union.DatasetTableInfoDTO;
import io.dataease.api.permissions.auth.dto.BusiPerCheckDTO;
import io.dataease.api.permissions.dataset.api.RowPermissionsApi;
import io.dataease.api.permissions.dataset.dto.DataSetRowPermissionsTreeDTO;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.chart.utils.ChartDataBuild;
import io.dataease.commons.utils.SqlparserUtils;
import io.dataease.constant.AuthEnum;
import io.dataease.constant.SQLConstants;
import io.dataease.dataset.constant.DatasetTableType;
import io.dataease.dataset.utils.DatasetUtils;
import io.dataease.dataset.utils.FieldUtils;
import io.dataease.dataset.utils.SqlUtils;
import io.dataease.dataset.utils.TableUtils;
import io.dataease.datasource.dao.auto.entity.CoreDatasource;
import io.dataease.datasource.dao.auto.mapper.CoreDatasourceMapper;
import io.dataease.datasource.manage.DataSourceManage;
import io.dataease.datasource.manage.EngineManage;
import io.dataease.datasource.utils.DatasourceUtils;
import io.dataease.engine.constant.ExtFieldConstant;
import io.dataease.engine.sql.SQLProvider;
import io.dataease.engine.trans.*;
import io.dataease.engine.utils.SQLUtils;
import io.dataease.engine.utils.Utils;
import io.dataease.exception.DEException;
import io.dataease.extensions.datasource.api.PluginManageApi;
import io.dataease.extensions.datasource.constant.SqlPlaceholderConstants;
import io.dataease.extensions.datasource.dto.*;
import io.dataease.extensions.datasource.factory.ProviderFactory;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import io.dataease.extensions.view.dto.ChartExtFilterDTO;
import io.dataease.extensions.view.dto.ChartExtRequest;
import io.dataease.extensions.view.dto.ColumnPermissionItem;
import io.dataease.extensions.view.dto.SqlVariableDetails;
import io.dataease.i18n.Translator;
import io.dataease.system.manage.CorePermissionManage;
import io.dataease.utils.AuthUtils;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.JsonUtil;
import io.dataease.utils.TreeUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static io.dataease.chart.manage.ChartDataManage.START_END_SEPARATOR;
import static io.dataease.dataset.utils.TableUtils.format;

/**
 * 数据集数据管理类
 * 负责数据集数据的查询、预览、字段获取等核心业务逻辑
 * 协调数据源、SQL引擎、权限控制等多个模块完成复杂的数据处理任务
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>数据集表字段的获取和转换</li>
 *   <li>数据预览和分页查询</li>
 *   <li>字段枚举值查询（支持多字段、树形结构）</li>
 *   <li>数据总数统计</li>
 *   <li>SQL预览和日志记录</li>
 *   <li>行权限和列权限的过滤</li>
 *   <li>数据脱敏处理</li>
 *   <li>跨数据源查询支持</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>数据集编辑器中的数据预览</li>
 *   <li>图表组件的数据查询</li>
 *   <li>筛选组件的枚举值获取</li>
 *   <li>数据集权限验证</li>
 * </ul>
 *
 * @author Junjun
 */
@Component
public class DatasetDataManage {
    @Resource
    private DatasetSQLManage datasetSQLManage;
    @Resource
    private CoreDatasourceMapper coreDatasourceMapper;
    @Resource
    private DatasetTableFieldManage datasetTableFieldManage;
    @Resource
    private EngineManage engineManage;
    @Resource
    private DatasetGroupManage datasetGroupManage;
    @Resource
    private PermissionManage permissionManage;
    @Resource
    private DatasetTableSqlLogManage datasetTableSqlLogManage;
    @Autowired(required = false)
    private PluginManageApi pluginManage;
    @Resource
    private CorePermissionManage corePermissionManage;
    @Autowired(required = false)
    private RowPermissionsApi rowPermissionsApi;
    @Resource
    private DataSourceManage dataSourceManage;

    private static Logger logger = LoggerFactory.getLogger(DatasetDataManage.class);

    /**
     * 获取行权限API接口
     * 用于获取企业版的行权限控制功能
     *
     * @return 行权限API接口，社区版返回null
     */
    private RowPermissionsApi getRowPermissionsApi() {
        return rowPermissionsApi;
    }

    /**
     * 不支持完整连接的数据源类型列表
     * 这些数据源在执行某些关联查询时可能存在限制
     */
    public static final List<String> notFullDs = List.of("mysql", "mariadb", "Excel", "API", "H2", "h2");

    /**
     * 获取数据集表的字段信息
     * 根据数据表类型（DB/SQL/Excel/API/ES）从数据源获取原始字段信息
     * 并转换为DataEase统一的字段格式
     *
     * @param datasetTableDTO 数据表信息，包含数据源ID、表类型、表信息等
     * @return 字段列表，包含字段的名称、类型、DataEase类型等信息
     * @throws Exception 数据源连接失败或SQL解析失败时抛出异常
     */
    public List<DatasetTableFieldDTO> getTableFields(DatasetTableDTO datasetTableDTO) throws Exception {
        List<DatasetTableFieldDTO> list = null;
        List<TableField> tableFields = null;
        String type = datasetTableDTO.getType();
        // 解析表信息JSON，获取表名、SQL等详细信息
        DatasetTableInfoDTO tableInfoDTO = JsonUtil.parseObject(datasetTableDTO.getInfo(), DatasetTableInfoDTO.class);

        // 处理数据库表(DB)和SQL查询(SQL)类型的数据集
        if (StringUtils.equalsIgnoreCase(type, DatasetTableType.DB) || StringUtils.equalsIgnoreCase(type, DatasetTableType.SQL)) {
            // 获取数据源配置信息
            CoreDatasource coreDatasource = dataSourceManage.getCoreDatasource(datasetTableDTO.getDatasourceId());
            DatasourceSchemaDTO datasourceSchemaDTO = new DatasourceSchemaDTO();

            // 如果是Excel或API类型，使用DataEase内置的引擎数据源
            if (coreDatasource.getType().contains(DatasourceConfiguration.DatasourceType.Excel.name()) || coreDatasource.getType().contains(DatasourceConfiguration.DatasourceType.API.name())) {
                coreDatasource = engineManage.getDeEngine();
            }

            // 检查数据源状态，如果状态为Error则抛出异常
            if (StringUtils.isNotEmpty(coreDatasource.getStatus()) && "Error".equalsIgnoreCase(coreDatasource.getStatus())) {
                DEException.throwException(Translator.get("i18n_invalid_ds"));
            }

            // 复制数据源属性到Schema DTO，并设置Schema别名
            BeanUtils.copyBean(datasourceSchemaDTO, coreDatasource);
            datasourceSchemaDTO.setSchemaAlias(String.format(SQLConstants.SCHEMA, datasourceSchemaDTO.getId()));

            // 根据数据源类型获取对应的数据源Provider
            Provider provider = ProviderFactory.getProvider(coreDatasource.getType());

            // 构建数据源请求对象
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setIsCross(datasetTableDTO.getIsCross());
            datasourceRequest.setDsList(Map.of(datasourceSchemaDTO.getId(), datasourceSchemaDTO));
            String sql;

            if (StringUtils.equalsIgnoreCase(type, DatasetTableType.DB)) {
                // 处理数据库表类型：构建查询表的SQL
                // 使用LIMIT 0 OFFSET 0只获取表结构不获取数据
                sql = TableUtils.tableName2Sql(datasourceSchemaDTO, tableInfoDTO.getTable()) + " LIMIT 0 OFFSET 0";

                // 替换Schema别名并转换SQL方言
                Map map = JsonUtil.parseObject(datasourceSchemaDTO.getConfiguration(), Map.class);
                if (!datasourceRequest.getIsCross()) {
                    // 非跨数据源场景：处理schema替换
                    if (ObjectUtils.isNotEmpty(map.get("schema"))) {
                        // 如果配置了schema，用实际schema替换别名
                        sql = sql.replaceAll(SqlPlaceholderConstants.KEYWORD_PREFIX_REGEX + datasourceSchemaDTO.getSchemaAlias() + SqlPlaceholderConstants.KEYWORD_SUFFIX_REGEX, String.format(format, map.get("schema").toString()) );
                    } else {
                        // 如果没有配置schema，移除schema别名前缀
                        sql = sql.replaceAll(SqlPlaceholderConstants.KEYWORD_PREFIX_REGEX + datasourceSchemaDTO.getSchemaAlias() + SqlPlaceholderConstants.KEYWORD_SUFFIX_REGEX + "\\.", "");
                    }
                    // 转换为目标数据库的SQL方言
                    sql = provider.transSqlDialect(sql, datasourceRequest.getDsList());
                } else {
                    // 跨数据源场景：移除schema别名前缀
                    sql = sql.replaceAll(SqlPlaceholderConstants.KEYWORD_PREFIX_REGEX + datasourceSchemaDTO.getSchemaAlias() + SqlPlaceholderConstants.KEYWORD_SUFFIX_REGEX + "\\.", "");
                    // 添加跨数据源的table schema
                    String tableSchema = datasetSQLManage.putObj2Map(datasourceRequest.getDsList(), datasetTableDTO, datasourceRequest.getIsCross());
                    sql = SqlUtils.addSchema(sql, tableSchema);
                }
            } else {
                // 处理SQL查询类型：解析SQL参数并替换默认值
                // Base64解码获取原始SQL
                String s = new String(Base64.getDecoder().decode(tableInfoDTO.getSql()));
                // 处理SQL中的变量参数，替换为默认值
                String originSql = new SqlparserUtils().handleVariableDefaultValue(s, datasetTableDTO.getSqlVariableDetails(), true, false, null, datasourceRequest.getIsCross(), datasourceRequest.getDsList(), pluginManage, getUserEntity());
                // 移除SQL注释
                originSql = provider.replaceComment(originSql);

                if (!datasourceRequest.getIsCross()) {
                    // 非跨数据源场景：构建预览SQL模板
                    sql = SQLUtils.buildOriginPreviewSql(SqlPlaceholderConstants.TABLE_PLACEHOLDER, 0, 0);
                    // 转换为目标数据库的SQL方言
                    sql = provider.transSqlDialect(sql, datasourceRequest.getDsList());
                    // 将SQL占位符替换为实际SQL
                    sql = provider.replaceTablePlaceHolder(sql, originSql);
                } else {
                    // 跨数据源场景：添加table schema
                    String tableSchema = datasetSQLManage.putObj2Map(datasourceRequest.getDsList(), datasetTableDTO, datasourceRequest.getIsCross());
                    sql = SqlUtils.addSchema(originSql, tableSchema);
                }
            }

            // 格式化SQL：移除换行符，替换为空格
            datasourceRequest.setQuery(sql.replaceAll("\r\n", " ")
                    .replaceAll("\n", " "));
            logger.debug("calcite data table field sql: " + datasourceRequest.getQuery());

            // 设置表名信息（仅DB类型需要）
            if (StringUtils.equalsIgnoreCase(type, DatasetTableType.DB)) {
                datasourceRequest.setTable(tableInfoDTO.getTable());
            }

            // 调用数据源Provider获取字段信息
            tableFields = provider.fetchTableField(datasourceRequest);

        } else if (StringUtils.equalsIgnoreCase(type, DatasetTableType.Es)) {
            // 处理Elasticsearch类型的数据集
            CoreDatasource coreDatasource = dataSourceManage.getCoreDatasource(datasetTableDTO.getDatasourceId());
            Provider provider = ProviderFactory.getProvider(type);
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            DatasourceSchemaDTO datasourceSchemaDTO = new DatasourceSchemaDTO();
            BeanUtils.copyBean(datasourceSchemaDTO, coreDatasource);
            datasourceRequest.setDatasource(datasourceSchemaDTO);
            datasourceRequest.setTable(datasetTableDTO.getTableName());
            // 直接从ES索引获取字段信息
            tableFields = provider.fetchTableField(datasourceRequest);

        } else {
            // 处理Excel和API类型的数据集
            // 这些类型的数据存储在DataEase内置引擎中
            CoreDatasource coreDatasource = engineManage.getDeEngine();
            DatasourceSchemaDTO datasourceSchemaDTO = new DatasourceSchemaDTO();
            BeanUtils.copyBean(datasourceSchemaDTO, coreDatasource);
            datasourceSchemaDTO.setSchemaAlias(String.format(SQLConstants.SCHEMA, datasourceSchemaDTO.getId()));
            Provider provider = ProviderFactory.getDefaultProvider();

            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setDsList(Map.of(datasourceSchemaDTO.getId(), datasourceSchemaDTO));
            // 构建查询Excel/API表的SQL
            String sql = TableUtils.tableName2Sql(datasourceSchemaDTO, tableInfoDTO.getTable()) + " LIMIT 0 OFFSET 0";
            // 替换Schema别名并转换SQL方言
            sql = Utils.replaceSchemaAlias(sql, datasourceRequest.getDsList());
            sql = provider.transSqlDialect(sql, datasourceRequest.getDsList());
            datasourceRequest.setQuery(sql);
            logger.debug("calcite data table field sql: " + datasourceRequest.getQuery());
            // 从DataEase引擎获取字段信息
            tableFields = provider.fetchTableField(datasourceRequest);
        }

        // 将数据源原始字段转换为DataEase标准字段格式
        return transFields(tableFields, true);
    }

    /**
     * 将数据源原始字段转换为DataEase字段格式
     * 统一字段的类型、维度/维度标识、扩展字段类型等属性
     *
     * @param tableFields 数据源返回的原始字段列表
     * @param defaultStatus 字段的默认选中状态
     * @return 转换后的DataEase字段列表
     */
    public List<DatasetTableFieldDTO> transFields(List<TableField> tableFields, boolean defaultStatus) {
        return tableFields.stream().map(ele -> {
            DatasetTableFieldDTO dto = new DatasetTableFieldDTO();
            dto.setName(StringUtils.isNotEmpty(ele.getName()) ? ele.getName() : ele.getOriginName());
            dto.setOriginName(ele.getOriginName());
            dto.setChecked(defaultStatus);
            dto.setType(ele.getType());
            int deType = FieldUtils.transType2DeType(ele.getType());
            dto.setDeExtractType(ObjectUtils.isEmpty(ele.getDeExtractType()) ? deType : ele.getDeExtractType());
            dto.setDeType(ObjectUtils.isEmpty(ele.getDeType()) ? deType : ele.getDeType());
            dto.setGroupType(FieldUtils.transDeType2DQ(dto.getDeType()));
            dto.setExtField(0);
            dto.setDescription(StringUtils.isNotEmpty(ele.getName()) ? ele.getName() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 带分页的数据预览
     * 执行数据集查询并返回指定范围的数据，支持权限检查和数据编码
     *
     * @param datasetGroupInfoDTO 数据集信息，包含SQL、字段、排序等
     * @param start 起始位置，从0开始
     * @param count 返回的数据条数
     * @param checkPermission 是否检查列权限和行权限
     * @param encode 是否对敏感信息进行编码
     * @return 包含数据、字段、SQL的结果Map
     * @throws Exception 数据源异常或权限异常
     */
    public Map<String, Object> previewDataWithLimit(DatasetGroupInfoDTO datasetGroupInfoDTO, Integer start, Integer count, boolean checkPermission, boolean encode) throws Exception {
        // 如果需要编码，先对计算字段进行Base64解码
        if (encode) {
            DatasetUtils.dsDecode(datasetGroupInfoDTO);
        }

        // 获取数据集的联合SQL及数据源映射信息
        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, null);
        String sql = (String) sqlMap.get("sql");

        // 获取数据集的所有字段
        List<DatasetTableFieldDTO> fields = datasetGroupInfoDTO.getAllFields();
        if (ObjectUtils.isEmpty(fields)) {
            DEException.throwException(Translator.get("i18n_no_fields"));
        }

        // 列权限过滤和脱敏处理
        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        if (checkPermission) {
            // 根据用户权限过滤字段，获取脱敏字段列表
            fields = permissionManage.filterColumnPermissions(fields, desensitizationList, datasetGroupInfoDTO.getId(), null);
            if (ObjectUtils.isEmpty(fields)) {
                DEException.throwException(Translator.get("i18n_no_column_permission"));
            }
        }

        // 构建字段名称（dataeaseName和fieldShortName）
        buildFieldName(sqlMap, fields);

        // 获取数据源映射表
        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        // 检查数据源状态，如果有Error状态的数据源则抛出异常
        DatasourceUtils.checkDsStatus(dsMap);

        // 收集所有数据源的类型
        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }

        // 判断是否需要添加排序（某些数据源需要排序来保证结果一致性）
        boolean needOrder = Utils.isNeedOrder(dsList);
        // 判断是否为跨数据源查询
        boolean crossDs = datasetGroupInfoDTO.getIsCross();

        if (!crossDs) {
            // 非跨数据源场景：检查是否支持完整连接
            if (notFullDs.contains(dsMap.entrySet().iterator().next().getValue().getType()) && (boolean) sqlMap.get("isFullJoin")) {
                DEException.throwException(Translator.get("i18n_not_full"));
            }
            // 替换SQL中的schema别名为实际的schema
            sql = Utils.replaceSchemaAlias(sql, dsMap);
        }

        // 获取行权限树（用于数据行级别的权限过滤）
        List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = new ArrayList<>();
        TokenUserBO user = AuthUtils.getUser();
        if (user != null && checkPermission) {
            rowPermissionsTree = permissionManage.getRowPermissionsTree(datasetGroupInfoDTO.getId(), user.getUserId());
        }

        // 根据是否跨数据源选择对应的Provider
        Provider provider;
        if (crossDs) {
            // 跨数据源使用默认Provider（Calcite）
            provider = ProviderFactory.getDefaultProvider();
        } else {
            // 单数据源使用对应类型的Provider
            provider = ProviderFactory.getProvider(dsList.getFirst());
        }

        // 构建查询SQL的元数据对象
        SQLMeta sqlMeta = new SQLMeta();
        // 将SQL转换为SQL对象，添加到元数据中
        Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);
        // 处理字段，生成字段SQL
        Field2SQLObj.field2sqlObj(sqlMeta, fields, fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);
        // 处理行权限过滤条件，转换为WHERE子句
        WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);
        // 处理排序字段，生成ORDER BY子句
        Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);

        // 生成查询SQL
        String querySQL;
        if (start == null || count == null) {
            // 没有分页参数，生成不带LIMIT的SQL
            querySQL = SQLProvider.createQuerySQL(sqlMeta, false, needOrder, false);
        } else {
            // 有分页参数，生成带LIMIT的SQL
            querySQL = SQLProvider.createQuerySQLWithLimit(sqlMeta, false, needOrder, false, start, count);
        }

        // 重建SQL（处理方言转换等）
        querySQL = provider.rebuildSQL(querySQL, sqlMeta, crossDs, dsMap);
        logger.debug("calcite data preview sql: " + querySQL);

        // 执行查询
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setQuery(querySQL);
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);
        // 通过数据源Provider获取查询结果
        Map<String, Object> data = provider.fetchResultField(datasourceRequest);

        // 构建返回结果
        Map<String, Object> map = new LinkedHashMap<>();
        // 处理数据：脱敏、编码等
        Map<String, Object> previewData = buildPreviewData(data, fields, desensitizationList, encode);
        map.put("data", previewData);

        // 返回字段列表
        if (ObjectUtils.isEmpty(datasetGroupInfoDTO.getId())) {
            // 新建数据集（没有ID），使用当前字段
            map.put("allFields", fields);
        } else {
            // 已存在的数据集，从数据库查询字段信息
            List<DatasetTableFieldDTO> fieldList = datasetTableFieldManage.selectByDatasetGroupId(datasetGroupInfoDTO.getId());
            if (encode) {
                // 如果需要编码，对计算字段进行Base64编码
                DatasetUtils.listEncode(fieldList);
            }
            map.put("allFields", fieldList);
        }

        // 将执行的SQL进行Base64编码后返回
        map.put("sql", Base64.getEncoder().encodeToString(querySQL.getBytes()));
        return map;
    }

    /**
     * 获取数据集的总数据量
     * 执行COUNT查询获取数据集的总记录数
     *
     * @param datasetGroupId 数据集ID
     * @return 数据集总记录数，如果数据集不存在或为文件夹类型则返回0
     * @throws Exception 查询异常
     */
    public Long getDatasetTotal(Long datasetGroupId) throws Exception {
        DatasetGroupInfoDTO dto = datasetGroupManage.getForCount(datasetGroupId);
        if (ObjectUtils.isEmpty(dto)) return 0L;
        if (StringUtils.equalsIgnoreCase(dto.getNodeType(), "dataset")) {
            return getDatasetTotal(dto, null, new ChartExtRequest());
        }
        return 0L;
    }

    /**
     * 获取带权限过滤的数据集总数
     * 在应用行权限过滤后统计数据集的记录数
     *
     * @param datasetGroupId 数据集ID
     * @return 数据集记录数，查询失败返回null
     */
    public Long getDatasetCountWithWhere(Long datasetGroupId) {
        try {
            DatasetGroupInfoDTO datasetGroupInfoDTO = datasetGroupManage.getForCount(datasetGroupId);
            Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, null);
            String sql = (String) sqlMap.get("sql");

            // 获取allFields
            List<DatasetTableFieldDTO> fields = datasetGroupInfoDTO.getAllFields();
            if (ObjectUtils.isEmpty(fields)) {
                DEException.throwException(Translator.get("i18n_no_fields"));
            }

            buildFieldName(sqlMap, fields);

            Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
            DatasourceUtils.checkDsStatus(dsMap);
            List<String> dsList = new ArrayList<>();
            for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
                dsList.add(next.getValue().getType());
            }
            boolean crossDs = datasetGroupInfoDTO.getIsCross();
            if (!crossDs) {
                if (notFullDs.contains(dsMap.entrySet().iterator().next().getValue().getType()) && (boolean) sqlMap.get("isFullJoin")) {
                    DEException.throwException(Translator.get("i18n_not_full"));
                }
                sql = Utils.replaceSchemaAlias(sql, dsMap);
            }

            List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = new ArrayList<>();
            TokenUserBO user = AuthUtils.getUser();
            if (user != null) {
                rowPermissionsTree = permissionManage.getRowPermissionsTree(datasetGroupInfoDTO.getId(), user.getUserId());
            }

            Provider provider;
            if (crossDs) {
                provider = ProviderFactory.getDefaultProvider();
            } else {
                provider = ProviderFactory.getProvider(dsList.getFirst());
            }

            // build query sql
            SQLMeta sqlMeta = new SQLMeta();
            Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);
            Field2SQLObj.field2sqlObj(sqlMeta, fields, fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);
            WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);
            Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), fields, crossDs, dsMap, Utils.getParams(fields), null, pluginManage);
            String replaceSql = provider.rebuildSQL(SQLProvider.createQuerySQL(sqlMeta, false, false, false), sqlMeta, crossDs, dsMap);
            return getDatasetTotal(datasetGroupInfoDTO, replaceSql, null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 执行COUNT查询获取数据集总数
     * 支持自定义SQL和扩展请求参数
     *
     * @param datasetGroupInfoDTO 数据集信息
     * @param s 自定义SQL，为null时使用数据集的默认SQL
     * @param request 扩展请求参数（过滤器、参数等）
     * @return 数据集总记录数
     * @throws Exception 查询异常
     */
    public Long getDatasetTotal(DatasetGroupInfoDTO datasetGroupInfoDTO, String s, ChartExtRequest request) throws Exception {
        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, request);
        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        boolean crossDs = datasetGroupInfoDTO.getIsCross();
        String sql;
        if (StringUtils.isEmpty(s)) {
            sql = (String) sqlMap.get("sql");
            if (!crossDs) {
                sql = Utils.replaceSchemaAlias(sql, dsMap);
            }
        } else {
            sql = s;
        }
        String querySQL = "SELECT COUNT(*) FROM (" + sql + ") t_a_0";
        logger.debug("calcite data count sql: " + querySQL);

        // 通过数据源请求数据
        // 调用数据源的calcite获得data
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setQuery(querySQL);
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsMap.entrySet().iterator().next().getValue().getType());
        }
        Map<String, Object> data = provider.fetchResultField(datasourceRequest);
        List<String[]> dataList = (List<String[]>) data.get("data");
        if (ObjectUtils.isNotEmpty(dataList) && ObjectUtils.isNotEmpty(dataList.get(0)) && ObjectUtils.isNotEmpty(dataList.get(0)[0])) {
            return Long.valueOf(dataList.get(0)[0]);
        }
        return 0L;
    }

    /**
     * 带日志记录的SQL预览
     * 执行SQL查询并记录执行日志（开始时间、结束时间、耗时、状态）
     * 用于数据集编辑器中的SQL测试功能
     *
     * @param dto SQL预览请求，包含SQL、数据源、参数等信息
     * @return 预览结果，包含数据、字段、执行的SQL
     */
    public Map<String, Object> previewSqlWithLog(PreviewSqlDTO dto) {
        if (dto == null) {
            return null;
        }
        SqlLogDTO sqlLogDTO = new SqlLogDTO();
        String sql = new String(Base64.getDecoder().decode(dto.getSql()));
        sqlLogDTO.setSql(sql);
        Map<String, Object> map = null;
        try {
            sqlLogDTO.setStartTime(System.currentTimeMillis());
            map = previewSql(dto);
            sqlLogDTO.setEndTime(System.currentTimeMillis());
            sqlLogDTO.setSpend(sqlLogDTO.getEndTime() - sqlLogDTO.getStartTime());
            sqlLogDTO.setStatus("Completed");
        } catch (Exception e) {
            sqlLogDTO.setStatus("Error");
            DEException.throwException(e.getMessage());
        } finally {
            if (ObjectUtils.isNotEmpty(dto.getTableId())) {
                sqlLogDTO.setTableId(dto.getTableId());
                datasetTableSqlLogManage.save(sqlLogDTO);
            }
        }
        return map;
    }

    /**
     * 获取当前登录用户信息
     * 用于SQL变量替换中的用户相关参数
     *
     * @return 用户信息，企业版返回详细用户信息，社区版返回null
     */
    private UserFormVO getUserEntity() {
        if (getRowPermissionsApi() == null) {
            return null;
        }
        return getRowPermissionsApi().getUserById(AuthUtils.getUser().getUserId());
    }

    /**
     * SQL预览核心方法
     * 解析SQL参数、处理变量、执行查询并返回结果
     * 支持跨数据源查询和多种数据源类型
     *
     * @param dto SQL预览请求参数
     * @return 预览结果
     * @throws DEException SQL解析错误或数据源异常
     */
    public Map<String, Object> previewSql(PreviewSqlDTO dto) throws DEException {
        CoreDatasource coreDatasource = dataSourceManage.getCoreDatasource(dto.getDatasourceId());
        DatasourceSchemaDTO datasourceSchemaDTO = new DatasourceSchemaDTO();
        if (coreDatasource.getType().contains(DatasourceConfiguration.DatasourceType.API.name()) || coreDatasource.getType().contains(DatasourceConfiguration.DatasourceType.Excel.name())) {
            BeanUtils.copyBean(datasourceSchemaDTO, engineManage.getDeEngine());
        } else {
            BeanUtils.copyBean(datasourceSchemaDTO, coreDatasource);
        }

        if (StringUtils.isNotEmpty(datasourceSchemaDTO.getStatus()) && "Error".equalsIgnoreCase(datasourceSchemaDTO.getStatus())) {
            DEException.throwException(Translator.get("i18n_invalid_ds"));
        }

        String alias = String.format(SQLConstants.SCHEMA, datasourceSchemaDTO.getId());
        datasourceSchemaDTO.setSchemaAlias(alias);

        Map<Long, DatasourceSchemaDTO> dsMap = new LinkedHashMap<>();
        dsMap.put(datasourceSchemaDTO.getId(), datasourceSchemaDTO);
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(dto.getIsCross());
        Provider provider = ProviderFactory.getProvider(datasourceSchemaDTO.getType());

        // parser sql params and replace default value

        String s = new String(Base64.getDecoder().decode(dto.getSql()));
        String originSql = new SqlparserUtils().handleVariableDefaultValue(datasetSQLManage.subPrefixSuffixChar(s), dto.getSqlVariableDetails(), true, true, null, dto.getIsCross(), dsMap, pluginManage, getUserEntity());
        originSql = provider.replaceComment(originSql);

        // sql 作为临时表，外层加上limit
        String sql;
        if (dto.getIsCross()) {
            DatasetTableDTO currentDs = new DatasetTableDTO();
            BeanUtils.copyBean(currentDs, dto);
            currentDs.setType("sql");
            String tableSchema = datasetSQLManage.putObj2Map(dsMap, currentDs, dto.getIsCross());
            sql = SqlUtils.addSchema(originSql, tableSchema);
            if (Utils.isNeedOrder(List.of(datasourceSchemaDTO.getType()))) {
                // 先根据sql获取表字段
                String sqlField = SQLUtils.buildOriginPreviewSql(sql, 0, 0);
                datasourceRequest.setQuery(sqlField);

                // 获取数据源表的原始字段
                List<TableField> list = provider.fetchTableField(datasourceRequest);
                if (ObjectUtils.isEmpty(list)) {
                    return null;
                }
                sql = SQLUtils.buildOriginPreviewSqlWithOrderBy(sql, 100, 0, String.format(SQLConstants.FIELD_DOT_FIX, list.get(0).getOriginName()) + " ASC ");
            } else {
                sql = SQLUtils.buildOriginPreviewSql(sql, 100, 0);
            }
        } else {
            if (Utils.isNeedOrder(List.of(datasourceSchemaDTO.getType()))) {
                // 先根据sql获取表字段
                String sqlField = SQLUtils.buildOriginPreviewSql(SqlPlaceholderConstants.TABLE_PLACEHOLDER, 0, 0);

                sqlField = provider.transSqlDialect(sqlField, datasourceRequest.getDsList());
                // replace placeholder
                sqlField = provider.replaceTablePlaceHolder(sqlField, originSql);
                datasourceRequest.setQuery(sqlField);

                // 获取数据源表的原始字段
                List<TableField> list = provider.fetchTableField(datasourceRequest);
                if (ObjectUtils.isEmpty(list)) {
                    return null;
                }
                sql = SQLUtils.buildOriginPreviewSqlWithOrderBy(SqlPlaceholderConstants.TABLE_PLACEHOLDER, 100, 0, String.format(SQLConstants.FIELD_DOT_FIX, list.get(0).getOriginName()) + " ASC ");
            } else {
                sql = SQLUtils.buildOriginPreviewSql(SqlPlaceholderConstants.TABLE_PLACEHOLDER, 100, 0);
            }
            sql = provider.transSqlDialect(sql, datasourceRequest.getDsList());
            // replace placeholder
            sql = provider.replaceTablePlaceHolder(sql, originSql);
        }

        logger.debug("calcite data preview sql: " + sql);
        datasourceRequest.setQuery(sql);
        Map<String, Object> data = provider.fetchResultField(datasourceRequest);
        // 重新构造data
        List<TableField> fList = (List<TableField>) data.get("fields");
        List<DatasetTableFieldDTO> fields = transFields(fList, false);
        Map<String, Object> previewData = buildPreviewData(data, fields, new HashMap<>(), false);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("data", previewData);
        map.put("sql", Base64.getEncoder().encodeToString(sql.getBytes()));
        return map;
    }

    /**
     * 构建预览数据
     * 将查询结果转换为前端需要的格式，处理数据脱敏和编码
     *
     * @param data 原始查询结果
     * @param fields 字段列表
     * @param desensitizationList 脱敏字段列表
     * @param isEncode 是否编码敏感信息
     * @return 格式化后的数据Map
     */
    public Map<String, Object> buildPreviewData(Map<String, Object> data, List<DatasetTableFieldDTO> fields, Map<String, ColumnPermissionItem> desensitizationList, boolean isEncode) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<String[]> dataList = (List<String[]>) data.get("data");
        List<LinkedHashMap<String, Object>> dataObjectList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                String[] row = dataList.get(i);
                LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
                if (row.length > 0) {
                    for (int j = 0; j < fields.size(); j++) {
                        String res = row[j];
                        // 如果字段类型是数值类型的小数，则去除科学计数
                        if (fields.get(j).getDeType() == 3 && StringUtils.containsIgnoreCase(res, "E")) {
                            BigDecimal bigDecimal = new BigDecimal(res);
                            res = String.format("%.8f", bigDecimal);
                        }
                        if (desensitizationList.keySet().contains(fields.get(j).getDataeaseName())) {
                            obj.put(fields.get(j).getDataeaseName(), ChartDataBuild.desensitizationValue(desensitizationList.get(fields.get(j).getDataeaseName()), String.valueOf(res)));
                        } else {
                            obj.put(ObjectUtils.isNotEmpty(fields.get(j).getDataeaseName()) ?
                                    fields.get(j).getDataeaseName() : fields.get(j).getOriginName(), res);
                        }
                    }
                }
                dataObjectList.add(obj);
            }
        }

        if (isEncode) {
            DatasetUtils.listEncode(fields);
        }

        map.put("fields", fields);
        map.put("data", dataObjectList);
        return map;
    }

    /**
     * 构建字段名称
     * 为字段设置dataeaseName和fieldShortName，用于SQL生成
     * 区分原始字段、计算字段、分组字段的不同命名规则
     *
     * @param sqlMap SQL映射信息
     * @param fields 需要构建名称的字段列表
     */
    public void buildFieldName(Map<String, Object> sqlMap, List<DatasetTableFieldDTO> fields) {
        // 获取内层union sql和字段
        List<DatasetTableFieldDTO> unionFields = (List<DatasetTableFieldDTO>) sqlMap.get("field");
        for (DatasetTableFieldDTO datasetTableFieldDTO : fields) {
            DatasetTableFieldDTO dto = datasetTableFieldManage.selectById(datasetTableFieldDTO.getId());
            if (ObjectUtils.isEmpty(dto)) {
                if (Objects.equals(datasetTableFieldDTO.getExtField(), ExtFieldConstant.EXT_NORMAL)) {
                    for (DatasetTableFieldDTO fieldDTO : unionFields) {
                        if (Objects.equals(datasetTableFieldDTO.getDatasetTableId(), fieldDTO.getDatasetTableId())
                                && Objects.equals(datasetTableFieldDTO.getOriginName(), fieldDTO.getOriginName())) {
                            datasetTableFieldDTO.setDataeaseName(fieldDTO.getDataeaseName());
                            datasetTableFieldDTO.setFieldShortName(fieldDTO.getFieldShortName());
                        }
                    }
                }
                if (Objects.equals(datasetTableFieldDTO.getExtField(), ExtFieldConstant.EXT_CALC)) {
                    String dataeaseName = TableUtils.fieldNameShort(datasetTableFieldDTO.getId() + "_" + datasetTableFieldDTO.getOriginName());
                    datasetTableFieldDTO.setDataeaseName(dataeaseName);
                    datasetTableFieldDTO.setFieldShortName(dataeaseName);
                    datasetTableFieldDTO.setDeExtractType(datasetTableFieldDTO.getDeType());
                }
                if (Objects.equals(datasetTableFieldDTO.getExtField(), ExtFieldConstant.EXT_GROUP)) {
                    String dataeaseName = TableUtils.fieldNameShort(datasetTableFieldDTO.getId() + "_" + datasetTableFieldDTO.getOriginName());
                    datasetTableFieldDTO.setDataeaseName(dataeaseName);
                    datasetTableFieldDTO.setFieldShortName(dataeaseName);
                    datasetTableFieldDTO.setDeExtractType(0);
                    datasetTableFieldDTO.setDeType(0);
                    datasetTableFieldDTO.setGroupType("d");
                }
            } else {
                datasetTableFieldDTO.setDataeaseName(dto.getDataeaseName());
                datasetTableFieldDTO.setFieldShortName(dto.getFieldShortName());
            }
        }
    }

    /**
     * 获取字段枚举值（跨数据源）
     * 查询字段的所有不重复值，用于下拉筛选组件
     *
     * @param map 包含字段和数据集信息的枚举对象
     * @return 字段的所有不重复值列表
     * @throws Exception 查询异常
     */
    public List<String> getFieldEnumDs(EnumObj map) throws Exception {
        DatasetTableFieldDTO field = map.getField();
        DatasetGroupInfoDTO datasetGroupInfoDTO = map.getDataset();
        if (field == null) {
            DEException.throwException(Translator.get("i18n_no_field"));
        }
        List<DatasetTableFieldDTO> allFields = new ArrayList<>();

        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, new ChartExtRequest());
        String sql = (String) sqlMap.get("sql");

        allFields.addAll(datasetGroupInfoDTO.getAllFields());

        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        boolean crossDs = datasetGroupInfoDTO.getIsCross();
        if (!crossDs) {
            sql = Utils.replaceSchemaAlias(sql, dsMap);
        }

        // build query sql
        SQLMeta sqlMeta = new SQLMeta();
        Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);

        // 获取allFields
        List<DatasetTableFieldDTO> fields = Collections.singletonList(field);
        buildFieldName(sqlMap, fields);

        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsList.getFirst());
        }

        String dsType = null;
        if (dsMap != null && dsMap.entrySet().iterator().hasNext()) {
            Map.Entry<Long, DatasourceSchemaDTO> next = dsMap.entrySet().iterator().next();
            dsType = next.getValue().getType();
        }

        Field2SQLObj.field2sqlObj(sqlMeta, fields, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        WhereTree2Str.transFilterTrees(sqlMeta, null, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        String querySQL;
        querySQL = SQLProvider.createQuerySQL(sqlMeta, false, needOrder, !StringUtils.equalsIgnoreCase(dsType, "es"));
        querySQL = provider.rebuildSQL(querySQL, sqlMeta, crossDs, dsMap);
        logger.debug("calcite data enum sql: " + querySQL);

        // 通过数据源请求数据
        // 调用数据源的calcite获得data
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setQuery(querySQL);
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);

        Map<String, Object> data = provider.fetchResultField(datasourceRequest);
        List<String[]> dataList = (List<String[]>) data.get("data");
        dataList = dataList.stream().filter(row -> {
            boolean hasEmpty = false;
            for (String s : row) {
                if (StringUtils.isBlank(s)) {
                    hasEmpty = true;
                    break;
                }
            }
            return !hasEmpty;
        }).toList();
        List<String> previewData = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(dataList)) {
            List<String> tmpData = dataList.stream().map(ele -> (ObjectUtils.isNotEmpty(ele) && ele.length > 0) ? ele[0] : null).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(tmpData)) {
                for (int i = 0; i < tmpData.size(); i++) {
                    String val = tmpData.get(i);
                    if (field.getDeType() == 3 && StringUtils.containsIgnoreCase(val, "E")) {
                        BigDecimal bigDecimal = new BigDecimal(val);
                        val = String.format("%.8f", bigDecimal);
                        tmpData.set(i, val);
                    }
                }
                previewData = tmpData;
            }
        }
        return previewData;
    }

    /**
     * 获取多个字段的枚举值并去重合并
     * 用于查询组件，支持多字段联合查询
     * 会应用权限过滤和数据脱敏
     *
     * @param multFieldValuesRequest 多字段查询请求，包含字段ID列表、过滤条件等
     * @return 所有字段的不重复值列表
     * @throws Exception 查询异常或权限异常
     */
    public List<String> getFieldEnum(MultFieldValuesRequest multFieldValuesRequest) throws Exception {
        if (CollectionUtils.isEmpty(multFieldValuesRequest.getFieldIds())) {
            return Collections.emptyList();
        }
        // 根据前端传的查询组件field ids，获取所有字段枚举值并去重合并
        List<List<String>> list = new ArrayList<>();
        for (Long id : new LinkedHashSet<>(multFieldValuesRequest.getFieldIds())) {
            DatasetTableFieldDTO field = datasetTableFieldManage.selectById(id);
            if (field == null) {
                DEException.throwException(Translator.get("i18n_no_field"));
            }
            List<DatasetTableFieldDTO> allFields = new ArrayList<>();
            // 根据图表计算字段，获取数据集
            Long datasetGroupId = field.getDatasetGroupId();

            // check permission
            BusiPerCheckDTO dto = new BusiPerCheckDTO();
            dto.setId(datasetGroupId);
            dto.setAuthEnum(AuthEnum.READ);
            boolean checked = corePermissionManage.checkAuth(dto);
            if (!checked) {
                DEException.throwException(Translator.get("i18n_no_dataset_permission"));
            }
            if (field.getChartId() != null) {
                allFields.addAll(datasetTableFieldManage.getChartCalcFields(field.getChartId()));
            }
            DatasetGroupInfoDTO datasetGroupInfoDTO = datasetGroupManage.getDatasetGroupInfoDTO(datasetGroupId, null);

            Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, new ChartExtRequest());
            String sql = (String) sqlMap.get("sql");

            allFields.addAll(datasetGroupInfoDTO.getAllFields());

            Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
            boolean crossDs = datasetGroupInfoDTO.getIsCross();
            if (!crossDs) {
                sql = Utils.replaceSchemaAlias(sql, dsMap);
            }

            // build query sql
            SQLMeta sqlMeta = new SQLMeta();
            Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);

            // 获取allFields
            List<DatasetTableFieldDTO> fields = Collections.singletonList(field);
            Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
            fields = permissionManage.filterColumnPermissions(fields, desensitizationList, datasetGroupInfoDTO.getId(), null);
            if (ObjectUtils.isEmpty(fields)) {
                DEException.throwException(Translator.get("i18n_no_column_permission"));
            }
            buildFieldName(sqlMap, fields);

            List<String> dsList = new ArrayList<>();
            for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
                dsList.add(next.getValue().getType());
            }
            boolean needOrder = Utils.isNeedOrder(dsList);

            List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = new ArrayList<>();
            TokenUserBO user = AuthUtils.getUser();
            if (user != null) {
                rowPermissionsTree = permissionManage.getRowPermissionsTree(datasetGroupInfoDTO.getId(), user.getUserId());
            }

            Provider provider;
            if (crossDs) {
                provider = ProviderFactory.getDefaultProvider();
            } else {
                provider = ProviderFactory.getProvider(dsList.getFirst());
            }

            String dsType = null;
            if (dsMap != null && dsMap.entrySet().iterator().hasNext()) {
                Map.Entry<Long, DatasourceSchemaDTO> next = dsMap.entrySet().iterator().next();
                dsType = next.getValue().getType();
            }

            Field2SQLObj.field2sqlObj(sqlMeta, fields, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
            WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
            Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
            String querySQL;
            if (multFieldValuesRequest.getResultMode() == 0) {
                querySQL = SQLProvider.createQuerySQLWithLimit(sqlMeta, false, needOrder, !StringUtils.equalsIgnoreCase(dsType, "es"), 0, 1000);
            } else {
                querySQL = SQLProvider.createQuerySQL(sqlMeta, false, needOrder, !StringUtils.equalsIgnoreCase(dsType, "es"));
            }
            querySQL = provider.rebuildSQL(querySQL, sqlMeta, crossDs, dsMap);
            logger.debug("calcite data enum sql: " + querySQL);

            // 通过数据源请求数据
            // 调用数据源的calcite获得data
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setQuery(querySQL);
            datasourceRequest.setDsList(dsMap);
            datasourceRequest.setIsCross(crossDs);

            Map<String, Object> data = provider.fetchResultField(datasourceRequest);
            List<String[]> dataList = (List<String[]>) data.get("data");
            dataList = dataList.stream().filter(row -> {
                boolean hasEmpty = false;
                for (String s : row) {
                    if (StringUtils.isBlank(s)) {
                        hasEmpty = true;
                        break;
                    }
                }
                return !hasEmpty;
            }).toList();
            List<String> previewData = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(dataList)) {
                List<String> tmpData = dataList.stream().map(ele -> (ObjectUtils.isNotEmpty(ele) && ele.length > 0) ? ele[0] : null).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(tmpData)) {
                    for (int i = 0; i < tmpData.size(); i++) {
                        String val = tmpData.get(i);
                        if (field.getDeType() == 3 && StringUtils.containsIgnoreCase(val, "E")) {
                            BigDecimal bigDecimal = new BigDecimal(val);
                            val = String.format("%.8f", bigDecimal);
                            tmpData.set(i, val);
                        }
                    }
                    if (desensitizationList.keySet().contains(field.getDataeaseName())) {
                        for (int i = 0; i < tmpData.size(); i++) {
                            previewData.add(ChartDataBuild.desensitizationValue(desensitizationList.get(field.getDataeaseName()), tmpData.get(i)));
                        }
                    } else {
                        previewData = tmpData;
                    }
                }
                list.add(previewData);
            }
        }

        // 重新构造data
        Set<String> result = new LinkedHashSet<>();
        for (List<String> l : list) {
            result.addAll(l);
        }
        return result.stream().toList();
    }

    /**
     * 获取字段枚举值对象列表
     * 返回包含字段ID和值对应关系的对象列表
     * 支持排序、搜索、过滤等高级功能
     *
     * @param request 枚举值请求，包含查询字段、显示字段、排序字段、过滤条件等
     * @return 字段值对象列表，格式: [{字段ID: 值}, ...]
     * @throws Exception 查询异常
     */
    public List<Map<String, Object>> getFieldEnumObj(EnumValueRequest request) throws Exception {
        List<Long> ids = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(request.getQueryId())) {
            ids.add(request.getQueryId());
        }
        if (ObjectUtils.isNotEmpty(request.getDisplayId())) {
            ids.add(request.getDisplayId());
        }

        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        if (ids.size() == 2 && Objects.equals(ids.get(0), ids.get(1))) {
            ids.remove(1);
        }

        SQLMeta sqlMeta = new SQLMeta();
        DatasetGroupInfoDTO datasetGroupInfoDTO = null;
        List<DatasetTableFieldDTO> fields = new ArrayList<>();
        Map<String, Object> sqlMap = null;
        boolean crossDs = false;
        Map<Long, DatasourceSchemaDTO> dsMap = null;

        if (ObjectUtils.isNotEmpty(request.getSortId())) {
            // 如果排序字段和查询字段显示字段不一致，则加入到查询列表中
            if (!request.getSortId().equals(request.getQueryId()) && !request.getSortId().equals(request.getDisplayId())) {
                ids.add(request.getSortId());
            }
        }

        List<DatasetTableFieldDTO> allFields = new ArrayList<>();

        for (Long id : ids) {
            DatasetTableFieldDTO field = datasetTableFieldManage.selectById(id);
            if (field == null) {
                DEException.throwException(Translator.get("i18n_no_field"));
            }

            // 根据图表计算字段，获取数据集
            Long datasetGroupId = field.getDatasetGroupId();

            // check permission
            BusiPerCheckDTO dto = new BusiPerCheckDTO();
            dto.setId(datasetGroupId);
            dto.setAuthEnum(AuthEnum.READ);
            boolean checked = corePermissionManage.checkAuth(dto);
            if (!checked) {
                DEException.throwException(Translator.get("i18n_no_dataset_permission"));
            }

            if (field.getChartId() != null) {
                allFields.addAll(datasetTableFieldManage.getChartCalcFields(field.getChartId()));
            }
            datasetGroupInfoDTO = datasetGroupManage.getDatasetGroupInfoDTO(datasetGroupId, null);

            sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, new ChartExtRequest());
            String sql = (String) sqlMap.get("sql");

            allFields.addAll(datasetGroupInfoDTO.getAllFields());

            dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
            crossDs = datasetGroupInfoDTO.getIsCross();
            if (!crossDs) {
                sql = Utils.replaceSchemaAlias(sql, dsMap);
            }

            // build query sql
            Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);
            fields.add(field);
        }

        // 获取allFields
        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        fields = permissionManage.filterColumnPermissions(fields, desensitizationList, datasetGroupInfoDTO.getId(), null);
        if (ObjectUtils.isEmpty(fields)) {
            DEException.throwException(Translator.get("i18n_no_column_permission"));
        }
        buildFieldName(sqlMap, fields);

        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);

        List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = new ArrayList<>();
        TokenUserBO user = AuthUtils.getUser();
        if (user != null) {
            rowPermissionsTree = permissionManage.getRowPermissionsTree(datasetGroupInfoDTO.getId(), user.getUserId());
        }

        //组件过滤条件
        List<ChartExtFilterDTO> extFilterList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(request.getFilter())) {
            for (ChartExtFilterDTO filterDTO : request.getFilter()) {
                // 解析多个fieldId,fieldId是一个逗号分隔的字符串
                String fieldId = filterDTO.getFieldId();
                if (filterDTO.getIsTree() == null) {
                    filterDTO.setIsTree(false);
                }

                boolean hasParameters = false;
                List<SqlVariableDetails> sqlVariables = datasetGroupManage.getSqlParams(Arrays.asList(datasetGroupInfoDTO.getId()));
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(sqlVariables)) {
                    for (SqlVariableDetails parameter : Optional.ofNullable(filterDTO.getParameters()).orElse(new ArrayList<>())) {
                        String parameterId = StringUtils.endsWith(parameter.getId(), START_END_SEPARATOR) ? parameter.getId().split(START_END_SEPARATOR)[0] : parameter.getId();
                        if (sqlVariables.stream().map(SqlVariableDetails::getId).collect(Collectors.toList()).contains(parameterId)) {
                            hasParameters = true;
                        }
                    }
                }

                if (hasParameters) {
                    continue;
                }

                if (StringUtils.isNotEmpty(fieldId)) {
                    List<Long> fieldIds = Arrays.stream(fieldId.split(",")).map(Long::valueOf).collect(Collectors.toList());

                    if (filterDTO.getIsTree()) {
                        ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                        BeanUtils.copyBean(filterRequest, filterDTO);
                        filterRequest.setDatasetTableFieldList(new ArrayList<>());
                        for (Long fId : fieldIds) {
                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), datasetGroupInfoDTO.getId())) {
                                filterRequest.getDatasetTableFieldList().add(datasetTableField);
                            }
                        }
                        if (ObjectUtils.isNotEmpty(filterRequest.getDatasetTableFieldList())) {
                            extFilterList.add(filterRequest);
                        }
                    } else {
                        for (Long fId : fieldIds) {
                            ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                            BeanUtils.copyBean(filterRequest, filterDTO);
                            filterRequest.setFieldId(fId + "");

                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            filterRequest.setDatasetTableField(datasetTableField);
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), datasetGroupInfoDTO.getId())) {
                                extFilterList.add(filterRequest);
                            }
                        }
                    }
                }
            }
        }

        // 搜索备选项
        if (StringUtils.isNotEmpty(request.getSearchText())) {
            ChartExtFilterDTO dto = new ChartExtFilterDTO();
            DatasetTableFieldDTO field = null;
            if (ids.size() == 1) {
                field = datasetTableFieldManage.selectById(ids.get(0));
            } else {
                field = datasetTableFieldManage.selectById(ids.get(1));
            }
            dto.setDatasetTableField(field);
            dto.setFieldId(field.getId() + "");
            dto.setIsTree(false);
            dto.setOperator("like");
            dto.setValue(List.of(request.getSearchText()));
            extFilterList.add(dto);
        }

        // 排序
        boolean sortDistinct = true;
        if (ObjectUtils.isNotEmpty(request.getSortId())) {
            DatasetTableFieldDTO field = datasetTableFieldManage.selectById(request.getSortId());
            if (field == null) {
                DEException.throwException(Translator.get("i18n_no_field"));
            }
            DeSortField deSortField = new DeSortField();
            BeanUtils.copyBean(deSortField, field);
            deSortField.setOrderDirection(request.getSort());
            datasetGroupInfoDTO.setSortFields(Collections.singletonList(deSortField));
            sortDistinct = false;
        }

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsList.getFirst());
        }

        String dsType = null;
        if (dsMap != null && dsMap.entrySet().iterator().hasNext()) {
            Map.Entry<Long, DatasourceSchemaDTO> next = dsMap.entrySet().iterator().next();
            dsType = next.getValue().getType();
        }

        Field2SQLObj.field2sqlObj(sqlMeta, fields, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        ExtWhere2Str.extWhere2sqlOjb(sqlMeta, extFilterList, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        String querySQL;
        if (request.getResultMode() == 0) {
            querySQL = SQLProvider.createQuerySQLWithLimit(sqlMeta, false, needOrder, sortDistinct && ids.size() == 1 && !StringUtils.equalsIgnoreCase(dsType, "es"), 0, 1000);
        } else {
            querySQL = SQLProvider.createQuerySQL(sqlMeta, false, needOrder, sortDistinct && ids.size() == 1 && !StringUtils.equalsIgnoreCase(dsType, "es"));
        }
        querySQL = provider.rebuildSQL(querySQL, sqlMeta, crossDs, dsMap);
        logger.debug("calcite data enum sql: " + querySQL);

        // 通过数据源请求数据
        // 调用数据源的calcite获得data
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setQuery(querySQL);
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);

        Map<String, Object> data = provider.fetchResultField(datasourceRequest);
        List<String[]> dataList = (List<String[]>) data.get("data");
        dataList = dataList.stream().filter(row -> {
            boolean hasEmpty = false;
            for (String s : row) {
                if (StringUtils.isBlank(s)) {
                    hasEmpty = true;
                    break;
                }
            }
            return !hasEmpty;
        }).toList();
        Map<String, String[]> distinctData = new LinkedHashMap<>();
        for (String[] arr : dataList) {
            String key = Arrays.toString(arr);
            if (!distinctData.containsKey(key)) {
                distinctData.put(key, arr);
            }
        }

        List<String[]> distinctDataList = new ArrayList<>();
        for (Map.Entry<String, String[]> ele : distinctData.entrySet()) {
            distinctDataList.add(ele.getValue());
        }

        List<Map<String, Object>> previewData = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(distinctDataList)) {
            for (String[] ele : distinctDataList) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < fields.size(); i++) {
                    String val = ele[i];
                    DatasetTableFieldDTO field = fields.get(i);
                    if (field.getDeType() == 3 && StringUtils.containsIgnoreCase(val, "E")) {
                        BigDecimal bigDecimal = new BigDecimal(val);
                        val = String.format("%.8f", bigDecimal);
                    }
                    if (desensitizationList.containsKey(field.getDataeaseName())) {
                        String str = ChartDataBuild.desensitizationValue(desensitizationList.get(field.getDataeaseName()), val);
                        map.put(field.getId() + "", str);
                    } else {
                        map.put(field.getId() + "", val);
                    }
                }
                previewData.add(map);
            }
        }
        return previewData;
    }

    /**
     * 获取字段值的树形结构
     * 查询字段的层级值并构建为树形结构，用于树形下拉筛选组件
     * 支持多字段组合查询，自动去重和层级构建
     *
     * @param multFieldValuesRequest 多字段查询请求
     * @return 树形节点列表
     * @throws Exception 查询异常
     */
    public List<BaseTreeNodeDTO> getFieldValueTree(MultFieldValuesRequest multFieldValuesRequest) throws Exception {
        List<Long> ids = multFieldValuesRequest.getFieldIds();
        if (ids.isEmpty()) {
            DEException.throwException("no field selected.");
        }
        // 根据前端传的查询组件field ids，获取所有字段枚举值并去重合并
        List<List<String>> list = new ArrayList<>();
        List<DatasetTableFieldDTO> fields = new ArrayList<>();

        // 根据图表计算字段，获取数据集
        List<DatasetTableFieldDTO> allFields = new ArrayList<>();
        DatasetTableFieldDTO field = datasetTableFieldManage.selectById(ids.getFirst());
        Long datasetGroupId = field.getDatasetGroupId();
        if (field.getChartId() != null) {
            allFields.addAll(datasetTableFieldManage.getChartCalcFields(field.getChartId()));
        }
        DatasetGroupInfoDTO datasetGroupInfoDTO = datasetGroupManage.getDatasetGroupInfoDTO(datasetGroupId, null);

        Map<String, Object> sqlMap = datasetSQLManage.getUnionSQLForEdit(datasetGroupInfoDTO, new ChartExtRequest());
        String sql = (String) sqlMap.get("sql");

        allFields.addAll(datasetGroupInfoDTO.getAllFields());

        Map<Long, DatasourceSchemaDTO> dsMap = (Map<Long, DatasourceSchemaDTO>) sqlMap.get("dsMap");
        boolean crossDs = datasetGroupInfoDTO.getIsCross();
        if (!crossDs) {
            sql = Utils.replaceSchemaAlias(sql, dsMap);
        }

        // build query sql
        SQLMeta sqlMeta = new SQLMeta();
        Table2SQLObj.table2sqlobj(sqlMeta, null, "(" + sql + ")", crossDs);

        for (Long id : ids) {
            DatasetTableFieldDTO f = datasetTableFieldManage.selectById(id);
            if (f == null) {
                DEException.throwException(Translator.get("i18n_no_field"));
            }
            // 获取allFields
            fields.add(f);
        }

        Map<String, ColumnPermissionItem> desensitizationList = new HashMap<>();
        fields = permissionManage.filterColumnPermissions(fields, desensitizationList, datasetGroupInfoDTO.getId(), null);
        if (ObjectUtils.isEmpty(fields)) {
            DEException.throwException(Translator.get("i18n_no_column_permission"));
        }
        buildFieldName(sqlMap, fields);

        List<String> dsList = new ArrayList<>();
        for (Map.Entry<Long, DatasourceSchemaDTO> next : dsMap.entrySet()) {
            dsList.add(next.getValue().getType());
        }
        boolean needOrder = Utils.isNeedOrder(dsList);

        List<DataSetRowPermissionsTreeDTO> rowPermissionsTree = new ArrayList<>();
        TokenUserBO user = AuthUtils.getUser();
        if (user != null) {
            rowPermissionsTree = permissionManage.getRowPermissionsTree(datasetGroupInfoDTO.getId(), user.getUserId());
        }

        Provider provider;
        if (crossDs) {
            provider = ProviderFactory.getDefaultProvider();
        } else {
            provider = ProviderFactory.getProvider(dsList.getFirst());
        }

        //组件过滤条件
        List<ChartExtFilterDTO> extFilterList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(multFieldValuesRequest.getFilter())) {
            for (ChartExtFilterDTO filterDTO : multFieldValuesRequest.getFilter()) {
                // 解析多个fieldId,fieldId是一个逗号分隔的字符串
                String fieldId = filterDTO.getFieldId();
                if (filterDTO.getIsTree() == null) {
                    filterDTO.setIsTree(false);
                }

                boolean hasParameters = false;
                List<SqlVariableDetails> sqlVariables = datasetGroupManage.getSqlParams(Arrays.asList(datasetGroupInfoDTO.getId()));
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(sqlVariables)) {
                    for (SqlVariableDetails parameter : Optional.ofNullable(filterDTO.getParameters()).orElse(new ArrayList<>())) {
                        String parameterId = StringUtils.endsWith(parameter.getId(), START_END_SEPARATOR) ? parameter.getId().split(START_END_SEPARATOR)[0] : parameter.getId();
                        if (sqlVariables.stream().map(SqlVariableDetails::getId).collect(Collectors.toList()).contains(parameterId)) {
                            hasParameters = true;
                        }
                    }
                }

                if (hasParameters) {
                    continue;
                }

                if (StringUtils.isNotEmpty(fieldId)) {
                    List<Long> fieldIds = Arrays.stream(fieldId.split(",")).map(Long::valueOf).collect(Collectors.toList());

                    if (filterDTO.getIsTree()) {
                        ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                        BeanUtils.copyBean(filterRequest, filterDTO);
                        filterRequest.setDatasetTableFieldList(new ArrayList<>());
                        for (Long fId : fieldIds) {
                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), datasetGroupInfoDTO.getId())) {
                                filterRequest.getDatasetTableFieldList().add(datasetTableField);
                            }
                        }
                        if (ObjectUtils.isNotEmpty(filterRequest.getDatasetTableFieldList())) {
                            extFilterList.add(filterRequest);
                        }
                    } else {
                        for (Long fId : fieldIds) {
                            ChartExtFilterDTO filterRequest = new ChartExtFilterDTO();
                            BeanUtils.copyBean(filterRequest, filterDTO);
                            filterRequest.setFieldId(fId + "");

                            DatasetTableFieldDTO datasetTableField = datasetTableFieldManage.selectById(fId);
                            if (datasetTableField == null) {
                                continue;
                            }
                            filterRequest.setDatasetTableField(datasetTableField);
                            if (Objects.equals(datasetTableField.getDatasetGroupId(), datasetGroupInfoDTO.getId())) {
                                extFilterList.add(filterRequest);
                            }
                        }
                    }
                }
            }
        }

        Field2SQLObj.field2sqlObj(sqlMeta, fields, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        ExtWhere2Str.extWhere2sqlOjb(sqlMeta, extFilterList, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        WhereTree2Str.transFilterTrees(sqlMeta, rowPermissionsTree, allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        Order2SQLObj.getOrders(sqlMeta, datasetGroupInfoDTO.getSortFields(), allFields, crossDs, dsMap, Utils.getParams(allFields), null, pluginManage);
        String querySQL;
        if (multFieldValuesRequest.getResultMode() == 0) {
            querySQL = SQLProvider.createQuerySQLWithLimit(sqlMeta, false, needOrder, false, 0, 1000);
        } else {
            querySQL = SQLProvider.createQuerySQL(sqlMeta, false, needOrder, false);
        }
        querySQL = provider.rebuildSQL(querySQL, sqlMeta, crossDs, dsMap);
        logger.debug("filter tree sql: " + querySQL);

        // 通过数据源请求数据
        // 调用数据源的calcite获得data
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setQuery(querySQL);
        datasourceRequest.setDsList(dsMap);
        datasourceRequest.setIsCross(crossDs);

        Map<String, Object> data = provider.fetchResultField(datasourceRequest);
        List<String[]> rows = (List<String[]>) data.get("data");

        // 重新构造data
        Set<String> pkSet = new HashSet<>();
        rows = rows.stream().filter(row -> {
            boolean hasEmpty = false;
            int emptyCount = 0;
            for (String s : row) {
                if (StringUtils.isBlank(s)) {
                    emptyCount++;
                    hasEmpty = true; // 标记已遇到第一个null
                } else if (hasEmpty) {
                    return false; // 在null后出现非null元素，不符合要求
                }
            }
            return emptyCount != row.length;
        }).toList();
        List<BaseTreeNodeDTO> treeNodes = rows.stream().map(row -> buildTreeNode(row, pkSet)).flatMap(Collection::stream).collect(Collectors.toList());
        List<BaseTreeNodeDTO> tree = DatasetUtils.mergeDuplicateTree(treeNodes, "root");
        return tree;
    }

    private List<BaseTreeNodeDTO> buildTreeNode(String[] row, Set<String> pkSet) {
        List<BaseTreeNodeDTO> nodes = new ArrayList<>();
        List<String> parentPkList = new ArrayList<>();
        for (int i = 0; i < row.length; i++) {
            String text = row[i];
            if (StringUtils.isEmpty(text)) {
                continue;
            }
            parentPkList.add(text);
            String val = String.join(TreeUtils.SEPARATOR, parentPkList);
            String parentVal = i == 0 ? TreeUtils.DEFAULT_ROOT : row[i - 1];
            String pk = String.join(TreeUtils.SEPARATOR, parentPkList);
            if (pkSet.contains(pk)) continue;
            pkSet.add(pk);
            BaseTreeNodeDTO node = new BaseTreeNodeDTO(val, parentVal, StringUtils.isNotBlank(text) ? text.trim() : text, pk + TreeUtils.SEPARATOR + i);
            nodes.add(node);
        }
        return nodes;

    }
}

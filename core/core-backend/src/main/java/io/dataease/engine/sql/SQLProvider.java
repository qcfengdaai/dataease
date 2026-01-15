package io.dataease.engine.sql;

import io.dataease.constant.SQLConstants;
import io.dataease.extensions.datasource.model.SQLMeta;
import io.dataease.extensions.datasource.model.SQLObj;
import io.dataease.extensions.view.dto.ChartViewDTO;
import io.dataease.extensions.view.dto.SortAxis;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * SQL构建提供者
 * 负责将SQLMeta对象转换为完整的SQL查询语句，支持多种查询模式和数据库方言
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>根据SQLMeta构建完整的查询SQL</li>
 *   <li>支持聚合查询和普通查询</li>
 *   <li>支持排序、分页、去重等功能</li>
 *   <li>使用StringTemplate进行SQL模板化构建</li>
 * </ul>
 *
 * <p>设计模式：</p>
 * <ul>
 *   <li>将SQLMeta各个部分先构建完毕，然后在这个类中进行SQL拼接</li>
 *   <li>使用模板引擎确保SQL语法的正确性</li>
 *   <li>支持不同数据库的SQL方言差异</li>
 * </ul>
 *
 * @author Junjun
 */
public class SQLProvider {

    /**
     * 创建临时查询SQL
     * 将SQLMeta转换为可作为子查询使用的SQL语句，首尾用括号包围
     *
     * @param sqlMeta SQL元数据对象，包含查询的各个组成部分
     * @param isGroup 是否进行聚合查询
     * @param needOrder 是否需要排序
     * @param distinct 是否去重
     * @return 构建好的SQL查询语句
     */
    public static String createQuerySQLAsTmp(SQLMeta sqlMeta, boolean isGroup, boolean needOrder, boolean distinct) {
        return createQuerySQL(sqlMeta, isGroup, needOrder, distinct);
    }

    /**
     * 创建带分页的查询SQL
     * 在基础查询SQL后添加LIMIT和OFFSET子句实现分页功能
     *
     * @param sqlMeta SQL元数据对象
     * @param isGroup 是否进行聚合查询
     * @param needOrder 是否需要排序
     * @param distinct 是否去重
     * @param start 分页起始位置（偏移量）
     * @param count 每页记录数
     * @return 包含分页功能的SQL查询语句
     */
    public static String createQuerySQLWithLimit(SQLMeta sqlMeta, boolean isGroup, boolean needOrder, boolean distinct, int start, int count) {
        return createQuerySQL(sqlMeta, isGroup, needOrder, distinct) + " LIMIT " + count + " OFFSET " + start;
    }

    public static String createQuerySQL(SQLMeta sqlMeta, boolean isGroup, boolean needOrder, boolean distinct) {
        List<SQLObj> xFields = sqlMeta.getXFields();
        SQLObj tableObj = sqlMeta.getTable();
        List<SQLObj> xOrders = sqlMeta.getXOrders();

        STGroup stg = new STGroupString(SqlTemplate.PREVIEW_SQL);
        ST st_sql = stg.getInstanceOf("previewSql");
        st_sql.add("isGroup", isGroup);
        st_sql.add("distinct", distinct);
        if (ObjectUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);
        String customWheres = sqlMeta.getCustomWheres();
        String extWheres = sqlMeta.getExtWheres();
        String whereTrees = sqlMeta.getWhereTrees();
        List<String> wheres = new ArrayList<>();
        if (customWheres != null) wheres.add(customWheres);
        if (extWheres != null) wheres.add(extWheres);
        if (whereTrees != null) wheres.add(whereTrees);
        if (ObjectUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);

        // check datasource 是否需要排序
        if (needOrder && ObjectUtils.isEmpty(xOrders)) {
            if (ObjectUtils.isNotEmpty(xFields)) {
                xOrders = new ArrayList<>();
                SQLObj sqlObj = xFields.get(0);
                SQLObj result = SQLObj.builder()
                        .orderField(String.format(SQLConstants.FIELD_DOT, sqlObj.getFieldAlias()))
                        .orderAlias(String.format(SQLConstants.FIELD_DOT, sqlObj.getFieldAlias()))
                        .orderDirection("ASC").build();
                xOrders.add(result);
            }
        }
        if (ObjectUtils.isNotEmpty(xOrders)) {
            st_sql.add("orders", xOrders);
        }

        return st_sql.render();
    }

    public static String createQuerySQL(SQLMeta sqlMeta, boolean isGroup, boolean needOrder, ChartViewDTO view) {
        STGroup stg = new STGroupString(SqlTemplate.PREVIEW_SQL);
        ST st_sql = stg.getInstanceOf("previewSql");

        st_sql.add("isGroup", isGroup);

        SQLObj tableObj = sqlMeta.getTable();
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);

        List<SQLObj> xFields = sqlMeta.getXFields();
        List<SQLObj> xOrders = sqlMeta.getXOrders();
        if (ObjectUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);

        List<SQLObj> yFields = sqlMeta.getYFields();
        List<String> yWheres = sqlMeta.getYWheres();
        List<SQLObj> yOrders = sqlMeta.getYOrders();
        if (ObjectUtils.isNotEmpty(yFields)) st_sql.add("aggregators", yFields);

        String customWheres = sqlMeta.getCustomWheres();
        String extWheres = sqlMeta.getExtWheres();
        String whereTrees = sqlMeta.getWhereTrees();
        List<String> wheres = new ArrayList<>();
        if (customWheres != null) wheres.add(customWheres);
        if (extWheres != null) wheres.add(extWheres);
        if (whereTrees != null) wheres.add(whereTrees);
        if (ObjectUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        String sql = st_sql.render();

        ST st = stg.getInstanceOf("previewSql");
        st_sql.add("isGroup", isGroup);

        SQLObj tableSQL = SQLObj.builder()
                .tableName(String.format(SQLConstants.BRACKETS, sql))
                .tableAlias(String.format(SQLConstants.TABLE_ALIAS_PREFIX, 1))
                .build();
        if (ObjectUtils.isNotEmpty(tableSQL)) st.add("table", tableSQL);

        List<String> aggWheres = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(yWheres)) aggWheres.addAll(yWheres);
        if (ObjectUtils.isNotEmpty(aggWheres)) st.add("filters", aggWheres);

        List<SQLObj> orders = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(xOrders)) orders.addAll(xOrders);
        if (ObjectUtils.isNotEmpty(yOrders)) orders.addAll(yOrders);
        if (!orders.isEmpty() && CollectionUtils.isNotEmpty(view.getSortPriority())) {
            var sortPriority = view.getSortPriority();
            var tmp = new ArrayList<SQLObj>();
            var ids = new HashSet<Long>();
            for (SortAxis sortAxis : sortPriority) {
                for (SQLObj order : orders) {
                    if (sortAxis.getId().equals(order.getId())){
                        tmp.add(order);
                        ids.add(order.getId());
                    }
                }
            }
            for (SQLObj order : orders) {
                if (!ids.contains(order.getId())) {
                    tmp.add(order);
                }
            }
            orders = tmp;
        }
        // check datasource 是否需要排序
        if (needOrder && ObjectUtils.isEmpty(orders)) {
            if (ObjectUtils.isNotEmpty(xFields) || ObjectUtils.isNotEmpty(yFields)) {
                SQLObj sqlObj = ObjectUtils.isNotEmpty(xFields) ? xFields.get(0) : yFields.get(0);
                SQLObj result = SQLObj.builder()
                        .orderField(String.format(SQLConstants.FIELD_DOT, sqlObj.getFieldAlias()))
                        .orderAlias(String.format(SQLConstants.FIELD_DOT, sqlObj.getFieldAlias()))
                        .orderDirection("ASC").build();
                orders.add(result);
            }
        }
        if (ObjectUtils.isNotEmpty(orders)) st.add("orders", orders);

        return sqlLimit(st.render(), view);
    }

    public static String createQuerySQLNoSort(SQLMeta sqlMeta, boolean isGroup, ChartViewDTO view) {
        STGroup stg = new STGroupString(SqlTemplate.PREVIEW_SQL);
        ST st_sql = stg.getInstanceOf("previewSql");

        st_sql.add("isGroup", isGroup);

        SQLObj tableObj = sqlMeta.getTable();
        if (ObjectUtils.isNotEmpty(tableObj)) st_sql.add("table", tableObj);

        List<SQLObj> xFields = sqlMeta.getXFields();
        if (ObjectUtils.isNotEmpty(xFields)) st_sql.add("groups", xFields);

        List<SQLObj> yFields = sqlMeta.getYFields();
        List<String> yWheres = sqlMeta.getYWheres();
        if (ObjectUtils.isNotEmpty(yFields)) st_sql.add("aggregators", yFields);

        String customWheres = sqlMeta.getCustomWheres();
        String extWheres = sqlMeta.getExtWheres();
        String whereTrees = sqlMeta.getWhereTrees();
        List<String> wheres = new ArrayList<>();
        if (customWheres != null) wheres.add(customWheres);
        if (extWheres != null) wheres.add(extWheres);
        if (whereTrees != null) wheres.add(whereTrees);
        if (ObjectUtils.isNotEmpty(wheres)) st_sql.add("filters", wheres);
        String sql = st_sql.render();

        ST st = stg.getInstanceOf("previewSql");
        st_sql.add("isGroup", isGroup);

        SQLObj tableSQL = SQLObj.builder()
                .tableName(String.format(SQLConstants.BRACKETS, sql))
                .tableAlias(String.format(SQLConstants.TABLE_ALIAS_PREFIX, 1))
                .build();
        if (ObjectUtils.isNotEmpty(tableSQL)) st.add("table", tableSQL);

        List<String> aggWheres = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(yWheres)) aggWheres.addAll(yWheres);
        if (ObjectUtils.isNotEmpty(aggWheres)) st.add("filters", aggWheres);

        return sqlLimit(st.render(), view);
    }

    public static String sqlLimit(String sql, ChartViewDTO view) {
        if (StringUtils.equalsAnyIgnoreCase(view.getType(), "table-info", "table-normal")) {
            return sql;
        }
        if (StringUtils.equalsIgnoreCase(view.getResultMode(), "custom")) {
            return sql + " LIMIT " + view.getResultCount() + " OFFSET 0";
        } else {
            return sql;
        }
    }
}

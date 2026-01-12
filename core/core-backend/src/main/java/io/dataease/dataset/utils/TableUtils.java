package io.dataease.dataset.utils;

import io.dataease.extensions.datasource.dto.DatasourceSchemaDTO;
import io.dataease.extensions.datasource.dto.DsTypeDTO;
import io.dataease.extensions.datasource.model.SQLObj;
import io.dataease.utils.Md5Utils;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.commons.lang3.StringUtils;

/**
 * 表名和字段名处理工具类
 * 提供表名、字段名的格式化和转换功能
 * 支持多种数据库的SQL生成和名称规范化
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>表名转换和格式化</li>
 *   <li>字段名MD5缩写生成</li>
 *   <li>临时表名生成（tmp_前缀）</li>
 *   <li>Schema和表名的SQL拼接</li>
 *   <li>跨数据源表名处理</li>
 * </ul>
 *
 * <p>命名规范：</p>
 * <ul>
 *   <li>字段名短格式：f_ + MD5中间16位</li>
 *   <li>列名：C_ + MD5值</li>
 *   <li>临时表：tmp_ + 原表名</li>
 * </ul>
 *
 * @author Junjun
 */
public class TableUtils {

    public static String format = Quoting.BACK_TICK.string + "%s" + Quoting.BACK_TICK.string;

    public static String tableName(String name) {
        return name;
    }

    public static String tmpName(String name) {
        return "tmp_" + name;
    }

    public static String deleteName(String dorisName) {
        return "delete_" + dorisName;
    }

    public static String addName(String dorisName) {
        return "add_" + dorisName;
    }

    public static String fieldName(String dorisName) {
        return "f_" + Md5Utils.md5(dorisName);
    }

    public static String fieldNameShort(String dorisName) {
        return "f_" + Md5Utils.md5(dorisName).substring(8, 24);
    }

    public static String columnName(String fieldName) {
        return "C_" + Md5Utils.md5(fieldName);
    }

    public static String getTableAndAlias(SQLObj sqlObj, DsTypeDTO datasourceType, boolean isCross) {
        String schema = "";
        String prefix = "";
        String suffix = "";
        if (StringUtils.isNotEmpty(sqlObj.getTableSchema())) {
            if (isCross) {
                prefix = "`";
                suffix = "`";
            } else {
                prefix = datasourceType.getPrefix();
                suffix = datasourceType.getSuffix();
            }
            schema = prefix + sqlObj.getTableSchema() + suffix + ".";
        }
        return schema + prefix + sqlObj.getTableName() + suffix + " " + sqlObj.getTableAlias();
    }

    public static String tableName2Sql(DatasourceSchemaDTO ds, String tableName) {
        return "SELECT * FROM " + ds.getSchemaAlias() + "." + String.format(format, tableName);
    }
}

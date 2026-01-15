package io.dataease.dds.provider;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 租户数据源提供者
 * 负责从管理数据库中获取租户数据源配置信息，并构建对应的数据源实例
 * 支持获取所有租户数据源和指定租户数据源两种模式
 */
public class TenantDatasourceProvider {

    /**
     * MySQL数据库驱动类名
     * 默认的数据库驱动，支持MySQL 8.0+版本
     */
    private static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    /**
     * 数据源名称前缀模板
     * 格式：tenant_{租户ID}_{创建时间戳}
     */
    private static final String DS_NAME_PREFIX = "tenant_%s_%s";

    /**
     * 查询所有租户数据源配置的SQL
     * 关联tenant_db和tenant_info表，获取完整的租户数据源信息
     */
    private static final String ALL_TENANT_SQL = "select d.*, i.create_time from tenant_db d left join tenant_info i on d.tenant_id = i.id";

    /**
     * 查询指定租户数据源配置的SQL
     * 根据租户ID获取特定租户的数据源配置信息
     */
    private static final String ONE_TENANT_SQL = "select d.*, i.create_time from tenant_db d left join tenant_info i on d.tenant_id = i.id where d.tenant_id = ? ";


    /**
     * 获取所有租户的数据源信息
     * 从管理数据库中查询所有租户的数据源配置，并构建对应的DataSource实例
     *
     * <p>注意：如果某个数据源构建失败，不应影响其他数据源的构建过程</p>
     *
     * @param manage 管理数据源，用于查询租户配置信息的数据库连接
     * @return 租户数据源映射表，key为数据源名称，value为对应的DataSource实例；查询失败时返回null
     */
    public static Map<String, DataSource> getDbInfo(DataSource manage) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 获取管理数据库连接
            connection = manage.getConnection();
            // 准备查询所有租户数据源配置的SQL
            statement = connection.prepareStatement(ALL_TENANT_SQL);
            // 执行查询
            resultSet = statement.executeQuery();
            // 解析查询结果为Map列表
            List<Map<String, Object>> mapList = parseResultSet(resultSet);
            if (!CollectionUtils.isEmpty(mapList)) {
                // 将查询结果转换为数据源映射表
                // key: 通过getDsKey方法生成的数据源名称
                // value: 通过buildHikari方法构建的HikariDataSource实例
                return mapList.stream().collect(Collectors.toMap(TenantDatasourceProvider::getDsKey, TenantDatasourceProvider::buildHikari));
            }
        } catch (Exception e) {
            // 捕获异常并打印堆栈信息，不抛出以避免影响系统启动
            e.printStackTrace();
        } finally {
            // 确保资源被正确关闭
            close(connection, statement, resultSet);
        }
        return null;
    }



    /**
     * 获取指定租户的数据源信息
     * 根据租户ID从管理数据库中查询特定租户的数据源配置，并构建对应的DataSource实例
     * 主要用于懒加载模式下按需创建租户数据源
     *
     * @param manage 管理数据源，用于查询租户配置信息的数据库连接
     * @param tenantId 租户ID，用于查询特定租户的数据源配置
     * @return 租户数据源映射表，key为数据源名称，value为对应的DataSource实例；查询失败时返回null
     * @throws Exception 数据库操作异常
     */
    public static Map<String, DataSource> getDbInfo(DataSource manage, Long tenantId) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 获取管理数据库连接
            connection = manage.getConnection();
            // 准备查询指定租户数据源配置的SQL
            statement = connection.prepareStatement(ONE_TENANT_SQL);
            // 设置租户ID参数
            statement.setLong(1, tenantId);
            // 执行查询
            resultSet = statement.executeQuery();
            // 解析查询结果为Map列表
            List<Map<String, Object>> mapList = parseResultSet(resultSet);
            if (!CollectionUtils.isEmpty(mapList)) {
                // 将查询结果转换为数据源映射表
                // key: 通过getDsKey方法生成的数据源名称
                // value: 通过buildHikari方法构建的HikariDataSource实例
                return mapList.stream().collect(Collectors.toMap(TenantDatasourceProvider::getDsKey, TenantDatasourceProvider::buildHikari));
            }
        } catch (Exception e) {
            // 捕获异常并打印堆栈信息
            e.printStackTrace();
        } finally {
            // 确保资源被正确关闭
            close(connection, statement, resultSet);
        }
        return null;
    }

    /**
     * 生成数据源键值
     * 根据数据库行数据生成唯一的数据源名称标识
     *
     * @param row 数据库查询结果行，包含租户ID、创建时间、自定义键值等信息
     * @return 数据源键值，优先使用自定义ds_key，否则使用"tenant_{租户ID}_{创建时间}"格式
     */
    private static String getDsKey(Map<String, Object> row) {
        Long tenantId = (Long) row.get("tenant_id");
        Long createTime = (Long) row.get("create_time");
        Object dsKeyObj = null;
        // 优先使用自定义的数据源键值
        if (!ObjectUtils.isEmpty((dsKeyObj = row.get("ds_key")))) {
            return dsKeyObj.toString();
        }
        // 如果没有自定义键值，使用默认格式生成
        return String.format(DS_NAME_PREFIX, tenantId.toString(), createTime.toString());
    }

    /**
     * 构建Hikari数据源实例
     * 根据数据库配置参数创建HikariDataSource连接池
     *
     * @param param 数据源配置参数，包含驱动类名、URL、用户名、密码等信息
     * @return 配置完成的HikariDataSource实例
     */
    private static DataSource buildHikari(Map<String, Object> param) {
        HikariConfig config = new HikariConfig();
        // 设置数据库驱动类名，默认使用MySQL驱动
        config.setDriverClassName(param.getOrDefault("driver_class", MYSQL_DRIVER_CLASS_NAME).toString());
        // 设置数据库连接URL
        config.setJdbcUrl(param.get("url").toString());
        // 设置数据库用户名
        config.setUsername(param.get("username").toString());
        // 设置数据库密码
        config.setPassword(param.get("pwd").toString());
        // 创建并返回HikariDataSource实例
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        return hikariDataSource;
    }

    /**
     * 解析ResultSet结果集
     * 将JDBC查询结果转换为Map列表，便于后续处理
     *
     * @param resultSet 数据库查询结果集
     * @return Map列表，每个Map代表一行数据，key为列名，value为列值
     * @throws Exception SQL异常或其他数据处理异常
     */
    private static List<Map<String, Object>> parseResultSet(ResultSet resultSet) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取结果集元数据
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 获取列数
        int columnCount = metaData.getColumnCount();
        // 遍历结果集中的每一行
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            // 遍历当前行的每一列
            for (int i = 1; i <= columnCount; i++) {
                // 获取列值
                Object cellVal = resultSet.getObject(i);
                // 获取列标签名称
                String columnLabel = metaData.getColumnLabel(i);
                // 将列名和列值存入Map
                row.put(columnLabel, cellVal);
            }
            resultList.add(row);
        }
        return resultList;
    }

    /**
     * 关闭数据库资源
     * 安全地关闭数据库连接、语句和结果集，避免资源泄漏
     *
     * @param connection 数据库连接
     * @param statement 预编译语句
     * @param rs 结果集
     */
    public static void close(Connection connection, PreparedStatement statement, ResultSet rs) {
        // 关闭数据库连接
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭预编译语句
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭结果集
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

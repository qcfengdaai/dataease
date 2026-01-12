package io.dataease.extensions.datasource.dto;

import com.jcraft.jsch.Session;
import io.dataease.extensions.datasource.provider.Provider;
import io.dataease.extensions.datasource.vo.DatasourceConfiguration;
import lombok.Data;

import java.sql.Connection;

/**
 * DataEase数据库连接对象
 * <p>
 * 用于封装数据库连接相关的资源，包括数据库连接、SSH隔道会话、
 * 端口管理等。实现AutoCloseable接口，支持资源的自动清理和释放。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class ConnectionObj implements AutoCloseable {


    /**
     * 数据库连接对象
     * <p>
     * Java JDBC数据库连接对象，用于执行SQL语句和数据操作。
     * 是DataEase与数据库交互的核心连接对象。
     * </p>
     */
    private Connection connection;
    /**
     * SSH陇道会话
     * <p>
     * SSH远程连接会话，用于建立安全的远程数据库连接。
     * 在需要通过SSH陇道访问数据库时使用，提供额外的安全层。
     * </p>
     */
    private Session session;
    /**
     * 本地端口
     * <p>
     * SSH陇道转发使用的本地端口号。
     * 用于建立本地到远程数据库的SSH陇道，实现安全连接。
     * </p>
     */
    private Integer lPort;
    /**
     * 数据源配置信息
     * <p>
     * 数据源的完整配置信息，包括连接参数、认证信息等。
     * 用于连接建立和管理过程中的参数引用和配置查询。
     * </p>
     */
    private DatasourceConfiguration configuration;

    /**
     * 关闭和清理所有连接资源
     * <p>
     * 实现AutoCloseable接口，自动清理和释放所有相关资源。
     * 包括关闭数据库连接、断开SSH会话和清理端口资源。
     * </p>
     *
     * <p><b>资源清理顺序：</b></p>
     * <ol>
     *   <li>关闭数据库连接（connection）</li>
     *   <li>断开SSH会话（session）</li>
     *   <li>清理本地端口资源（lPort）</li>
     * </ol>
     *
     * @throws Exception 在资源关闭过程中可能抛出的异常
     */
    @Override
    public void close() throws Exception {
        // 关闭数据库连接
        if (this.connection != null) {
            this.connection.close();
        }

        // 断开SSH会话
        if (session != null) {
            session.disconnect();
        }

        // 清理端口资源
        if(lPort != null){
            Provider.getLPorts().remove(Long.valueOf(lPort));
        }
    }
}

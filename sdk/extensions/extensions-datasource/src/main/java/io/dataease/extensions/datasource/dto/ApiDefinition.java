package io.dataease.extensions.datasource.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DataEase API定义数据传输对象
 * <p>
 * 用于传输API数据源的定义和配置信息，包括API端点、请求参数、
 * 数据结构等。支持对各种第三方API数据源的集成配置。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class ApiDefinition {
    /**
     * API名称
     * <p>
     * API数据源的显示名称，用于系统中的标识和管理。
     * 应该为有意义的名称，便于用户识别和选择。
     * </p>
     */
    private String name;
    /**
     * DataEase表名
     * <p>
     * 在DataEase系统中生成的表名，用于内部数据表标识。
     * 由系统自动生成或用户自定义，与数据集名称对应。
     * </p>
     */
    private String deTableName;
    /**
     * API描述
     * <p>
     * API数据源的详细描述信息，说明API的作用和数据内容。
     * 帮助用户理解API的功能和使用方式。
     * </p>
     */
    private String desc;
    /**
     * API端点URL
     * <p>
     * API数据源的完整URL地址，包括协议、主机、端口和路径。
     * DataEase将通过该URL进行数据获取和同步。
     * </p>
     */
    private String url;
    /**
     * HTTP请求方法
     * <p>
     * API请求的HTTP方法，默认为GET。
     * 支持GET、POST、PUT、DELETE等常见HTTP方法。
     * </p>
     */
    private String method = "GET";
    /**
     * API字段列表
     * <p>
     * API返回数据中包含的字段定义列表。
     * 包含字段名、类型、默认值等详细信息。
     * </p>
     */
    private List<TableField> fields;
    /**
     * JSON字段列表
     * <p>
     * 解析JSON响应数据后的字段信息列表。
     * 以Map形式存储，支持动态字段结构解析。
     * </p>
     */
    private List<Map<String, Object>> jsonFields =new ArrayList<>();
    /**
     * API请求定义
     * <p>
     * API请求的详细配置信息，包括请求头、参数、身体等。
     * 封装了完整的请求配置，支持复杂的API调用场景。
     * </p>
     */
    private ApiDefinitionRequest request;
    /**
     * API状态
     * <p>
     * API数据源的当前状态，如启用、禁用、测试中等。
     * 用于控制API数据源的可用性和生命周期。
     * </p>
     */
    private String status;
    /**
     * API数据列表
     * <p>
     * 从 API获取到的实际数据列表，用于数据预览和验证。
     * 以Map形式存储，支持各种数据类型和结构。
     * </p>
     */
    private List<Map<String, Object>> data = new ArrayList<>();
    /**
     * API查询超时时间
     * <p>
     * API请求的超时时间设置，单位为秒，默认为10秒。
     * 用于控制API请求的最大等待时间，防止长时间等待。
     * </p>
     */
    private Integer apiQueryTimeout = 10;
    /**
     * 预览数据数量
     * <p>
     * 数据预览时显示的记录数量限制，默认为100条。
     * 用于控制预览数据的大小，提高响应速度。
     * </p>
     */
    private int previewNum = 100;
    /**
     * 序列号
     * <p>
     * API定义的序列号，用于排序和标识。
     * 在多个API定义中用于维持显示和执行顺序。
     * </p>
     */
    private int serialNumber;
    /**
     * 是否使用JSON路径
     * <p>
     * 标识是否使用JSONPath表达式来解析API响应数据。
     * 启用时将根据JSONPath配置提取特定路径的数据。
     * </p>
     */
    private boolean useJsonPath;
    /**
     * JSON路径表达式
     * <p>
     * 用于从复杂JSON响应中提取数据的JSONPath表达式。
     * 支持标准JSONPath语法，用于嵌套数据结构的解析。
     * </p>
     */
    private String jsonPath;
    /**
     * 是否重命名
     * <p>
     * 标识是否对字段名进行重命名处理，默认为false。
     * 启用时将根据规则对原始字段名进行标准化处理。
     * </p>
     */
    private boolean reName = false;
    /**
     * 原始名称
     * <p>
     * API数据源的原始名称，保留用于历史记录和追溯。
     * 在重命名功能启用时用于对比和还原。
     * </p>
     */
    private String orgName;
    /**
     * 是否显示API结构
     * <p>
     * 控制是否在用户界面中显示API的详细结构信息。
     * 用于调试和开发时的结构检查和验证。
     * </p>
     */
    private boolean showApiStructure;
    /**
     * 更新时间
     * <p>
     * API定义的最后修改时间戳（毫秒）。
     * 用于版本控制和变更追踪，及缓存失效判断。
     * </p>
     */
    private Long updateTime;
    /**
     * API数据类型
     * <p>
     * 指定API返回数据的类型，默认为“table”。
     * 支持表格数据、JSON数据等多种数据结构类型。
     * </p>
     */
    private String type = "table";
    /**
     * 认证令牌
     * <p>
     * API访问所需的认证令牌，用于鉴权和权限控制。
     * 支持Bearer Token、API Key等多种认证方式。
     * </p>
     */
    private  String token;
    /**
     * 应用令牌
     * <p>
     * 特定应用的认证令牌，用于应用级别的认证和授权。
     * 与通用token区分，提供更精细的权限控制。
     * </p>
     */
    private  String appToken;
    /**
     * 表标识符
     * <p>
     * 在第三方系统中的表或数据集标识符。
     * 用于在API请求中指定特定的数据表或资源。
     * </p>
     */
    private  String tableId;
    /**
     * 视图标识符
     * <p>
     * 在第三方系统中的视图或查询视图标识符。
     * 用于在API请求中指定特定的数据视图或过滤器。
     * </p>
     */
    private  String viewId;
}

package io.dataease.extensions.datasource.vo;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataEase数据源配置视图对象
 * <p>
 * 用于封装数据源的完整配置信息，包括数据库连接参数、SSH隧道配置、
 * 连接池设置等。支持多种数据库类型和连接方式的配置管理。
 * </p>
 *
 * @author fit2cloud
 * @since 1.0
 */
@Data
public class Configuration {

    /**
     * 数据源类型
     * <p>
     * 数据源的类型标识，如mysql、postgresql、oracle等。
     * 用于确定数据源的具体类型和相应的处理逻辑。
     * </p>
     */
    private String type;

    /**
     * 数据源名称
     * <p>
     * 数据源的显示名称，用于系统中的标识和管理。
     * 应该为有意义的名称，便于用户识别和选择。
     * </p>
     */
    private String name;

    /**
     * 数据库目录名
     * <p>
     * 数据库中的目录（catalog）名称，用于多数据库实例的管理。
     * 在某些数据库系统中用于区分不同的数据库实例。
     * </p>
     */
    private String catalog;

    /**
     * 目录描述
     * <p>
     * 数据库目录的详细描述信息。
     * 用于提供额外的说明和文档化信息。
     * </p>
     */
    private String catalogDesc;

    /**
     * 额外参数
     * <p>
     * 数据库连接的额外参数配置，用于传递特殊的连接选项。
     * 可以包含数据库特有的配置参数和优化设置。
     * </p>
     */
    private String extraParams;

    /**
     * 关键字前缀
     * <p>
     * 数据库关键字和标识符的转义前缀字符，默认为空。
     * 用于处理数据库保留关键字和特殊字符的转义。
     * </p>
     */
    private String keywordPrefix = "";

    /**
     * 关键字后缀
     * <p>
     * 数据库关键字和标识符的转义后缀字符，默认为空。
     * 与前缀配合使用，完成完整的关键字转义。
     * </p>
     */
    private String keywordSuffix = "";

    /**
     * 别名前缀
     * <p>
     * 表别名和字段别名的转义前缀字符，默认为空。
     * 用于处理SQL语句中别名的正确转义和识别。
     * </p>
     */
    private String aliasPrefix = "";

    /**
     * 别名后缀
     * <p>
     * 表别名和字段别名的转义后缀字符，默认为空。
     * 与前缀配合使用，完成完整的别名转义。
     * </p>
     */
    private String aliasSuffix = "";
    protected String jdbc;
    private String host;
    private String jdbcUrl;
    private String urlType;
    private Integer port;
    private String username;
    private String password;
    private String dataBase;
    private String schema;
    private String customDriver = "default";
    private String authMethod = "passwd";
    private String connectionType;
    private String charset;
    private String targetCharset;
    private String driver;
    private int initialPoolSize = 50;
    private int minPoolSize = 50;
    private int maxPoolSize = 100;
    private int queryTimeout = 30;
    private boolean useSSH = false;
    private String sshHost;
    private Integer sshPort;
    private Integer lPort;
    private String sshUserName;
    private String sshType = "password";
    private String sshPassword;
    private String sshKey;
    private String sshKeyPassword;
    private String url;


    public String getLHost(){
        if(useSSH){
            return "127.0.0.1";
        }else {
            return this.host;
        }
    }

    public Integer getLPort(){
        if(useSSH && lPort != null){
            return lPort;
        }else {
            return this.port;
        }
    }

    protected static final Pattern HOST_PORT_PATTERN = Pattern.compile("//([^:/]+)(?::(\\d+))?");
    protected static final Pattern PARAMETERS_PATTERN = Pattern.compile("([^&=]+)=([^&]*)");
    private static final Pattern DB_NAME_PATTERN = Pattern.compile("//[^/]+/([^?]+)");
    private Map<String, String> parameters = new HashMap<>();
    protected void parseHostAndPort(String jdbcUrl) {
        Matcher matcher = HOST_PORT_PATTERN.matcher(jdbcUrl);
        if (matcher.find()) {
            setHost(matcher.group(1));
            if (matcher.group(2) != null) {
                setPort(Integer.parseInt(matcher.group(2)));
            }
        }
    }

    protected void parseParameters(String jdbcUrl) {
        int paramStart = jdbcUrl.indexOf('?');
        if (paramStart > 0) {
            String paramString = jdbcUrl.substring(paramStart + 1);
            Matcher matcher = PARAMETERS_PATTERN.matcher(paramString);
            while (matcher.find()) {
                parameters.put(matcher.group(1), matcher.group(2));
            }
        }
    }

    protected void convertParameters(){
        if (ObjectUtils.isEmpty(parameters)) {
            return;
        }
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.equalsIgnoreCase(key, "user")) {
                setUsername(value);
            }
            if (StringUtils.equalsIgnoreCase(key, "password")) {
                setPassword(value);
            }
        }
    }

    protected void convertDatabase(String jdbcUrl) {
        Matcher matcher = getDatabasePattern().matcher(jdbcUrl);
        if (matcher.find()) {
            setDataBase(matcher.group(1));
        }
    }

    protected Pattern getDatabasePattern() {
        return DB_NAME_PATTERN;
    }

    public void convertJdbcUrl() {
        if (StringUtils.isNotBlank(urlType) && StringUtils.equalsAnyIgnoreCase(this.urlType, "jdbcUrl")) {
            parseHostAndPort(jdbcUrl);
            parseParameters(jdbcUrl);
            convertParameters();
            convertDatabase(jdbcUrl);
        }
    }

}

package io.dataease.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 嵌套数据映射工具类
 * <p>
 * 提供从复杂嵌套结构的Map中提取数据并映射到目标Map的功能。
 * 支持通过路径表达式访问多层嵌套的数据结构，包括对象属性和数组元素。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>嵌套对象映射 - 通过点号路径访问嵌套对象的属性（如：user.profile.name）</li>
 *   <li>数组元素访问 - 支持通过索引访问数组元素（如：roles[0].name）</li>
 *   <li>批量映射 - 根据映射规则批量提取和转换数据</li>
 *   <li>空值安全 - 路径中任何层级为null时返回null而不抛出异常</li>
 * </ul>
 *
 * <p><b>路径表达式语法：</b></p>
 * <ul>
 *   <li>点号分隔：使用点号(.)分隔嵌套层级，如：user.name</li>
 *   <li>数组访问：使用方括号和索引访问数组，如：roles[0] 或 user.roles[0].name</li>
 *   <li>混合路径：支持对象和数组混合嵌套，如：data.users[1].addresses[0].city</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>OIDC/OAuth用户信息映射 - 从第三方认证返回的复杂JSON结构中提取用户信息</li>
 *   <li>外部API数据转换 - 将外部系统的复杂响应数据映射到内部数据结构</li>
 *   <li>配置文件解析 - 从嵌套的配置数据中提取所需字段</li>
 *   <li>数据ETL处理 - 数据导入时的字段映射和转换</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：简单嵌套对象映射
 * Map&lt;String, Object&gt; userMap = new HashMap&lt;&gt;();
 * Map&lt;String, Object&gt; profile = new HashMap&lt;&gt;();
 * profile.put("name", "张三");
 * profile.put("email", "zhangsan@example.com");
 * userMap.put("profile", profile);
 *
 * Map&lt;String, String&gt; mappingRules = new HashMap&lt;&gt;();
 * mappingRules.put("userName", "profile.name");
 * mappingRules.put("userEmail", "profile.email");
 *
 * Map&lt;String, String&gt; result = MappingUtils.mapNestedUserData(userMap, mappingRules);
 * System.out.println(result.get("userName"));   // 输出: 张三
 * System.out.println(result.get("userEmail"));  // 输出: zhangsan@example.com
 *
 * // 示例2：数组元素访问
 * Map&lt;String, Object&gt; dataMap = new HashMap&lt;&gt;();
 * List&lt;String&gt; roles = Arrays.asList("admin", "user", "viewer");
 * dataMap.put("roles", roles);
 *
 * Map&lt;String, String&gt; mapping = new HashMap&lt;&gt;();
 * mapping.put("primaryRole", "roles[0]");
 * mapping.put("secondaryRole", "roles[1]");
 *
 * Map&lt;String, String&gt; roles = MappingUtils.mapNestedUserData(dataMap, mapping);
 * System.out.println(roles.get("primaryRole"));    // 输出: admin
 * System.out.println(roles.get("secondaryRole"));  // 输出: user
 *
 * // 示例3：复杂嵌套结构映射（OIDC用户信息场景）
 * // 模拟OIDC返回的用户信息
 * Map&lt;String, Object&gt; oidcResponse = new HashMap&lt;&gt;();
 * oidcResponse.put("sub", "user123");
 * oidcResponse.put("email", "user@example.com");
 *
 * Map&lt;String, Object&gt; userInfo = new HashMap&lt;&gt;();
 * userInfo.put("given_name", "三");
 * userInfo.put("family_name", "张");
 * oidcResponse.put("user_info", userInfo);
 *
 * List&lt;Map&lt;String, Object&gt;&gt; rolesList = new ArrayList&lt;&gt;();
 * Map&lt;String, Object&gt; roleMap = new HashMap&lt;&gt;();
 * roleMap.put("name", "Administrator");
 * roleMap.put("id", "role_001");
 * rolesList.add(roleMap);
 * oidcResponse.put("roles", rolesList);
 *
 * // 定义映射规则
 * Map&lt;String, String&gt; fieldMapping = new HashMap&lt;&gt;();
 * fieldMapping.put("userId", "sub");
 * fieldMapping.put("email", "email");
 * fieldMapping.put("firstName", "user_info.given_name");
 * fieldMapping.put("lastName", "user_info.family_name");
 * fieldMapping.put("primaryRole", "roles[0].name");
 * fieldMapping.put("roleId", "roles[0].id");
 *
 * Map&lt;String, String&gt; mappedUser = MappingUtils.mapNestedUserData(oidcResponse, fieldMapping);
 * System.out.println("用户ID: " + mappedUser.get("userId"));        // 用户ID: user123
 * System.out.println("邮箱: " + mappedUser.get("email"));           // 邮箱: user@example.com
 * System.out.println("姓名: " + mappedUser.get("lastName") +
 *                    mappedUser.get("firstName"));                  // 姓名: 张三
 * System.out.println("角色: " + mappedUser.get("primaryRole"));     // 角色: Administrator
 *
 * // 示例4：处理null值和不存在的路径
 * Map&lt;String, Object&gt; incompleteData = new HashMap&lt;&gt;();
 * incompleteData.put("name", "测试用户");
 *
 * Map&lt;String, String&gt; safeMapping = new HashMap&lt;&gt;();
 * safeMapping.put("name", "name");
 * safeMapping.put("phone", "contact.phone");  // contact不存在
 * safeMapping.put("city", "address.city");    // address不存在
 *
 * Map&lt;String, String&gt; safeResult = MappingUtils.mapNestedUserData(incompleteData, safeMapping);
 * System.out.println(safeResult.get("name"));   // 输出: 测试用户
 * System.out.println(safeResult.get("phone"));  // 输出: null（不会抛出异常）
 * System.out.println(safeResult.get("city"));   // 输出: null（不会抛出异常）
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>所有提取的值都会转换为String类型（调用toString()）</li>
 *   <li>路径中任何层级为null时，该字段在结果Map中不存在（而不是值为null）</li>
 *   <li>数组索引越界时返回null，不会抛出异常</li>
 *   <li>路径表达式区分大小写</li>
 *   <li>数组索引必须是非负整数</li>
 *   <li>不支持负索引或范围访问（如Python的[-1]或[0:2]）</li>
 *   <li>路径中的点号(.)和方括号([])是特殊字符，字段名中不应包含这些字符</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>映射配置化：将映射规则保存在配置文件或数据库中，支持动态调整</li>
 *   <li>字段验证：提取后验证必填字段是否存在，避免空值导致的问题</li>
 *   <li>类型转换：如需非String类型，提取后进行类型转换和验证</li>
 *   <li>错误处理：对关键字段进行非空判断和默认值设置</li>
 *   <li>文档记录：记录外部数据结构和映射规则，便于维护</li>
 *   <li>测试覆盖：针对各种数据结构（包括边界情况）编写测试用例</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
public class MappingUtils {

    /**
     * 映射嵌套用户数据
     * <p>
     * 根据提供的映射规则，从嵌套的源Map中提取数据并转换为目标Map。
     * 支持通过路径表达式访问深层嵌套的数据和数组元素。
     * </p>
     *
     * <p><b>路径表达式示例：</b></p>
     * <ul>
     *   <li>简单路径：name</li>
     *   <li>嵌套对象：user.profile.nickname</li>
     *   <li>数组元素：roles[0]</li>
     *   <li>混合路径：user.addresses[0].city</li>
     * </ul>
     *
     * @param userMap 源数据Map，可以包含嵌套的Map和List结构
     * @param mappingMap 映射规则Map，key为目标字段名，value为源数据路径表达式
     * @return 映射后的结果Map，key为目标字段名，value为从源数据提取的字符串值
     */
    public static Map<String, String> mapNestedUserData(Map<String, Object> userMap, Map<String, String> mappingMap) {

        Map<String, String> resultMap = new HashMap<>();
        mappingMap.forEach((targetKey, sourcePath) -> {
            Object value = getNestedValue(userMap, sourcePath);
            if (value != null) {
                resultMap.put(targetKey, value.toString());
            }
        });

        return resultMap;
    }

    /**
     * 获取嵌套值
     * <p>
     * 根据路径表达式从嵌套的Map结构中提取值。
     * 支持对象属性访问和数组元素访问的混合路径。
     * </p>
     *
     * <p><b>路径解析规则：</b></p>
     * <ul>
     *   <li>使用点号(.)分隔路径层级</li>
     *   <li>识别数组访问模式：属性名[索引]，如roles[0]</li>
     *   <li>正则匹配数组访问：.+\[\\d+]</li>
     *   <li>提取属性名和索引：分别处理获取数组后再访问元素</li>
     * </ul>
     *
     * @param sourceMap 源数据Map
     * @param path 路径表达式，如：user.profile.name 或 roles[0].name
     * @return 路径对应的值；如果路径无效、中间值为null或类型不匹配则返回null
     */
    private static Object getNestedValue(Map<String, Object> sourceMap, String path) {
        String[] keys = path.split("\\.");
        Object current = sourceMap;

        for (String key : keys) {
            if (current == null) {
                return null;
            }

            if (key.matches(".+\\[\\d+]")) {
                // 处理数组访问：如roles[0]
                String propertyName = key.replaceAll("\\[\\d+]", "");
                String indexStr = key.replaceAll(".*\\[(\\d+)]", "$1");

                if (!(current instanceof Map)) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> currentMap = (Map<String, Object>) current;
                Object arrayOrList = currentMap.get(propertyName);

                if (!(arrayOrList instanceof List)) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) arrayOrList;

                try {
                    int index = Integer.parseInt(indexStr);
                    if (index < 0 || index >= list.size()) {
                        return null;
                    }
                    current = list.get(index);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                // 普通对象属性
                if (!(current instanceof Map)) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> currentMap = (Map<String, Object>) current;
                current = currentMap.get(key);
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }

}

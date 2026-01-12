package io.dataease.constant;

/**
 * DataEase系统缓存常量定义类
 * <p>
 * 集中定义系统中所有缓存相关的键名常量，按功能模块分类组织，便于统一管理和维护。
 * 系统使用分布式缓存（如Redis）来提高性能，减少数据库访问压力，
 * 并为用户提供更快的响应速度。
 * </p>
 *
 * <p><b>缓存策略分类：</b></p>
 * <ul>
 *   <li><b>用户缓存</b> - 用户信息、身份验证、权限数据等</li>
 *   <li><b>角色缓存</b> - 角色权限、菜单权限、业务权限等</li>
 *   <li><b>组织缓存</b> - 组织架构、资源权限、组织数据等</li>
 *   <li><b>通用缓存</b> - 系统配置、地理信息、公用数据等</li>
 *   <li><b>许可证缓存</b> - 许可证信息、授权状态、功能限制等</li>
 * </ul>
 *
 * <p><b>缓存命名规范：</b></p>
 * <ul>
 *   <li>所有缓存键以 "de_v2_" 开头，标识DataEase V2版本</li>
 *   <li>按功能模块分类，如 "user_"、"role_"、"org_" 等</li>
 *   <li>使用下划线分隔单词，保持统一风格</li>
 *   <li>名称简洁明确，体现缓存内容的业务含义</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 1.0
 */
public class CacheConstant {
    /**
     * 用户相关缓存常量定义
     * <p>
     * 包含所有与用户信息、身份验证、权限管理相关的缓存键定义。
     * 这些缓存主要用于提高用户认证、授权和个人信息查询的性能，
     * 减少频繁的数据库查询操作，提升用户体验。
     * </p>
     */
    public static class UserCacheConstant {
        /**
         * 系统用户总数缓存键
         * <p>
         * 缓存当前系统中的用户总数信息，用于首页统计、仪表盘显示等场景。
         * 定期更新，避免实时计算用户数量对数据库的性能影响。
         * </p>
         */
        public static final String USER_COUNT_CACHE = "de_v2_user_count";

        /**
         * 用户梯队信息缓存键
         * <p>
         * 缓存用户的组织架构和级别关系信息，用于权限控制和组织管理。
         * 包含用户所属部门、职级、上下级关系等组织架构信息。
         * </p>
         */
        public static final String USER_ECHELON_CACHE = "de_v2_user_echelon";

        /**
         * 登录用户信息缓存键
         * <p>
         * 缓存当前登录用户的基本信息，包括用户ID、用户名、显示名称等。
         * 在用户会话期间内保持数据，避免频繁查询用户表，提高系统响应性能。
         * </p>
         */
        public static final String LOGIN_USER_CACHE = "de_v2_login_user_cache";

        /**
         * 用户角色关系缓存键
         * <p>
         * 缓存用户与角色的对应关系，用于快速进行角色权限判断和授权验证。
         * 包含用户拥有的所有角色信息，支持多角色管理和组合权限计算。
         * </p>
         */
        public static final String USER_ROLES_CACHE = "de_v2_user_roles";

        /**
         * 用户业务权限缓存键
         * <p>
         * 缓存用户在各个业务模块中的操作权限，如数据集、仪表盘、报告等。
         * 用于快速判断用户是否有权访问特定资源，支持细粒度的权限控制。
         * </p>
         */
        public static final String USER_BUSI_PERS_CACHE = "de_v2_user_busi_pers";

        /**
         * 用户业务交互权限缓存键
         * <p>
         * 缓存用户在业务模块中的交互权限，如编辑、删除、分享、导出等操作。
         * 区别于基础的查看权限，这里用于管理用户的高级交互操作权限。
         * </p>
         */
        public static final String USER_BUSI_PERS_INTERACTIVE_CACHE = "de_v2_user_busi_pers_interactive";

        /**
         * 用户社区版语言设置缓存键
         * <p>
         * 缓存用户在社区版中的语言偏好设置，用于个性化用户界面和内容显示。
         * 支持多语言切换，为不同地区的用户提供本地化体验。
         * </p>
         */
        public static final String USER_COMMUNITY_LANGUAGE = "de_v2_user_community_language";
    }

    /**
     * 角色相关缓存常量定义
     * <p>
     * 包含所有与角色权限管理相关的缓存键定义。角色是权限管理的核心概念，
     * 通过缓存角色权限信息，可以显著提高权限检查的性能，减少
     * 数据库访问压力，特别是在高并发场景下的效果显著。
     * </p>
     */
    public static class RoleCacheConstant {
        /**
         * 角色菜单权限缓存键
         * <p>
         * 缓存角色与系统菜单的对应权限关系，用于控制用户界面的菜单显示。
         * 包括各级菜单项的访问权限，用于动态渲染用户可见的功能模块。
         * </p>
         */
        public static final String ROLE_MENU_PERS_CACHE = "de_v2_role_menu_pers";

        /**
         * 角色业务权限缓存键
         * <p>
         * 缓存角色在各个业务模块中的基础权限，如查看、管理等权限级别。
         * 用于快速判断角色是否具有访问特定业务资源的基本权限。
         * </p>
         */
        public static final String ROLE_BUSI_PERS_CACHE = "de_v2_role_busi_pers";

        /**
         * 角色业务交互权限缓存键
         * <p>
         * 缓存角色在业务模块中的高级交互权限，如创建、编辑、删除、分享等。
         * 区别于基础的查看权限，这里主要用于管理更复杂的业务操作权限。
         * </p>
         */
        public static final String ROLE_BUSI_PERS_INTERACTIVE_CACHE = "de_v2_role_busi_pers_interactive";
    }

    /**
     * 组织相关缓存常量定义
     * <p>
     * 包含所有与组织架构、组织资源管理相关的缓存键定义。组织架构是DataEase多租户、
     * 多组织管理的基础，通过缓存机制可以显著提高组织相关数据的查询效率，
     * 特别是在大型组织中的复杂层级结构下。
     * </p>
     */
    public static class OrgCacheConstant {
        /**
         * 组织全局资源缓存键
         * <p>
         * 缓存组织级别的全局资源信息，包括数据集、仪表盘、报告等共享资源。
         * 用于在组织内部快速查找和访问各种业务资源，支持跨部门的数据共享。
         * </p>
         */
        public static final String ORG_GLOBAL_RESOURCE_CACHE = "de_v2_org_global_resource";

        /**
         * 所有组织ID标识资源缓存键
         * <p>
         * 缓存系统中所有组织的ID标识和相关资源对应关系，用于全局资源检索和权限验证。
         * 包含组织级别的标识信息和资源分配状态，支持快速的资源归属判断。
         * </p>
         */
        public static final String ALL_OID_FLAG_RESOURCE_CACHE = "de_v2_all_oid_flag_resource";
    }

    /**
     * 通用缓存常量定义
     * <p>
     * 包含系统级别的通用缓存键定义，这些缓存通常存储全局配置、公用数据和
     * 系统资源信息。这类数据通常有着较高的访问频率和相对稳定的内容，
     * 非常适合使用缓存来提高系统整体性能。
     * </p>
     */
    public static class CommonCacheConstant {
        /**
         * 世界地图数据缓存键
         * <p>
         * 缓存世界地图的基础地理信息数据，包括国家、省份、城市等行政区划信息。
         * 用于地理可视化图表的数据渲染和地图组件的快速加载。
         * </p>
         */
        public static final String WORLD_MAP_CACHE = "de_v2_world_map";

        /**
         * 自定义地理信息缓存键
         * <p>
         * 缓存用户自定义的地理信息数据，如企业内部的办公区域、项目地点等。
         * 支持用户根据业务需求定制地理可视化的区域范围和地理属性。
         * </p>
         */
        public static final String CUSTOM_GEO_CACHE = "de_v2_custom_geo";

        /**
         * RSA加密密钥缓存键
         * <p>
         * 缓存RSA公私钥对信息，用于系统的数据加密和密码保护。
         * 主要用于用户密码传输加密、敏感数据保护等安全相关场景。
         * </p>
         */
        public static final String RSA_CACHE = "de_v2_rsa";

        /**
         * 权限菜单ID缓存键
         * <p>
         * 缓存系统中所有菜单项的ID和权限标识对应关系，用于菜单权限的快速检查。
         * 支持动态菜单渲染和权限控制，确保用户只能看到有权访问的菜单项。
         * </p>
         */
        public static final String PER_MENU_ID_CACHE = "de_v2_per_menu_id";

        /**
         * 全局MFA（多因子认证）配置缓存键
         * <p>
         * 缓存系统级别的MFA认证配置信息，包括MFA启用状态、支持的认证方式等。
         * 用于全局安全策略的快速判断，提高系统安全性和用户认证的便捷性。
         * </p>
         */
        public static final String GLOBAL_MFA_CACHE = "de_v2_global_mfa";
    }

    /**
     * 许可证相关缓存常量定义
     * <p>
     * 包含所有与系统许可证验证、功能授权相关的缓存键定义。许可证管理是
     * DataEase商业版本的重要组成部分，通过缓存机制可以避免频繁的许可证验证，
     * 提高系统整体响应性能和用户体验。
     * </p>
     */
    public static class LicenseCacheConstant {
        /**
         * 许可证数据库缓存名
         * <p>
         * 定义许可证相关数据在数据库缓存中的存储区域名称。
         * 用于统一管理和组织许可证相关的缓存数据，便于批量操作和统一清理。
         * </p>
         */
        public static final String cacheName = "de_v2_lic_cache_db";

        /**
         * 许可证验证结果缓存键
         * <p>
         * 缓存许可证的验证结果信息，包括许可证有效性、功能授权范围、过期时间等。
         * 用于快速判断系统功能是否可用，避免每次访问时都进行完整的许可证验证。
         * </p>
         */
        public static final String LIC_RESULT_CACHE = "de_v2_lic_cache_result";

        /**
         * 许可证键值缓存键
         * <p>
         * 缓存许可证的具体内容和加密信息，用于本地存储和快速访问许可证数据。
         * 包括许可证的数字签名、加密内容等安全相关信息，确保许可证的安全性。
         * </p>
         */
        public static final String cacheKey = "de_v2_lic_key";
    }
}

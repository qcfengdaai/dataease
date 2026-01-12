package io.dataease.api.permissions.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.login.dto.MfaLoginDTO;
import io.dataease.api.permissions.login.vo.MfaQrVO;
import io.dataease.api.permissions.role.dto.UserRequest;
import io.dataease.api.permissions.user.dto.*;
import io.dataease.api.permissions.user.vo.*;
import io.dataease.auth.DeApiPath;
import io.dataease.auth.DePermit;
import io.dataease.auth.vo.TokenVO;
import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static io.dataease.constant.AuthResourceEnum.USER;

/**
 * 用户管理 API 接口
 *
 * <p>提供用户的完整生命周期管理功能，包括用户的创建、编辑、删除、查询等操作，
 * 以及用户信息管理、权限控制、组织切换、多因素认证（MFA）等功能。</p>
 *
 * <p>主要功能模块：</p>
 * <ul>
 *   <li>用户基本操作：创建、编辑、删除、查询用户信息</li>
 *   <li>用户权限管理：角色绑定、组织切换、权限查询</li>
 *   <li>账号管理：密码重置、状态启用/禁用、语言切换</li>
 *   <li>批量操作：批量导入、批量删除用户</li>
 *   <li>第三方集成：第三方平台用户创建、绑定/解绑</li>
 *   <li>安全功能：多因素认证（MFA）绑定/解绑、密码修改</li>
 *   <li>个人信息：查询和修改个人信息、切换语言偏好</li>
 * </ul>
 *
 * <p>权限控制：所有接口均通过 @DePermit 注解进行权限验证，确保操作安全性</p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "用户")
@ApiSupport(order = 888, author = "fit2cloud-someone")
@DeApiPath(value = "/user", rt = USER)
public interface UserApi {

    /**
     * 分页查询用户列表
     *
     * <p>支持分页和条件筛选的用户列表查询接口。
     * 可以根据关键字、角色、状态等条件筛选用户，并返回分页结果。</p>
     *
     * @param goPage 目标页码，从 1 开始
     * @param pageSize 每页记录数
     * @param request 查询条件，包含关键字、筛选条件等
     * @return 用户分页数据，包含用户基本信息和角色信息
     */
    @Operation(summary = "查询用户列表")
    @Parameters({
            @Parameter(name = "goPage", description = "目标页码", required = true, in = ParameterIn.PATH),
            @Parameter(name = "pageSize", description = "每页容量", required = true, in = ParameterIn.PATH),
            @Parameter(name = "request", description = "过滤条件", required = true)
    })
    @DePermit("m:read")
    @PostMapping("/pager/{goPage}/{pageSize}")
    IPage<UserGridVO> pager(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody UserGridRequest request);

    /**
     * 查询用户详情
     *
     * <p>根据用户 ID 查询用户的完整信息，包括基本信息、角色、系统变量等。
     * 用于用户编辑表单的数据回显。</p>
     *
     * @param id 用户 ID
     * @return 用户详细信息，包含所有可编辑字段
     */
    @Operation(summary = "查询用户详情")
    @Parameter(name = "id", description = "ID", required = true, in = ParameterIn.PATH)
    @DePermit({"m:read", "#p0 + ':read'"})
    @GetMapping("/queryById/{id}")
    UserFormVO queryById(@PathVariable("id") Long id);

    /**
     * 查询当前登录用户的个人信息
     *
     * <p>获取当前登录用户的详细信息，用于个人信息页面展示和编辑。</p>
     *
     * @return 当前用户的详细信息
     */
    @Operation(summary = "查询个人信息")
    @GetMapping("/personInfo")
    UserFormVO personInfo();

    /**
     * 查询用户的系统变量信息
     *
     * <p>获取指定用户的系统变量配置信息。
     * 系统变量用于存储用户级别的配置参数。</p>
     *
     * @param id 用户 ID
     * @return 用户的系统变量信息
     */
    @Operation(summary = "查询用户系统变量信息")
    @GetMapping("/personSysVariableInfo/{id}")
    UserGridVO personSysVariableInfo(@PathVariable("id") Long id);

    /**
     * 查询当前客户端的 IP 信息
     *
     * <p>获取当前请求客户端的 IP 地址和相关信息，
     * 用于安全审计和访问控制。</p>
     *
     * @return 客户端 IP 信息
     */
    @Operation(summary = "查询客户端IP信息")
    @GetMapping("/ipInfo")
    CurIpVO ipInfo();

    /**
     * 创建用户
     *
     * <p>创建新的系统用户，需要指定用户的基本信息和角色。
     * 创建成功后返回新用户的 ID。</p>
     *
     * @param creator 用户创建信息，包含账号、姓名、邮箱、角色等
     * @return 新创建用户的 ID
     */
    @Operation(summary = "创建")
    @DePermit("m:read")
    @PostMapping("/create")
    Long create(@RequestBody UserCreator creator);

    /**
     * 创建第三方平台用户
     *
     * <p>创建来自第三方平台（如 LDAP、OAuth 等）的用户。
     * 第三方用户的认证由外部平台管理，但需要在系统中创建账号以管理权限。</p>
     *
     * @param creator 第三方用户创建信息
     */
    @Operation(summary = "创建第三方用户")
    @DePermit("m:read")
    @PostMapping("/createPlatform")
    void createPlatform(@RequestBody PlatformUserCreator creator);

    /**
     * 编辑用户信息
     *
     * <p>更新用户的基本信息和角色分配。
     * 需要对该用户有管理权限。</p>
     *
     * @param editor 用户编辑信息，包含用户 ID 和需要更新的字段
     */
    @Operation(summary = "编辑")
    @DePermit({"m:read", "#p0.id + ':manage'"})
    @PostMapping("/edit")
    void edit(@RequestBody UserEditor editor);

    /**
     * 修改个人信息
     *
     * <p>当前登录用户修改自己的个人信息。
     * 用户只能修改自己的基本信息，不能修改角色等权限相关信息。</p>
     *
     * @param editor 用户编辑信息
     */
    @Operation(summary = "变更个人信息")
    @PostMapping("/personEdit")
    void personEdit(@RequestBody UserEditor editor);

    /**
     * 删除用户
     *
     * <p>删除指定的用户账号。需要对该用户有管理权限。
     * 删除后，用户的所有数据和权限将被移除。</p>
     *
     * @param id 用户 ID
     */
    @Operation(summary = "删除")
    @Parameter(name = "id", description = "ID", required = true, in = ParameterIn.PATH)
    @DePermit({"m:read", "#p0 + ':manage'"})
    @PostMapping("/delete/{id}")
    void delete(@PathVariable("id") Long id);

    /**
     * 批量删除用户
     *
     * <p>批量删除多个用户账号。需要对这些用户都有管理权限。
     * 适用于清理多个无效或测试账号的场景。</p>
     *
     * @param ids 用户 ID 列表
     */
    @Operation(summary = "批量删除")
    @DePermit({"m:read", "#p0 + ':manage'"})
    @PostMapping("/batchDel")
    void batchDel(@RequestBody List<Long> ids);

    /**
     * 查询角色可绑定的用户列表
     *
     * <p>获取可以绑定到指定角色的用户列表。
     * 返回尚未绑定该角色的用户，用于角色管理界面添加成员。</p>
     *
     * @param request 查询请求，包含角色 ID 和搜索条件
     * @return 可绑定到该角色的用户列表
     */
    @Operation(summary = "角色可绑用户")
    @PostMapping("/role/option")
    List<UserItemVO> optionForRole(@RequestBody UserRequest request);

    /**
     * 查询当前组织内的所有用户
     *
     * <p>获取当前组织内的用户列表，用于下拉选择等场景。
     * 返回组织内所有活跃用户的简要信息。</p>
     *
     * @return 组织内用户列表
     */
    @Operation(summary = "组织内用户")
    @GetMapping("/org/option")
    List<UserItemVO> optionForOrg();

    /**
     * 分页查询角色已绑定的用户列表
     *
     * <p>获取已经绑定到指定角色的用户列表，支持分页和搜索。
     * 用于角色管理界面展示角色的成员列表。</p>
     *
     * @param goPage 目标页码，从 1 开始
     * @param pageSize 每页记录数
     * @param request 查询请求，包含角色 ID 和搜索条件
     * @return 已绑定该角色的用户分页数据
     */
    @Operation(summary = "角色已绑用户")
    @Parameters({
            @Parameter(name = "goPage", description = "目标页码", required = true, in = ParameterIn.PATH),
            @Parameter(name = "pageSize", description = "每页容量", required = true, in = ParameterIn.PATH),
            @Parameter(name = "request", description = "过滤条件", required = true)
    })
    @PostMapping("/role/selected/{goPage}/{pageSize}")
    IPage<UserItemVO> selectedForRole(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody UserRequest request);

    /**
     * 切换当前工作组织
     *
     * <p>切换当前登录用户的工作组织。用户可以属于多个组织，
     * 通过此接口可以在不同组织间切换，切换后将获得新的访问令牌。</p>
     *
     * @param oId 目标组织 ID
     * @return 新的访问令牌信息
     */
    @Operation(summary = "切换组织")
    @Parameter(name = "oId", description = "目标组织ID", required = true, in = ParameterIn.PATH)
    @PostMapping("/switch/{oId}")
    TokenVO switchOrg(@PathVariable("oId") Long oId);

    /**
     * 获取当前登录用户信息
     *
     * <p>获取当前登录用户的基本信息和权限信息。
     * 用于前端页面展示用户头像、姓名等信息，以及权限控制。</p>
     *
     * @return 当前登录用户的信息
     */
    @Operation(summary = "获取当前登录人信息")
    @GetMapping("/info")
    CurUserVO info();

    /**
     * 查询当前组织内的用户
     *
     * <p>根据关键字搜索当前组织内的用户。
     * 支持按用户名、账号等信息进行模糊搜索。</p>
     *
     * @param request 查询请求，包含搜索关键字
     * @return 匹配的用户列表
     */
    @Operation(summary = "查询当前组织内用户")
    @PostMapping("/byCurOrg")
    List<UserItem> byCurOrg(@RequestBody KeywordRequest request);

    /**
     * 查询系统用户总数
     *
     * <p>获取系统中的用户总数。内部接口，用于系统监控和统计。</p>
     *
     * @return 用户总数
     */
    @Operation(summary = "用户数量", hidden = true)
    @Hidden
    @GetMapping("/userCount")
    int userCount();

    /**
     * 切换界面语言
     *
     * <p>切换当前用户的界面显示语言偏好。
     * 支持中文、英文等多种语言，切换后前端界面将使用新的语言显示。</p>
     *
     * @param request 语言切换请求，包含目标语言代码
     */
    @Operation(summary = "切换语言")
    @PostMapping("/switchLanguage")
    void switchLanguage(@RequestBody LangSwitchRequest request);

    /**
     * 下载用户批量导入模板
     *
     * <p>下载用户批量导入的 Excel 模板文件。
     * 模板文件包含必填字段说明和示例数据，用于批量创建用户。</p>
     */
    @Operation(summary = "下载批量导入模版")
    @PostMapping("/excelTemplate")
    void excelTemplate();

    /**
     * 批量导入用户
     *
     * <p>通过上传 Excel 文件批量创建用户。
     * 文件格式需符合模板要求，导入结果包含成功数量和失败记录。</p>
     *
     * @param file 用户数据 Excel 文件
     * @return 导入结果，包含成功数量、失败数量和失败记录的下载 key
     */
    @Operation(summary = "批量导入")
    @PostMapping("/batchImport")
    UserImportVO batchImport(@RequestPart(value = "file") MultipartFile file);

    /**
     * 下载批量导入失败记录
     *
     * <p>下载批量导入时失败的记录。
     * 失败记录包含错误原因，方便用户修正后重新导入。</p>
     *
     * @param key 导入结果的唯一标识
     */
    @Operation(summary = "下载批量导入失败记录")
    @Parameter(name = "key", description = "导入结果key", required = true, in = ParameterIn.PATH)
    @GetMapping("/errorRecord/{key}")
    void errorRecord(@PathVariable("key") String key);

    /**
     * 清理批量导入失败记录
     *
     * <p>清理批量导入产生的临时失败记录文件。
     * 用户确认处理完失败记录后可调用此接口清理。</p>
     *
     * @param key 导入结果的唯一标识
     */
    @Operation(summary = "清理批量导入失败记录")
    @Parameter(name = "key", description = "导入结果key", required = true, in = ParameterIn.PATH)
    @GetMapping("/clearErrorRecord/{key}")
    void clearErrorRecord(@PathVariable("key") String key);

    /**
     * 查询系统默认密码
     *
     * <p>获取系统配置的默认密码。
     * 用于创建新用户时设置初始密码，或重置用户密码。</p>
     *
     * @return 系统默认密码
     */
    @Operation(summary = "查询默认密码")
    @DePermit({"m:read"})
    @GetMapping("/defaultPwd")
    String defaultPwd();

    /**
     * 重置用户密码为默认密码
     *
     * <p>将指定用户的密码重置为系统默认密码。
     * 需要对该用户有管理权限。用户下次登录时应修改密码。</p>
     *
     * @param id 用户 ID
     */
    @Operation(summary = "重置为默认密码")
    @Parameter(name = "id", description = "用户ID", required = true, in = ParameterIn.PATH)
    @DePermit({"m:read", "#p0 + ':manage'"})
    @PostMapping("/resetPwd/{id}")
    void resetPwd(@PathVariable("id") Long id);

    /**
     * 切换用户启用/禁用状态
     *
     * <p>启用或禁用指定用户。禁用后，用户将无法登录系统。
     * 需要对该用户有管理权限。</p>
     *
     * @param request 状态切换请求，包含用户 ID 和目标状态
     */
    @Operation(summary = "切换用户状态")
    @DePermit({"m:read", "#p0.id + ':manage'"})
    @PostMapping("/enable")
    void enable(@RequestBody EnableSwitchRequest request);

    /**
     * 修改个人密码
     *
     * <p>当前登录用户修改自己的登录密码。
     * 需要提供旧密码进行验证，确保安全性。</p>
     *
     * @param request 密码修改请求，包含旧密码和新密码
     */
    @Operation(summary = "修改个人密码")
    @PostMapping("/modifyPwd")
    void modifyPwd(@RequestBody ModifyPwdRequest request);

    /**
     * 查询第一梯队用户
     *
     * <p>查询系统中活跃度最高或权限最高的前 N 个用户。
     * 内部接口，用于系统分析和推荐。</p>
     *
     * @param limit 返回的用户数量限制
     * @return 用户 ID 列表
     */
    @Hidden
    @GetMapping("/firstEchelon/{limit}")
    List<Long> firstEchelon(@PathVariable("limit") Long limit);

    /**
     * 根据账号查询用户
     *
     * <p>通过用户账号查询用户的详细信息。</p>
     *
     * @param account 用户账号
     * @return 用户信息
     */
    @Operation(summary = "根据账号查询用户")
    @GetMapping("/queryByAccount/{account}")
    CurUserVO queryByAccount(@PathVariable("account") String account);

    /**
     * 查询所有用户
     *
     * <p>查询系统中的所有用户。内部接口，支持关键字搜索。</p>
     *
     * @param request 查询请求，包含搜索条件
     * @return 用户列表
     */
    @Hidden
    @PostMapping("/all")
    List<UserItem> allUser(@RequestBody KeywordRequest request);

    /**
     * 管理员绑定第三方账号
     *
     * <p>系统管理员为用户绑定第三方平台账号。
     * 内部接口，用于第三方集成管理。</p>
     *
     * @param request 管理员绑定请求
     */
    @Hidden
    @PostMapping("/admin/bind")
    void adminBind(@RequestBody AdminBindRequest request);

    /**
     * 用户绑定第三方账号
     *
     * <p>用户自己绑定第三方平台账号，实现多平台账号关联。
     * 内部接口，用于第三方集成。</p>
     *
     * @param request 用户绑定请求
     */
    @Hidden
    @PostMapping("/bind")
    void bind(@RequestBody UserBindRequest request);

    /**
     * 解除第三方账号绑定
     *
     * <p>解除当前用户与指定第三方平台的账号绑定关系。
     * 解绑后将无法使用该第三方账号登录。</p>
     *
     * @param origin 第三方平台标识
     */
    @Operation(summary = "解除绑定")
    @PostMapping("/unBind/{origin}")
    void unBind(@PathVariable("origin") Integer origin);

    /**
     * 查询第三方账号绑定状态
     *
     * <p>查询当前用户已绑定的第三方平台列表。
     * 返回已绑定平台的标识列表。</p>
     *
     * @return 已绑定的第三方平台标识列表
     */
    @Operation(summary = "绑定状态")
    @GetMapping("/bindStatus")
    List<Integer> bindStatus();

    /**
     * 获取消息接收人
     *
     * <p>根据条件查询消息通知的接收人列表。
     * 内部接口，用于消息推送功能。</p>
     *
     * @param request 接收人查询请求
     * @return 接收人信息列表
     */
    @Hidden
    @GetMapping("/getRecipient")
    List<Map<String, Object>> getRecipient(@RequestBody UserReciRequest request);

    /**
     * 判断当前用户是否为组织管理员
     *
     * <p>检查当前登录用户是否具有组织管理员权限。
     * 内部接口，用于权限控制。</p>
     *
     * @return true：是组织管理员；false：不是组织管理员
     */
    @Hidden
    @GetMapping("/orgAdmin")
    boolean orgAdmin();

    /**
     * 判断当前用户是否为默认组织管理员
     *
     * <p>检查当前登录用户是否为默认组织的管理员。
     * 内部接口，用于权限控制。</p>
     *
     * @return true：是默认组织管理员；false：不是默认组织管理员
     */
    @Hidden
    @GetMapping("/defaultOrgAdmin")
    boolean defaultOrgAdmin();

    /**
     * 查询子组织用户
     *
     * <p>查询指定组织列表下的所有用户。
     * 内部接口，用于组织架构管理。</p>
     *
     * @param oidList 组织 ID 列表
     * @return 用户列表
     */
    @Hidden
    @PostMapping("/subOrgUser")
    List<UserItem> subOrgUser(@RequestBody List<Long> oidList);

    /**
     * 获取接收人用户 ID 列表
     *
     * <p>内部方法，根据条件获取消息接收人的用户 ID 列表。</p>
     *
     * @param request 接收人查询请求
     * @return 用户 ID 列表
     */
    List<Long> getRecipientUserIds(UserReciRequest request);

    /**
     * 根据账号获取用户 ID
     *
     * <p>内部方法，通过账号查询用户 ID。</p>
     *
     * @param account 用户账号
     * @return 用户 ID 列表
     */
    List<Long> getUserIdByAccount(String account);

    /**
     * 根据姓名获取用户 ID
     *
     * <p>内部方法，通过姓名查询用户 ID。</p>
     *
     * @param name 用户姓名
     * @return 用户 ID 列表
     */
    List<Long> getUserIdByName(String name);

    /**
     * 根据用户 ID 列表批量查询用户信息
     *
     * <p>内部方法，批量获取用户的基本信息。</p>
     *
     * @param ids 用户 ID 列表
     * @return 用户信息列表
     */
    List<Map<String, Object>> listUserInfosByIds(List<Long> ids);

    /**
     * 获取 MFA 二维码信息
     *
     * <p>获取多因素认证（MFA）的二维码信息，用于绑定认证器应用。
     * 用户扫描二维码后可在认证器应用中生成动态验证码。</p>
     *
     * @return MFA 二维码信息，包含二维码图片和密钥
     */
    @Operation(summary = "MFA二维码信息")
    @GetMapping("/mfaQr")
    MfaQrVO mfaQr();

    /**
     * 查询 MFA 绑定状态
     *
     * <p>检查当前用户是否已绑定多因素认证（MFA）。</p>
     *
     * @return true：已绑定；false：未绑定
     */
    @Operation(summary = "MFA绑定状态")
    @GetMapping("/mfabound")
    Boolean mfaBound();

    /**
     * 绑定 MFA
     *
     * <p>将多因素认证（MFA）绑定到当前用户账号。
     * 绑定后，用户登录时需要提供动态验证码。</p>
     *
     * @param dto MFA 绑定信息，包含验证码等
     */
    @Operation(summary = "绑定MFA")
    @PostMapping("/mfaBind")
    void mfaBind(@RequestBody MfaLoginDTO dto);

    /**
     * 解绑 MFA
     *
     * <p>解除当前用户的多因素认证（MFA）绑定。
     * 解绑后，用户登录时不再需要提供动态验证码。</p>
     *
     * @param code 验证码，用于确认解绑操作
     * @return 操作结果消息
     */
    @Operation(summary = "解绑MFA")
    @PostMapping("/mfaUnbind/{code}")
    String mfaUnbind(@PathVariable("code") String code);

    /**
     * 重置用户 MFA 绑定状态
     *
     * <p>管理员重置指定用户的 MFA 绑定状态。
     * 用于用户丢失认证器时的应急处理。</p>
     *
     * @param id 用户 ID
     */
    @Operation(summary = "重置MFA绑定状态")
    @PostMapping("/mfaRest/{id}")
    void resetBind(@PathVariable("id") Long id);

    /**
     * 获取用户语言偏好
     *
     * <p>获取当前用户的界面语言设置。
     * 内部接口，用于前端国际化。</p>
     *
     * @return 语言代码
     */
    @Hidden
    @GetMapping("/lang")
    String userLang();

}

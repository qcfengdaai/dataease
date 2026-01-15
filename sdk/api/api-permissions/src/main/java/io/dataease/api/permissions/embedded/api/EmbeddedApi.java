package io.dataease.api.permissions.embedded.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.embedded.dto.EmbeddedCreator;
import io.dataease.api.permissions.embedded.dto.EmbeddedEditor;
import io.dataease.api.permissions.embedded.dto.EmbeddedOrigin;
import io.dataease.api.permissions.embedded.dto.EmbeddedResetRequest;
import io.dataease.api.permissions.embedded.vo.EmbeddedGridVO;
import io.dataease.license.config.XpackResource;
import io.dataease.model.KeywordRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 嵌入式集成管理接口
 *
 * <p>提供 DataEase 嵌入式集成功能的完整管理，支持将 DataEase 的仪表板、图表等资源嵌入到
 * 第三方系统中。通过 AppId 和 AppSecret 进行安全认证，确保嵌入内容的安全性。</p>
 *
 * <p>嵌入式集成的核心功能：
 * <ul>
 *   <li>应用管理：创建、编辑、删除嵌入式应用配置</li>
 *   <li>密钥管理：生成和重置 AppId/AppSecret 密钥对</li>
 *   <li>域名白名单：配置允许嵌入的域名，防止跨域攻击</li>
 *   <li>Token 认证：提供基于 Token 的安全认证机制</li>
 * </ul>
 * </p>
 *
 * <p>嵌入式集成的应用场景：
 * <ul>
 *   <li>第三方系统集成：在 OA、ERP 等系统中嵌入 DataEase 报表</li>
 *   <li>门户网站集成：在企业门户中展示数据可视化内容</li>
 *   <li>移动应用集成：在 App 中嵌入数据分析功能</li>
 *   <li>客户系统交付：为客户提供白标化的数据分析能力</li>
 * </ul>
 * </p>
 *
 * <p>安全机制：
 * <ul>
 *   <li>AppId/AppSecret 认证：防止未授权访问</li>
 *   <li>域名白名单：限制嵌入页面的来源域名</li>
 *   <li>Token 过期机制：定时刷新 Token，避免长期有效的凭证</li>
 *   <li>HTTPS 加密传输：保护数据传输安全</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 2.0
 */
@Tag(name = "嵌入式")
@ApiSupport(order = 883, author = "fit2cloud-someone")
@XpackResource
public interface EmbeddedApi {

    /**
     * 分页查询嵌入式应用列表
     *
     * <p>查询所有配置的嵌入式应用，支持关键字搜索和分页。
     * 返回结果包含应用名称、AppId、AppSecret、域名白名单等信息。</p>
     *
     * @param goPage 页码，从1开始
     * @param pageSize 每页记录数
     * @param request 查询条件，支持按应用名称关键字搜索
     * @return 嵌入式应用的分页列表
     */
    @Operation(summary = "查询")
    @ApiOperationSupport(order = 1)
    @PostMapping("/pager/{goPage}/{pageSize}")
    IPage<EmbeddedGridVO> queryGrid(@PathVariable("goPage") int goPage, @PathVariable("pageSize") int pageSize, @RequestBody KeywordRequest request);

    /**
     * 创建嵌入式应用
     *
     * <p>创建一个新的嵌入式应用配置，系统会自动生成 AppId 和 AppSecret。
     * AppSecret 仅在创建时返回一次，请妥善保管。</p>
     *
     * <p>创建流程：
     * <ol>
     *   <li>填写应用名称和域名白名单</li>
     *   <li>系统生成唯一的 AppId 和随机的 AppSecret</li>
     *   <li>返回完整的应用配置信息</li>
     * </ol>
     * </p>
     *
     * @param creator 嵌入式应用创建参数，包含名称、域名等
     */
    @Operation(summary = "创建")
    @ApiOperationSupport(order = 2)
    @PostMapping("/create")
    void create(@RequestBody EmbeddedCreator creator);

    /**
     * 编辑嵌入式应用
     *
     * <p>修改已存在的嵌入式应用配置，可以更新应用名称和域名白名单。
     * 注意：编辑操作不会修改 AppId 和 AppSecret。</p>
     *
     * @param editor 嵌入式应用编辑参数，包含应用ID、名称、域名等
     */
    @Operation(summary = "编辑")
    @ApiOperationSupport(order = 3)
    @PostMapping("/edit")
    void edit(@RequestBody EmbeddedEditor editor);

    /**
     * 删除嵌入式应用
     *
     * <p>删除指定的嵌入式应用配置。删除后，使用该应用凭证的所有嵌入页面将无法访问。
     * 请确保该应用不再被使用后再执行删除操作。</p>
     *
     * @param id 嵌入式应用ID
     */
    @Operation(summary = "删除")
    @ApiOperationSupport(order = 4)
    @Parameter(name = "id", description = "ID", required = true, in = ParameterIn.PATH)
    @PostMapping("/delete/{id}")
    void delete(@PathVariable("id") Long id);

    /**
     * 批量删除嵌入式应用
     *
     * <p>一次删除多个嵌入式应用配置。删除后，使用这些应用凭证的所有嵌入页面将无法访问。</p>
     *
     * @param ids 要删除的嵌入式应用ID列表
     */
    @Operation(summary = "批量删除")
    @ApiOperationSupport(order = 4)
    @PostMapping("/batchDelete")
    void batchDelete(@RequestBody List<Long> ids);

    /**
     * 重置应用密钥
     *
     * <p>为指定的嵌入式应用重新生成 AppSecret。重置后，旧的 AppSecret 立即失效，
     * 所有使用旧密钥的嵌入页面需要更新为新密钥才能继续访问。</p>
     *
     * <p>重置场景：
     * <ul>
     *   <li>密钥泄露：怀疑密钥被泄露时立即重置</li>
     *   <li>定期轮换：按照安全策略定期更换密钥</li>
     *   <li>密钥丢失：遗忘密钥时通过重置获取新密钥</li>
     * </ul>
     * </p>
     *
     * @param request 重置请求，包含应用ID和新的 AppSecret
     */
    @ApiOperationSupport(order = 5)
    @Operation(summary = "重置密钥")
    @PostMapping("/reset")
    void reset(@RequestBody EmbeddedResetRequest request);

    /**
     * 获取所有嵌入式应用的域名列表
     *
     * <p>返回所有已配置嵌入式应用的域名白名单集合，用于系统内部跨域配置。
     * 内部接口，不对外暴露。</p>
     *
     * @return 域名列表
     */
    @ApiOperationSupport(order = 6)
    @Operation(summary = "嵌入式应用域名集合", hidden = true)
    @GetMapping("/domainList")
    List<String> domainList();

    /**
     * 初始化 iframe 嵌入
     *
     * <p>内部接口，用于验证嵌入页面的来源域名和 Token，确保嵌入请求的合法性。
     * 在 iframe 加载 DataEase 页面时调用。</p>
     *
     * @param origin 包含 Token 和来源域名的对象
     */
    @Hidden
    @PostMapping("/initIframe")
    void initIframe(@RequestBody EmbeddedOrigin origin);

    /**
     * 获取 Token 生成参数
     *
     * <p>返回生成嵌入式 Token 所需的参数配置，包括加密算法、过期时间等。
     * 第三方系统使用这些参数和 AppSecret 生成访问 Token。</p>
     *
     * @return Token 参数配置 Map，包含算法、过期时间等信息
     */
    @ApiOperationSupport(order = 7)
    @Operation(summary = "获取Token参数")
    @GetMapping("/getTokenArgs")
    Map<String, Object> getTokenArgs();

    /**
     * 获取嵌入式应用数量限制
     *
     * <p>内部接口，返回系统允许创建的嵌入式应用最大数量。
     * 不同的许可证版本可能有不同的限制。</p>
     *
     * @return 允许创建的最大应用数量
     */
    @Hidden
    @GetMapping("/limitCount")
    int getLimitCount();
}

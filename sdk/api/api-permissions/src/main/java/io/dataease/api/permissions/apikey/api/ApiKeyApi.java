package io.dataease.api.permissions.apikey.api;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.api.permissions.apikey.dto.ApikeyEnableEditor;
import io.dataease.api.permissions.apikey.vo.ApiKeyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * API Key 管理接口
 *
 * <p>提供 API Key 的完整生命周期管理功能，包括生成、查询、状态切换和删除操作。
 * API Key 用于系统的外部 API 调用鉴权，每个用户可以拥有多个 API Key。</p>
 *
 * <p>API Key 包含 accessKey 和 accessSecret 两部分：
 * <ul>
 *   <li>accessKey: 公开的访问标识</li>
 *   <li>accessSecret: 私密的访问密钥，仅在生成时显示一次</li>
 * </ul>
 * </p>
 *
 * @author fit2cloud-someone
 * @since 1.0
 */
@Tag(name = "API Key")
@ApiSupport(order = 884, author = "fit2cloud-someone")
public interface ApiKeyApi {

    /**
     * 生成新的 API Key
     *
     * <p>为当前登录用户生成一个新的 API Key 密钥对。生成的 accessSecret 仅在本次返回时显示，
     * 后续无法再次查看，请妥善保管。每个用户可以生成多个 API Key，用于不同的应用场景。</p>
     *
     * <p>生成规则：
     * <ul>
     *   <li>accessKey: 32位随机字符串</li>
     *   <li>accessSecret: 64位随机字符串（加密存储）</li>
     *   <li>默认状态: 启用</li>
     * </ul>
     * </p>
     */
    @Operation(summary = "生成")
    @ApiOperationSupport(order = 1)
    @PostMapping("/generate")
    void generate();

    /**
     * 查询当前用户的所有 API Key
     *
     * <p>查询当前登录用户的所有 API Key 列表，返回结果中不包含 accessSecret 信息，
     * 仅包含 accessKey、状态、创建时间等基本信息。</p>
     *
     * @return API Key 列表，按创建时间倒序排列
     */
    @Operation(summary = "查询")
    @ApiOperationSupport(order = 2)
    @GetMapping("/query")
    List<ApiKeyVO> query();

    /**
     * 切换 API Key 的启用/禁用状态
     *
     * <p>用于临时禁用或重新启用某个 API Key。禁用后，使用该 Key 的 API 调用将被拒绝。
     * 这比删除 Key 更灵活，可以在需要时重新启用。</p>
     *
     * <p>应用场景：
     * <ul>
     *   <li>临时停用某个应用的 API 访问权限</li>
     *   <li>疑似密钥泄露时快速禁用</li>
     *   <li>定期轮换密钥时的过渡期管理</li>
     * </ul>
     * </p>
     *
     * @param editor 状态切换参数，包含 API Key ID 和目标状态
     */
    @Operation(summary = "切换状态")
    @ApiOperationSupport(order = 3)
    @PostMapping("/switch")
    void switchEnable(@RequestBody ApikeyEnableEditor editor);

    /**
     * 删除指定的 API Key
     *
     * <p>永久删除指定的 API Key，删除后该 Key 将立即失效，所有使用该 Key 的 API 调用都将被拒绝。
     * 此操作不可恢复，请谨慎使用。</p>
     *
     * <p>删除前建议：
     * <ul>
     *   <li>确认该 Key 不再被任何应用使用</li>
     *   <li>检查相关应用的日志，确认最后使用时间</li>
     *   <li>如果只是临时停用，建议使用"切换状态"功能</li>
     * </ul>
     * </p>
     *
     * @param id API Key 的唯一标识符
     */
    @Operation(summary = "删除")
    @ApiOperationSupport(order = 4)
    @Parameter(name = "id", description = "ID", required = true, in = ParameterIn.PATH)
    @PostMapping("/delete/{id}")
    void delete(@PathVariable("id") Long id);
}

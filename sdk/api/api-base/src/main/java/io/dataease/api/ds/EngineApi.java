package io.dataease.api.ds;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.dataease.auth.DeApiPath;
import io.dataease.extensions.datasource.dto.DatasourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import static io.dataease.constant.AuthResourceEnum.DATASOURCE;

/**
 * DataEase数据引擎管理API接口
 * <p>
 * 提供数据引擎的配置、管理和维护功能。数据引擎是DataEase系统的核心组件，
 * 负责数据的存储、查询、计算和缓存等操作。
 * </p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>引擎配置</b> - 获取和保存数据引擎配置</li>
 *   <li><b>连接校验</b> - 验证数据引擎的连接状态</li>
 *   <li><b>功能支持</b> - 检查引擎特性支持情况</li>
 * </ul>
 *
 * @author DataEase团队
 * @since 1.0.0
 */
@Tag(name = "引擎管理:基础")
@ApiSupport(order = 970)
@DeApiPath(value = "/engine", rt = DATASOURCE)
public interface EngineApi {

    /**
     * 获取数据引擎配置
     * <p>
     * 获取当前系统的数据引擎配置信息。数据引擎是系统的核心存储组件，
     * 用于存储和处理所有的数据集和缓存数据。
     * </p>
     *
     * @return 数据引擎的配置信息
     */
    @Operation(summary = "查询")
    @GetMapping("/getEngine")
    DatasourceDTO getEngine();

    /**
     * 保存数据引擎配置
     * <p>
     * 更新数据引擎的配置参数。包括连接参数、性能设置、
     * 缓存策略等。保存前会验证配置的有效性。
     * </p>
     *
     * @param datasourceDTO 数据引擎配置信息
     */
    @Operation(summary = "保存")
    @PostMapping("/save")
    void save(@RequestBody DatasourceDTO datasourceDTO);

    /**
     * 校验数据引擎连接
     * <p>
     * 验证数据引擎的连接配置是否正确。检查网络连通性、
     * 认证信息和数据库访问权限，确保引擎能够正常工作。
     * </p>
     *
     * @param datasourceDTO 待验证的数据引擎配置
     * @throws Exception 当验证失败时抛出异常
     */
    @Operation(summary = "校验")
    @PostMapping("/validate")
    void validate(@RequestBody DatasourceDTO datasourceDTO) throws Exception;

    /**
     * 根据ID校验数据引擎
     * <p>
     * 通过数据引擎ID获取已保存的配置并进行连接验证。
     * 用于检查现有引擎配置的有效性和健康状态。
     * </p>
     *
     * @param id 数据引擎ID
     * @throws Exception 当验证失败或引擎不存在时抛出异常
     */
    @Operation(summary = "根据ID校验")
    @PostMapping("/validate/{id}")
    void validateById(@PathVariable Long id) throws Exception;

    /**
     * 检查是否支持设置主键
     * <p>
     * 检查当前数据引擎是否支持主键设置功能。不同的数据引擎
     * 对主键的支持程度不同，需要检查特性支持情况。
     * </p>
     *
     * @return true表示支持主键设置，false表示不支持
     * @throws Exception 当检查过程发生错误时抛出异常
     */
    @Operation(summary = "是否支持设置主键")
    @GetMapping("/supportSetKey")
    boolean supportSetKey() throws Exception;
}

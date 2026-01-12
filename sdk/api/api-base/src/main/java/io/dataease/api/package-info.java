/**
 * DataEase业务API定义包（SDK/API模块）
 * <p>
 * 定义DataEase系统所有业务模块的API接口、DTO、VO等数据传输对象。
 * 本包遵循前后端分离架构，提供统一的API规范，供core-backend模块实现。
 * </p>
 *
 * <h2>模块结构</h2>
 *
 * <h3>主要业务模块</h3>
 * <ul>
 *   <li><b>io.dataease.api.dataset</b> - 数据集管理API（数据集创建、编辑、查询、联合等）</li>
 *   <li><b>io.dataease.api.chart</b> - 图表管理API（图表配置、数据查询、渲染等）</li>
 *   <li><b>io.dataease.api.panel</b> - 仪表板API（仪表板布局、组件、分享等）</li>
 *   <li><b>io.dataease.api.visualization</b> - 可视化API（可视化资源管理）</li>
 *   <li><b>io.dataease.api.ds</b> - 数据源API（数据源连接、测试、查询等）</li>
 *   <li><b>io.dataease.api.template</b> - 模板API（仪表板模板、图表模板等）</li>
 * </ul>
 *
 * <h3>系统管理模块</h3>
 * <ul>
 *   <li><b>io.dataease.api.system</b> - 系统设置API（系统参数、许可证、邮件等）</li>
 *   <li><b>io.dataease.api.menu</b> - 菜单管理API</li>
 *   <li><b>io.dataease.api.log</b> - 日志管理API</li>
 *   <li><b>io.dataease.api.license</b> - 许可证API</li>
 *   <li><b>io.dataease.api.map</b> - 地图配置API</li>
 *   <li><b>io.dataease.api.font</b> - 字体管理API</li>
 * </ul>
 *
 * <h3>集成和通知模块</h3>
 * <ul>
 *   <li><b>io.dataease.api.email</b> - 邮件API</li>
 *   <li><b>io.dataease.api.dingtalk</b> - 钉钉集成API</li>
 *   <li><b>io.dataease.api.wecom</b> - 企业微信集成API</li>
 *   <li><b>io.dataease.api.lark</b> - 飞书集成API</li>
 *   <li><b>io.dataease.api.webhook</b> - Webhook API</li>
 *   <li><b>io.dataease.api.msgCenter</b> - 消息中心API</li>
 * </ul>
 *
 * <h3>高级功能模块</h3>
 * <ul>
 *   <li><b>io.dataease.api.report</b> - 报表API（定时报表、报表订阅等）</li>
 *   <li><b>io.dataease.api.export</b> - 导出API</li>
 *   <li><b>io.dataease.api.exportCenter</b> - 导出中心API</li>
 *   <li><b>io.dataease.api.threshold</b> - 阈值告警API</li>
 *   <li><b>io.dataease.api.ai</b> - AI功能API</li>
 *   <li><b>io.dataease.api.free</b> - 自由布局API</li>
 *   <li><b>io.dataease.api.communicate</b> - 通讯API</li>
 * </ul>
 *
 * <h3>企业版扩展（XPack）</h3>
 * <ul>
 *   <li><b>io.dataease.api.xpack.appearance</b> - 外观定制API</li>
 *   <li><b>io.dataease.api.xpack.component</b> - 组件扩展API</li>
 *   <li><b>io.dataease.api.xpack.dataFilling</b> - 数据填报API</li>
 *   <li><b>io.dataease.api.xpack.plugin</b> - 插件管理API</li>
 *   <li><b>io.dataease.api.xpack.settings</b> - 高级设置API</li>
 *   <li><b>io.dataease.api.xpack.share</b> - 高级分享API</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 *
 * <h3>1. 接口定义规范</h3>
 * <ul>
 *   <li>使用Java接口定义API方法</li>
 *   <li>使用Feign注解标注HTTP方法和路径</li>
 *   <li>core-backend模块实现这些接口</li>
 * </ul>
 *
 * <h3>2. DTO/VO分离</h3>
 * <ul>
 *   <li><b>DTO（dto包）</b>：数据传输对象，用于参数传递和数据转换</li>
 *   <li><b>VO（vo包）</b>：视图对象，用于API响应</li>
 *   <li><b>Request（request包）</b>：请求对象，用于接收前端参数</li>
 *   <li><b>Response（response包）</b>：响应对象，用于返回处理结果</li>
 * </ul>
 *
 * <h3>3. 跨模块调用</h3>
 * <ul>
 *   <li>使用Feign进行模块间通信</li>
 *   <li>所有接口标注@FeignClient</li>
 *   <li>支持分布式部署</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：数据集API定义</h3>
 * <pre>
 * // SDK/API模块 - API接口定义
 * package io.dataease.api.dataset;
 *
 * {@literal @}FeignClient(value = "dataset-service")
 * public interface DatasetApi {
 *
 *     {@literal @}PostMapping("/api/dataset")
 *     DatasetVO create(@RequestBody DatasetRequest request);
 *
 *     {@literal @}GetMapping("/api/dataset/{id}")
 *     DatasetVO getById(@PathVariable Long id);
 *
 *     {@literal @}DeleteMapping("/api/dataset/{id}")
 *     void deleteById(@PathVariable Long id);
 * }
 *
 * // core-backend模块 - API实现
 * package io.dataease.dataset.controller;
 *
 * {@literal @}RestController
 * {@literal @}DeApiPath(value = "/api/dataset", rt = AuthResourceEnum.DATASET)
 * public class DatasetController implements DatasetApi {
 *
 *     {@literal @}Override
 *     {@literal @}DePermit(value = "dataset:create")
 *     public DatasetVO create(DatasetRequest request) {
 *         return datasetService.create(request);
 *     }
 *
 *     {@literal @}Override
 *     {@literal @}DePermit(value = "#p0 + ':read'")
 *     public DatasetVO getById(Long id) {
 *         return datasetService.getById(id);
 *     }
 * }
 * </pre>
 *
 * <h3>示例2：DTO和VO定义</h3>
 * <pre>
 * // DTO - 用于业务逻辑
 * package io.dataease.api.dataset.dto;
 *
 * public class DatasetDTO {
 *     private Long id;
 *     private String name;
 *     private String type;
 *     private String config;
 *     // getters and setters
 * }
 *
 * // VO - 用于API响应
 * package io.dataease.api.dataset.vo;
 *
 * public class DatasetVO {
 *     private Long id;
 *     private String name;
 *     private String type;
 *     private Long createTime;
 *     private String createBy;
 *     // getters and setters
 * }
 *
 * // Request - 用于接收请求
 * package io.dataease.api.dataset;
 *
 * public class DatasetRequest {
 *     {@literal @}NotBlank(message = "名称不能为空")
 *     private String name;
 *
 *     {@literal @}NotBlank(message = "类型不能为空")
 *     private String type;
 *
 *     private String config;
 *     // getters and setters
 * }
 * </pre>
 *
 * <h3>示例3：Feign调用</h3>
 * <pre>
 * // 在其他模块中调用DatasetApi
 * {@literal @}Service
 * public class ChartService {
 *
 *     {@literal @}Resource
 *     private DatasetApi datasetApi;  // Feign自动注入
 *
 *     public Chart createChartWithDataset(Long datasetId) {
 *         // 通过Feign调用数据集API
 *         DatasetVO dataset = datasetApi.getById(datasetId);
 *
 *         // 创建图表
 *         Chart chart = new Chart();
 *         chart.setDatasetId(datasetId);
 *         chart.setDatasetName(dataset.getName());
 *
 *         return chartMapper.insert(chart);
 *     }
 * }
 * </pre>
 *
 * <h2>模块依赖关系</h2>
 *
 * <pre>
 * core-backend (业务实现)
 *     ↓ 依赖
 * sdk/api (API定义)
 *     ↓ 依赖
 * sdk/common (公共组件)
 * </pre>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 接口稳定性</h3>
 * <ul>
 *   <li>API接口一旦发布，要保持向后兼容</li>
 *   <li>修改接口签名要谨慎，可能影响多个模块</li>
 *   <li>使用版本号管理重大变更</li>
 * </ul>
 *
 * <h3>2. DTO/VO转换</h3>
 * <ul>
 *   <li>不要直接返回Entity对象</li>
 *   <li>使用BeanUtils或MapStruct进行转换</li>
 *   <li>避免暴露敏感字段</li>
 * </ul>
 *
 * <h3>3. 参数校验</h3>
 * <ul>
 *   <li>使用JSR-303注解进行参数校验</li>
 *   <li>在Controller层统一处理校验异常</li>
 *   <li>提供清晰的错误提示</li>
 * </ul>
 *
 * <h3>4. 文档维护</h3>
 * <ul>
 *   <li>使用Swagger/OpenAPI生成API文档</li>
 *   <li>保持JavaDoc注释的完整性</li>
 *   <li>及时更新变更日志</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.auth.DePermit
 * @see io.dataease.result.Result
 */
package io.dataease.api;

/**
 * 通用数据模型包
 * <p>
 * 定义DataEase系统中通用的数据模型、基础类和接口，
 * 包括树形结构、分页模型、导出任务等跨模块使用的数据结构。
 * </p>
 *
 * <h2>核心模型类</h2>
 *
 * <h3>1. 基础模型</h3>
 * <ul>
 *   <li>{@link io.dataease.model.DeModel} - DataEase基础模型接口</li>
 *   <li>{@link io.dataease.model.RSAModel} - RSA加密模型</li>
 * </ul>
 *
 * <h3>2. 树形结构</h3>
 * <ul>
 *   <li>{@link io.dataease.model.ITreeBase} - 树节点基础接口</li>
 *   <li>{@link io.dataease.model.TreeModel} - 树形数据模型</li>
 *   <li>{@link io.dataease.model.TreeBaseModel} - 树形基础模型</li>
 *   <li>{@link io.dataease.model.TreeResultModel} - 树形结果模型</li>
 *   <li>{@link io.dataease.model.BusiNodeVO} - 业务节点视图对象</li>
 *   <li>{@link io.dataease.model.BusiNodeRequest} - 业务节点请求对象</li>
 * </ul>
 *
 * <h3>3. 查询和请求</h3>
 * <ul>
 *   <li>{@link io.dataease.model.KeywordRequest} - 关键词查询请求</li>
 * </ul>
 *
 * <h3>4. 导出任务</h3>
 * <ul>
 *   <li>{@link io.dataease.model.ExportTaskDTO} - 导出任务数据传输对象</li>
 * </ul>
 *
 * <h3>5. 日志模型</h3>
 * <ul>
 *   <li>{@link io.dataease.model.LogItemModel} - 日志项模型</li>
 * </ul>
 *
 * <h3>6. Excel相关</h3>
 * <ul>
 *   <li>{@link io.dataease.model.excel.ErrWriteHandler} - Excel错误写入处理器</li>
 *   <li>{@link io.dataease.model.excel.AutoAdaptWidthStyleStrategy} - 自动列宽策略</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 *
 * <h3>使用位置</h3>
 * <ul>
 *   <li>数据集目录树 - 使用TreeModel</li>
 *   <li>组织架构树 - 使用TreeBaseModel</li>
 *   <li>仪表板文件夹 - 使用BusiNodeVO</li>
 *   <li>数据导出任务 - 使用ExportTaskDTO</li>
 *   <li>操作日志 - 使用LogItemModel</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>示例1：构建树形结构</h3>
 * <pre>
 * // 定义树节点实体
 * public class DatasetFolder implements ITreeBase&lt;Long, DatasetFolder&gt; {
 *     private Long id;
 *     private Long pid;
 *     private String name;
 *     private List&lt;DatasetFolder&gt; children;
 *
 *     {@literal @}Override
 *     public Long getId() { return id; }
 *
 *     {@literal @}Override
 *     public Long getPid() { return pid; }
 *
 *     {@literal @}Override
 *     public void setChildren(List&lt;DatasetFolder&gt; children) {
 *         this.children = children;
 *     }
 * }
 *
 * // 构建树
 * List&lt;DatasetFolder&gt; allFolders = folderMapper.selectList(null);
 * List&lt;DatasetFolder&gt; tree = TreeUtils.buildTree(
 *     allFolders,
 *     0L,  // 根节点ID
 *     DatasetFolder::getId,
 *     DatasetFolder::getPid,
 *     DatasetFolder::setChildren
 * );
 * </pre>
 *
 * <h3>示例2：使用TreeModel</h3>
 * <pre>
 * // 查询文件夹树
 * {@literal @}GetMapping("/folder/tree")
 * public List&lt;TreeModel&lt;FolderVO&gt;&gt; getFolderTree() {
 *     List&lt;Folder&gt; folders = folderService.list();
 *
 *     // 转换为TreeModel
 *     List&lt;TreeModel&lt;FolderVO&gt;&gt; tree = folders.stream()
 *         .map(folder -> {
 *             TreeModel&lt;FolderVO&gt; node = new TreeModel&lt;&gt;();
 *             node.setId(folder.getId());
 *             node.setPid(folder.getPid());
 *             node.setText(folder.getName());
 *
 *             FolderVO vo = new FolderVO();
 *             vo.setType(folder.getType());
 *             vo.setCreateTime(folder.getCreateTime());
 *             node.setData(vo);
 *
 *             return node;
 *         })
 *         .collect(Collectors.toList());
 *
 *     return TreeUtils.buildTree(tree);
 * }
 * </pre>
 *
 * <h3>示例3：导出任务</h3>
 * <pre>
 * // 创建导出任务
 * ExportTaskDTO task = new ExportTaskDTO();
 * task.setId(IDUtils.snowID());
 * task.setTaskName("数据集导出");
 * task.setTaskType("DATASET");
 * task.setStatus("PENDING");
 * task.setCreateBy(AuthUtils.getUser().getUserId());
 *
 * exportTaskService.create(task);
 *
 * // 异步执行导出
 * CompletableFuture.runAsync(() -> {
 *     try {
 *         // 执行导出逻辑
 *         String filePath = exportService.exportDataset(datasetId);
 *
 *         // 更新任务状态
 *         task.setStatus("SUCCESS");
 *         task.setFilePath(filePath);
 *         exportTaskService.update(task);
 *     } catch (Exception e) {
 *         // 导出失败
 *         task.setStatus("FAILED");
 *         task.setErrorMsg(e.getMessage());
 *         exportTaskService.update(task);
 *     }
 * });
 * </pre>
 *
 * <h3>示例4：关键词搜索</h3>
 * <pre>
 * // 接收搜索请求
 * {@literal @}PostMapping("/dataset/search")
 * public List&lt;Dataset&gt; searchDataset(@RequestBody KeywordRequest request) {
 *     String keyword = request.getKeyword();
 *
 *     // 模糊查询
 *     QueryWrapper&lt;Dataset&gt; wrapper = new QueryWrapper&lt;&gt;();
 *     wrapper.like("name", keyword)
 *            .or()
 *            .like("description", keyword);
 *
 *     return datasetMapper.selectList(wrapper);
 * }
 * </pre>
 *
 * <h3>示例5：业务节点</h3>
 * <pre>
 * // 查询业务节点树
 * {@literal @}PostMapping("/node/tree")
 * public List&lt;BusiNodeVO&gt; getNodeTree(@RequestBody BusiNodeRequest request) {
 *     Long parentId = request.getParentId();
 *     String nodeType = request.getNodeType();
 *
 *     // 查询子节点
 *     List&lt;BusiNodeVO&gt; nodes = busiNodeService.getChildren(parentId, nodeType);
 *
 *     return nodes;
 * }
 * </pre>
 *
 * <h3>示例6：Excel导出（自动列宽）</h3>
 * <pre>
 * // 导出Excel，自动调整列宽
 * {@literal @}GetMapping("/export/excel")
 * public void exportExcel(HttpServletResponse response) {
 *     // 设置响应头
 *     response.setContentType("application/vnd.ms-excel");
 *     response.setHeader("Content-Disposition",
 *         "attachment;filename=dataset.xlsx");
 *
 *     // 获取数据
 *     List&lt;DatasetExportVO&gt; data = datasetService.getExportData();
 *
 *     // 使用EasyExcel导出，自动调整列宽
 *     EasyExcel.write(response.getOutputStream(), DatasetExportVO.class)
 *         .registerWriteHandler(new AutoAdaptWidthStyleStrategy())
 *         .sheet("数据集")
 *         .doWrite(data);
 * }
 * </pre>
 *
 * <h2>树形结构设计</h2>
 *
 * <h3>树节点接口（ITreeBase）</h3>
 * <ul>
 *   <li><code>getId()</code> - 获取节点ID</li>
 *   <li><code>getPid()</code> - 获取父节点ID</li>
 *   <li><code>setChildren()</code> - 设置子节点列表</li>
 * </ul>
 *
 * <h3>树形数据特点</h3>
 * <ul>
 *   <li>每个节点有唯一ID</li>
 *   <li>每个节点有父节点ID（根节点pid为null或0）</li>
 *   <li>节点可以有多个子节点（children列表）</li>
 *   <li>支持无限层级嵌套</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 *
 * <h3>1. 树形结构</h3>
 * <ul>
 *   <li>避免循环引用（A的父节点是B，B的父节点又是A）</li>
 *   <li>注意性能，层级过深会影响查询效率</li>
 *   <li>使用递归查询时要设置深度限制</li>
 * </ul>
 *
 * <h3>2. 导出任务</h3>
 * <ul>
 *   <li>大数据量导出使用异步任务</li>
 *   <li>导出文件保存在临时目录，定期清理</li>
 *   <li>提供下载链接，设置过期时间</li>
 * </ul>
 *
 * <h3>3. Excel处理</h3>
 * <ul>
 *   <li>使用EasyExcel处理大文件，避免OOM</li>
 *   <li>自动列宽策略会遍历所有数据，注意性能</li>
 *   <li>错误处理器用于标记错误行</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.utils.TreeUtils
 */
package io.dataease.model;

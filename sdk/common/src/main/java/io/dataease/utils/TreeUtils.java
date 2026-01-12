package io.dataease.utils;

import io.dataease.constant.SortConstants;
import io.dataease.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 树形结构工具类
 * <p>
 * 提供树形数据结构的构建、转换和排序功能。
 * 主要用于将扁平化的列表数据转换为树形结构，支持多种树形结构模型的转换。
 * 广泛应用于组织架构、菜单管理、资源目录等需要展示层级关系的业务场景。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>扁平转树形 - 将扁平列表根据父子关系转换为树形结构</li>
 *   <li>树形数据排序 - 支持按名称、时间等多种排序方式</li>
 *   <li>多模型支持 - 支持ITreeBase、TreeBaseModel等多种树形模型</li>
 *   <li>重复节点处理 - 支持处理路径重复的树形结构</li>
 *   <li>根节点识别 - 自动识别和处理根节点</li>
 * </ul>
 *
 * <p><b>支持的树形模型：</b></p>
 * <ul>
 *   <li>{@link ITreeBase} - 基础树形接口，包含id、pid、children等基本属性</li>
 *   <li>{@link TreeBaseModel} - 树形基础模型</li>
 *   <li>{@link TreeResultModel} - 树形结果模型</li>
 *   <li>{@link BusiNodeVO} - 业务节点VO模型</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>资源管理 - 仪表板、数据集、数据源等资源的树形展示</li>
 *   <li>组织架构 - 部门、用户组等组织结构的树形展示</li>
 *   <li>菜单管理 - 系统菜单的树形结构构建</li>
 *   <li>权限管理 - 权限资源的树形展示</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本的树形转换（最常用）
 * // 假设有部门列表：[{id:1,pid:0,name:"总部"}, {id:2,pid:1,name:"研发部"}, {id:3,pid:1,name:"市场部"}]
 * List&lt;Department&gt; departments = departmentService.getAllDepartments();
 * List&lt;Department&gt; tree = TreeUtils.mergeTree(departments, 0L);  // 0L是根节点的父ID
 * // 结果：总部 -> [研发部, 市场部]
 *
 * // 示例2：指定多个根节点PID
 * List&lt;Resource&gt; resources = resourceService.getAllResources();
 * List&lt;Resource&gt; tree = TreeUtils.mergeTree(resources, 0L, -1L);  // 支持pid=0或pid=-1的根节点
 *
 * // 示例3：使用默认根节点（pid=0）
 * List&lt;Menu&gt; menus = menuService.getAllMenus();
 * List&lt;Menu&gt; tree = TreeUtils.mergeTree(menus);  // 默认使用pid=0作为根节点
 *
 * // 示例4：树形结果排序
 * List&lt;BusiNodeVO&gt; nodes = nodeService.getNodes();
 * List&lt;BusiNodeVO&gt; sortedNodes = TreeUtils.customSortVO(nodes, SortConstants.NAME_ASC);
 * // 按名称升序排序
 *
 * // 示例5：处理带国际化前缀的树形结构
 * List&lt;AuthMenuModel&gt; authMenus = authService.getMenus();
 * List&lt;AuthMenuVO&gt; menuTree = TreeUtils.mergeTree(authMenus, AuthMenuVO.class, true);
 * // 第三个参数true表示添加i18n前缀："i18n_auth_menu." + menuName
 *
 * // 示例6：处理重复路径的树形结构
 * List&lt;PathNode&gt; pathNodes = pathService.getPathNodes();
 * List&lt;PathNode&gt; tree = TreeUtils.mergeDuplicateTree(pathNodes, 0L);
 * // 适用于路径类型的树形结构，如：/a/b/c
 *
 * // 示例7：在Controller中返回树形数据
 * {@code @GetMapping("/departments/tree")}
 * public List&lt;DepartmentVO&gt; getDepartmentTree() {
 *     List&lt;Department&gt; departments = departmentService.list();
 *     return TreeUtils.mergeTree(departments, 0L);
 * }
 *
 * // 示例8：完整的业务场景示例
 * public List&lt;ResourceVO&gt; getResourceTree(Long userId) {
 *     // 1. 查询用户有权限的资源列表
 *     List&lt;Resource&gt; resources = resourceService.getUserResources(userId);
 *
 *     // 2. 转换为树形结构
 *     List&lt;Resource&gt; tree = TreeUtils.mergeTree(resources, 0L);
 *
 *     // 3. 转换为VO（如果需要）
 *     return tree.stream()
 *         .map(r -&gt; BeanUtils.copyBean(new ResourceVO(), r))
 *         .collect(Collectors.toList());
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>数据完整性 - 确保父节点存在，否则子节点会被截断</li>
 *   <li>循环引用 - 避免出现循环引用（如A的父节点是B，B的父节点是A）</li>
 *   <li>根节点PID - 正确设置根节点的PID，通常是0或-1</li>
 *   <li>泛型约束 - 实体类必须实现ITreeBase接口或继承TreeBaseModel</li>
 *   <li>空列表处理 - 空列表会返回null而非空List，调用方需注意判空</li>
 *   <li>浮动节点 - 父节点缺失的节点会被挂到根节点下（如果根节点存在）</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>一次性查询 - 一次性查询所有节点，在内存中构建树形结构，避免递归查询数据库</li>
 *   <li>缓存树形结构 - 对于不常变化的树形数据（如菜单），可以缓存树形结构</li>
 *   <li>延迟加载 - 数据量大时，考虑只返回顶层节点，子节点按需加载</li>
 *   <li>权限过滤 - 先过滤权限，再构建树形结构，避免返回无权限的节点</li>
 *   <li>排序优化 - 在数据库查询时就进行排序，而非在树形结构构建后排序</li>
 * </ul>
 *
 * <p><b>树形结构示意图：</b></p>
 * <pre>
 * 扁平列表：
 * [{id:1, pid:0, name:"总部"},
 *  {id:2, pid:1, name:"研发部"},
 *  {id:3, pid:1, name:"市场部"},
 *  {id:4, pid:2, name:"后端组"},
 *  {id:5, pid:2, name:"前端组"}]
 *
 * 转换后的树形结构：
 * └─ 总部 (id:1, pid:0)
 *    ├─ 研发部 (id:2, pid:1)
 *    │  ├─ 后端组 (id:4, pid:2)
 *    │  └─ 前端组 (id:5, pid:2)
 *    └─ 市场部 (id:3, pid:1)
 * </pre>
 *
 * @author DataEase
 * @since 1.0.0
 * @see io.dataease.model.ITreeBase
 * @see io.dataease.model.TreeBaseModel
 * @see io.dataease.model.TreeResultModel
 */
public class TreeUtils {

    /**
     * 默认根节点标识
     */
    public final static String DEFAULT_ROOT = "root";

    /**
     * 路径分隔符
     * <p>
     * 用于构建和解析树形路径，如：a-de-b-de-c
     * </p>
     */
    public final static String SEPARATOR = "-de-";

    /**
     * 国际化前缀
     * <p>
     * 用于权限菜单的国际化key前缀
     * </p>
     */
    private final static String I18N_PREFIX = "i18n_auth_menu.";

    /**
     * 合并扁平列表为树形结构并转换类型
     * <p>
     * 将扁平的TreeBaseModel列表转换为树形的TreeResultModel结构。
     * 支持添加国际化前缀，主要用于权限菜单等需要国际化的场景。
     * </p>
     *
     * @param <T> 目标树形结果类型，必须继承TreeResultModel
     * @param <R> 源数据类型，必须继承TreeBaseModel
     * @param list 扁平的数据列表
     * @param tClass 目标类型的Class对象
     * @param appendI18nPrefix 是否在节点名称前添加国际化前缀（i18n_auth_menu.）
     * @return 树形结构的结果列表
     */
    public static <T extends TreeResultModel, R extends TreeBaseModel> List<T> mergeTree(List<R> list, Class<T> tClass, boolean appendI18nPrefix) {
        AtomicBoolean rootExist = new AtomicBoolean(false);
        List<TreeModel> modelList = list.stream().map(item -> {
            TreeModel treeModel = new TreeModel(item);
            if (isRoot(treeModel)) {
                rootExist.set(true);
            }
            return treeModel;
        }).toList();
        List<TreeModel> modelResult = new ArrayList<>();
        Map<Long, List<TreeModel>> childMap = modelList.stream().collect(Collectors.groupingBy(TreeModel::getPid));
        List<Long> existedList = new ArrayList<>();
        modelList.forEach(po -> {
            List<TreeModel> children = null;
            if (CollectionUtils.isNotEmpty(children = childMap.get(po.getId()))) {
                po.setChildren(children);
                existedList.addAll(children.stream().map(TreeModel::getId).toList());
            }
        });
        if (CollectionUtils.isEmpty(modelList)) {
            return null;
        }
        List<TreeModel> floatingList = modelList.stream().filter(node -> !isRoot(node) && !existedList.contains(node.getId())).toList();
        if (CollectionUtils.isNotEmpty(existedList)) {
            modelResult = modelList.stream().filter(node -> !existedList.contains(node.getId())).toList();
        } else {
            modelResult = modelList;
        }
        if (rootExist.get() && CollectionUtils.isNotEmpty(floatingList)) {
            modelResult = modelResult.stream().filter(TreeUtils::isRoot).collect(Collectors.toList());
            TreeModel root = modelResult.get(0);
            if (root.getChildren() == null) {
                root.setChildren(new ArrayList<>());
            }
            root.getChildren().addAll(floatingList);
        }

        return convertTree(modelResult, tClass, appendI18nPrefix);
    }

    /**
     * 判断节点是否为根节点
     * <p>
     * 根节点的判断条件：id=0 且 (pid为空 或 pid=-1)
     * </p>
     *
     * @param node 树形节点模型
     * @return true-是根节点，false-不是根节点
     */
    private static boolean isRoot(TreeModel node) {
        return node.getId().equals(0L) && (ObjectUtils.isEmpty(node.getPid()) || node.getPid().equals(-1L));
    }

    /**
     * 转换TreeModel列表为指定类型的树形结构
     * <p>
     * 递归转换TreeModel为目标类型，支持添加国际化前缀。
     * </p>
     *
     * @param <T> 目标树形结果类型
     * @param roots TreeModel根节点列表
     * @param tClass 目标类型的Class对象
     * @param appendI18nPrefix 是否添加国际化前缀
     * @return 转换后的树形结构列表
     */
    public static <T extends TreeResultModel> List<T> convertTree(List<TreeModel> roots, Class<T> tClass, boolean appendI18nPrefix) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < roots.size(); i++) {
            TreeModel node = roots.get(i);
            if (appendI18nPrefix) {
                node.getData().setName(I18N_PREFIX + node.getName());
            }
            T instance = null;
            try {
                instance = tClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            T vo = BeanUtils.copyBean(instance, node.getData(), "children");
            result.add(vo);
            List<TreeModel> children = null;
            if (!CollectionUtils.isEmpty(children = node.getChildren())) {
                vo.setChildren(convertTree(children, tClass, appendI18nPrefix));
            }
        }
        return result;
    }

    /**
     * 合并扁平列表为树形结构（ITreeBase接口）
     * <p>
     * 将实现ITreeBase接口的扁平列表转换为树形结构。
     * 这是最常用的树形转换方法，支持指定一个或多个根节点的父ID。
     * </p>
     *
     * <p><b>转换逻辑：</b></p>
     * <ol>
     *   <li>构建id到节点的映射表</li>
     *   <li>遍历所有节点，根据pid找到父节点</li>
     *   <li>将子节点添加到父节点的children列表</li>
     *   <li>返回根节点列表（pid在rootPid数组中的节点）</li>
     * </ol>
     *
     * @param <T> 树形节点类型，必须实现ITreeBase接口
     * @param tree 扁平的节点列表
     * @param rootPid 根节点的父ID数组，支持多个值（如0L, -1L）
     * @return 树形结构的根节点列表，如果输入为空则返回null
     * @throws IllegalArgumentException 当rootPid为null时抛出
     */
    public static <T extends ITreeBase> List<T> mergeTree(List<T> tree, Long... rootPid) {
        Assert.notNull(rootPid, "Root Pid cannot be null");
        if (CollectionUtils.isEmpty(tree)) {
            return null;
        }
        List<T> result = new ArrayList<>();
        // 构建id-节点map映射
        Map<Long, T> treePidMap = tree.stream().collect(Collectors.toMap(T::getId, t -> t));
        tree.stream().forEach(node -> {
            // 判断根节点
            if (Arrays.asList(rootPid).contains(node.getPid())) {
                result.add(node);
            } else {
                //找到父元素
                T parentNode = treePidMap.get(node.getPid());
                if (parentNode == null) {
                    // 可能出现 rootPid 更高的节点 这个操作相当于截断
                    return;
                }
                if (parentNode.getChildren() == null) {
                    parentNode.setChildren(new ArrayList());
                }
                parentNode.getChildren().add(node);
            }
        });
        return result;
    }

    /**
     * 合并扁平列表为树形结构（使用默认根节点PID）
     * <p>
     * 默认使用0作为根节点的父ID。
     * 这是mergeTree(tree, 0L)的简化版本。
     * </p>
     *
     * @param <T> 树形节点类型，必须实现ITreeBase接口
     * @param tree 扁平的节点列表
     * @return 树形结构的根节点列表
     */
    public static <T extends ITreeBase> List<T> mergeTree(List<T> tree) {
        return mergeTree(tree, 0L);
    }

    /**
     * 合并包含重复路径的扁平列表为树形结构
     * <p>
     * 用于处理基于路径（nodeType）的树形结构，支持路径重复的场景。
     * 路径格式：a-de-b-de-c-de-0，最后一位是层级level。
     * </p>
     *
     * <p><b>应用场景：</b></p>
     * <ul>
     *   <li>文件路径树形展示</li>
     *   <li>菜单路径树形展示</li>
     *   <li>分类路径树形展示</li>
     * </ul>
     *
     * @param <T> 树形节点类型，必须实现ITreeBase接口
     * @param tree 扁平的节点列表
     * @param rootPid 根节点的父ID数组
     * @return 树形结构的根节点列表，如果输入为空则返回null
     * @throws IllegalArgumentException 当rootPid为null时抛出
     */
    public static <T extends ITreeBase> List<T> mergeDuplicateTree(List<T> tree, Long... rootPid) {
        Assert.notNull(rootPid, "Root Pid cannot be null");
        if (CollectionUtils.isEmpty(tree)) {
            return null;
        }
        List<T> result = new ArrayList<>();
        // 构建id-节点map映射
        Map<String, T> treePidMap = tree.stream().collect(Collectors.toMap(node -> node.getNodeType(), t -> t));
        tree.stream().filter(item -> ObjectUtils.isNotEmpty(item.getId())).forEach(node -> {

            String nodeType = node.getNodeType();
            String[] links = nodeType.split(SEPARATOR);
            int length = links.length;
            int level = Integer.parseInt(links[length - 1]);
            // 判断根节点
            if (Arrays.asList(rootPid).contains(node.getPid()) && 0 == level) {
                result.add(node);
            } else {
                //找到父元素
                String[] pLinks = new String[level];
                System.arraycopy(links, 0, pLinks, 0, level);
                String parentType = Arrays.stream(pLinks).collect(Collectors.joining(SEPARATOR)) + TreeUtils.SEPARATOR + (level - 1);
                T parentNode = treePidMap.get(parentType);
                if (parentNode == null) {
                    // 可能出现 rootPid 更高的节点 这个操作相当于截断
                    return;
                }
                if (parentNode.getChildren() == null) {
                    parentNode.setChildren(new ArrayList());
                }
                parentNode.getChildren().add(node);
            }
        });
        return result;
    }

    /**
     * 自定义排序业务节点VO列表
     * <p>
     * 根据指定的排序类型对BusiNodeVO列表进行排序。
     * 支持按名称升序/降序、按时间升序/降序等多种排序方式。
     * 名称排序使用中文拼音排序规则。
     * </p>
     *
     * <p><b>支持的排序类型：</b></p>
     * <ul>
     *   <li>{@link SortConstants#NAME_DESC} - 按名称降序（A-Z）</li>
     *   <li>{@link SortConstants#NAME_ASC} - 按名称升序（Z-A）</li>
     *   <li>{@link SortConstants#TIME_ASC} - 按时间升序（最早在前）</li>
     *   <li>默认 - 按时间降序（最新在前）</li>
     * </ul>
     *
     * @param list 待排序的业务节点列表
     * @param sortType 排序类型，参见{@link SortConstants}
     * @return 排序后的业务节点列表
     */
    public static List<BusiNodeVO> customSortVO(List<BusiNodeVO> list, String sortType) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        if (StringUtils.equalsIgnoreCase(SortConstants.NAME_DESC, sortType)) {
            Set<BusiNodeVO> poSet = new TreeSet<>(Comparator.comparing(BusiNodeVO::getName, collator));
            poSet.addAll(list);
            return poSet.stream().collect(Collectors.toList());
        } else if (StringUtils.equalsIgnoreCase(SortConstants.NAME_ASC, sortType)) {
            Set<BusiNodeVO> poSet = new TreeSet<>(Comparator.comparing(BusiNodeVO::getName, collator).reversed());
            poSet.addAll(list);
            return poSet.stream().collect(Collectors.toList());
        } else if (StringUtils.equalsIgnoreCase(SortConstants.TIME_ASC, sortType)) {
            Collections.reverse(list);
            return list;
        } else {
            // 默认时间倒序
            return list;
        }
    }
}

package io.dataease.menu.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.api.menu.vo.MenuMeta;
import io.dataease.api.menu.vo.MenuVO;
import io.dataease.i18n.Translator;
import io.dataease.license.config.XpackInteract;
import io.dataease.menu.bo.MenuTreeNode;
import io.dataease.menu.dao.auto.entity.CoreMenu;
import io.dataease.menu.dao.auto.mapper.CoreMenuMapper;
import io.dataease.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理类
 *
 * 功能描述：
 * 1. 构建系统菜单树结构
 * 2. 处理菜单的国际化翻译
 * 3. 识别企业版功能菜单（通过XpackInteract）
 * 4. 过滤不需要展示的菜单项
 *
 * 菜单类型：
 * - 0: 目录（分组）
 * - 1: 菜单项（页面）
 *
 * @author DataEase
 * @since 2023-06-02
 */
@Component
public class MenuManage {

    /**
     * 国际化前缀
     * 用于从国际化资源文件中获取菜单名称的翻译
     */
    private static final String I18N_PREFIX = "i18n_menu.";

    /**
     * 根节点ID
     * 顶级菜单的父节点ID为0
     */
    private final static int ROOTID = 0;

    /**
     * 菜单数据访问接口
     */
    @Resource
    private CoreMenuMapper coreMenuMapper;


    /**
     * 查询菜单树
     * 构建前端路由所需的菜单树结构，包含国际化处理
     *
     * 业务流程：
     * 1. 将数据库实体转换为业务节点
     * 2. 按菜单排序字段排序
     * 3. 构建树形结构
     * 4. 转换为前端VO对象，包含国际化信息
     *
     * @param coreMenus 菜单数据列表（可能被Xpack过滤）
     * @return 菜单树列表
     */
    @XpackInteract(value = "menuApi")
    public List<MenuVO> query(List<CoreMenu> coreMenus) {
        // 1. 转换为菜单树节点并排序
        List<MenuTreeNode> menuTreeNodes = new ArrayList<>(coreMenus.stream().map(menu -> BeanUtils.copyBean(new MenuTreeNode(), menu)).toList());
        menuTreeNodes.sort(Comparator.comparing(MenuTreeNode::getMenuSort));

        // 2. 构建树形结构
        List<MenuTreeNode> treeNodes = buildPOTree(menuTreeNodes);

        // 3. 转换为前端VO对象
        return convertTree(treeNodes);
    }

    /**
     * 查询所有菜单数据
     * 从数据库按排序字段获取所有菜单记录
     *
     * @return 所有菜单列表，按menu_sort字段升序排列
     */
    public List<CoreMenu> coreMenus() {
        QueryWrapper<CoreMenu> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("menu_sort");
        return coreMenuMapper.selectList(wrapper);
    }

    /**
     * 构建菜单树形结构
     * 将扁平的菜单列表转换为树形结构，通过父节点ID关联
     *
     * 实现逻辑：
     * 1. 按父节点ID分组，构建子节点映射表
     * 2. 遍历所有节点，设置子节点列表
     * 3. 提取根节点（pid=0）作为返回结果
     *
     * @param coreMenus 扁平的菜单列表
     * @return 树形结构的菜单列表（只包含根节点）
     */
    private List<MenuTreeNode> buildPOTree(List<MenuTreeNode> coreMenus) {
        List<MenuTreeNode> result = new ArrayList<>();

        // 1. 按父节点ID分组，构建子节点映射表
        Map<Long, List<MenuTreeNode>> childMap = coreMenus.stream().collect(Collectors.groupingBy(CoreMenu::getPid));

        // 2. 遍历所有节点，设置子节点列表
        coreMenus.forEach(po -> {
            po.setChildren(childMap.get(po.getId()));
            if (po.getPid() == ROOTID) {
                // 只收集根节点
                result.add(po);
            }
        });

        return result;
    }

    /**
     * 将菜单树转换为前端VO对象
     * 递归处理整个树，添加国际化信息和元数据
     *
     * 过滤规则：
     * - 如果是目录(type=1)且没有子菜单，则不显示
     * - 如果有子菜单，则显示
     *
     * @param roots 根节点列表
     * @return 转换后的菜单VO列表
     */
    private List<MenuVO> convertTree(List<MenuTreeNode> roots) {
        List<MenuVO> result = new ArrayList<>();

        for (MenuTreeNode menuTreeNode : roots) {
            // 转换单个节点
            MenuVO vo = convert(menuTreeNode);

            // 递归转换子节点
            List<MenuTreeNode> children = null;
            if (CollectionUtils.isNotEmpty(children = menuTreeNode.getChildren())) {
                vo.setChildren(convertTree(children));
            }

            // 过滤规则：有子菜单或者不是目录(type!=1)才添加到结果
            if (CollectionUtils.isNotEmpty(vo.getChildren()) || menuTreeNode.getType() != 1) {
                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 将单个菜单实体转换为VO对象
     * 处理路径、国际化、元数据和插件标识
     *
     * 处理逻辑：
     * 1. 去掉路径开头的"/"（前端路由处理）
     * 2. 复制基础属性
     * 3. 创建meta对象，包含国际化标题和图标
     * 4. 标识是否为企业版功能
     *
     * @param coreMenu 菜单实体对象
     * @return 菜单VO对象
     */
    private MenuVO convert(CoreMenu coreMenu) {

        // 1. 处理路径：去掉开头的"/"（根节点除外）
        if (ROOTID != coreMenu.getPid() && StringUtils.startsWith(coreMenu.getPath(), "/")) {
            coreMenu.setPath(coreMenu.getPath().substring(1));
        }

        // 2. 复制基础属性
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyBean(menuVO, coreMenu, "children");

        // 3. 创建meta对象，包含国际化标题和图标
        MenuMeta meta = new MenuMeta();
        meta.setTitle(Translator.get(I18N_PREFIX + coreMenu.getName()));
        meta.setIcon(coreMenu.getIcon());
        menuVO.setMeta(meta);

        // 4. 标识是否为企业版功能
        menuVO.setPlugin(isXpackMenu(coreMenu));

        return menuVO;
    }

    /**
     * 判断菜单是否为企业版功能
     * 根据菜单ID或父节点ID判断该菜单是否属于企业版功能
     *
     * 企业版菜单ID列表：
     * - 7: 系统管理（及其子菜单）
     * - 14, 17, 18: 各种企业功能模块
     * - 21的子菜单: 特定企业功能（21本身除外）
     * - 25, 26, 27, 28, 35, 40, 50, 60, 61, 65, 80, 90: 其他企业功能
     * - 70的子菜单: 另一组企业功能
     *
     * @param coreMenu 菜单实体对象
     * @return 如果是企业版菜单返回true，否则返回false
     */
    private boolean isXpackMenu(CoreMenu coreMenu) {
        // ID为21的菜单本身不是企业版功能，但其子菜单是
        if (coreMenu.getId().equals(21L)) return false;

        // 判断是否为企业版菜单（通过ID或父节点ID）
        return coreMenu.getId().equals(7L)           // 系统管理
                || coreMenu.getPid().equals(7L)      // 系统管理的子菜单
                || coreMenu.getId().equals(14L)      // 企业功能模块1
                || coreMenu.getId().equals(17L)      // 企业功能模块2
                || coreMenu.getId().equals(18L)      // 企业功能模块3
                || coreMenu.getPid().equals(21L)     // ID为21的子菜单
                || coreMenu.getId().equals(25L)      // 企业功能模块4
                || coreMenu.getId().equals(26L)      // 企业功能模块5
                || coreMenu.getId().equals(27L)      // 企业功能模块6
                || coreMenu.getId().equals(28L)      // 企业功能模块7
                || coreMenu.getId().equals(35L)      // 企业功能模块8
                || coreMenu.getId().equals(40L)      // 企业功能模块9
                || coreMenu.getId().equals(50L)      // 企业功能模块10
                || coreMenu.getId().equals(60L)      // 企业功能模块11
                || coreMenu.getId().equals(61L)      // 企业功能模块12
                || coreMenu.getId().equals(65L)      // 企业功能模块13
                || coreMenu.getId().equals(80L)      // 企业功能模块14
                || coreMenu.getId().equals(90L)      // 企业功能模块15
                || coreMenu.getPid().equals(70L);    // ID为70的子菜单
    }
}

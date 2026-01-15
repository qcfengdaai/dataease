package io.dataease.menu.server;

import io.dataease.api.menu.MenuApi;
import io.dataease.api.menu.vo.MenuVO;
import io.dataease.menu.dao.auto.entity.CoreMenu;
import io.dataease.menu.manage.MenuManage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单服务控制器
 * 提供系统菜单相关的REST API接口
 */
@RestController
@RequestMapping("/menu")
public class MenuServer implements MenuApi {

    @Resource
    private MenuManage menuManage;

    /**
     * 查询系统菜单列表
     * @return 菜单视图对象列表
     */
    @Override
    public List<MenuVO> query() {
        List<CoreMenu> coreMenus = menuManage.coreMenus();
        return menuManage.query(new ArrayList<>(coreMenus));
    }
}

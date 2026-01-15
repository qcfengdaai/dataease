package io.dataease.substitute.permissions.user;


import io.dataease.api.permissions.user.dto.LangSwitchRequest;
import io.dataease.api.permissions.user.vo.CurIpVO;
import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.exception.DEException;
import io.dataease.i18n.Lang;
import io.dataease.utils.CacheUtils;
import io.dataease.utils.IPUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static io.dataease.constant.CacheConstant.UserCacheConstant.USER_COMMUNITY_LANGUAGE;

/**
 * 用户服务替补实现
 *
 * <p>这是企业版用户服务的社区版替补实现,用于桌面版(社区版)中。</p>
 *
 * <p>当企业版的权限服务不可用时,使用此替补实现提供基础的用户功能。</p>
 *
 * <p>此实现提供:</p>
 * <ul>
 * <li>固定的管理员用户信息(ID=1, 账号=admin)</li>
 * <li>语言切换功能(支持中文、英文、繁体中文)</li>
 * <li>基础的用户信息和个人信息查询</li>
 * </ul>
 *
 * @author DataEase
 * @since 2.0
 */
@Component
@ConditionalOnMissingBean(name = "userServer")
@RestController
@RequestMapping("/user")
public class SubstituteUserServer {

    /**
     * 获取当前用户基本信息
     *
     * <p>返回固定的管理员用户信息,支持语言切换。</p>
     *
     * @return 用户信息Map,包含:
     *         <ul>
     *         <li>id: 用户ID(固定为"1")</li>
     *         <li>name: 用户名(固定为"管理员")</li>
     *         <li>oid: 组织ID(固定为"1")</li>
     *         <li>language: 语言设置(默认"zh-CN",可从缓存读取)</li>
     *         </ul>
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", "1");
        result.put("name", "管理员");
        result.put("oid", "1");
        result.put("language", "zh-CN");
        Object langObj = CacheUtils.get(USER_COMMUNITY_LANGUAGE, "de");
        if (ObjectUtils.isNotEmpty(langObj) && StringUtils.isNotBlank(langObj.toString())) {
            result.put("language", langObj.toString());
        }
        return result;
    }

    /**
     * 获取当前用户的详细信息
     *
     * <p>返回管理员用户的详细信息,包括当前IP和模式信息。</p>
     *
     * @return 用户表单信息对象,包含:
     *         <ul>
     *         <li>id: 用户ID(固定为1)</li>
     *         <li>account: 账号(固定为"admin")</li>
     *         <li>name: 用户名(固定为"管理员")</li>
     *         <li>ip: 当前IP地址</li>
     *         <li>model: 模式(固定为"lose",表示无XPack)</li>
     *         </ul>
     */
    @GetMapping("/personInfo")
    public UserFormVO personInfo() {
        UserFormVO userFormVO = new UserFormVO();
        userFormVO.setId(1L);
        userFormVO.setAccount("admin");
        userFormVO.setName("管理员");
        userFormVO.setIp(IPUtils.get());
        // 当前模式为无XPack
        userFormVO.setModel("lose");
        return userFormVO;
    }

    /**
     * 获取当前用户的IP信息
     *
     * <p>返回管理员用户的IP信息。</p>
     *
     * @return 当前IP信息对象,包含:
     *         <ul>
     *         <li>account: 账号(固定为"admin")</li>
     *         <li>name: 用户名(固定为"管理员")</li>
     *         <li>ip: 当前IP地址</li>
     *         </ul>
     */
    @GetMapping("/ipInfo")
    public CurIpVO ipInfo() {
        CurIpVO curIpVO = new CurIpVO();
        curIpVO.setAccount("admin");
        curIpVO.setName("管理员");
        curIpVO.setIp(IPUtils.get());
        return curIpVO;
    }

    /**
     * 切换用户语言设置
     *
     * <p>支持切换语言并保存到缓存中。</p>
     *
     * @param request 语言切换请求对象,包含要切换的语言代码
     * @throws DEException 当语言代码无效时抛出异常
     */
    @PostMapping("/switchLanguage")
    public void switchLanguage(@RequestBody LangSwitchRequest request) {
        String lang = request.getLang();
        if (StringUtils.equalsIgnoreCase(Lang.zh_CN.getDesc(), lang)) {
            lang = Lang.zh_CN.getDesc();
        } else if (StringUtils.equalsAnyIgnoreCase(lang, "en", "tw")) {
            lang = lang.toLowerCase();
        } else {
            DEException.throwException("无效language");
        }
        CacheUtils.put(USER_COMMUNITY_LANGUAGE, "de", lang);
    }
}

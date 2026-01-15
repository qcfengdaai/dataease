package io.dataease.share.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.dataease.api.system.vo.ShareBaseVO;
import io.dataease.api.visualization.request.VisualizationWorkbranchQueryRequest;
import io.dataease.api.xpack.share.request.XpackShareProxyRequest;
import io.dataease.api.xpack.share.request.XpackSharePwdValidator;
import io.dataease.api.xpack.share.request.XpackShareUuidEditor;
import io.dataease.api.xpack.share.vo.TicketValidVO;
import io.dataease.api.xpack.share.vo.XpackShareGridVO;
import io.dataease.api.xpack.share.vo.XpackShareProxyVO;
import io.dataease.auth.bo.TokenUserBO;
import io.dataease.constant.AuthConstant;
import io.dataease.constant.BusiResourceEnum;
import io.dataease.exception.DEException;
import io.dataease.i18n.Translator;
import io.dataease.license.config.XpackInteract;
import io.dataease.license.utils.LicenseUtil;
import io.dataease.share.dao.auto.entity.XpackShare;
import io.dataease.share.dao.auto.mapper.XpackShareMapper;
import io.dataease.share.dao.ext.mapper.XpackShareExtMapper;
import io.dataease.share.dao.ext.po.XpackSharePO;
import io.dataease.share.util.LinkTokenUtil;
import io.dataease.system.manage.SysParameterManage;
import io.dataease.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Xpack分享业务管理类
 *
 * 负责处理分享链接的核心业务逻辑，包括：
 * <ul>
 * <li>分享链接的创建、删除、查询</li>
 * <li>分享UUID、过期时间、密码的编辑</li>
 * <li>分享链接的访问控制和权限验证</li>
 * <li>分享列表的分页查询</li>
 * <li>链接Token的生成和验证</li>
 * </ul>
 *
 * @author fit2cloud
 * @since 2024-06-21
 */
@Component("xpackShareManage")
public class XpackShareManage {

    @Resource(name = "xpackShareMapper")
    private XpackShareMapper xpackShareMapper;

    @Resource(name = "xpackShareExtMapper")
    private XpackShareExtMapper xpackShareExtMapper;

    @Resource
    private ShareTicketManage shareTicketManage;

    @Resource
    private SysParameterManage sysParameterManage;

    /**
     * 根据资源ID查询分享信息
     *
     * @param resourceId 资源ID
     * @return 分享信息，不存在则返回null
     */
    public XpackShare queryByResource(Long resourceId) {
        Long userId = AuthUtils.getUser().getUserId();
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator", userId);
        queryWrapper.eq("resource_id", resourceId);
        return xpackShareMapper.selectOne(queryWrapper);
    }

    /**
     * 查询资源的分享密码
     *
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 分享密码，不存在则返回null
     */
    public String queryPwd(Long resourceId, Long userId) {
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator", userId);
        queryWrapper.eq("resource_id", resourceId);
        XpackShare xpackShare = xpackShareMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(xpackShare)) return null;
        return xpackShare.getPwd();
    }

    /**
     * 切换资源的分享状态
     * 如果已存在分享则删除，否则创建新分享
     *
     * @param resourceId 资源ID
     */
    @Transactional
    public void switcher(Long resourceId) {
        XpackShare originData = queryByResource(resourceId);
        // 如果已存在分享，删除分享和关联的Ticket
        if (ObjectUtils.isNotEmpty(originData)) {
            xpackShareMapper.deleteById(originData.getId());
            shareTicketManage.deleteByShare(originData.getUuid());
            return;
        }
        TokenUserBO user = AuthUtils.getUser();
        Long userId = user.getUserId();
        XpackShare xpackShare = new XpackShare();
        xpackShare.setId(IDUtils.snowID());
        xpackShare.setCreator(userId);
        xpackShare.setTime(System.currentTimeMillis());
        xpackShare.setResourceId(resourceId);
        xpackShare.setUuid(RandomStringUtils.randomAlphanumeric(8));
        xpackShare.setOid(user.getDefaultOid());
        // 查询资源的类型（仪表板或数据大屏）
        String dType = xpackShareExtMapper.visualizationType(resourceId);
        xpackShare.setType(StringUtils.equalsIgnoreCase("dataV", dType) ? 2 : 1);
        xpackShareMapper.insert(xpackShare);
    }

    /**
     * 编辑分享链接的UUID
     *
     * @param editor UUID编辑器，包含资源ID和新的UUID
     * @return 错误信息，成功返回空字符串
     */
    @Transactional
    public String editUuid(XpackShareUuidEditor editor) {
        Long resourceId = editor.getResourceId();
        String uuid = editor.getUuid();
        XpackShare originData = queryByResource(resourceId);
        if (ObjectUtils.isEmpty(originData)) {
            return "公共链接不存在，请先创建！";
        }
        if (StringUtils.isBlank(uuid)) {
            return "不能为空！";
        }
        if (StringUtils.equals(uuid, originData.getUuid())) {
            return "";
        }
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        if (xpackShareMapper.selectCount(queryWrapper) > 0) {
            return "已存在相同的链接，请重新输入！";
        }
        // 验证UUID格式：8-16位字母数字
        String regex = "^[a-zA-Z0-9]{8,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(uuid);
        if (!matcher.matches()) {
            return "仅支持8-16位(字母数字)，请重新输入！";
        }
        shareTicketManage.updateByUuidChange(originData.getUuid(), uuid);
        originData.setUuid(uuid);
        xpackShareMapper.updateById(originData);
        return "";
    }

    /**
     * 编辑分享链接的过期时间
     *
     * @param resourceId 资源ID
     * @param exp 过期时间（时间戳），0表示永不过期
     */
    public void editExp(Long resourceId, Long exp) {
        XpackShare originData = queryByResource(resourceId);
        if (ObjectUtils.isEmpty(originData)) {
            DEException.throwException("share instance not exist");
        }
        originData.setExp(exp);
        if (ObjectUtils.isEmpty(exp)) {
            originData.setExp(0L);
        }
        xpackShareMapper.updateById(originData);
    }

    /**
     * 编辑分享链接的密码
     *
     * @param resourceId 资源ID
     * @param pwd 密码，为空表示无密码
     * @param autoPwd 是否自动生成密码
     */
    public void editPwd(Long resourceId, String pwd, Boolean autoPwd) {
        XpackShare originData = queryByResource(resourceId);
        if (ObjectUtils.isEmpty(originData)) {
            DEException.throwException("share instance not exist");
        }
        originData.setPwd(pwd);
        originData.setAutoPwd(ObjectUtils.isEmpty(autoPwd) || autoPwd);
        xpackShareMapper.updateById(originData);
    }


    /**
     * 分页查询分享列表
     *
     * @param goPage 当前页码
     * @param pageSize 每页大小
     * @param request 查询请求参数，包含类型和关键词过滤
     * @return 分页结果
     */
    public IPage<XpackSharePO> querySharePage(int goPage, int pageSize, VisualizationWorkbranchQueryRequest request) {
        Long uid = AuthUtils.getUser().getUserId();
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("s.creator", uid);
        if (StringUtils.isNotBlank(request.getType())) {
            BusiResourceEnum busiResourceEnum = BusiResourceEnum.valueOf(request.getType().toUpperCase());
            if (ObjectUtils.isEmpty(busiResourceEnum)) {
                DEException.throwException("type is invalid");
            }
            String resourceType = convertResourceType(request.getType());
            if (StringUtils.isNotBlank(resourceType)) {
                queryWrapper.eq("v.type", resourceType);
            }
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            queryWrapper.like("v.name", request.getKeyword());
        }
        String info = CommunityUtils.getInfo();
        if (StringUtils.isNotBlank(info)) {
            queryWrapper.notExists(String.format(info, "s.resource_id"));
        }
        // 按创建时间排序
        queryWrapper.orderBy(true, request.isAsc(), "s.time");
        Page<XpackSharePO> page = new Page<>(goPage, pageSize);
        return xpackShareExtMapper.query(page, queryWrapper);
    }

    /**
     * 转换资源类型
     * 将业务类型转换为可视化资源类型
     *
     * @param busiFlag 业务类型（panel/screen）
     * @return 可视化资源类型（dashboard/dataV）
     */
    private String convertResourceType(String busiFlag) {
        return switch (busiFlag) {
            case "panel" -> "dashboard";
            case "screen" -> "dataV";
            default -> null;
        };
    }

    /**
     * 查询分享列表（带权限过滤）
     * 使用XpackInteract进行企业版权限过滤扩展
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param request 查询请求参数
     * @return 分页结果
     */
    @XpackInteract(value = "perFilterShareManage", recursion = true, invalid = true)
    public IPage<XpackShareGridVO> query(int pageNum, int pageSize, VisualizationWorkbranchQueryRequest request) {
        // 格式化查询结果
        IPage<XpackSharePO> poiPage = proxy().querySharePage(pageNum, pageSize, request);
        List<XpackShareGridVO> vos = proxy().formatResult(poiPage.getRecords());
        if (!org.springframework.util.CollectionUtils.isEmpty(vos)) {
            vos.forEach(item -> {
                item.setCreator(StringUtils.equals(item.getCreator(), "1") ? Translator.get("i18n_sys_admin") : item.getCreator());
            });
        }
        IPage<XpackShareGridVO> ipage = new Page<>();
        ipage.setSize(poiPage.getSize());
        ipage.setCurrent(poiPage.getCurrent());
        ipage.setPages(poiPage.getPages());
        ipage.setTotal(poiPage.getTotal());
        ipage.setRecords(vos);
        return ipage;
    }

    /**
     * 格式化查询结果
     * 将持久化对象转换为视图对象
     *
     * @param pos 持久化对象列表
     * @return 视图对象列表
     */
    public List<XpackShareGridVO> formatResult(List<XpackSharePO> pos) {
        if (CollectionUtils.isEmpty(pos)) return new ArrayList<>();
        return pos.stream().map(po ->
                new XpackShareGridVO(
                        po.getShareId(), po.getResourceId(), po.getName(), po.getCreator().toString(),
                        po.getTime(), po.getExp(), 9, po.getExtFlag(),po.getExtFlag1(), po.getType())).toList();
    }

    /**
     * 获取代理对象
     * 用于支持AOP事务和扩展点调用
     *
     * @return 当前类的代理对象
     */
    private XpackShareManage proxy() {
        return CommonBeanFactory.getBean(this.getClass());
    }

    /**
     * 验证企业版要求
     * 检查是否满足企业版的分享要求（必须设置密码和过期时间）
     *
     * @param sharedBase 分享基础配置
     * @param share 分享信息
     * @return true-满足要求，false-不满足
     */
    private boolean peRequireValid(ShareBaseVO sharedBase, XpackShare share) {
        if (ObjectUtils.isEmpty(sharedBase) || !sharedBase.isPeRequire()) return true;
        Long exp = share.getExp();
        String pwd = share.getPwd();
        return StringUtils.isNotBlank(pwd) && ObjectUtils.isNotEmpty(exp) && exp > 0L;
    }

    /**
     * 获取分享代理信息
     * 验证分享链接的有效性并生成访问Token
     *
     * @param request 分享代理请求
     * @return 分享代理信息，包含Token、有效性等
     */
    public XpackShareProxyVO proxyInfo(XpackShareProxyRequest request) {
        // 检查分享功能是否被禁用
        ShareBaseVO sharedBase = sysParameterManage.shareBase();
        if (ObjectUtils.isNotEmpty(sharedBase) && sharedBase.isDisable()) {
            XpackShareProxyVO vo = new XpackShareProxyVO();
            vo.setShareDisable(true);
            return vo;
        }
        // 检查是否在iframe中且许可证无效
        boolean inIframeError = request.isInIframe() && !LicenseUtil.licenseValid();
        if (inIframeError) {
            return new XpackShareProxyVO();
        }
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", request.getUuid());
        XpackShare xpackShare = xpackShareMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(xpackShare))
            return null;
        // 验证企业版要求
        if (!peRequireValid(sharedBase, xpackShare)) {
            XpackShareProxyVO vo = new XpackShareProxyVO();
            vo.setPeRequireValid(false);
            vo.setInIframeError(false);
            return vo;
        }
        // 生成链接Token并添加到响应头
        String linkToken = LinkTokenUtil.generate(xpackShare.getCreator(), xpackShare.getResourceId(), xpackShare.getExp(), xpackShare.getPwd(), xpackShare.getOid());
        HttpServletResponse response = ServletUtils.response();
        response.addHeader(AuthConstant.LINK_TOKEN_KEY, linkToken);
        Integer type = xpackShare.getType();
        String typeText = (ObjectUtils.isNotEmpty(type) && type == 1) ? "dashboard" : "dataV";
        // 验证Ticket
        TicketValidVO validVO = shareTicketManage.validateTicket(request.getTicket(), xpackShare);
        return new XpackShareProxyVO(xpackShare.getResourceId(), xpackShare.getCreator(), linkExp(xpackShare), pwdValid(xpackShare, request.getCiphertext()), typeText, inIframeError, false, true, validVO);
    }

    /**
     * 检查分享链接是否过期
     *
     * @param xpackShare 分享信息
     * @return true-已过期，false-未过期
     */
    private boolean linkExp(XpackShare xpackShare) {
        if (ObjectUtils.isEmpty(xpackShare.getExp()) || xpackShare.getExp().equals(0L)) return false;
        return System.currentTimeMillis() > xpackShare.getExp();
    }

    /**
     * 验证分享密码
     * 解密密文并验证密码和UUID是否匹配
     *
     * @param xpackShare 分享信息
     * @param ciphertext RSA加密的密文
     * @return true-密码正确，false-密码错误
     */
    private boolean pwdValid(XpackShare xpackShare, String ciphertext) {
        // 解密密文
        if (StringUtils.isBlank(xpackShare.getPwd())) return true;
        if (StringUtils.isBlank(ciphertext)) return false;
        String text = RsaUtils.decryptStr(ciphertext);
        int splitIndex = text.indexOf(",");
        String pwd;
        if (splitIndex == -1) {
            splitIndex = 8;
            pwd = text.substring(splitIndex);
        } else {
            pwd = text.substring(splitIndex + 1);
        }
        String uuid = text.substring(0, splitIndex);
        // 验证UUID和密码
        return StringUtils.equals(xpackShare.getUuid(), uuid) && StringUtils.equals(xpackShare.getPwd(), pwd);
    }

    /**
     * 验证分享密码
     *
     * @param validator 密码验证器，包含加密的密码信息
     * @return true-密码正确，false-密码错误
     */
    public boolean validatePwd(XpackSharePwdValidator validator) {
        String ciphertext = RsaUtils.decryptStr(validator.getCiphertext());
        String pwd;
        int splitIndex = ciphertext.indexOf(",");
        if (splitIndex == -1) {
            splitIndex = 8;
            pwd = ciphertext.substring(splitIndex);
        } else {
            pwd = ciphertext.substring(splitIndex + 1);
        }
        String uuid = ciphertext.substring(0, splitIndex);
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        XpackShare xpackShare = xpackShareMapper.selectOne(queryWrapper);
        return StringUtils.equals(xpackShare.getUuid(), uuid) && StringUtils.equals(xpackShare.getPwd(), pwd);
    }

    /**
     * 查询用户的所有分享关系
     * 返回资源ID到UUID的映射
     *
     * @param uid 用户ID
     * @return 资源ID->UUID的映射
     */
    public Map<String, String> queryRelationByUserId(Long uid) {
        QueryWrapper<XpackShare> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator", uid);
        List<XpackShare> result = xpackShareMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(result)) {
            return result.stream()
                    .collect(Collectors.toMap(xpackShare -> String.valueOf(xpackShare.getResourceId()), XpackShare::getUuid));
        }
        return new HashMap<>();
    }
}
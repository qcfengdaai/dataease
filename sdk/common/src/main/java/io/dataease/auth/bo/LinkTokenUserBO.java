package io.dataease.auth.bo;

import java.io.Serializable;

/**
 * 链接Token用户业务对象
 * 用于分享链接访问时的用户信息载体，与普通Token用户区分
 *
 * <p>主要用于：</p>
 * <ul>
 *   <li>仪表板分享链接访问时的临时用户身份</li>
 *   <li>图表分享链接的用户上下文</li>
 *   <li>公开访问API的身份标识</li>
 * </ul>
 */
public class LinkTokenUserBO implements Serializable {
}

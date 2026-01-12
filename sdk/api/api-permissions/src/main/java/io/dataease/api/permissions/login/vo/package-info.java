/**
 * 登录认证视图对象包
 *
 * <p>本包包含登录认证模块的所有 VO (View Object) 类,
 * 用于封装返回给客户端的响应数据。VO 对象是数据展示层对象,通常包含格式化后的数据。</p>
 *
 * <h2>VO 列表</h2>
 * <ul>
 *   <li>{@link io.dataease.api.permissions.login.vo.MfaQrVO} - MFA 二维码视图
 *     <ul>
 *       <li>包含 MFA 二维码的 Base64 编码图片</li>
 *       <li>包含 MFA 密钥(用于手动输入)</li>
 *       <li>包含签发者和账户信息</li>
 *       <li>用于用户绑定认证器应用</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>所有 VO 类实现 {@link java.io.Serializable} 接口,支持序列化</li>
 *   <li>使用 Lombok 的 {@code @Data} 注解简化 getter/setter</li>
 *   <li>使用 Swagger {@code @Schema} 注解进行 API 文档标注</li>
 *   <li>Long 类型的 ID 使用 {@code @JsonSerialize(using=ToStringSerializer.class)} 避免前端精度丢失</li>
 *   <li>敏感字段(如密钥)需要在合适的时机返回</li>
 * </ul>
 *
 * <h2>VO 与实体的区别</h2>
 * <ul>
 *   <li><strong>实体(Entity)</strong>:与数据库表一一对应,包含所有数据库字段</li>
 *   <li><strong>VO</strong>:面向前端展示,可能包含多个实体的组合数据,或对字段进行特殊格式化</li>
 * </ul>
 *
 * <h2>使用场景</h2>
 * <p>VO 对象主要用于:</p>
 * <ol>
 *   <li>封装 MFA 二维码和密钥信息</li>
 *   <li>返回格式化的登录结果</li>
 *   <li>提供前端展示所需的数据结构</li>
 * </ol>
 *
 * <h2>数据流转</h2>
 * <pre>
 * Service 生成 → VO 对象 → Controller 返回 → 前端展示
 * </pre>
 *
 * <h2>MFA 二维码视图示例</h2>
 * <pre>{@code
 * @Data
 * @Schema(description = "MFA二维码视图")
 * public class MfaQrVO implements Serializable {
 *
 *     @Schema(description = "二维码图片(Base64编码)")
 *     private String qrCode;
 *
 *     @Schema(description = "MFA密钥")
 *     private String secret;
 *
 *     @Schema(description = "签发者")
 *     private String issuer;
 *
 *     @Schema(description = "账户名")
 *     private String account;
 *
 *     @Schema(description = "TOTP URI")
 *     private String uri;
 * }
 * }</pre>
 *
 * <h2>前端展示示例</h2>
 *
 * <h3>Vue 3 示例:展示 MFA 二维码</h3>
 * <pre>{@code
 * <template>
 *   <div class="mfa-setup">
 *     <h3>绑定多因子认证</h3>
 *
 *     <!-- 显示二维码 -->
 *     <div class="qr-code">
 *       <img :src="mfaInfo.qrCode" alt="MFA二维码" />
 *     </div>
 *
 *     <!-- 显示密钥(供手动输入) -->
 *     <div class="secret-key">
 *       <p>如果无法扫码,请手动输入以下密钥:</p>
 *       <el-input
 *         v-model="mfaInfo.secret"
 *         readonly
 *         class="secret-input"
 *       >
 *         <template #append>
 *           <el-button @click="copySecret">复制</el-button>
 *         </template>
 *       </el-input>
 *     </div>
 *
 *     <!-- 使用说明 -->
 *     <div class="instructions">
 *       <h4>使用步骤:</h4>
 *       <ol>
 *         <li>下载认证器应用(如Google Authenticator、Microsoft Authenticator)</li>
 *         <li>打开认证器应用,扫描上方二维码</li>
 *         <li>认证器将显示6位动态验证码</li>
 *         <li>输入验证码完成绑定</li>
 *       </ol>
 *     </div>
 *
 *     <!-- 验证码输入 -->
 *     <el-input
 *       v-model="verificationCode"
 *       placeholder="请输入6位验证码"
 *       maxlength="6"
 *     />
 *     <el-button type="primary" @click="verifyMfa">
 *       验证并绑定
 *     </el-button>
 *   </div>
 * </template>
 *
 * <script setup>
 * import { ref, onMounted } from 'vue';
 * import { ElMessage } from 'element-plus';
 * import { getMfaQr, verifyMfaCode } from '@/api/login';
 *
 * const mfaInfo = ref({});
 * const verificationCode = ref('');
 *
 * onMounted(async () => {
 *   // 获取 MFA 二维码
 *   const userId = getUserId();
 *   mfaInfo.value = await getMfaQr(userId);
 * });
 *
 * // 复制密钥
 * function copySecret() {
 *   navigator.clipboard.writeText(mfaInfo.value.secret);
 *   ElMessage.success('密钥已复制到剪贴板');
 * }
 *
 * // 验证并绑定 MFA
 * async function verifyMfa() {
 *   if (!/^\d{6}$/.test(verificationCode.value)) {
 *     ElMessage.error('请输入6位数字验证码');
 *     return;
 *   }
 *
 *   try {
 *     await verifyMfaCode({
 *       userId: getUserId(),
 *       secret: mfaInfo.value.secret,
 *       code: verificationCode.value
 *     });
 *     ElMessage.success('MFA绑定成功');
 *   } catch (error) {
 *     ElMessage.error('验证码错误,请重试');
 *   }
 * }
 * </script>
 * }</pre>
 *
 * <h3>React 示例:展示 MFA 二维码</h3>
 * <pre>{@code
 * import React, { useState, useEffect } from 'react';
 * import { message, Input, Button } from 'antd';
 * import { getMfaQr, verifyMfaCode } from '@/api/login';
 *
 * function MfaSetup() {
 *   const [mfaInfo, setMfaInfo] = useState({});
 *   const [verificationCode, setVerificationCode] = useState('');
 *
 *   useEffect(() => {
 *     // 获取 MFA 二维码
 *     const fetchMfaQr = async () => {
 *       const userId = getUserId();
 *       const data = await getMfaQr(userId);
 *       setMfaInfo(data);
 *     };
 *     fetchMfaQr();
 *   }, []);
 *
 *   // 复制密钥
 *   const copySecret = () => {
 *     navigator.clipboard.writeText(mfaInfo.secret);
 *     message.success('密钥已复制到剪贴板');
 *   };
 *
 *   // 验证并绑定 MFA
 *   const verifyMfa = async () => {
 *     if (!/^\d{6}$/.test(verificationCode)) {
 *       message.error('请输入6位数字验证码');
 *       return;
 *     }
 *
 *     try {
 *       await verifyMfaCode({
 *         userId: getUserId(),
 *         secret: mfaInfo.secret,
 *         code: verificationCode
 *       });
 *       message.success('MFA绑定成功');
 *     } catch (error) {
 *       message.error('验证码错误,请重试');
 *     }
 *   };
 *
 *   return (
 *     <div className="mfa-setup">
 *       <h3>绑定多因子认证</h3>
 *       <img src={mfaInfo.qrCode} alt="MFA二维码" />
 *       <Input
 *         value={mfaInfo.secret}
 *         readOnly
 *         addonAfter={<Button onClick={copySecret}>复制</Button>}
 *       />
 *       <Input
 *         value={verificationCode}
 *         onChange={(e) => setVerificationCode(e.target.value)}
 *         placeholder="请输入6位验证码"
 *         maxLength={6}
 *       />
 *       <Button type="primary" onClick={verifyMfa}>
 *         验证并绑定
 *       </Button>
 *     </div>
 *   );
 * }
 *
 * export default MfaSetup;
 * }</pre>
 *
 * <h2>二维码格式说明</h2>
 * <p>MFA 二维码的数据格式:</p>
 * <ul>
 *   <li><strong>编码格式</strong>:Base64 编码的 PNG 图片</li>
 *   <li><strong>数据格式</strong>:{@code data:image/png;base64,iVBORw0KGgo...}</li>
 *   <li><strong>URI 格式</strong>:{@code otpauth://totp/DataEase:admin?secret=JBSWY3DPEHPK3PXP&issuer=DataEase}</li>
 *   <li><strong>尺寸</strong>:建议 200x200 像素</li>
 * </ul>
 *
 * <h2>密钥格式说明</h2>
 * <p>MFA 密钥的格式:</p>
 * <ul>
 *   <li><strong>编码</strong>:Base32 编码</li>
 *   <li><strong>长度</strong>:通常为 16 位或 32 位字符</li>
 *   <li><strong>字符集</strong>:A-Z、2-7(Base32 字符集)</li>
 *   <li><strong>示例</strong>:{@code JBSWY3DPEHPK3PXP}</li>
 * </ul>
 *
 * <h2>安全注意事项</h2>
 * <ul>
 *   <li><strong>密钥保密</strong>:MFA 密钥是敏感信息,仅在设置时返回一次</li>
 *   <li><strong>安全存储</strong>:前端不应长期存储 MFA 密钥</li>
 *   <li><strong>HTTPS 传输</strong>:二维码和密钥必须通过 HTTPS 传输</li>
 *   <li><strong>二维码有效期</strong>:建议设置二维码的有效期,过期需要重新生成</li>
 * </ul>
 *
 * @see io.dataease.api.permissions.login.api
 * @see io.dataease.api.permissions.login.dto
 * @since 1.0
 */
package io.dataease.api.permissions.login.vo;

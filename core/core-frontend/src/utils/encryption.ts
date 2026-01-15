/**
 * 加密解密工具
 *
 * 功能说明：
 * 1. 提供 RSA 非对称加密功能
 * 2. 提供 AES 对称加密解密功能
 * 3. 支持密码、敏感数据的加密传输
 *
 * 安全机制：
 * - 使用 RSA 加密传输敏感数据（如密码）
 * - 使用 AES 进行本地数据加密
 * - 密钥从服务器动态获取并缓存
 *
 * 依赖库：
 * - crypto-js: JavaScript 加密库
 * - jsencrypt: RSA 加密库
 * - js-base64: Base64 编解码
 *
 * 使用场景：
 * - 用户登录密码加密
 * - 敏感配置信息加密
 * - 数据传输安全保护
 *
 * @example
 * import { rsaEncryp } from '@/utils/encryption'
 *
 * // 加密密码
 * const encrypted = rsaEncryp('mypassword')
 */

import CryptoJS from 'crypto-js/crypto-js'
import JSEncrypt from 'jsencrypt/bin/jsencrypt.min'
import { Base64 } from 'js-base64'
import { useCache } from '@/hooks/web/useCache'
import { useAppStoreWithOut } from '@/store/modules/app'

const appStore = useAppStoreWithOut()
const { wsCache } = useCache()

const rsaKey = '-pk_separator-'
const crypt = new JSEncrypt()

// ==================== AES 加密解密 ====================

/**
 * AES 解密
 *
 * 使用 AES 算法解密数据
 * - 模式：CBC
 * - 填充：PKCS7
 * - IV：固定为 16 个零
 *
 * @param word - 要解密的密文（Base64 编码）
 * @param keyStr - 解密密钥
 * @returns 解密后的明文
 *
 * @example
 * const decrypted = aesDecrypt(encryptedData, 'secret-key')
 */
const aesDecrypt = (word, keyStr) => {
  const keyHex = CryptoJS.enc.Utf8.parse(keyStr)
  const ivHex = CryptoJS.enc.Utf8.parse('0000000000000000') // 固定 IV
  const decrypt = CryptoJS.AES.decrypt(word, keyHex, {
    iv: ivHex,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return decrypt.toString(CryptoJS.enc.Utf8)
}

// ==================== RSA 加密 ====================

/**
 * RSA 加密
 *
 * 使用 RSA 公钥加密数据
 * 公钥从服务器获取并缓存在本地
 *
 * 加密流程：
 * 1. 从缓存获取加密的公钥
 * 2. 使用 AES 解密公钥
 * 3. 使用 RSA 公钥加密明文
 *
 * @param word - 要加密的明文
 * @returns RSA 加密后的密文（Base64 编码）
 *
 * @example
 * // 加密密码
 * const encryptedPassword = rsaEncryp('user-password')
 *
 * // 发送到服务器
 * api.login({ password: encryptedPassword })
 */
export const rsaEncryp = word => {
  // 分隔符（Base64 编码）
  const separator = Base64.encodeURI(rsaKey) + '='

  // 从缓存获取加密的公钥
  const dekey = wsCache.get(appStore.getDekey)
  const keyArray = dekey.split(separator)

  // k1: 加密的公钥
  // k2: 解密密钥
  const k1 = keyArray[0]
  const k2 = keyArray[1]

  // 使用 AES 解密公钥
  const pk = aesDecrypt(k1, k2)

  // 设置 RSA 公钥
  crypt.setKey(pk)

  // 使用 RSA 加密
  return crypt.encrypt(word)
}

// ==================== AES 对称解密 ====================

/**
 * AES 对称解密（使用 Base64 编码的密钥）
 *
 * 使用 Base64 编码的密钥进行 AES 解密
 * 与 aesDecrypt 的区别是密钥格式不同
 *
 * @param data - 要解密的密文（Base64 编码）
 * @param keyStr - Base64 编码的密钥
 * @returns 解密后的明文
 *
 * @example
 * const decrypted = symmetricDecrypt(encryptedData, 'base64-key')
 */
export const symmetricDecrypt = (data, keyStr) => {
  const iv = CryptoJS.enc.Utf8.parse('0000000000000000') // 固定 IV
  const key = CryptoJS.enc.Base64.parse(keyStr)
  const decodedCiphertext = CryptoJS.enc.Base64.parse(data)
  const decrypted = CryptoJS.AES.decrypt({ ciphertext: decodedCiphertext }, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  })
  return decrypted.toString(CryptoJS.enc.Utf8)
}

/**
 * 开发环境配置
 *
 * 功能说明：
 * 1. 配置开发服务器（Vite Dev Server）
 * 2. 配置 API 代理转发
 * 3. 设置开发服务器端口
 *
 * 配置说明：
 * - /api/f: 专用 API 代理（可根据实际需求修改）
 * - /api: 通用 API 代理，将 /api 重写为 de2api
 *
 * 使用方式：
 * - 运行 npm run dev 时自动加载此配置
 * - API 请求会自动转发到配置的目标服务器
 *
 * 注意事项：
 * - 修改 target 后需要重启开发服务器
 * - changeOrigin: true 处理跨域问题
 * - 生产环境不使用此配置
 *
 * @example
 * // 启动开发服务器
 * npm run dev
 *
 * // API 请求示例（会被代理到 192.168.17.31:8100）
 * fetch('/api/user/list')  // 实际请求: http://192.168.17.31:8100/de2api/user/list
 */

export default {
  // ==================== 开发服务器配置 ====================

  server: {
    // ==================== API 代理配置 ====================

    proxy: {
      /**
       * 专用 API 代理
       *
       * 将 /api/f 开头的请求代理到指定服务器
       * 移除路径前缀 /api/f
       *
       * 使用场景：
       * - 特定功能的独立服务
       * - 第三方 API 代理
       * - 测试环境接口
       */
      '/api/f': {
        target: 'http://localhost:8100', // 目标服务器地址
        changeOrigin: true, // 改变请求源（处理跨域）
        rewrite: path => path.replace(/^\/api\/f/, '') // 路径重写：移除 /api/f 前缀
      },

      /**
       * 通用 API 代理（主要使用）
       *
       * 将所有 /api 开头的请求代理到后端服务器
       * 路径重写：/api -> de2api
       *
       * @example
       * // 前端请求
       * fetch('/api/user/list')
       *
       * // 实际转发到
       * // http://192.168.17.31:8100/de2api/user/list
       */
      '/api': {
        target: 'http://192.168.17.30:8100', // 后端服务器地址（请根据实际环境修改）
        changeOrigin: true, // 改变请求源（处理跨域）
        rewrite: path => path.replace(/^\/api/, 'de2api') // 路径重写：/api -> de2api
      }
    },

    // ==================== 服务器端口配置 ====================

    /**
     * 开发服务器端口
     *
     * 启动后访问地址：http://localhost:8080
     * 如需修改端口，同时要考虑与后端 API 配置的匹配
     */
    port: 8080
  }
}

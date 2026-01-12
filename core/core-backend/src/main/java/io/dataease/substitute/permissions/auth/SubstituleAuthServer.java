//package io.dataease.substitute.permissions.auth;
//
///*import io.dataease.api.permissions.auth.api.AuthApi;
//import io.dataease.api.permissions.dto.AuthDTO;
//import io.dataease.api.permissions.request.AuthRequest;
//import org.springframework.stereotype.Service;
//
///**
///**
// * 权限服务替补实现
// *
// * <p>这是企业版权限服务的社区版替补实现,用于桌面版(社区版)中。</p>
// *
// * <p>当企业版的权限服务不可用时,使用此替补实现提供基础的权限功能。</p>
// *
// * <p>注意: 此实现当前已被注释,说明权限服务可能已不再需要替补实现。</p>
// *
// * @author DataEase
// * @since 2.0
// */
//@Service
//public class SubstituleAuthServer implements AuthApi {
//    /**
//     * 查询权限信息
//     *
//     * <p>在替补实现中,固定返回模式为0(基础模式)。</p>
//     *
//     * @param request 权限请求对象(此替补实现中不使用)
//     * @return 权限信息对象,模式固定为0
//     */
//    @Override
//    public AuthDTO query(AuthRequest request) {
//        AuthDTO authDTO = new AuthDTO();
//        authDTO.setModel(0);
//        return authDTO;
//    }
//
//    /**
//     * 根据用户ID查询权限信息
//     *
//     * <p>在替补实现中,固定返回模式为0(基础模式)。</p>
//     *
//     * @param userId 用户ID(此替补实现中不使用)
//     * @return 权限信息对象,模式固定为0
//     */
//    @Override
//    public AuthDTO queryByUserId(Long userId) {
//        AuthDTO authDTO = new AuthDTO();
//        authDTO.setModel(0);
//        return authDTO;
//    }
//}*/

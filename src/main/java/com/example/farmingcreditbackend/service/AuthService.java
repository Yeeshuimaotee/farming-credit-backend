package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.LoginRequestDTO;
import com.example.farmingcreditbackend.dto.LoginResponseDTO;
import com.example.farmingcreditbackend.dto.UserInfoDTO;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务
 */
@Service
@Slf4j
public class AuthService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户登录
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // 1. 验证验证码
        boolean isCaptchaValid = captchaService.validateCaptcha(
                loginRequest.getCaptchaKey(),
                loginRequest.getCaptchaCode()
        );

        if (!isCaptchaValid) {
            throw new BusinessException("登录失败,请检查账号密码及验证码");
        }

        // 2. 根据用户名查询用户
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        if (user.getDeleted() == 1) {
            throw new BusinessException("账号已被删除");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 5. 验证用户类型是否匹配
        validateUserType(user, loginRequest.getRole());

        // 6. 获取用户角色和权限
        List<String> roles = roleService.getRoleCodesByUserId(user.getId());
        List<String> permissions = permissionService.getPermissionCodesByUserId(user.getId());

        // 7. 更新登录信息
        String clientIp = getClientIp();
        userService.updateLoginInfo(user.getId(), clientIp, LocalDateTime.now());

        // 7. 生成Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roles);
        log.info("用户 {} 登录成功，生成Token：{}", user.getUsername(), token);

        // 9. 构建响应
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .token(token)
                .loginTime(LocalDateTime.now())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    /**
     * 验证用户类型是否匹配
     */
    private void validateUserType(User user, String requestedRole) {
        String userType = user.getUserType();

        switch (requestedRole) {
            case "admin":
                if (!"ADMIN".equals(userType)) {
                    throw new BusinessException("非管理员账号");
                }
                break;
            case "store_owner":
                if (!"STORE_OWNER".equals(userType)) {
                    throw new BusinessException("非店主账号");
                }
                break;
            case "farmer":
                if (!"FARMER".equals(userType)) {
                    throw new BusinessException("非农户账号");
                }
                break;
            default:
                throw new BusinessException("无效的角色类型");
        }
    }

    /**
     * 获取用户信息
     */
    public UserInfoDTO getUserInfo(String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userService.findById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        List<String> roles = roleService.getRoleCodesByUserId(userId);
        List<String> permissions = permissionService.getPermissionCodesByUserId(userId);

        return UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    /**
     * 退出登录
     */
    public void logout(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        jwtTokenProvider.invalidateToken(token);
        log.info("用户 {} 已退出登录", username);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取角色和权限
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色
        List<String> roles = roleService.getRoleCodesByUserId(user.getId());
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

        // 添加权限
        List<String> permissions = permissionService.getPermissionCodesByUserId(user.getId());
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }
}
package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.farmingcreditbackend.dto.*;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.UserMapper;
import com.example.farmingcreditbackend.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务
 */
@Service
@Slf4j
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleMapper userRoleMapper;
    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新登录信息
     */
    public void updateLoginInfo(Long userId, String loginIp, LocalDateTime loginTime) {
        userMapper.updateLoginInfo(userId, loginIp, loginTime);
    }

    /**
     * 获取用户角色
     */
    public List<String> getUserRoles(Long userId) {
        return userMapper.selectRoleCodesByUserId(userId);
    }

    /**
     * 获取用户权限
     */
    public List<String> getUserPermissions(Long userId) {
        return userMapper.selectPermissionCodesByUserId(userId);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    /**
     * 创建新用户（注册）
     */
    public User createUser(User user) {
        // 1. 检查用户名是否已存在
        if (existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 检查手机号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        // 3. 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 设置默认值
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 5. 保存用户
        this.save(user);
        return user;
    }

    /**
     * 分页查询用户列表
     */
    public UserListResponseDTO getUserList(UserListRequestDTO request) {
        Page<UserListDTO> page = new Page<>(request.getCurrentPage(), request.getSize());
        Page<UserListDTO> resultPage = userMapper.selectUserListPage(
                page,
                request.getUsername(),
                request.getUserType(),
                request.getPhone()
        );
        UserListResponseDTO response = new UserListResponseDTO();
        response.setList(resultPage.getRecords());
        response.setTotal(resultPage.getTotal());
        return response;
    }

    /**
     * 创建用户（管理员后台）
     */
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserRequestDTO request) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(1); // 默认启用
        user.setDeleted(0);
        userMapper.insert(user);

        // 3. 分配角色（根据用户类型）
        String roleCode = request.getUserType(); // ADMIN, STORE_OWNER, FARMER
        Long roleId = roleService.getRoleIdByCode(roleCode);
        if (roleId == null) {
            throw new BusinessException("角色不存在");
        }
        roleService.assignRoleToUser(user.getId(), roleId);
    }

    /**
     * 更新用户
     */
    public void updateUser(Long id, UpdateUserRequestDTO request) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        // 更新字段
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        userMapper.updateById(user);
    }

    /**
     * 删除用户（逻辑删除）
     */
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        userMapper.logicDeleteById(id);
    }

}
package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id} AND deleted = 0")
    User selectById(@Param("id") Long id);

    @Update("UPDATE sys_user SET last_login_time = #{loginTime}, last_login_ip = #{loginIp}, update_time = NOW() WHERE id = #{id}")
    int updateLoginInfo(@Param("id") Long id,
                        @Param("loginIp") String loginIp,
                        @Param("loginTime") LocalDateTime loginTime);

    @Select("SELECT r.role_code FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.permission_type = 'MENU'")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND deleted = 0")
    int countByUsername(@Param("username") String username);
}
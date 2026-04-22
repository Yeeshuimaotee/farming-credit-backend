package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.RegisterRequestDTO;
import com.example.farmingcreditbackend.dto.RegisterResponseDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Store;
import com.example.farmingcreditbackend.entity.User;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.service.*;
import com.example.farmingcreditbackend.vo.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Transactional
public class RegisterController {

    private final UserService userService;
    private final RoleService roleService;
    private final FarmerService farmerService;
    private final StoreService storeService;

    @PostMapping("/register")
    public Result<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        // 1. 验证角色
        if ("admin".equals(request.getRole())) {
            throw new BusinessException("管理员账号不能注册");
        }

        // 2. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());

        String userType;
        if ("farmer".equals(request.getRole())) {
            userType = "FARMER";
        } else if ("store_owner".equals(request.getRole())) {
            userType = "STORE_OWNER";
        } else {
            throw new BusinessException("无效的角色类型");
        }
        user.setUserType(userType);

        User savedUser = userService.createUser(user);

        // 3. 分配角色
        Long roleId = roleService.getRoleIdByCode(userType);
        if (roleId == null) {
            throw new BusinessException("角色不存在");
        }
        roleService.assignRoleToUser(savedUser.getId(), roleId);

        // 4. 创建业务实体
        if ("farmer".equals(request.getRole())) {
            Farmer farmer = new Farmer();
            farmer.setUserId(savedUser.getId());
            farmer.setFarmerName(savedUser.getRealName());
            farmer.setPhone(savedUser.getPhone());
            farmer.setStatus(1);
            // 生成农户编号（建议调用数据库函数，这里临时用时间戳）
            farmer.setFarmerCode("FM" + System.currentTimeMillis());
            farmerService.createFarmer(farmer);
        } else if ("store_owner".equals(request.getRole())) {
            Store store = new Store();
            store.setOwnerId(savedUser.getId());
            String storeName = request.getStoreName();
            if (storeName == null || storeName.trim().isEmpty()) {
                storeName = savedUser.getRealName() + "的店铺";
            }
            store.setStoreName(storeName);
            store.setPhone(savedUser.getPhone());
            store.setContactPerson(savedUser.getRealName());
            store.setStatus(1);
            store.setStoreCode("ST" + System.currentTimeMillis());
            storeService.createStore(store);
        }

        // 5. 返回响应
        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .realName(savedUser.getRealName())
                .userType(savedUser.getUserType())
                .message("注册成功")
                .build();

        return Result.success(response);
    }
}
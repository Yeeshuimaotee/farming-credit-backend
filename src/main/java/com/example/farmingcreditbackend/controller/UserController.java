package com.example.farmingcreditbackend.controller.admin;

import com.example.farmingcreditbackend.dto.*;
import com.example.farmingcreditbackend.service.UserService;
import com.example.farmingcreditbackend.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 仅管理员可访问
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<UserListResponseDTO> getUserList(@ModelAttribute UserListRequestDTO request) {
        UserListResponseDTO response = userService.getUserList(request);
        return Result.success(response);
    }

    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        userService.createUser(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
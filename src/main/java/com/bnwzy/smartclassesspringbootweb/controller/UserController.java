package com.bnwzy.smartclassesspringbootweb.controller;

import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserChangePasswordDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserLoginDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserRegisterDTO;
import com.bnwzy.smartclassesspringbootweb.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public ResponseMessage login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseMessage.success("<Login success>", userService.userLogin(userLoginDTO));
    }

    @PostMapping("/register")
    public ResponseMessage register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        return ResponseMessage.success("<Register success>", userService.userRegister(userRegisterDTO));
    }

    @PostMapping("/changePassword")
    public ResponseMessage changePassword(@Validated @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        return ResponseMessage.success("<Change password success>", userService.userChangePassword(userChangePasswordDTO));
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseMessage getUserByUsername(@PathVariable("username") String username) {
        return ResponseMessage.success("<Get user successfully>", userService.getUserByUsername(username));
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseMessage deleteUserByid(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Delete user successfully>", userService.deleteUserById(id));
    }

    @GetMapping("/getUserById/{id}")
    public ResponseMessage getUserById(@PathVariable("id") Long id) {
        return ResponseMessage.success("<Get user successfully>", userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseMessage getAllUser() {
        return ResponseMessage.success("<Get all users successfully>", userService.getAllUser());
    }
}

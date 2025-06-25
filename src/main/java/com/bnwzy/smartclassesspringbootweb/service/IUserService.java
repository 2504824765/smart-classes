package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserChangePasswordDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserLoginDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserRegisterDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserReturnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    Boolean userLogin(UserLoginDTO userLoginDTO);

    Boolean userRegister(UserRegisterDTO userRegisterDTO);

    Boolean userChangePassword(UserChangePasswordDTO userChangePasswordDTO);

    UserReturnDTO getUserByUsername(String username);

    Boolean deleteUserById(Long id);

    UserReturnDTO getUserById(Long id);

    List<User> getAllUser();
}

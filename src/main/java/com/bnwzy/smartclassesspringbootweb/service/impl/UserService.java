package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.UserAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.exception.UserNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.WrongPasswordException;
import com.bnwzy.smartclassesspringbootweb.pojo.User;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserChangePasswordDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserLoginDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserRegisterDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.UserReturnDTO;
import com.bnwzy.smartclassesspringbootweb.repository.UserRepository;
import com.bnwzy.smartclassesspringbootweb.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;

    public Boolean userLogin(UserLoginDTO userLoginDTO) {
        if (!userRepository.findByUsername(userLoginDTO.getUsername()).isPresent()) {
            throw new UserNotFoundException("<User not found>");
        } else {
            User user = userRepository.findByUsername(userLoginDTO.getUsername()).get();
            if (user.getPassword().equals(userLoginDTO.getPassword())) {
                return true;
            } else {
                throw new WrongPasswordException("<Wrong Password>");
            }
        }
    }

    @Override
    public Boolean userRegister(UserRegisterDTO userRegisterDTO) {
        if (userRepository.findByUsername(userRegisterDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("<UserAlreadyExistException>");
        } else {
            User user = new User();
            BeanUtils.copyProperties(userRegisterDTO, user);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public Boolean userChangePassword(UserChangePasswordDTO userChangePasswordDTO) {
        if (!userRepository.findByUsername(userChangePasswordDTO.getUsername()).isPresent()) {
            throw new UserNotFoundException("<UserNotFoundException>");
        } else {
            User user = userRepository.findByUsername(userChangePasswordDTO.getUsername()).get();
            user.setPassword(userChangePasswordDTO.getNewPassword());
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public UserReturnDTO getUserByUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();
            UserReturnDTO userReturnDTO = new UserReturnDTO();
            userReturnDTO.setUsername(user.getUsername());
            userReturnDTO.setId(user.getId());
            userReturnDTO.setRole(user.getRole());
            userReturnDTO.setImageURL(user.getImageURL());
            return userReturnDTO;
        } else {
            throw new UserNotFoundException("<UserNotFoundException>");
        }
    }

    @Override
    public Boolean deleteUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            throw new UserNotFoundException("<UserNotFoundException>");
        }
    }

    @Override
    public UserReturnDTO getUserById(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException("<UserNotFoundException>");
        } else {
            User user = userRepository.findById(id).get();
            UserReturnDTO userReturnDTO = new UserReturnDTO();
            userReturnDTO.setUsername(user.getUsername());
            userReturnDTO.setId(user.getId());
            userReturnDTO.setRole(user.getRole());
            userReturnDTO.setImageURL(user.getImageURL());
            return userReturnDTO;
        }
    }

    @Override
    public List<User> getAllUser() {
        List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }
}

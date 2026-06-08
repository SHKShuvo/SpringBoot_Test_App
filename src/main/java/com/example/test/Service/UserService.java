package com.example.test.Service;

import com.example.test.DTO.UserDTO;
import com.example.test.Entity.UserEntity;
import com.example.test.ResponseModel.ResponseModel;
import com.example.test.ResponseModel.UserAccessModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public UserDTO getUserByUserId(long id);

    List<UserDTO> getAllUser();
    List<UserEntity> getAllUsers();

    ResponseModel createUser(UserDTO userDTO);

    ResponseModel createMultipleUsers(List<UserDTO> userDTOList);

    ResponseModel updateUser(long id, UserDTO userDTO);

    //ResponseModel deleteUser(long id, UserDTO userDTO);
    ResponseModel deleteUser(long id);

   // List<Object[]>
   List<UserAccessModel> getDeptById(long deptId);

    Object menuTreePopulate(long applicationUniqueId);

    public void save(MultipartFile file);
}

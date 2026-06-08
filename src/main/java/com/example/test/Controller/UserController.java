package com.example.test.Controller;

import com.example.test.Common.DBConnection;
import com.example.test.Common.LoggerManager;
import com.example.test.DTO.UserDTO;
import com.example.test.Entity.UserEntity;
import com.example.test.Mapper.UserRequest;
import com.example.test.Procedure.UserInsertProcedure;
import com.example.test.ResponseModel.Parameters;
import com.example.test.ResponseModel.ResponseModel;
import com.example.test.ResponseModel.UserAccessModel;
import com.example.test.Service.ServiceImpl.ExcelHelper;
import com.example.test.Service.ServiceImpl.TestService;
import com.example.test.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins ="*", allowedHeaders = "*")
@RequestMapping("/gums/v1/user_role")
@Tag(name = "User Management", description = "User Managements")
public class UserController {
    @Autowired
    private UserService userService;


    @Autowired
    private UserInsertProcedure userInsertProcedure;

    // Swagger UI - http://localhost:8091/swagger-ui/


    // Get All Users
    @GetMapping("/users")
    public List<UserEntity> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    public List<UserDTO> getAllUser() {
        return userService.getAllUser();
    }

    // Get User By ID
    @GetMapping("/user/{id}")
    public UserDTO getUserById(@Valid @PathVariable(name = "id") long id){
        return userService.getUserByUserId(id);
    }

    // Save User
    @PostMapping("/save")
    public ResponseModel createNewUser(@Valid @RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @PostMapping("/users/save")
    public ResponseModel createEventWiseFields(@Valid @RequestBody List<UserDTO>
                                                           userDTOList){
        return userService.createMultipleUsers(userDTOList);
    }


    //Update User
    @PutMapping("/users/{id}")
    public ResponseModel updateUser(@Valid @PathVariable(name = "id") long id,
                                            @Valid @RequestBody UserDTO userDTO){
        return userService.updateUser(id, userDTO);
    }

    // Delete User
    @DeleteMapping("/users/{id}")
    public ResponseModel deleteUser(@Valid @PathVariable(name = "id") long id){
        return userService.deleteUser(id);
    }

    // Get Department By Id
    @GetMapping("/query/{deptId}")
    public List<UserAccessModel> getDepartmentById(@PathVariable(name = "deptId")
                                    long deptId) {
        return userService.getDeptById(deptId);
    }

    // Get Menu By Id
    @GetMapping("/menu/{applicationUniqueId}")
    public Object menuTreePopulate( @PathVariable(name = "applicationUniqueId")
                                    long applicationUniqueId) {
        return userService.menuTreePopulate(applicationUniqueId);

    }

    // Make New User Through Procedure
    @RequestMapping(value = "/make-user", method = RequestMethod.POST)
    public ResponseEntity<?> makeNewUser(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        System.out.println("Test ");
        Map<String, Object> info = new HashMap<>();
        Parameters parameters = new Parameters();
        parameters.setActFlg(userRequest.getActFlg());
        parameters.setEmail(userRequest.getEmail());
        parameters.setFirstName(userRequest.getFirstName());
        parameters.setLastName(userRequest.getLastName());
        parameters.setPhone(userRequest.getPhone());


        parameters = userInsertProcedure.MakeUser(parameters);

        if (parameters.getErrorMessage().equals("Success")) {
            info.put("Response Message", parameters.getErrorMessage());
        } else {
            info.put("Response Message", parameters.getErrorMessage());
        }
        return ResponseEntity.ok(info);
    }

    // Upload User Data from Excel
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("File is "+ file);
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                userService.save(file);

                return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file: " + file.getOriginalFilename() + "!");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an excel file!");
    }
}

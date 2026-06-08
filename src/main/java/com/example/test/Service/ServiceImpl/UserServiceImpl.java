package com.example.test.Service.ServiceImpl;

import com.example.test.DTO.UserDTO;
import com.example.test.Entity.UserEntity;
import com.example.test.Exception.ResourceNotFoundException;
import com.example.test.Mapper.UserMapper;
import com.example.test.Repository.UserRepository;
import com.example.test.ResponseModel.ResponseModel;
import com.example.test.ResponseModel.UserAccessModel;
import com.example.test.Service.UserService;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepo;

    private UserService userService;

    private UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepo,
                           UserMapper userMapper){
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @Override
    public ResponseModel createUser(UserDTO userDTO) {

        ResponseModel responseModel = new ResponseModel();

        UserEntity userEntity = this.userMapper.DtoToEntity(userDTO);

        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.println(userEntity);

        userRepo.save(userEntity);

        if (userEntity.getId() > 0) {
            responseModel.setOutCode("1");
            responseModel.setOutMessage("User saved successfully");
        }
        return responseModel;
    }

    @Override
    public ResponseModel createMultipleUsers(List<UserDTO> userDTOList) {

        ResponseModel commonResponseModelDTO = new ResponseModel();
        List<UserEntity> userEntity = null;

        /*userDTOList.forEach(userDto -> userDto
                .setCreatedDate(Timestamp.valueOf(LocalDateTime.now())
                ));*/

        try{
            userEntity = saveAllWithRollback(userDTOList
                    .stream().map(userMapper::DtoToEntity)
                    .collect(Collectors.toList()));
            if(userEntity.size() == userDTOList.size()) {
                commonResponseModelDTO.setOutCode("1");
                commonResponseModelDTO.setOutMessage("Field data saved successfully");
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        return commonResponseModelDTO;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<UserEntity> saveAllWithRollback(List<UserEntity> items) throws Exception {

        List<UserEntity> savedItems = new ArrayList<>();

        try {
            savedItems = userRepo.saveAll(items);
        } catch (Exception e) {
            throw new Exception("Error occurred while saving items. Rolling back transaction.", e);
        }
        return savedItems;
    }



    @Override
    public UserDTO getUserByUserId(long id) {

        Optional<UserEntity> userEntity = this.userRepo.findUserEntityById(id);

        return this.userMapper.entityToDto(userEntity.orElseThrow(()->
                new ResourceNotFoundException("UserEntity", "id", id)));

    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepo.getAllUsers();
    }



    @Override
    public List<UserDTO> getAllUser() {
        List<UserEntity> users = userRepo.findAll();

//        for (UserEntity user : users) {
//            user.setParentModDes(codeMasterService.getCoddesByHarcodAndSofcod("MOD",user.getParentModule()));
//            System.out.println(" document : " + user);
//
//        }
        return users.stream().map(user ->
                this.userMapper.entityToDto(user)).collect(Collectors.toList());
    }

    @Override
    public ResponseModel updateUser(long Id, UserDTO userDTO) {

        ResponseModel responseModel = new ResponseModel();

        UserEntity previousUser = userRepo.
                findUserEntityById(Id).
                orElseThrow(()->new ResourceNotFoundException("User","user Id",Id));

        UserEntity newUser = this.userMapper.DtoToEntity(userDTO);
        newUser.setId(previousUser.getId());

        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //newApplicationMaster.setCreateTime(previousApplicationInfo.getCreateTime());
        //newApplicationMaster.setCreatedBy(previousApplicationInfo.getCreatedBy());

        userRepo.save(newUser);

        responseModel.setOutCode("1");
        responseModel.setOutMessage("User Information updated successfully");

        return responseModel;
    }

    @Override
    public ResponseModel deleteUser(long Id) {
    //public ResponseModel deleteUser(long Id, UserDTO userDTO) {

        ResponseModel responseModel = new ResponseModel();

//        try{
            //userRepo.deleteById(Id);
            UserEntity getUser = userRepo.
                    findUserEntityById(Id).
                    orElseThrow(()->new ResourceNotFoundException("User","user Id",Id));

            userRepo.delete(getUser);
            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            responseModel.setOutCode("1");
            responseModel.setOutMessage("User Information deleted successfully");

            return responseModel;
//        }catch(Exception e){
//            responseModel.setOutCode("2");
//            responseModel.setOutMessage("Cannot Delete User");
//            return responseModel;
//        }
    }
    @Override
    public List<UserAccessModel> getDeptById(long deptId) {

        List<Object[]> accessList=userRepo.getDeptById(deptId);
        List<UserAccessModel> useracceslist = new ArrayList<>();
        for(Object[]  useraccess : accessList){

            UserAccessModel userAccessModule = new UserAccessModel();
            userAccessModule.setApplicationId(String.valueOf(useraccess[0]));
            userAccessModule.setApplicationName(String.valueOf(useraccess[1]));
            userAccessModule.setApplicationUrl(String.valueOf(useraccess[2]));
            useracceslist.add(userAccessModule);


        }
        //System.out.println(useracceslist);
        return useracceslist;
    }

    @Override
    public Object menuTreePopulate(long applicationUniqueId) {
        return userRepo.menuTreePopulate(applicationUniqueId);
    }


    public void save(MultipartFile file) {
        try {
            List<UserEntity> users = excelToUsers(file.getInputStream());
            userRepo.saveAll(users);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    private List<UserEntity> excelToUsers(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<UserEntity> users = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                UserEntity user = new UserEntity();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            user.setFirstName(currentCell.getStringCellValue());
                            break;
                        case 1:
                            user.setLastName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            user.setEmail(currentCell.getStringCellValue());
                            break;
                        case 3:
                            user.setPhone(currentCell.getStringCellValue());
                            break;
                        case 4:
                            user.setActFlg(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }

                users.add(user);
            }

            // workbook.close();

            return users;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}

package com.adkp.fuexchange.service;

import com.adkp.fuexchange.dto.RegisteredStudentDTO;
import com.adkp.fuexchange.mapper.RegisteredStudentMapper;
import com.adkp.fuexchange.pojo.RegisteredStudent;
import com.adkp.fuexchange.repository.RegisteredStudentRepository;
import com.adkp.fuexchange.request.UpdatePasswordRequest;
import com.adkp.fuexchange.response.ResponseObject;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisteredStudentServiceImpl implements RegisteredStudentService{

    public final RegisteredStudentRepository registeredStudentRepository;

    public final RegisteredStudentMapper registeredStudentMapper;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public RegisteredStudentServiceImpl(RegisteredStudentRepository registeredStudentRepository, RegisteredStudentMapper registeredStudentMapper, PasswordEncoder passwordEncoder) {
        this.registeredStudentRepository = registeredStudentRepository;
        this.registeredStudentMapper = registeredStudentMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisteredStudentDTO viewProfile(Integer registeredStudentId) {

        return registeredStudentMapper.toRegisteredStudentDTO(
                registeredStudentRepository.getReferenceById(registeredStudentId)
        );
    }

    @Override
    @Transactional
    public ResponseObject updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        try {
            boolean isExist = registeredStudentRepository.existsById(updatePasswordRequest.getIdWantUpdate());
            if (isExist){
                if(updatePasswordRequest.getPassword().equals(updatePasswordRequest.getConfirmPassword())){
                    RegisteredStudent registeredStudentUpdate = registeredStudentRepository.getReferenceById(updatePasswordRequest.getIdWantUpdate());
                    registeredStudentUpdate.setPassword(passwordEncoder.encode(updatePasswordRequest.getPassword()));
                    registeredStudentRepository.save(registeredStudentUpdate);
                    return ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message(HttpStatus.OK.name())
                            .content("Thay đổi mật khẩu thành công!")
                            .build();
                }
                return ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.name())
                        .content("Mật khẩu không trùng khớp")
                        .build();
            }
            return ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(HttpStatus.BAD_REQUEST.name())
                    .content("Thông tin không tồn tại!")
                    .build();
        } catch (DataAccessException dataAccessException){
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .content("Lỗi trong quá trình lưu trữ dữ liệu!")
                    .build();
        } catch (Exception exception){
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(HttpStatus.INTERNAL_SERVER_ERROR.name())
                    .content("Lỗi không xác định!")
                    .build();
        }
    }
}
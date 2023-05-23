package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.payload.ChangePasswordRequest;
import com.appointmentManagementSystem.payload.ResetPasswordRequest;
import com.appointmentManagementSystem.repository.ResetPasswordRepository;
import com.appointmentManagementSystem.enums.EnumResetPassword;
import com.appointmentManagementSystem.model.EntityResetPassword;
import com.appointmentManagementSystem.model.EntityUser;
import com.appointmentManagementSystem.repository.UserRepository;
import com.appointmentManagementSystem.util.CustomException;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class PasswordResetImpl implements PasswordResetService {

    private UserRepository userRepository;

    private ResetPasswordRepository resetPasswordRepository;

    private EmailService emailService;

    PasswordEncoder encoder;

    @Autowired
    public PasswordResetImpl(UserRepository userRepository,EmailService emailService,ResetPasswordRepository resetPasswordRepository,PasswordEncoder encoder ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.resetPasswordRepository = resetPasswordRepository;
        this.encoder=encoder;
    }

    @Override
    @Transactional
    public void sendSecurityCode(String email) throws Exception {

        Optional<EntityUser> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent() ){

            Map<String, Object> model = new HashMap<String, Object>();
            UUID uuid = UUID.randomUUID();
            String code = uuid.toString().split("-")[0].toUpperCase();
            model.put("code", code);
            model.put("sign", "Java Virtual Lab - Exhibition Management");
            try {
                emailService.sendPasswordResetCode(email, "PSK. Gamze Bayan - Şifre Sıfırlama ", model);
            }
            catch (Exception e){
                throw new Exception("E-posta gönderilememiştir. Lütfen daha sonra tekrar deneyiniz.");
            }
            EntityResetPassword resetPassword = new EntityResetPassword();
            resetPassword.setCode(code);
            resetPassword.setStatus(EnumResetPassword.AVAILABLE);
            resetPassword.setUser(byEmail.get());
            resetPassword.setDate(new Date());
            resetPasswordRepository.save(resetPassword);

        }
        else{
            throw  new Exception("Bu e-posta adresi sistemde kayıtlı değildir. Lütfen kayıt olunuz.");
        }
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest resetPasswordRequest) throws Exception {

        try {
            checkSecurityCode(resetPasswordRequest.getCode(),resetPasswordRequest.getEmail());
            Optional<EntityUser> user = userRepository.findByEmail(resetPasswordRequest.getEmail());
            if(user.isPresent()){
                EntityUser entityUser = user.get();
                entityUser.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
                userRepository.save(entityUser);
            }else{
                throw new Exception("Kullanıcı bulunamadı.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean ChangePassword(ChangePasswordRequest changePasswordRequest) throws Exception {
        Optional<EntityUser> u = userRepository.findByEmail(changePasswordRequest.getEmail());
        if(u.isPresent()){
            EntityUser user = u.get();
            if(encoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())){
                user.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
                return true;
            }else{
                throw new CustomException("Yanlış şifre girdiniz.");
            }
        }else{
            throw new CustomException("Kullanıcı bilgisi bulunamadı.");
        }

    }

    private void checkSecurityCode(String code, String email) throws Exception {

        Set<EntityResetPassword> byCode = resetPasswordRepository.findByCode(code);
        Iterator<EntityResetPassword> iterator = byCode.stream().iterator();
        if(iterator.hasNext()){
            EntityResetPassword resetPassword = iterator.next();
            EntityUser user = resetPassword.getUser();
            if(user.getEmail().equalsIgnoreCase(email)){
                    if(new Date().before(DateUtils.addMinutes(resetPassword.getDate(),15))){
                            if(code.equals(resetPassword.getCode())){

                            }
                    }else{

                        throw new Exception("Güvenlik kodu süresi geçmiştir. Tekrar kod gönderiniz.");

                    }
            }else{
                throw new Exception("Geçersiz mail adresi.");
            }
        }else{
            throw new Exception("Güvenlik kodu veya e-posta adresi hatalıdır. Lütfen tekrar deneyiniz.");
        }
    }
}

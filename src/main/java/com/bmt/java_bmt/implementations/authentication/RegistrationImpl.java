package com.bmt.java_bmt.implementations.authentication;

import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;
import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Source;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.mappers.IRegistrationMapper;
import com.bmt.java_bmt.mappers.IUserMapper;
import com.bmt.java_bmt.repositories.IPersonalInformationRepository;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IRedis;
import com.bmt.java_bmt.services.authentication.IRegistrationService;
import com.bmt.java_bmt.utils.Generator;
import com.bmt.java_bmt.utils.senders.OTPEmailSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class RegistrationImpl implements IRegistrationService {
    long OTP_EXPIRE_MINUTES = 5;
    int LENGTH_OF_OTP = 6;
    String SUBJECT = "Xác thực mã OTP";
    String HTML_FILE_PATH = "src/main/resources/templates/html/authentication/registration_otp_email.html";

    IRedis<String, Object> redisService;
    IUserRepository userRepository;
    IPersonalInformationRepository personalInformationRepository;
    OTPEmailSender otpEmailSender;
    IRegistrationMapper registrationMapper;
    IUserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public String sendOTP(SendOTPRequest request) {
         /*
            Check if this email exists in the database
          */
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        /*
            Check if there is a key in redis
            If it exists, then the email is in the process of being registered.
         */
        String registrationKey = RedisKey.IS_IN_REGISTRATION + request.getEmail();

        if (redisService.existsKey(registrationKey)) {
            throw new AppException(ErrorCode.EMAIL_IS_IN_REGISTRATION_PROCESS);
        }

        String otp = Generator.generateOTP(LENGTH_OF_OTP);

        try {
            otpEmailSender.sendOtpEmail(
                    request.getEmail(),
                    SUBJECT,
                    HTML_FILE_PATH,
                    otp,
                    String.valueOf(OTP_EXPIRE_MINUTES));
        } catch (IOException e) {
            throw new AppException(ErrorCode.EMAIL_TEMPLATE_ERROR);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_ERROR);
        }

        String registrationOTPKey = RedisKey.REGISTRATION_OTP + request.getEmail();

        redisService.save(registrationKey, true, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisService.save(registrationOTPKey, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return "Gửi mã OTP thành công";
    }

    @Override
    public String verifyOTP(VerifyOTPRequest request) {
        /*
            Check if this key is in redis
            If it does not exist, it means this email has not gone through the sendOTP api.
         */
        String registrationOTPKey = RedisKey.REGISTRATION_OTP + request.getEmail();
        String registrationCompleteKey = RedisKey.REGISTRATION_COMPLETE + request.getEmail();

        if (!redisService.existsKey(registrationOTPKey)) {
            if (redisService.existsKey(registrationCompleteKey)) {
                throw new AppException(ErrorCode.EMAIL_IS_IN_COMPLETION_REGISTRATION);
            }

            throw new AppException(ErrorCode.EMAIL_IS_NOT_REGISTRATION_PROCESS);
        }

        String otp = (String) redisService.get(registrationOTPKey);

        if (!request.getOtp().equals(otp)) {
            throw new AppException(ErrorCode.OTP_DONT_MATCH);
        }

        redisService.save(registrationCompleteKey, true, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisService.delete(registrationOTPKey);

        return "Xác thực mã OTP thành công";
    }

    @Override
    public RegistrationResponse completeRegistration(CompleteRegistrationRequest request) {
        String registrationCompleteKey = RedisKey.REGISTRATION_COMPLETE + request.getEmail();

        if (!redisService.existsKey(registrationCompleteKey)) {
            throw new AppException(ErrorCode.EMAIL_IS_NOT_IN_COMPLETION_REGISTRATION);
        }

        var personalInformation = personalInformationRepository
                .save(userMapper.toPersonalInformation(request.getPersonalInformation()));
        var user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPersonalInformation(personalInformation);
        user.setRole(Role.CUSTOMER);
        user.setSource(Source.APP);

        userRepository.save(user);

        String registrationKey = RedisKey.IS_IN_REGISTRATION + request.getEmail();

        redisService.delete(registrationKey);
        redisService.delete(registrationCompleteKey);

        request.setPassword(user.getPassword());

        return registrationMapper.toRegistrationResponse(request);
    }
}


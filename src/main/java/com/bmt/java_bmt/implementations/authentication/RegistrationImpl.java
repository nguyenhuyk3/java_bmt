package com.bmt.java_bmt.implementations.authentication;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.authentication.registration.RegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IRedis;
import com.bmt.java_bmt.services.authentication.IRegistrationService;
import com.bmt.java_bmt.utils.Generator;
import com.bmt.java_bmt.utils.senders.OTPEmailSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
    OTPEmailSender otpEmailSender;

    @Override
    public String sendOTP(SendOTPRequest request) {
        /*
            Check if there is a key in redis
            If it exists, then the email is in the process of being registered.
         */
        String registrationKey = RedisKey.IS_IN_REGISTRATION + request.getEmail();

        if (redisService.existsKey(registrationKey)) {
            throw new AppException(ErrorCode.EMAIL_IS_IN_REGISTRATION_PROCESS);
        }

        /*
            Check if this email exists in the database
         */
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
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

        redisService.save(registrationOTPKey, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisService.save(registrationKey, true, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return "Gửi mã OTP thành công";
    }

    @Override
    public String verifyOTP(VerifyOTPRequest request) {
        return null;
    }

    @Override
    public RegistrationResponse registration(RegistrationRequest request) {
        return null;
    }
}


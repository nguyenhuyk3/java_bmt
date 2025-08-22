package com.bmt.java_bmt.implementations.authentication;

import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.CompleteForgotPasswordRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.SendForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.VerifyForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.forgotPassword.CompleteForgotPasswordResponse;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IRedis;
import com.bmt.java_bmt.services.authentication.IForgotPasswordService;
import com.bmt.java_bmt.utils.Generator;
import com.bmt.java_bmt.utils.senders.OTPEmailSender;
import jakarta.transaction.Transactional;
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
public class ForgotPasswordImpl implements IForgotPasswordService {
    long OTP_EXPIRE_MINUTES = 5;
    int LENGTH_OF_OTP = 6;
    String SUBJECT = "Quên mật khẩu";
    String HTML_FILE_PATH = "src/main/resources/templates/html/authentication/forgot_password_otp_email.html";

    IRedis<String, Object> redisService;
    IUserRepository userRepository;
    OTPEmailSender otpEmailSender;
    PasswordEncoder passwordEncoder;

    @Override
    public String sendForgotPasswordOTP(SendForgotPasswordOTPRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }

        String forgotPasswordKey = RedisKey.IS_IN_FORGOT_PASSWORD_PROCESS + request.getEmail();

        if (redisService.existsKey(forgotPasswordKey)) {
            throw new AppException(ErrorCode.EMAIL_IS_IN_FORGOT_PASSWORD_PROCESS);
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

        String forgotPasswordOTPKey = RedisKey.FORGOT_PASSWORD_OTP + request.getEmail();

        redisService.save(forgotPasswordKey, true, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisService.save(forgotPasswordOTPKey, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);

        return "Gửi mã OTP thành công";
    }

    @Override
    public String verifyForgotPasswordOTP(VerifyForgotPasswordOTPRequest request) {
        String forgotPasswordOTPKey = RedisKey.FORGOT_PASSWORD_OTP + request.getEmail();
        String forgotPasswordCompletionKey = RedisKey.FORGOT_PASSWORD_COMPLETION + request.getEmail();

        if (!redisService.existsKey(forgotPasswordOTPKey)) {
            if (redisService.existsKey(forgotPasswordCompletionKey)) {
                throw new AppException(ErrorCode.EMAIL_IS_IN_FORGOT_PASSWORD_COMPLETION);
            }

            throw new AppException(ErrorCode.EMAIL_IS_NOT_REGISTRATION_PROCESS);
        }

        String otp = (String) redisService.get(forgotPasswordOTPKey);

        if (!request.getOtp().equals(otp)) {
            throw new AppException(ErrorCode.OTP_DONT_MATCH);
        }

        redisService.save(forgotPasswordCompletionKey, true, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisService.delete(forgotPasswordOTPKey);

        return "Xác thực mã OTP thành công";
    }

    @Override
    @Transactional
    public CompleteForgotPasswordResponse completeFortgotPassword(CompleteForgotPasswordRequest request) {
        String forgotPasswordCompletionKey = RedisKey.FORGOT_PASSWORD_COMPLETION + request.getEmail();

        if (!redisService.existsKey(forgotPasswordCompletionKey)) {
            throw new AppException(ErrorCode.EMAIL_IS_NOT_IN_FORGOT_PASSWORD_COMPLETION);
        }

        if (!request.getPassword().equals(request.getConfirmedPassword())) {
            throw new AppException(ErrorCode.PASSWORD_DONT_MATCH);
        }

        String newPassword = passwordEncoder.encode(request.getPassword());
        var updatedRecords = userRepository
                .updatePasswordByEmail(request.getEmail(), newPassword);

        if (updatedRecords == 0) {
            throw new AppException(ErrorCode.UPDATE_PASSWORD_FAILED);
        }

        redisService.delete(forgotPasswordCompletionKey);

        return CompleteForgotPasswordResponse.builder()
                .email(request.getEmail())
                .password(newPassword)
                .build();
    }
}

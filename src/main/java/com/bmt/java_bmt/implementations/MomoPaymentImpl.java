package com.bmt.java_bmt.implementations;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.clients.IMomoClient;
import com.bmt.java_bmt.dto.others.MomoPayload;
import com.bmt.java_bmt.dto.others.MomoProperties;
import com.bmt.java_bmt.dto.requests.payment.momo.CreateMomoPaymentRequest;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.services.IMomoPaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class MomoPaymentImpl implements IMomoPaymentService {
    //    @Value("${momo.partner-code}")
    //    @NonFinal
    //    String PARTNER_CODE;
    //
    //    @Value("${momo.access-key}")
    //    @NonFinal
    //    String ACCESS_KEY;
    //
    //    @Value("${momo.secret-key}")
    //    @NonFinal
    //    String SECRET_KEY;
    //
    //    @Value("${momo.redirect-url}")
    //    @NonFinal
    //    String REDIRECT_URL;
    //
    //    @Value("${momo.ipn-url}")
    //    @NonFinal
    //    String IPN_URL;
    //
    //    @Value("${momo.request-type}")
    //    @NonFinal
    //    String REQUEST_TYPE;

    IMomoClient momoClient;
    MomoProperties momoProperties;

    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        hmacSHA256.init(secretKey);

        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }

    @Override
    public CreateMomoPaymentResponse createMomoQR(CreateMomoPaymentRequest request) {
        String orderInfo = "Thanh toán vé phim có mã: " + request.getOrderId().toString();
        String requestId = UUID.randomUUID().toString();
        String extraData = "";
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                momoProperties.getAccessKey(),
                request.getAmount(),
                extraData,
                momoProperties.getIpnUrl(),
                request.getOrderId().toString(),
                orderInfo,
                momoProperties.getPartnerCode(),
                momoProperties.getRedirectUrl(),
                requestId,
                momoProperties.getRequestType());

        try {
            String prettySignature = signHmacSHA256(rawSignature, momoProperties.getSecretKey());

            if (prettySignature == null || prettySignature.isBlank()) {
                throw new AppException(ErrorCode.MOMO_SIGNATURE_FAILED);
            }

            MomoPayload momoPayload = MomoPayload.builder()
                    .partnerCode(momoProperties.getPartnerCode())
                    .accessKey(momoProperties.getAccessKey())
                    .requestId(requestId)
                    .amount(request.getAmount())
                    .orderId(request.getOrderId().toString())
                    .orderInfo(orderInfo)
                    .partnerName("Momo payment")
                    .storeId("Test Store")
                    .orderGroupId("")
                    .lang("vi")
                    .autoCapture(true)
                    .redirectUrl(momoProperties.getRedirectUrl())
                    .ipnUrl(momoProperties.getIpnUrl())
                    .extraData(extraData)
                    .requestType(momoProperties.getRequestType())
                    .signature(prettySignature)
                    .build();

            CreateMomoPaymentResponse response = momoClient.createMomoQR(momoPayload);

            if (response == null || response.getResultCode() != 0) {
                throw new AppException(ErrorCode.MOMO_RESPONSE_ERROR);
            }

            return response;
        } catch (Exception e) {
            log.info(e.getMessage());

            throw new AppException(ErrorCode.MOMO_REQUEST_FAILED);
        }
    }
}

package com.bmt.java_bmt.implementations;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.clients.IMomoClient;
import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.others.MomoPayload;
import com.bmt.java_bmt.dto.others.MomoProperties;
import com.bmt.java_bmt.dto.requests.payment.CreatePaymentRequest;
import com.bmt.java_bmt.dto.requests.payment.momo.CreateMomoPaymentRequest;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;
import com.bmt.java_bmt.entities.Outbox;
import com.bmt.java_bmt.entities.Payment;
import com.bmt.java_bmt.entities.enums.PaymentMethod;
import com.bmt.java_bmt.entities.enums.PaymentStatus;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IOrderRepository;
import com.bmt.java_bmt.repositories.IOutboxRepository;
import com.bmt.java_bmt.repositories.IPaymentRepository;
import com.bmt.java_bmt.services.IMomoPaymentService;
import com.bmt.java_bmt.services.IPaymentService;
import com.bmt.java_bmt.services.IRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class MomoPaymentImpl implements IMomoPaymentService, IPaymentService {
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
    IRedisService redisService;
    IPaymentRepository paymentRepository;
    IOrderRepository orderRepository;
    IOutboxRepository outboxRepository;

    ObjectMapper objectMapper;
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

    private void preCheck(CreateMomoPaymentRequest request) {
        String totalOfOrderKey = RedisKey.TOTAL_OF_ORDER + request.getOrderId().toString();

        if (!redisService.existsKey(totalOfOrderKey)) {
            throw new AppException(ErrorCode.ORDER_HAS_EXPIRED);
        }

        int totalOfOrder = (int) redisService.get(totalOfOrderKey);

        if (request.getAmount() != totalOfOrder) {
            throw new AppException(ErrorCode.TOTAL_DO_NOT_MATCH);
        }
    }

    @Override
    public CreateMomoPaymentResponse createMomoQR(CreateMomoPaymentRequest request) {
        preCheck(request);

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
            throw new AppException(ErrorCode.MOMO_REQUEST_FAILED);
        }
    }

    @Transactional
    @Override
    public void handlePayment(CreatePaymentRequest request, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        var order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        var savedPayment = paymentRepository.save(Payment.builder()
                .amount(String.valueOf(request.getAmount()))
                .status(paymentStatus)
                .method(paymentMethod)
                .transactionId(request.getTransactionId())
                .errorMessage(request.getErrorMessage())
                .order(order)
                .build());

        order.setPayment(savedPayment);

        orderRepository.save(order);

        try {
            Id orderId = Id.builder().id(request.getOrderId().toString()).build();

            outboxRepository.save(Outbox.builder()
                    .eventType(paymentStatus == PaymentStatus.SUCCESS ? Others.PAYMENT_SUCCESS : Others.PAYMENT_FAILED)
                    .payload(objectMapper.writeValueAsString(orderId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }
    }
}

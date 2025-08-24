package com.bmt.java_bmt.implementations;

import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.services.ICloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryImpl implements ICloudinaryService {
    @Autowired
    Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file,
                             String productId,
                             String productType, String type) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String prefix = switch (productType) {
            case Others.FILM -> "films";
            case Others.FAB -> "fabs";
            default -> throw new AppException(ErrorCode.INVALID_PRODUCT_TYPE);
        };
        String newFileName = String.format("%s/%s/%s", prefix, productId, uuid);
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", type.toLowerCase(),
                        "public_id", newFileName
                )
        );

        return uploadResult.get("secure_url").toString();
    }
}

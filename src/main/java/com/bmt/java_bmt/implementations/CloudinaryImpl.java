package com.bmt.java_bmt.implementations;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.services.ICloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryImpl implements ICloudinaryService {
    @Autowired
    Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String productId, String productType, String type) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String prefix =
                switch (productType) {
                    case Others.FILM -> "films";
                    case Others.FAB -> "fabs";
                    default -> throw new AppException(ErrorCode.INVALID_PRODUCT_TYPE);
                };
        String newFileName = String.format("%s/%s/%s", prefix, productId, uuid);
        Map<?, ?> uploadResult = cloudinary
                .uploader()
                .upload(
                        file.getBytes(),
                        ObjectUtils.asMap("resource_type", type.toLowerCase(), "public_id", newFileName));

        return uploadResult.get("secure_url").toString();
    }

    private String extractPublicId(String url) {
        // lấy phần sau "/upload/"
        String path = url.substring(url.indexOf("/upload/") + 8);
        // bỏ phần version "v123456789/"
        if (path.startsWith("v")) {
            int slashIndex = path.indexOf("/");

            if (slashIndex >= 0) {
                path = path.substring(slashIndex + 1);
            }
        }

        // bỏ phần extension (.png, .jpg, .mp4...)
        int dotIndex = path.lastIndexOf(".");

        if (dotIndex > 0) {
            path = path.substring(0, dotIndex);
        }

        return path;
    }

    @Override
    public void deleteFile(String url, String productType) {
        try {
            Map<?, ?> result = cloudinary
                    .uploader()
                    .destroy(extractPublicId(url), ObjectUtils.asMap("resource_type", productType.toLowerCase()));

            // Cloudinary trả về {"result":"ok"} nếu xóa thành công
            if (!"ok".equals(result.get("result"))) {
                throw new AppException(ErrorCode.CLOUDINARY_DELETE_FILE_FAILED);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.CLOUDINARY_DELETE_FILE_FAILED);
        }
    }
}

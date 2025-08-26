package com.bmt.java_bmt.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    String uploadFile(MultipartFile file, String productId, String productType, String type) throws IOException;

    void deleteFile(String url, String productType);
}

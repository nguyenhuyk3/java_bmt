package com.bmt.java_bmt.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadFile(MultipartFile file,
                      String productId,
                      String productType, String type) throws IOException;
}

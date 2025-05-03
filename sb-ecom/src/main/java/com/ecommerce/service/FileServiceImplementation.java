package com.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImplementation implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        String OrginalFileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(OrginalFileName.substring(OrginalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        //Check if file path exists
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdir();

        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return filePath;
    }
}

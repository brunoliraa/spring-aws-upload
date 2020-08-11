package com.br.springawsuploadimage.service;

import com.amazonaws.services.connect.model.UserNotFoundException;
import com.br.springawsuploadimage.bucket.Bucket;
import com.br.springawsuploadimage.model.User;
import com.br.springawsuploadimage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class UserService {

    private final FileService fileService;
    private final UserRepository userRepository;

    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(userRepository.findAll());
    }

    public void uploadUserImage(Long id, MultipartFile file) {
        // 1. Check if image is not empty
        isFileEmpty(file);
        // 2. If file is an image
        isImage(file);
        // 3. The user exists in our database
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("user not found with id: "+ id));
        // 4. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);
        // 5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
        String path = String.format("%s/%s", Bucket.UPLOAD_IMAGE.getName(), user.getId());
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileService.save(path, filename, Optional.of(metadata), file.getInputStream());
            user.setImageLink(filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadUserImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("user not found with id: "+ id));

        String path = String.format("%s/%s",
                Bucket.UPLOAD_IMAGE.getName(),
                user.getId());

        return user.getImageLink().getBytes();

    }


    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }
}

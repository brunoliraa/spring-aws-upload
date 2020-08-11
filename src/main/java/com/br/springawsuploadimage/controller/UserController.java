package com.br.springawsuploadimage.controller;

import com.br.springawsuploadimage.model.User;
import com.br.springawsuploadimage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return userService.findAll();
    }

    @PostMapping("/{id}/image/upload")
    public void uploadUserImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        userService.uploadUserImage(id, file);
    }

    @GetMapping("{id}/image/download")
    public byte[] downloadUserProfileImage(@PathVariable Long id) {
        return userService.downloadUserImage(id);
    }
}

package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

@Slf4j
@Controller
public class FileController {

    @Value("${post.image.upload_dir}")
    private String uploadDir;

    @Value("${user.image.avatar_dir}")
    private String avatarDir;

    @Autowired
    private FileService fileService;

    @GetMapping(value="/**/upload/{firstFolder}/{secondFolder}/{thirdFolder}/{image}")
    public ResponseEntity<?> getPostImage(@PathVariable(value = "image") String image,
                                      @PathVariable(value = "firstFolder") String firstFolder,
                                      @PathVariable(value = "secondFolder") String secondFolder,
                                      @PathVariable(value = "thirdFolder") String thirdFolder) {
        String pathToFile = uploadDir + File.separator +
                firstFolder + File.separator +
                secondFolder + File.separator +
                thirdFolder + File.separator
                + image;
        return fileService.getResponseWithImage(pathToFile);
    }

    @GetMapping(value="/**/avatars/{firstFolder}/{secondFolder}/{thirdFolder}/{image}")
    public ResponseEntity<?> getUserAvatar(@PathVariable(value = "image") String image,
                                      @PathVariable(value = "firstFolder") String firstFolder,
                                      @PathVariable(value = "secondFolder") String secondFolder,
                                      @PathVariable(value = "thirdFolder") String thirdFolder) {
        String pathToFile = avatarDir + File.separator +
                firstFolder + File.separator +
                secondFolder + File.separator +
                thirdFolder + File.separator
                + image;
        return fileService.getResponseWithImage(pathToFile);
    }
}

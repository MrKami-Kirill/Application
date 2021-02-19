package main.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.api.response.BadRequestMessageResponse;
import main.model.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
@Slf4j
@Data
public class FileService {

    @Value("${image.root_dir}")
    private String rootDir;

    @Value("${image.format}")
    private String format;

    @Value("${image.max_size}")
    private int maxSize;

    @Autowired
    private UserService userService;


    public ResponseEntity<?> uploadFile(MultipartFile file, HttpSession session) throws Exception {

        HashMap<String, String> errors = new HashMap<>();

        if (file == null) {
            log.warn("Изображение для загрузки на сервер отсутствует");
            throw new Exception("Изображение для загрузки на сервер отсутствует");
        }

        if (file.getSize() > maxSize) {
            errors.put("image", "Размер файла превышает допустимый размер (10Мб)");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        if (!Files.exists(Path.of(rootDir))) {
            createRandomDirectory(rootDir);
        }
        String path = "";
        String hash = RandomStringUtils.random(6, true, true);
        String firstFolder = hash.substring(0, hash.length() / 3);
        String secondFolder = hash.substring(firstFolder.length(), (firstFolder.length() + hash.length() / 3));
        String thirdFolder = hash.substring(firstFolder.length() + secondFolder.length());
        StringBuilder builder = new StringBuilder();
        builder.append(rootDir)
                .append(File.separator)
                .append(firstFolder)
                .append(File.separator)
                .append(secondFolder)
                .append(File.separator)
                .append(thirdFolder);
        path = builder.toString();
        if (createRandomDirectory(path)) {
            String image = RandomStringUtils.random(5, true, true) + "." + format;
            String responsePath = path + File.separator + image;

            while (Files.exists(Path.of(responsePath))) {
                image = RandomStringUtils.random(5, true, true) + "." + format;
                responsePath = path + File.separator + image;
            }

            try {
                file.transferTo(Paths.get(path, image));
                log.info("Файл успешно загружен на сервер по пути " + responsePath + ". Данные загружаемого файла: {" +
                        "original_name: " + file.getOriginalFilename() + ", " +
                        "size: " + file.getSize() + ", " +
                        "content_type: " + file.getContentType() + "}");
            } catch (IOException ex) {
                log.error("Не удалось загрузить файл на сервер по пути " + responsePath + ". Данные загружаемого файла: {" +
                        "original_name: " + file.getOriginalFilename() + ", " +
                        "size: " + file.getSize() + ", " +
                        "content_type: " + file.getContentType() + "}");
            }
            ResponseEntity<String> response = new ResponseEntity<>(responsePath, HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/image cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        } else {
            throw new Exception("Директория '" + path + "' не создана");
        }
    }

    private boolean createRandomDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
            log.info("Директория '" + path + "' успешно создана");
            return true;
        } catch (IOException ex) {
            log.error("Директория '" + rootDir + "' не создана");
            return false;
        }
    }

    public File getFileByPath(String pathToFile) throws FileNotFoundException {
        if (Files.exists(Path.of(pathToFile))) {
            File file = new File(pathToFile);
            log.info("--- Получен файл по пути: {" +
                    pathToFile + "}"
            );
            return file;
        } else {
            log.error("--- Не удалось найти файл: " + pathToFile);
            throw new FileNotFoundException("По указанному пути отсутствует файл");
        }
    }


}

package main.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileService {

    @Value("${image.folder_length}")
    private int folderLength;

    @Value("${image.name_length}")
    private int nameLength;

    public boolean createDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
            log.info("Директория '" + path + "' успешно создана");
            return true;
        } catch (IOException ex) {
            log.error("Директория '" + path + "' не создана");
            return false;
        }
    }

    private File getFileByPath(String pathToFile) throws FileNotFoundException {
        if (Files.exists(Path.of(pathToFile))) {
            File file = new File(pathToFile);
            log.info("Получен файл по пути: {" +
                    pathToFile + "}"
            );
            return file;
        } else {
            log.error("Не удалось найти файл по пути {: " + pathToFile + "}");
            throw new FileNotFoundException("По указанному пути отсутствует файл");
        }
    }

    public boolean deleteFileByPath(String pathToFile) {
        if (pathToFile != null && !pathToFile.isBlank() && !pathToFile.equals("")) {
            try {
                boolean isDeleteSucceed = Files.deleteIfExists(Path.of("." + pathToFile));
                log.info("Файл успешно удален по пути: {" + pathToFile + "}");
                return isDeleteSucceed;
            } catch (IOException e) {
                log.error("Не удалось удалить файл по пути: {" + pathToFile  + "}", e);
                return false;
            }
        }
        log.warn("Не удалось удалить файл по пути: {" + pathToFile + "}");
        return false;
    }

    public ResponseEntity<?> getResponseWithImage(String pathToFile) {
        log.info("Запрошен файл с изображением по пути: {" + pathToFile + "}");
        try {
            File file = getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(file.toPath());
            log.info("Файл с изображением успешно получен по пути: {" + file.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public String createFile(String uploadDir, String format, MultipartFile file) throws Exception {
        if (!Files.exists(Path.of(uploadDir))) {
            createDirectory(uploadDir);
        }
        String path;
        String responsePath = "";
        String hash = RandomStringUtils.random(folderLength, true, true);
        String firstFolder = hash.substring(0, hash.length() / 3);
        String secondFolder = hash.substring(firstFolder.length(), (firstFolder.length() + hash.length() / 3));
        String thirdFolder = hash.substring(firstFolder.length() + secondFolder.length());

        StringBuilder builder = new StringBuilder(uploadDir)
                .append(File.separator).append(firstFolder)
                .append(File.separator).append(secondFolder)
                .append(File.separator).append(thirdFolder);
        path = builder.toString();
        if (createDirectory(path)) {
            String image = RandomStringUtils.random(nameLength, true, true) + "." + format;
            responsePath = File.separator + path + File.separator + image;

            while (Files.exists(Path.of(responsePath))) {
                image = RandomStringUtils.random(nameLength, true, true) + "." + format;
                responsePath = File.separator + path + File.separator + image;
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
                throw new IOException("Не удалось загрузить файл на сервер");
            }
        } else {
            throw new Exception("Директория '" + path + "' не создана");
        }
        return responsePath;
    }


}

package ru.yandex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.exception.EmptyFileException;
import ru.yandex.exception.FileStorageException;
import ru.yandex.exception.InvalidFileTypeException;
import ru.yandex.repository.FileStorageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("#{'${app.upload-dir.allowed-image-extensions}'.split(',')}")
    private Set<String> allowedExtensions = new HashSet<>();

    private final FileStorageRepository fileStorageRepository;


    /**
     * Обновление картинки поста
     */
    @Transactional
    public void updatePostImage(Long postId, String imagePath) {
        log.info("Updating image for post={}", postId);
        fileStorageRepository.updatePostImage(postId, imagePath);
    }

    /**
     * Сохранение картинки
     */
    public String saveImage(MultipartFile file) {
        validateFile(file);
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            String extension = extractAndValidateExtension(originalFilename);

            String fileName = UUID.randomUUID() + extension;

            Path uploadPath = Paths.get(uploadDir);

            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);

            log.info("Saving file to {}", filePath.toAbsolutePath());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Image saved successfully {}", fileName);
            return fileName;

        } catch (IOException e) {

            log.error("Failed to save image {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to save image", e);
        }
    }

    /**
     * Получение картинки поста
     */
    @Transactional(readOnly = true)
    public byte[] getPostImage(Long postId) {
        log.info("Getting image for post={}", postId);

        try {

            String imagePath = fileStorageRepository.getImagePathByPostId(postId);

            if (!StringUtils.hasText(imagePath)) {
                throw new FileStorageException("Post image not found");
            }

            Path filePath = Paths.get(uploadDir).resolve(imagePath);

            if (!Files.exists(filePath)) {
                throw new FileStorageException("Post image not found");
            }

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            log.error("Failed to read image for post={}", postId, e);
            throw new FileStorageException("Failed to read image", e);
        }
    }

    /**
     * Валидация файла
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        }
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException("Only image files allowed");
        }
    }

    /**
     * Извлечение и проверка расширения
     */
    private String extractAndValidateExtension(String originalFilename) {

        int extensionIndex = originalFilename.lastIndexOf(".");
        if (extensionIndex == -1) {
            throw new InvalidFileTypeException("File extension is missing");
        }

        String extension = originalFilename.substring(extensionIndex).toLowerCase();

        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileTypeException("Invalid image extension");
        }
        return extension;
    }

}

package ru.yandex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.yandex.exception.EmptyFileException;
import ru.yandex.exception.InvalidFileTypeException;
import ru.yandex.repository.FileStorageRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {
    @Mock
    private FileStorageRepository fileStorageRepository;

    @InjectMocks
    private FileStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service.uploadDir = tempDir.toString();
        service.allowedExtensions = Set.of(".jpg", ".png");
    }


    @Test
    void shouldThrowException_whenFileIsEmpty() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[0]);

        assertThrows(EmptyFileException.class, () -> service.saveImage(file));
    }

    @Test
    void shouldThrowException_whenInvalidContentType() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "text/plain", "abc".getBytes());

        assertThrows(InvalidFileTypeException.class, () -> service.saveImage(file));
    }

    @Test
    void shouldThrowException_whenNoExtension() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "test", "image/jpeg", "abc".getBytes());

        assertThrows(InvalidFileTypeException.class, () -> service.saveImage(file));
    }

    @Test
    void shouldThrowException_whenInvalidExtension() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.gif", "image/gif", "abc".getBytes());

        assertThrows(InvalidFileTypeException.class, () -> service.saveImage(file));
    }


    @Test
    void shouldSaveImageSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "image-data".getBytes()
        );

        String result = service.saveImage(file);

        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));

        Path savedFile = tempDir.resolve(result);
        assertTrue(Files.exists(savedFile));
    }


    @Test
    void shouldReturnNull_whenImageNotExists() {

        when(fileStorageRepository.getImagePathByPostId(1L)).thenReturn("missing.jpg");

        byte[] result = service.getPostImage(1L);

        assertNull(result);
    }

    @Test
    void shouldReturnImageBytes_whenFileExists() throws Exception {

        Path file = tempDir.resolve("img.jpg");
        Files.write(file, "data".getBytes());

        when(fileStorageRepository.getImagePathByPostId(1L)).thenReturn("img.jpg");

        byte[] result = service.getPostImage(1L);

        assertArrayEquals("data".getBytes(), result);
    }


    @Test
    void shouldCallRepository_whenUpdateImage() {

        service.updatePostImage(1L, "img.jpg");

        verify(fileStorageRepository).updatePostImage(1L, "img.jpg");
    }
}
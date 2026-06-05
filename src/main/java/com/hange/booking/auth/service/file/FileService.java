package com.hange.booking.auth.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorFileCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;
	@Value("${file.upload-base-path}")
	private String basePath;

	@Value("${file.public-base-url}")
	private String publicBaseUrl;

	@Value("${file.storage-mode:local}")
	private String storageMode;
	private final Cloudinary cloudinary;

	public void createUploadFolder(String folder) {
		Path path = Paths.get(basePath, folder);
		try {
			Files.createDirectories(path); // ✅ FIX CHÍNH Ở ĐÂY
			System.out.println(">>> CREATE DIRECTORY SUCCESS: " + folder);

		} catch (IOException e) {
			throw new AppRuntimeException(ErrorFileCode.CREATE_FOLDER_FAILED);
		}
	}

	public String store(MultipartFile file, String folder) {

		try {
			String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();

			String finalName = System.currentTimeMillis() + "-" + originalName;

			// LOCAL STORAGE
			if ("local".equalsIgnoreCase(storageMode)) {

				Path path = Paths.get(basePath, folder, finalName);
				Files.createDirectories(path.getParent());

				try (InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
				}

				return publicBaseUrl + "/" + folder + "/" + finalName;
			}

			if ("s3".equalsIgnoreCase(storageMode)) {
				return "https://s3-url/" + folder + "/" + finalName;
			}
			if ("cloudinary".equalsIgnoreCase(storageMode)) {

				Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
						ObjectUtils.asMap("folder", folder, "resource_type", "auto"));

				return uploadResult.get("secure_url").toString();
			}
			throw new AppRuntimeException(ErrorFileCode.FILE_UPLOAD_FAILED);

		} catch (IOException e) {
			throw new AppRuntimeException(ErrorFileCode.FILE_UPLOAD_FAILED);
		}
	}

	public void validateFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new AppRuntimeException(ErrorFileCode.FILE_EMPTY);
		}

		String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();

		if (!fileName.contains(".")) {
			throw new AppRuntimeException(ErrorFileCode.INVALID_FILE_NAME);
		}

		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "webp", "jpeg", "png", "doc", "docx");
		if (!allowedExtensions.contains(ext)) {
			throw new AppRuntimeException(ErrorFileCode.UNSUPPORTED_EXTENSION);
		}

		List<String> allowedMimeTypes = Arrays.asList("application/pdf", "image/webp", "image/jpeg", "image/png",
				"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

		String mimeType = file.getContentType();
		if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
			throw new AppRuntimeException(ErrorFileCode.UNSUPPORTED_MIME_TYPE);
		}

		if (file.getSize() > maxFileSize.toBytes()) {
			throw new AppRuntimeException(ErrorFileCode.FILE_SIZE_EXCEEDED);
		}
	}

	public InputStreamResource getResource(String fileName, String folder) {

		if (fileName == null || fileName.isBlank() || folder == null || folder.isBlank()) {
			throw new AppRuntimeException(ErrorFileCode.MISSING_REQUIRED_PARAMS);
		}
		Path path = Paths.get(basePath, folder, fileName);
		try {
			return new InputStreamResource(Files.newInputStream(path));
		} catch (IOException e) {
			throw new AppRuntimeException(ErrorFileCode.FILE_NOT_FOUND);
		}
	}
}
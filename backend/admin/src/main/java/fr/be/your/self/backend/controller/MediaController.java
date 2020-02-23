package fr.be.your.self.backend.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.MEDIA)
public class MediaController {
	
	@Autowired
	private DataSetting dataSetting;
	
	@GetMapping(Constants.PATH.WEB_ADMIN.MEDIA_TYPE.AVATAR + "/{fileName}")
    public String avatar(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model, 
    		@PathVariable(name="fileName") String fileName) {
		// Read image file and response
        return "user";
    }
	
	@GetMapping("/{dataType}/image/view/{fileName}")
    public String viewImage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@PathVariable(name="dataType") String dataType,
    		@PathVariable(name="fileName") String fileName) {
		
		final String mediaFileName = this.dataSetting.getUploadFolder() + dataType + "/" + fileName;
		final File mediaFile = new File(mediaFileName);
		
		if (!mediaFile.exists()) {
			return "/";
		}
		
		final Path mediaFilePath = Paths.get(mediaFileName);
		final String contentType = this.getFileContentType(mediaFilePath);
		
		try (FileInputStream mediaStream = new FileInputStream(mediaFile)) {
			response.setContentType(contentType);
		    IOUtils.copy(mediaStream, response.getOutputStream());
		} catch (Exception e) {
			return "/";
		}
		
		return null;
    }
	
	@GetMapping("/{dataType}/image/download/{fileName}")
    public String downloadImage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@PathVariable(name="dataType") String dataType,
    		@PathVariable(name="fileName") String fileName) {
		
		final String mediaFileName = this.dataSetting.getUploadFolder() + dataType + "/" + fileName;
		final File mediaFile = new File(mediaFileName);
		
		if (!mediaFile.exists()) {
			return "/";
		}
		
		final Path mediaFilePath = Paths.get(mediaFileName);
		final String contentType = this.getFileContentType(mediaFilePath);
		
		try (FileInputStream mediaStream = new FileInputStream(mediaFile)) {
			response.setContentType(contentType);
		    IOUtils.copy(mediaStream, response.getOutputStream());
		} catch (Exception e) {
			return "/";
		}
		
		return null;
    }
	
	@GetMapping("/{dataType}/media/view/{fileName}")
    public String mediaImage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@PathVariable(name="dataType") String dataType,
    		@PathVariable(name="fileName") String fileName) {
		
		final String mediaFileName = this.dataSetting.getUploadFolder() + dataType + "/" + fileName;
		final File mediaFile = new File(mediaFileName);
		
		if (!mediaFile.exists()) {
			return "/";
		}
		
		final Path mediaFilePath = Paths.get(mediaFileName);
		final String contentType = this.getFileContentType(mediaFilePath);
		
		try (FileInputStream mediaStream = new FileInputStream(mediaFile)) {
			response.setContentType(contentType);
		    IOUtils.copy(mediaStream, response.getOutputStream());
		} catch (Exception e) {
			return "/";
		}
		
		return null;
    }
	
	/*
	private String getImageMediaType(String fileName) {
		final int dotIndex = fileName.lastIndexOf(".");
        final String fileExtension = dotIndex > 0 ? (fileName.substring(dotIndex) + 1).toLowerCase() : "png";
        
		if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
			return MediaType.IMAGE_JPEG_VALUE;
		}
		
		return MediaType.IMAGE_PNG_VALUE;
	}
	*/
	
	private String getFileContentType(final Path filePath) {
		try {
			final String contentType = Files.probeContentType(filePath);
			
			if (contentType != null) 
				return contentType;
		} catch (IOException ex) {}
		
		final String fileName = filePath.getFileName().toString();
		final int dotIndex = fileName.lastIndexOf(".");
        final String fileExtension = dotIndex > 0 ? (fileName.substring(dotIndex) + 1).toLowerCase() : "png";
        
		if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
			return MediaType.IMAGE_JPEG_VALUE;
		}
		
		return MediaType.IMAGE_PNG_VALUE;
	}
}

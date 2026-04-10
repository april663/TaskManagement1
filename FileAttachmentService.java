/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TaskManagement1.Service;

import com.TaskManagement1.Cloud.StorageServiceImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.TaskManagement1.Entity.FileAttachment;
import com.TaskManagement1.Repository.AttachmentRepository;
import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class FileAttachmentService {
	@Autowired
	private AttachmentRepository attachmentRepo;
	
	
	@Autowired
	private StorageServiceImpl storageService;
	
	@Autowired
	private Cloudinary clodinary;
	

	public FileAttachment upload(Long issuesId,MultipartFile file,String uploadedBy) {
		
		vaidationFile(file);
		
		try {
			
			Map<String,Object>uploadOption= new HashMap<>();
			uploadOption.put("resouce_type", "auto");
			
			Map uploadResult= clodinary.uploader().upload(file.getBytes(),uploadOption);
			
			
			FileAttachment attachment= new FileAttachment();
			attachment.setIssueId(issuesId);
			attachment.setFileName(file.getOriginalFilename());
			attachment.setContentType(file.getContentType());
			attachment.setSizeBytes(file.getSize());
			attachment.setStoragePath(uploadResult.get("source_url").toString());
			attachment.setCloudId(uploadResult.get("cloud_Id").toString());
			attachment.setUploadedBy(uploadedBy);
			
			return attachmentRepo.save(attachment);
			
		} catch (Exception e) {
			throw new RuntimeException("File Upload Failed",e);
		}
		
		
	}
	
	private void vaidationFile(MultipartFile file) {
		
		if(file.isEmpty()) {
			throw new RuntimeException("file can not be empty");
		}
		
		long MAX= 10*1024*1024;
		
		if(file.getSize()>MAX) {
			throw new RuntimeException("MAX file size is 10MB");
		}
		
		List<String>allowedFile= Arrays.asList("image/png","image/jpeg","text/plain","application/pdf");
		
		if(!allowedFile.contains(file.getContentType())) {
			throw new RuntimeException("Invalid file Format");
		}
	}
	
	public List<FileAttachment>fileGetByIssueId(Long issueId){
		return attachmentRepo.findByIssueId(issueId);
	}
	public  FileAttachment fileGetByCloudId(String cloudId){
		return attachmentRepo.findByCloudId(cloudId).orElseThrow(()-> new RuntimeException("Cloud not found"));
	}
	public FileAttachment getFileById(Long id) {
		return attachmentRepo.findById(id).orElseThrow(()-> new RuntimeException("Attachment not found"));
	}
	
	public void deleteFile(Long id) {
		FileAttachment atch= attachmentRepo.findById(id).orElseThrow(()-> new RuntimeException("File not found"));
		
		try {
			Map<String,Object>option= new HashMap<>();
			option.put("resource_type", "auto");
			
			clodinary.uploader().destroy(atch.getCloudId(), option);
			attachmentRepo.delete(atch);
			
		} catch (Exception e) {
			throw new RuntimeException("Delete failed",e);
		}
	}

	
	
}
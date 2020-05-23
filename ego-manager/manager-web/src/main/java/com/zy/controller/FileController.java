package com.zy.controller;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping({"/admin/file"})
public class FileController
{
  @Autowired
  private FastFileStorageClient fastFileStorageClient;
  @Value("${resources.url}")
  private String serverAddress;

  @SneakyThrows
  @PostMapping({"/upload/element"})
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//      String filePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), "jpg", null).getFullPath();
    String filePath = null;
    try {
      filePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), "jpg", null).getFullPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok(serverAddress + "/" + filePath);
  }
}

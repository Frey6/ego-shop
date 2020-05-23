package com.zy.test;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FileUploadTest {
  @Autowired
  private FastFileStorageClient fileStorageClient;

  @SneakyThrows
  @Test
  public void fileTest(){
    File file = new File("D:/TIM/MobileFile/ดีแล้ว...ที่ทิ้งกัน - KT Long Flowing【 COVER vers.mp4");
    String jpg = fileStorageClient.uploadFile(new FileInputStream(file), file.length(), "mp4", new HashSet<>(0)).getFullPath();
    System.out.println(jpg);
  }
}

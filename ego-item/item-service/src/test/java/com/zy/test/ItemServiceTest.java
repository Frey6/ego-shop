package com.zy.test;

import com.zy.entity.Prod;
import com.zy.service.ProdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemServiceTest {

  @Autowired
  private ProdService prodService ;


  @Test
  public void testProdQuery(){
    List<Prod> list = prodService.list();
    for (Prod prod : list) {
      System.out.println(prod);
    }
  }

}

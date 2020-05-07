//package com.zy.controller;
//
//import org.apache.catalina.User;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class TestController {
//  @GetMapping("/test")
//  public  String test(){
//    return  "ok";
//  }
//  @PostMapping("/user")
//  public  String add(@RequestBody User user){
//    return  "ok";
//  }
//  @DeleteMapping("/user/{id}")
//  public  String delete(@PathVariable("id") Integer id){
//    return "ok";
//  }
//  @GetMapping("/head")
//  public String getValueForHead(@RequestHeader("token") String token){
//    return  "ok";
//  }
//
//  /**
//   * 成功，没有错误
//   * @return
//   */
//  @GetMapping("/ok")
//  public ResponseEntity<String> test1(){
////    return ResponseEntity.ok("ok");
//    return  ResponseEntity.status(HttpStatus.OK).body("Ok");
//  }
//
//  /**
//   * 未登录/没有权限
//   * 401表示未授权
//   * @return
//   */
//  @GetMapping("/nologin")
//  public ResponseEntity<String> noLogin(){
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO LOGIN");
//
//  }
//
//  /**
//   * 参数有问题400
//   * @return
//   */
//  @GetMapping("/params")
//  public ResponseEntity<String> params(){
////    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PARAMS ERRO");
//    return  ResponseEntity.badRequest().body("params erro");
//  }
//
//  /**
//   * 运行时异常500
//   * @return
//   */
//  @GetMapping("/exc")
//  public ResponseEntity<String> runExec(){
//    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
//  }
//  @RequiresPermissions(value = {"admin"})
//  @GetMapping("/admin")
//  public  ResponseEntity<String> admin(){
//    return  ResponseEntity.ok("admin");
//  }
//
//
//}

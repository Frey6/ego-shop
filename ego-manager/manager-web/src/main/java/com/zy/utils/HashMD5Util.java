package com.zy.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

public class HashMD5Util {


  /**
   * 给一个原始的字符串+ 散列次数= 加密后的字符串
   * @param originStr
   * @param count
   * @return
   */
  public static String getMd5(String originStr,int count){
    Md5Hash whsxt = new Md5Hash(originStr, "whsxt", count);
    return whsxt.toHex() ;
  }

  public static void main(String[] args) {
    String md5 = getMd5("123456", 2);
    System.out.println(md5);
  }
}


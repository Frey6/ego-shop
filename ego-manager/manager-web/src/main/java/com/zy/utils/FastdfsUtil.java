package com.zy.utils;

public class FastdfsUtil
{
  public static String parseGroup(String fullPath)
  {
    String[] paths = fullPath.split("/");
    if (paths.length <= 1) {
      throw new RuntimeException("参数错误，无法解析");
    }
    return paths[0];
  }
}


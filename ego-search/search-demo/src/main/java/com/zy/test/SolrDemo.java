package com.zy.test;

public class SolrDemo {
  public static void main(String[] args) {

    Integer integer = new Integer(0);
    int i = integer + 1;
    System.out.println(i);

    String pic="[\"http://47.101.176.118/g1/M00/00/01/rBPi511TeqGAdjQfAABcteVfoYk439.jpg\"]";
    System.out.println("i的值为"+pic);

    String substring = pic.substring(1, pic.length() - 1);
    System.out.println("分割后的string"+substring);

  }
}

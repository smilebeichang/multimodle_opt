package net.eyun.controller;


import java.util.Random;

/**
 * Created by songb
 */
public class testMath {
    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            int i2 = Math.abs(new Random().nextInt()) % 10 + 20;

            System.out.println("=== main ==="+i2);
        }


    }
}





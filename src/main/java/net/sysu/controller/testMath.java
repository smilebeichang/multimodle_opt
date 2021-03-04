package net.sysu.controller;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by songb
 */
public class testMath {
    public static void main(String[] args) {
        System.out.println("=== main ===");
        //随着random_selection_id的递增,random_selection[random_selection_id]逐渐变大
        int population_size = 4;


        //初始化容器
        double[] random_selection = new double[population_size];
        random_selection[0] = 0.110347;
        random_selection[1] = 0.450126;
        random_selection[2] = 0.572496;
        random_selection[3] = 0.98503;
//        for (int i = 0; i < population_size; i++) {
//            random_selection[i] = Math.random();
//            System.out.println(random_selection[i]);
//        }
//        //排序
//        Arrays.sort(random_selection);

        double[] pie_fitness = new double[4];
        pie_fitness[0] = 0.14;
        pie_fitness[1] = 0.49;
        pie_fitness[2] = 0.06;
        pie_fitness[3] = 0.31;

        for (int j = 0; j < 4; j++) {

            double sum = 0.0;
            for (int i = 0; i < pie_fitness.length; i++) {
                sum += pie_fitness[i];
                if (sum > random_selection[j]) {
                    System.out.println(i);
                }
            }
        }

    }




}





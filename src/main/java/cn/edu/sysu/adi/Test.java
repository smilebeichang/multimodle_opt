package cn.edu.sysu.adi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @Author : song bei chang
 * @create 2021/5/26 0:18
 */
public class Test {


    /**
     * 排序
     */
    @org.junit.Test
    public void sort(){
        int[] ints = {20,1,4,8,3};
        Arrays.sort(ints);
        for (int i = 0; i < ints.length; i++) {
            System.out.print(ints[i]+" ");
        }
    }

    /**
     * 临时方法 测试专用
     */
    @org.junit.Test
    public void sss(){

        for (int i = 0; i < 100 ; i++) {
            //10~20
            Integer key = Math.abs(new Random().nextInt()) % 20 + 10;
            System.out.println(key);
            //10~30
//            int j = new Random().nextInt(20) + 10;
//            System.out.println(j);
        }
    }


    @org.junit.Test
    public void exp(){
        double x = 11.635;
        double y = 2.76;

        System.out.printf("e 的值为 %.4f%n", Math.E);
        System.out.printf("exp(%.3f) 为 %.3f%n", x, Math.exp(x));
        // 2.7183 * 2.7183 = 7.389
        System.out.printf("exp(%.3f) 为 %.3f%n", 2.0, Math.exp(2.0));
        // - 表示取倒数  1/(2.7183 * 2.7183)  =  0.135
        System.out.printf("exp(%.3f) 为 %.3f%n", -0.26, Math.exp(-0.26));
        System.out.printf("exp(%.3f) 为 %.3f%n", -0.55, Math.exp(-0.55));
        System.out.printf("exp(%.3f) 为 %.3f%n", -0.95, Math.exp(-0.95));
    }


    @org.junit.Test
    public void test1() {
        Set<String> result = new HashSet<>();
        Set<String> set1 = new HashSet<String>() {
            {
                add("王者荣耀");
                add("英雄联盟");
                add("穿越火线");
                add("地下城与勇士");
            }
        };

        Set<String> set2 = new HashSet<String>() {
            {
                add("王者荣耀");
                add("地下城与勇士");
                add("魔兽世界");
            }
        };

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        System.out.println("交集：" + result);

    }
}




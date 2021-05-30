package cn.edu.sysu.adi;

import cn.edu.sysu.controller.ADIController;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author : song bei chang
 * @create 2021/5/26 0:18
 */
public class Test {


    @org.junit.Test
    public void random(){

        for (int i = 0; i < 10; i++) {

            double randomId = Math.random();
            //打印出随机抽取的random概率值
            System.out.println(randomId);
        }

    }



    @org.junit.Test
    public void arrAsList(){
        int[] arr = new int[4];
        arr[0] = 0;
        arr[1] = 1;
        arr[2] = 2;
        arr[3] = 3;

        ArrayList< String> arrayList = new ArrayList<String>(arr.length);

        for (int anArr : arr) {
            arrayList.add(anArr + "");
        }
        System.out.println(arrayList.toString());

    }





    @org.junit.Test
    public void stringBuilder(){
        StringBuilder sb = new StringBuilder();
        int ab2 = -1;
        if(ab2>0){
            sb.append(" p2=0 and ");
        }else if (ab2<0){
            sb.append(" p2=1 and ");
        }
        System.out.println( sb.toString());
    }

    @org.junit.Test
    public void remove(){
        /**
         * 根据原集合 旧解 新解 三者的关系进行，属性比例要求判断
         *
         */
        ArrayList<String> bachItemList = new ArrayList<>();
        bachItemList.add("w");
        bachItemList.add("s");
        bachItemList.add("x");
        String s  = "s";
        String s1 = "s1";


        // 刪除元素s
        for (int i = 0; i < bachItemList.size(); i++) {
            if (bachItemList.get(i).equals(s)){
                bachItemList.set(i,s1);
            }
        }
        System.out.println(bachItemList.toString());
        // 输出 [0, 1, 2, 3, 4, 5, 6, 7, 9]，



    }


    @org.junit.Test
    public void test() throws SQLException {
        String[] temp1 = new String[6];
        temp1[0] = "51:(0,0,1,0,1):0.0:0.0:0.0075:0.0:0.055000000000000035:0.0:0.0:0.0:0.0:0.0";
        temp1[1] = "73:(1,0,0,1,0):0.08000000000000002:0.0:0.0:0.022500000000000003:0.0:0.0:0.0:0.0:0.0:0.0";
        temp1[2] = "99:(0,0,1,0,1):0.0:0.0:0.032500000000000015:0.0:0.045000000000000026:0.0:0.0:0.0:0.0:0.0";
        temp1[3] = "173:(1,0,1,0,1):0.00125:0.0:0.03625000000000002:0.0:0.043750000000000025:0.0:0.0:0.0:0.0:0.0";
        temp1[4] = "193:(0,1,0,1,1):0.0:0.03500000000000002:0.0:0.0075:0.07625000000000001:0.0:0.0:0.0:0.0:0.0";
        temp1[5] = "284:(1,1,1,1,0):0.06625:0.019375000000000003:0.024375000000000008:0.024375000000000008:0.0:0.0:0.0:0.0:0.0:0.0";
        new ADIController().correct(1,temp1);
    }


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




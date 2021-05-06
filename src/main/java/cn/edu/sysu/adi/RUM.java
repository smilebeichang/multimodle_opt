package cn.edu.sysu.adi;

import org.junit.Test;

import java.util.ArrayList;

/**
 * @Author : song bei chang
 * @create 2021/4/24 19:40
 *          实现RUM的 attribute与 pattern 的响应
 */
public class RUM {

    @Test
    public  void main1() {
        System.out.println(" junit 测试 ");
    }

    @Test
    public  void main2() {
        System.out.println(" 计算 D* ");
        //计算的是(1,1) vs (0,1)
        double v = (0.8) * Math.log((0.8) / (0.1))+(0.2) * Math.log((0.2) / (0.9));
        System.out.println(v);
        //(1,0) vs (0,1)
        double v1 = (0.8) * Math.log((0.8) / (0.1))+(0.2) * Math.log((0.2) / (0.9));
        System.out.println(v1);
        //(0,1) vs (1,1)
        double v2 = (0.1) * Math.log((0.1) / (0.8))+(0.9) * Math.log((0.9) / (0.2));
        System.out.println(v2);
        //(0,0) vs (1,0)
        double v3 = (0.1) * Math.log((0.1) / (0.8))+(0.9) * Math.log((0.9) / (0.2));
        System.out.println(v3);

        //(1,1) vs (1,1)
        System.out.println((0.8) * Math.log((0.8) / (0.8))+(0.2) * Math.log((0.2) / (0.2)));
        //(1,1) vs (1,0)
        System.out.println((0.8) * Math.log((0.8) / (0.8))+(0.2) * Math.log((0.2) / (0.2)));
        //(0,1) vs (0,1)
        System.out.println((0.1) * Math.log((0.1) / (0.1))+(0.9) * Math.log((0.9) / (0.9)));
        //(0,1) vs (0,0)
        System.out.println((0.1) * Math.log((0.1) / (0.1))+(0.9) * Math.log((0.9) / (0.9)));



    }


    /**
     *  K_L 矩阵计算
     *  行列分别表示 （0,0）（0,1）（1,0）（1,1）
     *               0.1    0.1   0.8   0.8
     *  Dj 表示 K_L 矩阵
     *    1. 定义一维数组 和 二维数组
     *    2. for 计算并存储
     *    3. 遍历输出
     */
    @Test
    public void main3(){
        //Method 1
        ArrayList<Double> lists1 = new ArrayList<>();
        lists1.add(0.1);
        lists1.add(0.1);
        lists1.add(0.8);
        lists1.add(0.8);

        //Method 2 , (double brace initialization)
        ArrayList<Double> lists2 = new ArrayList<Double>(){{
            add(0.1);
            add(0.1);
            add(0.8);
            add(0.8);
        }};

        Double[][] klArray =new Double[4][4];
        for (int i = 0;i<lists1.size();i++){
            for (int j = 0; j < lists2.size(); j++) {
                //(0,0) vs (0,0)
                double v = lists1.get(i) * Math.log(lists1.get(i) / lists2.get(j)) +
                        (1 - lists1.get(i)) * Math.log((1 - lists1.get(i)) / (1 - lists2.get(j)));

                v = Double.valueOf((v+"0000").substring(0,4));
                klArray[i][j] = v;
            }
        }
        foreach(klArray);
    }


    public void main4(){
        System.out.println(" D(A)ij 计算 ");
        // D(A)ij 表示 i vs j
    }

    //使用foreach方法对二维数组进行遍历，foreach一般和for循环差不多，不过foreach看着简单些。
    private  void foreach( Double [][] arr ) {
        System.out.println(" K_L 矩阵: ");
        for (Double[] fs:arr) {
            for (Double fss:fs) {
                System.out.print(fss+"  ");//相当于arr[i][j]
            }
            System.out.println();
        }
        System.out.println("====================");
    }






}




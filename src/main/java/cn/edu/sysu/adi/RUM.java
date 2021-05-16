package cn.edu.sysu.adi;

import cn.edu.sysu.utils.KLUtils;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @Author : song bei chang
 * @create 2021/4/24 19:40
 *          实现RUM的 attribute与 pattern 的响应之间的关系
 *          构成 K_L information 矩阵
 */
public class RUM {

    @Test
    public  void main1() {
        System.out.println(" junit 测试 ");
    }

    /**
     * Reduce-RUM 统一参数化模型  的实现
     *
     * double que_fit = πj * ( (rjk* * γ * β) .... );
     *
     * p* 是正确应用第j项所有必要属性的概率，
     * πj* 为项目难度参数，表示被试i掌握item j 所需要的全部属性，正确作答item j的概率，其值越大（接近1）表示被试掌握了所需属性很可能成功应答。
     * rjk*表示被试缺乏属性K,答对item j vs 被试掌握属性k答对j的概率比。其值越小（接近0）,表示掌握属性K很重要。rjk*也被称做属性K在itemj上的区分度参数。
     * super high   high      low
     * [0.05,0.2]  [0.4,0.85] [0.6,0.92]
     *
     * eg:0.8   0.125    (1,0)
     *      P(0,0)(1,0)=0.8 * (1*0.125) = 0.1
     *      P(0,1)(1,0)=0.8 * (1*0.125) = 0.1
     *      P(1,0)(1,0)=0.8 * (1*1) = 0.8
     *      P(1,1)(1,0)=0.8 * (1*1) = 0.8
     */
    @Test
    public  void main2() {
        System.out.println(" 计算 D* ");
        //计算的是(1,1) vs (0,1)
        double v = (0.8) * Math.log((0.8) / (0.1))+(0.2) * Math.log((0.2) / (0.9));
        System.out.println(v);

        //随机生成题库,每道试题的 πj*   rjk*  pattern
        //计算K_L information
        //计算adi



    }


    /**
     *  K_L 矩阵计算
     *  行列分别表示 （0,0）（0,1）（1,0）（1,1）
     *               0.1    0.1   0.8   0.8
     *  Dj 表示 K_L 矩阵
     *    1. 定义一维数组 和 二维数组  The probability of a correct response
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

        new KLUtils().foreach(lists1,lists2);
    }


    public void main4(){
        //TODO  暂时未实现  D(A)ij 表示 i vs j
        System.out.println(" D(A)ij 计算 ");

    }




}




package cn.edu.sysu.adi;

import cn.edu.sysu.utils.KLUtils;
import org.junit.Test;
import sun.print.SunMinMaxPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : song bei chang
 * @create 2021/4/24 19:40
 *          理解RUM的 attribute 与 pattern 的响应之间的关系
 *          构成 K_L information 矩阵
 */
public class RUM {

    @Test
    public  void TestMath() {

        System.out.println(" junit 测试 ");

        System.out.println(" 计算 D* ");
        //计算的是(0,0) vs (0,0)
        double v = (0.1) * Math.log((0.1) / (0.1))+(0.9) * Math.log((0.9) / (0.9));
        System.out.println(v);

        //new KLUtils().Combin();


    }

    /**
     * Reduce-RUM 统一参数化模型  的实现
     *
     * double que_fit = πj * ( (rjk* * γ * β) .... );
     *
     * p* 是正确应用第j项所有必要属性的概率，
     * πj* 为项目难度参数，表示被试i掌握item j 所需要的全部属性，正确作答item j的概率，其值越大（接近1）表示被试掌握了所需属性很可能成功应答。  每道题一个固定值
     * rjk*表示被试缺乏属性K,答对item j vs 被试掌握属性k答对j的概率比。其值越小（接近0）,表示掌握属性K很重要。rjk*也被称做属性K在itemj上的区分度参数。  每道题的一个考察属性的pattern
     * super high   high      low
     * [0.05,0.2]  [0.4,0.85] [0.6,0.92]
     *
     * eg:0.8   0.125    (1,0)
     *      P(0,0)(1,0)=0.8 * (0.125^1) * 1 = 0.1
     *      P(0,1)(1,0)=0.8 * (0.125^1) * 1 = 0.1
     *      P(1,0)(1,0)=0.8 * (1) * 1 = 0.8
     *      P(1,1)(1,0)=0.8 * (1) * 1 = 0.8
     *
     *          *         (0,0,0) (0,0,1) (0,1,0) (1,0,0) (0,1,1) (1,0,1) (1,1,0)  (1,1,1)
     *          * (0,0,0)  0.8
     *          * (0,0,1)  0.1
     *          * (0,1,0)  0.1
     *          * (1,0,0)  0.1
     *          * (0,1,1)  0.0125
     *          * (1,0,1)  0.0125
     *          * (1,1,0)  0.0125
     *          * (1,1,1)  0.0015625
     *          *
     *
     *         //错误
     *         //P(0,0,0)(0,0,0) = 0.8 * ( 1 * 1 *1 ) = 0.8
     *         //P(0,0,0)(0,0,1) = 0.8 * ( 1 * 1 * 0.125 ) = 0.1
     *         //P(0,0,0)(0,1,0) = 0.8 * ( 1 * 0.125 * 1 ) = 0.1
     *         //P(0,0,0)(1,0,0) = 0.8 * ( 0.125 * 1 * 1 ) = 0.1
     *         //P(0,0,0)(0,1,1) = 0.8 * ( 1 * 0.125 * 0.125 ) = 0.0125
     *         //P(0,0,0)(1,0,1) = 0.8 * ( 0.125 * 1 * 0.125 ) = 0.0125
     *         //P(0,0,0)(1,1,0) = 0.8 * ( 0.125 * 0.125 * 1 ) = 0.0125
     *         //P(0,0,0)(1,1,1) = 0.8 * ( 0.125 * 0.125 * 0.125 ) = 0.0015625
     *
     *
     *         //随机生成题库,每道试题的 0.86 πj*   （0.1,0,0,0.25,0）rjk*  pattern（1,0,0,1,0）
     *         //需要后期将adi完善
     *
     *
     *         //计算单道题的daj3=(daj30+daj31)/2
     *         //计算试卷的 adi1  adi2  adi3  adi4  adi5
     *
     */
    @Test
    public  void GetAdi() {

        //计算K_L information
        //学生的掌握patter vs  题目考察pattern  固定的
        //0.8 πj*   0.125 rjk*

        //试题pattern(0,0,0)
        //P(0,0,0)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(0,0,1)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(0,1,0)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,0,0)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(0,1,1)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,0,1)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,1,0)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,1,1)(0,0,0) = 0.8 * (1 * 1 * 1 ) = 0.8


        //试题pattern(1,0,0)
        //P(0,0,0)(1,0,0) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(0,0,1)(1,0,0) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(0,1,0)(1,0,0) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(1,0,0)(1,0,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8
        //P(0,1,1)(1,0,0) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(1,0,1)(1,0,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8
        //P(1,1,0)(1,0,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8
        //P(1,1,1)(1,0,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8

        //试题pattern(0,1,0)
        //P(0,0,0)(0,1,0) = 0.8 * (1 * 0.125^1  * 1 ) = 0.1
        //P(0,0,1)(0,1,0) = 0.8 * (1 * 0.125^1  * 1 ) = 0.1
        //P(0,1,0)(0,1,0) = 0.8 * (1 * 1  * 1 ) = 0.8
        //P(1,0,0)(0,1,0) = 0.8 * (1 * 0.125^1  * 1 ) = 0.1
        //P(0,1,1)(0,1,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,0,1)(0,1,0) = 0.8 * (1 * 0.125^1  * 1 ) = 0.1
        //P(1,1,0)(0,1,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8
        //P(1,1,1)(0,1,0) = 0.8 * ( 1 * 1 * 1 ) = 0.8

        //试题pattern(0,0,1)
        //P(0,0,0)(0,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(0,0,1)(0,0,1) = 0.8 * (1 * 1  * 1 ) = 0.8
        //P(0,1,0)(0,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,0,0)(0,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(0,1,1)(0,0,1) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,0,1)(0,0,1) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,1,0)(0,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,1,1)(0,0,1) = 0.8 * ( 1 * 1 * 1 ) = 0.8

        //试题pattern(0,1,1)
        //P(0,0,0)(0,1,1) = 0.8 * (1 * 0.125^1 * 0.125^1 ) = 0.0125
        //P(0,0,1)(0,1,1) = 0.8 * (1 * 0.125^1  * 1 ) = 0.1
        //P(0,1,0)(0,1,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,0,0)(0,1,1) = 0.8 * (1 * 0.125^1 * 0.125^1 ) = 0.0125
        //P(0,1,1)(0,1,1) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,0,1)(0,1,1) = 0.8 * (1 * 0.125^1 * 1 ) = 0.1
        //P(1,1,0)(0,1,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,1,1)(0,1,1) = 0.8 * ( 1 * 1 * 1 ) = 0.8

        //试题pattern(1,0,1)
        //P(0,0,0)(1,0,1) = 0.8 * (0.125^1 * 1 * 0.125^1  ) = 0.0125
        //P(0,0,1)(1,0,1) = 0.8 * (0.125^1 * 1  * 1 ) = 0.1
        //P(0,1,0)(1,0,1) = 0.8 * (0.125^1 * 1 * 0.125^1  ) = 0.0125
        //P(1,0,0)(1,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(0,1,1)(1,0,1) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(1,0,1)(1,0,1) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,1,0)(1,0,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,1,1)(1,0,1) = 0.8 * ( 1 * 1 * 1 ) = 0.8


        //试题pattern(1,1,0)
        //P(0,0,0)(1,1,0) = 0.8 * (0.125^1 * 0.125^1 * 1 ) = 0.0125
        //P(0,0,1)(1,1,0) = 0.8 * (0.125^1 * 0.125^1  * 1 ) = 0.125
        //P(0,1,0)(1,1,0) = 0.8 * (0.125^1 * 1 * 1  ) = 0.1
        //P(1,0,0)(1,1,0) = 0.8 * (1 * 0.125^1 * 1 ) = 0.1
        //P(0,1,1)(1,1,0) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(1,0,1)(1,1,0) = 0.8 * (1 * 0.125^1 * 1 ) = 0.1
        //P(1,1,0)(1,1,0) = 0.8 * (1 * 1 * 1 ) = 0.8
        //P(1,1,1)(1,1,0) = 0.8 * (1 * 1 * 1 ) = 0.8

        //试题pattern(1,1,1)
        //P(0,0,0)(1,1,1) = 0.8 * (0.125^1 * 0.125^1 * 0.125^1 ) = 0.0015625
        //P(0,0,1)(1,1,1) = 0.8 * (0.125^1 * 0.125^1  * 1 ) = 0.125
        //P(0,1,0)(1,1,1) = 0.8 * (0.125^1 * 1 * 0.125^1  ) = 0.125
        //P(1,0,0)(1,1,1) = 0.8 * (1 * 0.125^1 * 0.125^1 ) = 0.125
        //P(0,1,1)(1,1,1) = 0.8 * (0.125^1 * 1 * 1 ) = 0.1
        //P(1,0,1)(1,1,1) = 0.8 * (1 * 0.125^1 * 1 ) = 0.1
        //P(1,1,0)(1,1,1) = 0.8 * (1 * 1 * 0.125^1 ) = 0.1
        //P(1,1,1)(1,1,1) = 0.8 * (1 * 1 * 1 ) = 0.8

        /*
         * K_L information矩阵如下: 每道题
         * 0.0  0.0  0.0  1.14  0.0  1.14  1.14  1.14
         * 0.0  0.0  0.0  1.14  0.0  1.14  1.14  1.14
         * 0.0  0.0  0.0  1.14  0.0  1.14  1.14  1.14
         * 1.36  1.36  1.36  0.0  1.36  0.0  0.0  0.0
         * 0.0  0.0  0.0  1.14  0.0  1.14  1.14  1.14
         * 1.36  1.36  1.36  0.0  1.36  0.0  0.0  0.0
         * 1.36  1.36  1.36  0.0  1.36  0.0  0.0  0.0
         * 1.36  1.36  1.36  0.0  1.36  0.0  0.0  0.0
         *
         */
//以试题pattern(1,0)为单位,这样才能算出一个矩阵 rum，然后根据此矩阵，求出该试题的矩阵 k_L，然后求出该试题的矩阵 Da，最后平均求出该试题的 adi
        Map<String,Integer> map = new HashMap<>(8);
        map.put("(0 0 0)",1);
        map.put("(0 0 1)",2);
        map.put("(0 1 0)",3);
        map.put("(1 0 0)",4);
        map.put("(0 1 1)",5);
        map.put("(1 0 1)",6);
        map.put("(1 1 0)",7);
        map.put("(1 1 1)",8);

        //试题pattern(1,0,0)
        ArrayList<Double> lists1 = new ArrayList<Double>(){{
            //pattern1(1,0,0)
//            add(0.1);
//            add(0.1);
//            add(0.1);
//            add(0.8);
//            add(0.1);
//            add(0.8);
//            add(0.8);
//            add(0.8);
            //pattern1(0,1,0)
            add(0.1);
            add(0.1);
            add(0.8);
            add(0.1);
            add(0.8);
            add(0.1);
            add(0.8);
            add(0.8);
        }};

        Double[][] klArray = new KLUtils().foreach(lists1, lists1);
        new KLUtils().arrayPrint(klArray);

        //捋思路 计算 adi1
        //adi1 = D(A)jk1 +  D(A)jk0;
        // D(A)jk1 (1,X,Y) to (0,X,Y)  ;          D(A)jk0  (0,X,Y) to (1,X,Y)
        // (1,0,0)  (1,0,1)  (1,1,0)  (1,1,1)         (0,0,0)   (0,0,1)   (0,1,0)  (0,1,1)
        // (0,0,0)  (0,0,1)  (0,1,0)  (0,1,1)         (1,0,0)   (1,0,1)   (1,1,0)  (1,1,1)
        //  D41      D62      D73      D85             D14       D26       D37      D58
        // [41, 14, 62, 26, 73, 37, 85, 58]

        /*
         * 两个元素，遍历组合即可, 随机遍历顺序没影响
         */
        ArrayList<String> combineList = new ArrayList<>();
        for(int X=0;X<2; X++){
            for(int Y=0;Y<2; Y++) {
                String value = X +" "+ Y;
                combineList.add(value);
            }
        }


        //获取下标
        ArrayList<String> list1 = new ArrayList();
        ArrayList<String> list2 = new ArrayList();
        ArrayList<String> list3 = new ArrayList();
        Double sum1 = 0.0;
        Double sum2 = 0.0;
        Double sum3 = 0.0;
        Double adi1 ;
        Double adi2 ;
        Double adi3 ;

        for (int X =0;X<combineList.size();X++){
            //adi1
            Integer index11 = map.get("(1 " + combineList.get(X) + ")");
            Integer index12 = map.get("(0 " + combineList.get(X) + ")");
            Integer index13 = map.get("(0 " + combineList.get(X) + ")");
            Integer index14 = map.get("(1 " + combineList.get(X) + ")");


            list1.add(""+index11+index12);
            list1.add(""+index13+index14);

            //adi2
            Integer index21 = map.get("(" +combineList.get(X).substring(0,1)+ " 1 " +combineList.get(X).substring(2,3) + ")");
            Integer index22 = map.get("(" +combineList.get(X).substring(0,1)+ " 0 " +combineList.get(X).substring(2,3) + ")");
            Integer index23 = map.get("(" +combineList.get(X).substring(0,1)+ " 0 " +combineList.get(X).substring(2,3) + ")");
            Integer index24 = map.get("(" +combineList.get(X).substring(0,1)+ " 1 " +combineList.get(X).substring(2,3) + ")");


            list2.add(""+index21+index22);
            list2.add(""+index23+index24);

            //adi3
            Integer index31 = map.get("("+combineList.get(X)+" 1)");
            Integer index32 = map.get("("+combineList.get(X)+" 0)");
            Integer index33 = map.get("("+combineList.get(X)+" 0)");
            Integer index34 = map.get("("+combineList.get(X)+" 1)");


            list3.add(""+index31+index32);
            list3.add(""+index33+index34);


        }
        System.out.println(list1);
        System.out.println(list2);
        System.out.println(list3);

        System.out.println("list 遍历 ;拿list的值去Array中匹配寻找,然后输出其大小");
        for(String data  :    list1)    {
            System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            System.out.println("  "+v);
            sum1+=v;
        }
        adi1=sum1/8;
        System.out.println("adi1: "+adi1);

        for(String data  :    list2)    {
            System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            System.out.println("  "+v);
            sum2+=v;
        }
        adi2=sum2/8;
        System.out.println("adi2: "+adi2);

        for(String data  :    list3)    {
            System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            System.out.println("  "+v);
            sum3+=v;
        }
        adi3=sum3/8;
        System.out.println("adi3: "+adi3);
    }




    //TODO  暂时未实现  D(A)ij 表示 i vs j
    /*
     *  竖 vs 横
     *  Todo d(A)jk1  j表示Item K表示attribute  1表示掌握
     *  K_L information矩阵如下:
     *      0.0  0.0  1.14  1.14
     *      0.0  0.0  1.14  1.14
     *      1.36  1.36  0.0  0.0
     *      1.36  1.36  0.0  0.0
     *  则d(A)j11 = ((1 0)(0 0) + (1 1)(0 1))/2 =  (D42 + D31)/2 = (1.36 + 1.36)/2 = 1.36
     *   d(A)j10 = ((0 1)(1 1) + (0 0)(1 0))/2 =  (D24 + D13)/2 = (1.14 + 1.14)/2 = 1.14
     *   d(A)j21 = ((1 1)(1 0) + (0 1)(0 0))/2 =  (D43 + D21)/2 = (0 + 0)/2 = 0
     *   d(A)j20 = ((0 0)(0 1) +(1 0)(1 1))/2 =   (D12 + D34)/2 = (0 + 0)/2 = 0
     *
     *   d(A)1 = (1.36 + 1.14)/2 = 1.25
     *   d(A)2 = (0 + 0)/2 = 0
     */

    @Test
    public void GetRumAdi(){

        System.out.println("D(A)ij 计算 ");

        //d(A)j11 = 辨别第一个属性,(1 X) to (0 X)
        //d(A)j10 = 辨别第一个属性,(0 X) to (1 X)

        //X 可以是0和1 ,故(1 0)to(0 0),(1 1)to(0 1) ==》 D31 ,D42
        //根据value 求下标 ==》 可以定义一个map("(00)",1),然后用key求value

        Map<String,Integer> map = new HashMap<>(4);
        map.put("(0 0)",1);
        map.put("(0 1)",2);
        map.put("(1 0)",3);
        map.put("(1 1)",4);

        Double[][] klArray = GetRumKLArray();

        new KLUtils().arrayPrint(klArray);

        //d(A)j11 辨别第一个属性，且固定为(1 X) to (0 X)
        System.out.println("获取下标");
        ArrayList<String> list1 = new ArrayList();
        ArrayList<String> list2 = new ArrayList();
        Double sum1 = 0.0;
        Double sum2 = 0.0;
        Double adi1 ;
        Double adi2 ;


        for (int X =0;X<2;X++){
            //adi1
            Integer index1 = map.get("(1 " + X + ")");
            Integer index2 = map.get("(0 " + X + ")");
            Integer index3 = map.get("(0 " + X + ")");
            Integer index4 = map.get("(1 " + X + ")");

            //adi2
            Integer index5 = map.get("(" + X + " 1)");
            Integer index6 = map.get("(" + X + " 0)");
            Integer index7 = map.get("(" + X + " 0)");
            Integer index8 = map.get("(" + X + " 1)");

            System.out.println("D"+index1+index2);
            System.out.println("D"+index3+index4);
            list1.add(""+index1+index2);
            list1.add(""+index3+index4);

            System.out.println("D"+index5+index6);
            System.out.println("D"+index7+index8);
            list2.add(""+index5+index6);
            list2.add(""+index7+index8);
        }


        System.out.println("list 遍历 ;拿list的值去Array中匹配寻找,然后输出其大小");
        for(String data  :    list1)    {
            System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            System.out.println("  "+v);
            sum1+=v;
        }
        adi1=sum1/4;

        for(String data  :    list2)    {
            System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            System.out.println("  "+v);
            sum2+=v;
        }
        adi2=sum2/4;

        System.out.println("ADI结果如下:");
        System.out.println("adi1: "+adi1);
        System.out.println("adi2: "+adi2);

    }



    /*
     * eg:0.8   0.125    (1,0)
     *      //(0,0)
     *      P(0,0)(0,0)=0.8 * (1) * 1 = 0.8
     *      P(0,1)(0,0)=0.8 * (1) * 1 = 0.8
     *      P(1,0)(0,0)=0.8 * (1) * 1 = 0.8
     *      P(1,0)(0,0)=0.8 * (1) * 1 = 0.8
     *
     *
     *      P(0,0)(1,0)=0.8 * (0.125^1) * 1 = 0.1
     *      P(0,1)(1,0)=0.8 * (0.125^1) * 1 = 0.1
     *      P(1,0)(1,0)=0.8 * (1) * 1 = 0.8
     *      P(1,1)(1,0)=0.8 * (1) * 1 = 0.8
     *
     */

    /*  K_L information 和 rum 的区别
     *   hql 大厂急缺人   (1,0)
     *         (0,0)  (0,1)  (1,0)  (1,1)
     *  (0,0)
     *  (0,1)
     *  (1,0)
     *  (1,1)
     *
     */
    /*
     * K_L information矩阵如下:
     * 0.0  0.0  1.14  1.14
     * 0.0  0.0  1.14  1.14
     * 1.36  1.36  0.0  0.0
     * 1.36  1.36  0.0  0.0
     */

    /**
     *  K_L 矩阵计算
     *  行列分别表示 （0,0）（0,1）（1,0）（1,1）
     *               0.1    0.1   0.8   0.8
     *  Dj 表示 K_L 矩阵
     *    1. 定义一维数组 和 二维数组  The probability of a correct response
     *    2. for 计算并存储
     *    3. 遍历输出
     */
    public Double[][] GetRumKLArray(){
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

        Double[][] klArray = new KLUtils().foreach(lists1, lists2);

        new KLUtils().arrayPrint(klArray);

        return klArray;
    }




}




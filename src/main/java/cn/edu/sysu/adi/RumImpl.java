package cn.edu.sysu.adi;

import cn.edu.sysu.utils.KLUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : song bei chang
 * @create 2021/5/18 19:34
 *
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
 *
 *          *         (0,0,0)(0,0,1)(0,1,0)(1,0,0)(0,1,1)(1,0,1)(1,1,0)(1,1,1)
 *          * (0,0,0)
 *          * (0,0,1)
 *          * (0,1,0)
 *          * (1,0,0)
 *          * (0,1,1)
 *          * (1,0,1)
 *          * (1,1,0)
 *          * (1,1,1)
 *          *
 *
 */
public class RumImpl {

    private int id ;
    private String pattern ;
    private Double base ;
    private String penalty ;
    private Double adi1;
    private Double adi2;
    private Double adi3;


    //1. 实现一个方法 通过base 和 penalty来获取rum  否则题目太相似
    //2. 上层返回ArrayList<Double> lists
    //3. 根据list=》获取klArray,map=》获取index,拿index和klArray获取对应的值,并计算出adi1 adi2 adi3
    //4. 用list集合将上一步获取的adi1 adi2 adi3,保存到全局变量。  故一道试题pattern对应三个adi
    //5. 生成单道题的属性值：id  base概率  pattern  penalty  adi   共7个字段
    //6. 生成题库 要求比例均衡

    //存在问题： 惩罚系数 目前未实现,系数比例精细化

    @Test
    public  void TestMath() throws InterruptedException {

        //捋思路
        //3:3:1 假设题库50道题  则21：21:8
        for (int i = 1; i <= 3; i++) {
            id = i;
            start(1);
        }
        for (int i = 4; i <= 6; i++) {
            id = i;
            start(2);
        }
        for (int i = 7; i <= 7; i++) {
            id = i;
            start(3);
        }


    }

    public void start(int num) throws InterruptedException {
        pattern = new KLUtils().RandomInit(num);
        GetAdi(pattern);

        System.out.printf("id=%s \t pattern=%s \t base=%s \t penalty=%s \t adi1=%s \t adi2=%s \t adi3=%s \n", id, pattern, base,penalty,adi1,adi2,adi3);
    }




    public  void GetAdi(String ip) {

//以试题pattern(1,0,0)为单位,这样才能算出一个矩阵 rum，然后求出该试题的矩阵 k_L，然后求出该试题的矩阵 Da，最后平均求出该试题的 adi
        //index map
        Map<String,Integer> map = new HashMap<>(8);
        map.put("(0,0,0)",1);
        map.put("(0,0,1)",2);
        map.put("(0,1,0)",3);
        map.put("(1,0,0)",4);
        map.put("(0,1,1)",5);
        map.put("(1,0,1)",6);
        map.put("(1,1,0)",7);
        map.put("(1,1,1)",8);

        //试题pattern(1,0,0)
//        ArrayList<ArrayList<Double>> patternLists = GetPatternListsRandom();
//        ArrayList<Double> lists1 = patternLists.get(1);
        //为什么要获取 该题目各个pattern的8个rum （考虑全部的pattern,只是为了获取各个pattern的adi1 adi2 a）
        //new KLUtils().makeRandom(0.95f, 0.75f, 2)

        //捋思路  前33次 0.05~0.2  34~66 0.4~0.85 67~ 0.6~0.92
        //试题的patern 和 penalty 个数相关   故先随机生成pattern,同时生成 penalty


        base = new KLUtils().makeRandom(0.95f, 0.75f, 2);
//        System.out.println("base: "+ base);

        ArrayList<Double> rumList = GetRumListsRandom(base,ip);

        //基线系数 和 惩罚系数 随机生成,故 rumList 也将随机
        //根据 rumList 计算出K_L二维数组
        Double[][] klArray = new KLUtils().foreach(rumList, rumList);
        //打印
//        new KLUtils().arrayPrint(klArray);


        /*
         * 两个元素，遍历组合即可, 随机遍历顺序没影响
         * combineList 全局变量,保存另外几个属性的选取方案
         */
        ArrayList<String> combineList = new ArrayList<>();
        for(int X=0;X<2; X++){
            for(int Y=0;Y<2; Y++) {
                String value = X +","+ Y;
                combineList.add(value);
            }
        }

        //获取下标
        ArrayList<String> list1 = new ArrayList();
        ArrayList<String> list2 = new ArrayList();
        ArrayList<String> list3 = new ArrayList();


        for (int X =0;X<combineList.size();X++){
            //adi1
            Integer index11 = map.get("(1," + combineList.get(X) + ")");
            Integer index12 = map.get("(0," + combineList.get(X) + ")");
            Integer index13 = map.get("(0," + combineList.get(X) + ")");
            Integer index14 = map.get("(1," + combineList.get(X) + ")");

            list1.add(""+index11+index12);
            list1.add(""+index13+index14);

            //adi2
            Integer index21 = map.get("(" +combineList.get(X).substring(0,1)+ ",1," +combineList.get(X).substring(2,3) + ")");
            Integer index22 = map.get("(" +combineList.get(X).substring(0,1)+ ",0," +combineList.get(X).substring(2,3) + ")");
            Integer index23 = map.get("(" +combineList.get(X).substring(0,1)+ ",0," +combineList.get(X).substring(2,3) + ")");
            Integer index24 = map.get("(" +combineList.get(X).substring(0,1)+ ",1," +combineList.get(X).substring(2,3) + ")");


            list2.add(""+index21+index22);
            list2.add(""+index23+index24);

            //adi3
            Integer index31 = map.get("("+combineList.get(X)+",1)");
            Integer index32 = map.get("("+combineList.get(X)+",0)");
            Integer index33 = map.get("("+combineList.get(X)+",0)");
            Integer index34 = map.get("("+combineList.get(X)+",1)");


            list3.add(""+index31+index32);
            list3.add(""+index33+index34);


        }
//        System.out.println("adi的计算指标：");
//        System.out.println("adi1: "+list1);
//        System.out.println("adi2: "+list2);
//        System.out.println("adi3: "+list3);

//        System.out.println("list 遍历 ;拿list的值去Array中匹配寻找,然后输出其大小：");
        List<Double> calAdiList = CalAdiImple(klArray, list1, list2, list3);
        System.out.println(calAdiList);


    }


    /**
     * 返回 rum 的 集合 ArrayList<ArrayList<Double>>
     * @param base  基线系数
     * @param ip 题目的属性pattern
     */
    public ArrayList<Double> GetRumListsRandom(Double base,String ip){

        ArrayList<Double> rumLists = new ArrayList<>();

        //考生_pattern sps
        ArrayList<String> sps = new ArrayList<String>(){{
            add("(0,0,0)");
            add("(0,0,1)");
            add("(0,1,0)");
            add("(1,0,0)");
            add("(0,1,1)");
            add("(1,0,1)");
            add("(1,1,0)");
            add("(1,1,1)");
        }};

        //题目pattern
        int a1 = Integer.parseInt(ip.substring(1, 2));
        int a2 = Integer.parseInt(ip.substring(3, 4));
        int a3 = Integer.parseInt(ip.substring(5, 6));

        Double penalty1 = a1 == 1? new KLUtils().makeRandom(0.95f, 0.05f, 2):0;
        Double penalty2 = a2 == 1? new KLUtils().makeRandom(0.95f, 0.05f, 2):0;
        Double penalty3 = a3 == 1? new KLUtils().makeRandom(0.95f, 0.05f, 2):0;
        penalty = "("+penalty1+","+penalty2+","+penalty3+")";
        //根据学生pattern vs 题目pattern 获取答对此题的rum
        for (String ps : sps) {
            //学生pattern
            int b1 = Integer.parseInt(ps.substring(1, 2));
            int b2 = Integer.parseInt(ps.substring(3, 4));
            int b3 = Integer.parseInt(ps.substring(5, 6));

            //a>=b则*1, a<b 则*penalty^1
            boolean ab1 = b1 >= a1;
            boolean ab2 = b2 >= a2;
            boolean ab3 = b3 >= a3;
            int num1 = ab1?0:1;
            int num2 = ab2?0:1;
            int num3 = ab3?0:1;

//            System.out.printf("ip=%s \t base=%s \t penalty1=%s \t penalty2=%s \t penalty3=%s \n", ip, base,penalty1,penalty2,penalty3);

            double p = base * Math.pow(penalty1, num1) * Math.pow(penalty2, num2) * Math.pow(penalty3, num3) ;

            rumLists.add(p);
        }
        return  rumLists;

    }




    /**
     * 计算adi具体实现
     */
    public List<Double> CalAdiImple(Double[][] klArray,ArrayList<String> list1,ArrayList<String> list2,ArrayList<String> list3){
        Double sum1 = 0.0;
        Double sum2 = 0.0;
        Double sum3 = 0.0;

        for(String data  :    list1)    {
            //System.out.print(data);
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            //System.out.println("  "+v);
            sum1+=v;
        }
        adi1=sum1/8;
//        System.out.println("adi1: "+adi1);

        for(String data  :    list2)    {
            //System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            //System.out.println("  "+v);
            sum2+=v;
        }
        adi2=sum2/8;
//        System.out.println("adi2: "+adi2);

        for(String data  :    list3)    {
            //System.out.print(data);
            Double v  = klArray[Integer.parseInt(data.substring(0,1))-1][Integer.parseInt(data.substring(1,2))-1];
            //System.out.println("  "+v);
            sum3+=v;
        }
        adi3=sum3/8;
//        System.out.println("adi3: "+adi3);

        List<Double> adiList = new ArrayList<Double>(){{
            add(adi1);
            add(adi2);
            add(adi3);
        }};
        return adiList;

    }


}




package cn.edu.sysu.adi;

import cn.edu.sysu.pojo.ADI;
import cn.edu.sysu.pojo.Questions;
import cn.edu.sysu.utils.JDBCUtils;
import cn.edu.sysu.utils.KLUtils;
import org.junit.Test;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : song bei chang
 * @create 2021/5/1 23:28
 *
 *      DINA 模 型 ( Deterministic Inputs，Noisy“And”gate model)
 *      符号	     描述
 *      X 	    学生试题得分矩阵
 *      Xij     学生i在试题j上的得分
 *      Q 	    知识点考察矩阵
 *      qjk     试题j对知识点k的考察情况
 *      αi      学生i的知识点掌握情况
 *      αik     学生i对知识点k的掌握情况
 *      ηij     学生i在试题j的潜在作答情况
 */
public class DINA {


    /**
     * 定义一个student,其掌握的属性  (存在误差，因目前为止只是对一个学生进行测试的)
     * 在掌握了试题j所考察的所有知识点的情况下做错的概率 ps  也应该是每道试题,一个随机值
     * 在并不完全掌握试题j所考察的所有知识点下猜对的概率 pg
     * 容器：适应度值
     */
      int id ;
      String ip ;
      double ps ;
      double pg ;
      double adi1_d ;
      double adi2_d ;
      double adi3_d ;
      double adi4_d ;
      double adi5_d ;
      ADI adiBean = new ADI();

    private static HashMap<String,Integer> indexMap = new HashMap();

    static
    {
        indexMap.put("(0,0,0,0,0)",1);
        indexMap.put("(0,0,0,0,1)",2);
        indexMap.put("(0,0,0,1,0)",3);
        indexMap.put("(0,0,1,0,0)",4);
        indexMap.put("(0,1,0,0,0)",5);
        indexMap.put("(1,0,0,0,0)",6);

        indexMap.put("(0,0,0,1,1)",7);
        indexMap.put("(0,0,1,0,1)",8);
        indexMap.put("(0,1,0,0,1)",9);
        indexMap.put("(1,0,0,0,1)",10);
        indexMap.put("(0,0,1,1,0)",11);
        indexMap.put("(0,1,0,1,0)",12);
        indexMap.put("(1,0,0,1,0)",13);
        indexMap.put("(0,1,1,0,0)",14);
        indexMap.put("(1,0,1,0,0)",15);
        indexMap.put("(1,1,0,0,0)",16);

        indexMap.put("(0,0,1,1,1)",17);
        indexMap.put("(0,1,0,1,1)",18);
        indexMap.put("(1,0,0,1,1)",19);
        indexMap.put("(0,1,1,0,1)",20);
        indexMap.put("(1,0,1,0,1)",21);
        indexMap.put("(1,1,0,0,1)",22);
        indexMap.put("(0,1,1,1,0)",23);
        indexMap.put("(1,0,1,1,0)",24);
        indexMap.put("(1,1,0,1,0)",25);
        indexMap.put("(1,1,1,0,0)",26);

        indexMap.put("(0,1,1,1,1)",27);
        indexMap.put("(1,0,1,1,1)",28);
        indexMap.put("(1,1,0,1,1)",29);
        indexMap.put("(1,1,1,0,1)",30);
        indexMap.put("(1,1,1,1,0)",31);
        indexMap.put("(1,1,1,1,1)",32);
    }





    /**
     * 模仿RUM
     *       rum -- kl -- adi
     *       1.找相关文献，dina 如何定义 adi  ( 可能存在点难度，查找 + 翻阅 )
     *       2.计算出adi
     *       3.指标信息同步到同一套试卷上
     *       4.评价解的好坏--》试卷--》adi的avg/min
     *
     *       5.最直接的方式是 将计算方式    由 rum 换成 dina   存在问题在于: 同一道试题adi辨别指标一样,且偏小
     *       6.实现最佳性能的所需测试数量，且诊断性能平衡，同时满足重要在测试长度，项目类型分布和重叠比例
     *       7.evaluation  test quality: 1) index-oriented and 2) simulation-oriented.
     *       8.最大程度地提高整体测试质量，最小化测试之间的最大差异，或两者的加权组合。(基于ADI)
     *
     *
     */


    @Test
    public void start()  {
        JDBCUtils jdbcUtils = new JDBCUtils();
        for (int i = 1; i <= 62; i++) {
            System.out.println("============="+ i +"==============");
            GetAdi(i);
            jdbcUtils.updateDina(id,ps,pg,adi1_d,adi2_d,adi3_d,adi4_d,adi5_d);
        }
    }


    /**
     * 根据dina 获取adi
     * @param
     */
    public void GetAdi(int i){

        // 试题pattern ip
        ArrayList<String> db_list = new JDBCUtils().select();

        String[] split = db_list.get(i-1).split(":");
        adiBean.setId(Integer.valueOf(split[0]));
        adiBean.setPattern(split[1]);
        id = Integer.valueOf(split[0]) ;
        ip = split[1];

        ArrayList<Double> dinaList = GetDinaListsRandom(ip);
        System.out.println("dinaList: "+dinaList);
        //根据 dinaList 计算出K_L二维数组
        Double[][] klArray = new KLUtils().foreach(dinaList, dinaList);
        //打印
        new KLUtils().arrayPrint(klArray);

        /*
         * 四个元素，遍历组合即可, 随机遍历顺序没影响
         * combineList 全局变量,保存另外几个属性的选取方案
         */
        ArrayList<String> combineList = new ArrayList<>();

        for(int X=0;X<2; X++){
            for(int Y=0;Y<2; Y++) {
                for (int Z = 0; Z < 2; Z++) {
                    for (int W = 0; W < 2; W++) {
                        String value = X +","+ Y +","+ Z +","+ W;
                        combineList.add(value);
                    }
                }
            }
        }

        //获取下标
        ArrayList<String> list1 = new ArrayList();
        ArrayList<String> list2 = new ArrayList();
        ArrayList<String> list3 = new ArrayList();
        ArrayList<String> list4 = new ArrayList();
        ArrayList<String> list5 = new ArrayList();


        for (int X =0;X<combineList.size();X++){
            //adi1
            Integer index11 = indexMap.get("(1," + combineList.get(X) + ")");
            Integer index12 = indexMap.get("(0," + combineList.get(X) + ")");
            Integer index13 = indexMap.get("(0," + combineList.get(X) + ")");
            Integer index14 = indexMap.get("(1," + combineList.get(X) + ")");

            list1.add(""+index11+"_"+index12);
            list1.add(""+index13+"_"+index14);

            //adi2         1,0,1,0
            Integer index21 = indexMap.get("(" +combineList.get(X).substring(0,1)+ ",1," +combineList.get(X).substring(2,7) + ")");
            Integer index22 = indexMap.get("(" +combineList.get(X).substring(0,1)+ ",0," +combineList.get(X).substring(2,7) + ")");
            Integer index23 = indexMap.get("(" +combineList.get(X).substring(0,1)+ ",0," +combineList.get(X).substring(2,7) + ")");
            Integer index24 = indexMap.get("(" +combineList.get(X).substring(0,1)+ ",1," +combineList.get(X).substring(2,7) + ")");

            list2.add(""+index21+"_"+index22);
            list2.add(""+index23+"_"+index24);


            //adi3
            Integer index31 = indexMap.get("("+combineList.get(X).substring(0,3)+",1,"+combineList.get(X).substring(4,7)+ ")");
            Integer index32 = indexMap.get("("+combineList.get(X).substring(0,3)+",0,"+combineList.get(X).substring(4,7)+ ")");
            Integer index33 = indexMap.get("("+combineList.get(X).substring(0,3)+",0,"+combineList.get(X).substring(4,7)+ ")");
            Integer index34 = indexMap.get("("+combineList.get(X).substring(0,3)+",1,"+combineList.get(X).substring(4,7)+ ")");

            list3.add(""+index31+"_"+index32);
            list3.add(""+index33+"_"+index34);


            //adi4
            Integer index41 = indexMap.get("("+combineList.get(X).substring(0,5)+",1,"+combineList.get(X).substring(6,7)+ ")");
            Integer index42 = indexMap.get("("+combineList.get(X).substring(0,5)+",0,"+combineList.get(X).substring(6,7)+ ")");
            Integer index43 = indexMap.get("("+combineList.get(X).substring(0,5)+",0,"+combineList.get(X).substring(6,7)+ ")");
            Integer index44 = indexMap.get("("+combineList.get(X).substring(0,5)+",1,"+combineList.get(X).substring(6,7)+ ")");

            list4.add(""+index41+"_"+index42);
            list4.add(""+index43+"_"+index44);


            //adi5
            Integer index51 = indexMap.get("("+combineList.get(X)+",1)");
            Integer index52 = indexMap.get("("+combineList.get(X)+",0)");
            Integer index53 = indexMap.get("("+combineList.get(X)+",0)");
            Integer index54 = indexMap.get("("+combineList.get(X)+",1)");

            list5.add(""+index51+"_"+index52);
            list5.add(""+index53+"_"+index54);


        }
        System.out.println("adi的计算指标：");
        System.out.println("adi1个数: "+list1.size() +" 具体指标为:"+list1);
        System.out.println("adi2个数: "+list2.size() +" 具体指标为:"+list2);
        System.out.println("adi3个数: "+list3.size() +" 具体指标为:"+list3);
        System.out.println("adi4个数: "+list4.size() +" 具体指标为:"+list4);
        System.out.println("adi5个数: "+list5.size() +" 具体指标为:"+list5);

        System.out.println("list 遍历 ;拿list的值去Array中匹配寻找,然后输出其大小：");
        List<Double> calAdiList = CalAdiImple(klArray, list1, list2, list3, list4, list5);
        System.out.println(calAdiList);




    }

    /**
     *
     * @param ip 试题的考察属性
     * @return 长度为 32 位的dinaList
     */
    public ArrayList<Double> GetDinaListsRandom(String ip){

        ArrayList<Double> dinaList = new ArrayList<>();

        //考生_pattern sps
        ArrayList<String> sps = new ArrayList<String>(){{

            add("(0,0,0,0,0)");
            add("(0,0,0,0,1)");
            add("(0,0,0,1,0)");
            add("(0,0,1,0,0)");
            add("(0,1,0,0,0)");
            add("(1,0,0,0,0)");

            add("(0,0,0,1,1)");
            add("(0,0,1,0,1)");
            add("(0,1,0,0,1)");
            add("(1,0,0,0,1)");
            add("(0,0,1,1,0)");
            add("(0,1,0,1,0)");
            add("(1,0,0,1,0)");
            add("(0,1,1,0,0)");
            add("(1,0,1,0,0)");
            add("(1,1,0,0,0)");

            add("(0,0,1,1,1)");
            add("(0,1,0,1,1)");
            add("(1,0,0,1,1)");
            add("(0,1,1,0,1)");
            add("(1,0,1,0,1)");
            add("(1,1,0,0,1)");
            add("(0,1,1,1,0)");
            add("(1,0,1,1,0)");
            add("(1,1,0,1,0)");
            add("(1,1,1,0,0)");

            add("(0,1,1,1,1)");
            add("(1,0,1,1,1)");
            add("(1,1,0,1,1)");
            add("(1,1,1,0,1)");
            add("(1,1,1,1,0)");
            add("(1,1,1,1,1)");
        }};

        // 题目pattern
        // 根据属性 随机生成 ps pg
        int a1 = Integer.parseInt(ip.substring(1, 2));
        int a2 = Integer.parseInt(ip.substring(3, 4));
        int a3 = Integer.parseInt(ip.substring(5, 6));
        int a4 = Integer.parseInt(ip.substring(7, 8));
        int a5 = Integer.parseInt(ip.substring(9, 10));

        int sum = a1 + a2 + a3 + a4 + a5;
        if(sum == 5){
            // 生成dinaList  猜对率和猜错率 分别是多少合适
            // 辨别能力很小   [0.0, 0.0, 0.0175, 0.0175, 0.0175]
            // ps[0.15,0.4]  pg[0.4,0.65]
            ps = new KLUtils().makeRandom(0.20f, 0.15f, 2);
            pg = new KLUtils().makeRandom(0.65f, 0.60f, 2);
        }
        else if(sum == 4){
            ps = new KLUtils().makeRandom(0.25f, 0.20f, 2);
            pg = new KLUtils().makeRandom(0.60f, 0.55f, 2);
        }
        else if(sum == 3){
            ps = new KLUtils().makeRandom(0.30f, 0.25f, 2);
            pg = new KLUtils().makeRandom(0.55f, 0.50f, 2);
        }
        else if(sum == 2){
            ps = new KLUtils().makeRandom(0.35f, 0.30f, 2);
            pg = new KLUtils().makeRandom(0.50f, 0.45f, 2);
        }
        else if(sum == 1){
            ps = new KLUtils().makeRandom(0.40f, 0.35f, 2);
            pg = new KLUtils().makeRandom(0.45f, 0.40f, 2);
        }
        System.out.println( "ps: "+ps+" ,pg: "+pg);

        //根据学生pattern vs 题目pattern 获取答对此题的rum
        for (String p : sps) {
            //学生pattern
            int b1 = Integer.parseInt(p.substring(1, 2));
            int b2 = Integer.parseInt(p.substring(3, 4));
            int b3 = Integer.parseInt(p.substring(5, 6));
            int b4 = Integer.parseInt(p.substring(7, 8));
            int b5 = Integer.parseInt(p.substring(9, 10));

            //全部掌握所考的属性，则潜在答题概率为1，否则默认为0
            boolean ab1 = b1 >= a1;
            boolean ab2 = b2 >= a2;
            boolean ab3 = b3 >= a3;
            boolean ab4 = b4 >= a4;
            boolean ab5 = b5 >= a5;

            int potentResp = 0;
            if(ab1 && ab2 && ab3 && ab4 && ab5 ){
                potentResp = 1;
            }

            // DINA 的适应度值计算公式
            double fit = (Math.pow(pg,(1-potentResp))) * (Math.pow((1-ps),potentResp));

            dinaList.add(fit);
        }

        return  dinaList;
    }



    /**
     * 计算adi具体实现
     */
    public List<Double> CalAdiImple(Double[][] klArray,ArrayList<String> list1,ArrayList<String> list2,ArrayList<String> list3,ArrayList<String> list4,ArrayList<String> list5){
        Double sum1 = 0.0;
        Double sum2 = 0.0;
        Double sum3 = 0.0;
        Double sum4 = 0.0;
        Double sum5 = 0.0;

        for(String data  :    list1)    {
            //System.out.print(data);
            String[] spli = data.split("_");
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(spli[0])-1][Integer.parseInt(spli[1])-1];
            //System.out.println("  "+v);
            sum1+=v;
        }
        adiBean.setAdi1_d(NumCoversion(sum1/list1.size()));
        adi1_d = NumCoversion(sum1/list1.size());
        System.out.println("adi1: "+adiBean.getAdi1_d());

        for(String data  :    list2)    {
            //System.out.print(data);
            String[] spli = data.split("_");
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(spli[0])-1][Integer.parseInt(spli[1])-1];
            //System.out.println("  "+v);
            sum2+=v;
        }
        adiBean.setAdi2_d(NumCoversion(sum2/list2.size()));
        adi2_d = NumCoversion(sum2/list2.size());
        System.out.println("adi2: "+adiBean.getAdi2_d());

        for(String data  :    list3)    {
            //System.out.print(data);
            String[] spli = data.split("_");
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(spli[0])-1][Integer.parseInt(spli[1])-1];
            //System.out.println("  "+v);
            sum3+=v;
        }
        adiBean.setAdi3_d(NumCoversion(sum3/list3.size()));
        adi3_d = NumCoversion(sum3/list3.size());
        System.out.println("adi3: "+adiBean.getAdi3_d());

        for(String data  :    list4)    {
            //System.out.print(data);
            String[] spli = data.split("_");
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(spli[0])-1][Integer.parseInt(spli[1])-1];
            //System.out.println("  "+v);
            sum4+=v;
        }
        adiBean.setAdi4_d(NumCoversion(sum4/list4.size()));
        adi4_d = NumCoversion(sum4/list4.size());
        System.out.println("adi4: "+adiBean.getAdi4_d());

        for(String data  :    list5)    {
            //System.out.print(data);
            String[] spli = data.split("_");
            //注意小数点的位置
            Double v  = klArray[Integer.parseInt(spli[0])-1][Integer.parseInt(spli[1])-1];
            //System.out.println("  "+v);
            sum5+=v;
        }
        adiBean.setAdi5_d(NumCoversion(sum5/list5.size()));
        adi5_d = NumCoversion(sum5/list5.size());
        System.out.println("adi5: "+adiBean.getAdi5_d());

        List<Double> adiList = new ArrayList<Double>(){{
            add(adiBean.getAdi1_d());
            add(adiBean.getAdi2_d());
            add(adiBean.getAdi3_d());
            add(adiBean.getAdi4_d());
            add(adiBean.getAdi5_d());
        }};
        return adiList;

    }


    public Double NumCoversion(Double adi){

        return Double.valueOf(String.format("%.4f", adi));

    }


}




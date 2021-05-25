package cn.edu.sysu.controller;

import cn.edu.sysu.pojo.Papers;
import cn.edu.sysu.pojo.Questions;
import cn.edu.sysu.pojo.QuorumPeer;
import cn.edu.sysu.utils.JDBCUtils2;
import org.apache.lucene.document.Field;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 *
 * @Author : songbeichang
 * @create 2021/05/18 0:17
 */
public class ADIController {


    /*  容器  */

    static Questions[] questions =new Questions[40];



    static  double[]   paper_fitness =new double[10];

    /* 10套试卷 6道题  */

    private static String[][] paperGenetic =new String[10][6];



    @Test
    public  void ori() throws SQLException {

        //选择10套的原因，只有基数够大，才能为交叉变异提供相对较多的原始材料
        //抽取试卷  10套、每套试卷6题
        Papers papers = new Papers();
        papers.setPc(0.5);
        papers.setPm(0.5);

        //初始化试卷   从题库中选取题目构成试卷  长度，属性类型，属性比例   （题型）
        initItemBank();

        //计算适应度值  ①计算时机 轮盘赌
        //            ②计算单位（单套试卷）  适应度值、交叉变异都以试卷为单位
        //            取平均值，是按adi属性取列值的平均（横坐标代表pattern  纵坐标代表适应度值）
        //getPaperFitness();

        // i 迭代次数
        for (int i = 0; i < 5; i++) {
            //selection();
            //crossCover(papers);
            //mutate(papers);
            //小生境环境的搭建
            //elitiststrategy();
        }
    }



    /**
     *
     * 生成题库(以试卷为单位：id   长度，属性类型，属性比例)
     * 5+10+10+5+1 = 31
     * 属性类型 和 属性比例 最好不要取固定值（除初始化外），将会导致所能修补算子执行时选取的题目有限
     * 属性类型：1个属性的1题[1,50]  2个属性的2题[51,150]  3个属性的2题[151,250] 4个属性的1题[251,300]
     *
     * 因为一共有5个属性，选取了6道题
     * 属性比例：1+2*2+2*2+1 = 共选取了10个属性（可重复的前提下）
     * ①比例是否需要为同一值，还是每个属性有各自的比例  后者显然更贴近现实生活
     * ②比例的范围如何确定，按照权重吗？   假设第1、2个属性重要，
     * 第1属性[0.2,0.5]   第2属性[0.2,0.5]   第3属性[0.1,0.4]  第4属性[0.1,0.4]  第5属性[0.1,0.4]
     *
     *
     * 方案一 根据ab1的值得出一个误差标准,进行重新抽取题目
     * 方案二 进行乘以一个底数e 来进行适应度值的降低
     *
     */
    @Test
    public  void initItemBank() throws SQLException {

        /*  试卷数 */
        int paperNum = 10 ;
        /* 单张试卷每种属性的题目数量 6 */
        int oneAttNum = 1;
        int twoAttNum = 2;
        int threeAttNum = 2;
        int fourAttNum = 1;

        JDBCUtils2 jdbcUtils = new JDBCUtils2();

        // 题库310道题  则50:100:100:50:10   长度，属性类型，属性比例
        String sql1 = "SELECT CEILING( RAND () * 50 ) + 1  AS id" ;
        String sql2 = "SELECT CEILING( RAND () * 100 ) + 50 AS id" ;
        String sql3 = "SELECT CEILING( RAND () * 100 ) + 150 AS id" ;
        String sql4 = "SELECT CEILING( RAND () * 50 ) + 250 AS id" ;

        System.out.println("====== 开始选题,构成试卷  ======");

        /*  j 选取的试卷数  */
        for (int j = 0; j < paperNum; j++) {
            ArrayList<Integer> idList = new ArrayList<>();
            int id ;
            //随机抽取1个属性的1题
            for (int i = 0; i < oneAttNum; i++) {
                id = jdbcUtils.selectItem(sql1);
                idList.add(id);
            }

            //随机抽取2个属性的2题
            Set<Integer> id_set2 = new HashSet<>();
            for (int i = 0; i < twoAttNum; i++) {
                //去重操作
                while (id_set2.size() == i) {
                    id = jdbcUtils.selectItem(sql2);
                    id_set2.add(id);
                }
            }
            //添加所有元素到列表中
            idList.addAll(id_set2);

            //随机抽取3个属性的2题
            Set<Integer> id_set3 = new HashSet<>();
            for (int i = 0; i < threeAttNum; i++) {
                //去重操作
                while (id_set3.size() == i) {
                    id = jdbcUtils.selectItem(sql3);
                    id_set3.add(id);
                }
            }
            //添加所有元素到列表中
            idList.addAll(id_set3);

            //随机抽取4个属性的1题
            for (int i = 0; i < fourAttNum; i++) {
                id = jdbcUtils.selectItem(sql4);
                idList.add(id);
            }


            //list  排序
            Collections.sort(idList);
            //输出所有抽取到的试题id
            System.out.println("试卷"+j+"的试题id: "+idList);


            String ids = idList.toString().substring(1,idList.toString().length()-1);

            ArrayList bachItemList = jdbcUtils.selectBachItem(ids);


//***************************************    BEGIN   ****************************************************************
            //方案一 根据ab1的值得出一个误差标准,进行重新抽取题目
            bachItemList = correctAttribute(bachItemList);

            //方案二 进行乘以一个底数e 来进行适应度值的降低
            //TODO  待实现

//****************************************    END    ****************************************************************



            //把题库提升为全局变量，方便整体调用 容器：二维数组
            //交叉变异的对象是 试题的题目
            // private static String[][] paperGenetic =new String[10][5];
            String[] itemArray = new String[bachItemList.size()];
            for (int i = 0; i < bachItemList.size(); i++) {
                itemArray[i] = bachItemList.get(i).toString();
            }
            // 疑问：为什么不直接将 bachItemList(ArrayList) 赋值给  paperGenetic[i]([])
            paperGenetic[j] = itemArray;
        }


    }


    /**
     * 方案一 根据ab1的值得出一个误差标准,进行重新抽取题目
     *
     * 执行校验操作 <属性比例>
     *     1.分别获取每个试题全部信息
     *     2.截取字段，变成id,pattern，并统计每个属性所占的比例
     *                  id,pattern,a1,a2,a3,a4,a5,判断指标（最好是完全吻合的）
     *     3.统计比例信息
     *     4.校验，并重新选举
     *     5.输出最后的方案
     **/
    public ArrayList correctAttribute(ArrayList bachItemList) throws SQLException {

        //1.每道试题的全部信息
        String it0 = bachItemList.get(0).toString();
        String it1 = bachItemList.get(1).toString();
        String it2 = bachItemList.get(2).toString();
        String it3 = bachItemList.get(3).toString();
        String it4 = bachItemList.get(4).toString();
        String it5 = bachItemList.get(5).toString();
        //2.截取字段，是否需要封装成对象 or 直接统计判断
        //封装：便于后期直接进行筛选
        String[] sp1 = it0.split(":");
        String[] at1 = sp1[1].split(",");
        QuorumPeer quorumPeer1 = new QuorumPeer(sp1[0], at1[0], at1[1], at1[2], at1[3], at1[4]);

        String[] sp2 = it1.split(":");
        String[] at2 = sp2[1].split(",");
        QuorumPeer quorumPeer2 = new QuorumPeer(sp2[0], at2[0], at2[1], at2[2], at2[3], at2[4]);

        String[] sp3 = it2.split(":");
        String[] at3 = sp3[1].split(",");
        QuorumPeer quorumPeer3 = new QuorumPeer(sp3[0], at3[0], at3[1], at3[2], at3[3], at3[4]);

        String[] sp4 = it3.split(":");
        String[] at4 = sp4[1].split(",");
        QuorumPeer quorumPeer4 = new QuorumPeer(sp4[0], at4[0], at4[1], at4[2], at4[3], at4[4]);

        String[] sp5 = it4.split(":");
        String[] at5 = sp5[1].split(",");
        QuorumPeer quorumPeer5 = new QuorumPeer(sp5[0], at5[0], at5[1], at5[2], at5[3], at5[4]);

        String[] sp6 = it5.split(":");
        String[] at6 = sp6[1].split(",");
        QuorumPeer quorumPeer6 = new QuorumPeer(sp6[0], at6[0], at6[1], at6[2], at6[3], at6[4]);

        //统计校验 属性比例
        // 定义局部变量
        double as1  = 0 ;
        double as2  = 0 ;
        double as3  = 0 ;
        double as4  = 0 ;
        double as5  = 0 ;

        for (int i = 0; i < bachItemList.size(); i++) {
            as1 = as1 +  Double.parseDouble(bachItemList.get(i).toString().split(":")[1].split(",")[0].substring(1,2));
            as2 = as2 +  Double.parseDouble(bachItemList.get(i).toString().split(":")[1].split(",")[1]);
            as3 = as3 +  Double.parseDouble(bachItemList.get(i).toString().split(":")[1].split(",")[2]);
            as4 = as4 +  Double.parseDouble(bachItemList.get(i).toString().split(":")[1].split(",")[3]);
            as5 = as5 +  Double.parseDouble(bachItemList.get(i).toString().split(":")[1].split(",")[4].substring(0,1));

            System.out.println(as1 + " " + as2 + " " + as3 + " " + as4 + " " + as5 );

        }
        //需要判断 属性比例是多了还是少了
        //定义局部变量 ab1  (-1->少于,0->正常,1->大于)
        int ab1 ;
        int ab2 ;
        int ab3 ;
        int ab4 ;
        int ab5 ;
        System.out.println("=========================================");
        if(as1/10>=0.2){
            if(as1/10<=0.5){
                ab1 = 0 ;
            }else{
                ab1 = 1;
            }
        }else {
            ab1 = -1;
        }

        if(as2/10>=0.2){
            if(as2/10<=0.5){
                ab2 = 0 ;
            }else{
                ab2 = 1;
            }
        }else {
            ab2 = -1;
        }


        if(as3/10>=0.1){
            if(as3/10<=0.4){
                ab3 = 0 ;
            }else{
                ab3 = 1;
            }
        }else {
            ab3 = -1;
        }

        if(as4/10>=0.1){
            if(as4/10<=0.4){
                ab4 = 0 ;
            }else{
                ab4 = 1;
            }
        }else {
            ab4 = -1;
        }

        if(as5/10>=0.1){
            if(as5/10<=0.4){
                ab5 = 0 ;
            }else{
                ab5 = 1;
            }
        }else {
            ab5 = -1;
        }
        System.out.println("目前属性数量情况： as1:"+as1+" as2:"+as2+" as3:"+as3+" as4:"+as4+" as5:"+as5);
        System.out.println("目前属性占比情况： ab1:"+ab1+"   ab2:"+ab2+"   ab3:"+ab3+"   ab4:"+ab4+"   ab5:"+ab5);

        //根据ab1的值得出一个误差标准,进行删除题目 和 重新添加题目
        //拼接成一个flag
        //多一个    先遍历匹配，再选取一个,带有这个属性的题目，然后move,add   导致的问题均是：属性题型发生变化
        //少一个    可以随机选取一个，不带有这个属性的题目，然后move，add
        //多两个    先遍历匹配(完全匹配)，若没有，则进行随机选取两次
        //少两个    随机选取一个,不带这两属性的题目
        String flag = "("+ab1+","+ab2+","+ab3+","+ab4+","+ab5+")";

        //对于多了的值 mark = 1 ，对于少了的属性 mark = -1
        //则flag: (0,-1,0,0,0) -》 -1
        //则flag: (0,1,0,0,0) -》 1  去匹配
        System.out.println("flag: "+flag);

        //目前此处逻辑适合比例多了的属性值
        //目前属性数量情况： as1:3.0 as2:2.0 as3:5.0 as4:2.0 as5:3.0
        //目前属性占比情况： ab1:0   ab2:0   ab3:1   ab4:0   ab5:0

        //获取 i 的值，然后重新选取， 选取的标准是什么？随机选取，然后重新计算？这样会导致计算很冗余
        //① 选取一个不包含此属性的题，然后替换掉原解 最好利用到此试题中的相关属性 即as1 as2 as3 as4 as5 便于做更进一步的判断

        Set<String> resultAll = new HashSet<>();
        resultAll.clear();
        Set<String> resultMore = new HashSet<>();
        Set<String> resultLess = new HashSet<>();


        //取出属性比例过多的集合的交集
        if(ab1==1 || ab2==1 || ab3==1 || ab4==1 || ab5==1){
            //完全匹配
            for (int i = 0; i < bachItemList.size(); i++) {
                if(flag.equals(bachItemList.get(i).toString().split(":")[1])){
                    System.out.println("有完全匹配的值");
                }
            }
            //表明属性1比例过多，用set集合接收 //需要判断 set 是否为空  把判断为空的逻辑 放在上面判断

            resultMore.clear();
            HashSet<String> set1 = new HashSet<>();
            HashSet<String> set2 = new HashSet<>();
            HashSet<String> set3 = new HashSet<>();
            HashSet<String> set4 = new HashSet<>();
            HashSet<String> set5 = new HashSet<>();


            if(ab1==1){

                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[0].substring(1,2).equals("1")){
                        set1.add(bachItemList.get(i).toString());
                    }
                }
                if (resultMore.size()==0){
                    resultMore.addAll(set1);
                }else {
                    resultMore.retainAll(set1);
                }
            }
            if(ab2==1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[1].equals("1")){
                        set2.add(bachItemList.get(i).toString());
                    }
                }
                if (resultMore.size()==0){
                    resultMore.addAll(set2);
                }else {
                    resultMore.retainAll(set2);
                }
            }
            if(ab3==1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[2].equals("1")){
                        set3.add(bachItemList.get(i).toString());
                    }
                }
                if (resultMore.size()==0){
                    resultMore.addAll(set3);
                }else {
                    resultMore.retainAll(set3);
                }
            }
            if(ab4==1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[3].equals("1")){
                        set4.add(bachItemList.get(i).toString());
                    }
                }
                if (resultMore.size()==0){
                    resultMore.addAll(set4);
                }else {
                    resultMore.retainAll(set4);
                }
            }
            if(ab5==1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[4].substring(0,1).equals("1")){
                        set5.add(bachItemList.get(i).toString());
                    }
                }
                if (resultMore.size()==0){
                    resultMore.addAll(set5);
                }else {
                    resultMore.retainAll(set5);
                }
            }
            //集合取交集 获取最接近的解
            System.out.println("属性比例过多的交集：" + resultMore);

            //赋值到全局变量
            if (resultAll.size()==0){
                resultAll.addAll(resultMore);
            }else {
                resultAll.retainAll(resultMore);
            }

        }

        //取出属性比例不足的集合的交集
        if(ab1==-1 || ab2==-1 || ab3==-1 || ab4==-1 || ab5==-1){
            //表明属性1比例过多，用set集合接收 //需要判断 set 是否为空  把判断为空的逻辑 放在上面判断

            resultLess.clear();
            HashSet<String> set1 = new HashSet<>();
            HashSet<String> set2 = new HashSet<>();
            HashSet<String> set3 = new HashSet<>();
            HashSet<String> set4 = new HashSet<>();
            HashSet<String> set5 = new HashSet<>();

            if(ab1==-1){

                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[0].substring(1,2).equals("0")){
                        set1.add(bachItemList.get(i).toString());
                    }
                }
                if (resultLess.size()==0){
                    resultLess.addAll(set1);
                }else {
                    resultLess.retainAll(set1);
                }
            }
            if(ab2==-1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[1].equals("0")){
                        set2.add(bachItemList.get(i).toString());
                    }
                }
                if (resultLess.size()==0){
                    resultLess.addAll(set2);
                }else {
                    resultLess.retainAll(set2);
                }
            }
            if(ab3==-1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[2].equals("0")){
                        set3.add(bachItemList.get(i).toString());
                    }
                }
                if (resultLess.size()==0){
                    resultLess.addAll(set3);
                }else {
                    resultLess.retainAll(set3);
                }
            }
            if(ab4==-1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[3].equals("0")){
                        set4.add(bachItemList.get(i).toString());
                    }
                }
                if (resultLess.size()==0){
                    resultLess.addAll(set4);
                }else {
                    resultLess.retainAll(set4);
                }
            }
            if(ab5==-1){
                for (int i = 0; i < bachItemList.size(); i++) {
                    if(bachItemList.get(i).toString().split(":")[1].split(",")[4].substring(0,1).equals("0")){
                        set5.add(bachItemList.get(i).toString());
                    }
                }
                if (resultLess.size()==0){
                    resultLess.addAll(set5);
                }else {
                    resultLess.retainAll(set5);
                }
            }
            //集合取交集 获取最接近的解
            System.out.println("属性比例不足的交集：" + resultLess);

            //赋值到全局变量
            if (resultAll.size()>0){
                resultAll.retainAll(resultLess);
            }else {
                resultAll.addAll(resultLess);
            }
        }
        // resultAll  resultMore resultLess
        //1.三者都有值，则取resultAll
        //2.resultAll  resultMore有值 则取 resultMore
        //3.resultAll  resultLess有值 则取 resultLess
        if(resultAll.size()>0 && resultMore.size()>0 && resultLess.size()>0){
            System.out.println("取resultAll，再次抽取");
            System.out.println(resultAll);
        }


        if(resultAll.size()>0 && resultMore.size()==0 && resultLess.size()>0){
            System.out.println("取resultLess，再次抽取");
            System.out.println(resultLess);
        }

        if(resultAll.size()>0 && resultMore.size()>0 && resultLess.size()==0){
            System.out.println("取resultMore，再次抽取");
            System.out.println(resultMore);

            // 替换的标准是什么，是替换有，还是替换无==》需要拿上下文进行判断
            // as max 为最佳，as min 为最low  差值计算
            // 第1属性[0.2,0.5]   第2属性[0.2,0.5]   第3属性[0.1,0.4]  第4属性[0.1,0.4]  第5属性[0.1,0.4]
            //目前属性数量情况： as1:3.0 as2:2.0 as3:5.0 as4:2.0 as5:3.0
            //目前属性占比情况： ab1:0   ab2:0   ab3:1   ab4:0   ab5:0
            System.out.println("目前属性数量情况： as1:"+as1+" as2:"+as2+" as3:"+as3+" as4:"+as4+" as5:"+as5);
            System.out.println("目前属性占比情况： ab1:"+ab1+"   ab2:"+ab2+"   ab3:"+ab3+"   ab4:"+ab4+"   ab5:"+ab5);

            double b1 = 5 - as1  ;
            double b2 = 5 - as2  ;
            double b3 = 4 - as3  ;
            double b4 = 4 - as4  ;
            double b5 = 4 - as5  ;

            //求最大值
            double[] arr = {b1, b2, b3, b4,b5};
            double res = arr[0];
            for (int i = 1; i < arr.length; i++){
                //逻辑为：如果条件表达式成立则执行result，否则执行arr[i]
                res = (arr[i] < res ? res : arr[i]);
            }
            System.out.println("最大值为：" + res);

            //取出最大值 和 已超出 对应的属性
            //取出最大值对应的属性

            ArrayList<Integer> bigIndex = new ArrayList<>();
            for (int i = 0; i < arr.length; i++){
                if (arr[i]==res){
                    bigIndex.add(i+1);
                }
            }
            //已超出 对应的属性
            ArrayList<Integer> overIndex = new ArrayList<>();
            if(ab1==1){overIndex.add(1);}
            if(ab2==1){overIndex.add(2);}
            if(ab3==1){overIndex.add(3);}
            if(ab4==1){overIndex.add(4);}
            if(ab5==1){overIndex.add(5);}

            //bigIndex(1,2)   overIndex(5)
            JDBCUtils2 jdbcUtils = new JDBCUtils2();
            ArrayList initFixItem = jdbcUtils.selectInitFixItem(bigIndex,overIndex);
            System.out.println(initFixItem);

            //从返回的结果当中ArrayList<String> 随机选一条替换掉原有的那条数据，然后验证是否符合要求
            //①保证选的题目和之前没重复 ②最好属性题型一致 ③满足属性比例要求
            Integer key =new Random().nextInt(initFixItem.size());
            String newItem = (String) initFixItem.get(key);
            System.out.println(newItem);


            int oi = 0;
            for (int i = 0; i < overIndex.size(); i++) {
                oi = overIndex.get(i);
            }

            int bi = 0;
            for (int i = 0; i < bigIndex.size(); i++) {
                bi = bigIndex.get(i);
            }


            int lastIndex = 0;
            for (int i = 0; i < bachItemList.size(); i++) {
                // (1,0,1,1,1)
                String pattern = bachItemList.get(i).toString().split(":")[1];
                if (pattern.substring(oi*2-2,oi*2-1).equals("1") &&  pattern.substring(bi*2-2,bi*2-1).equals("0")){
                    lastIndex = i;
                }
            }
            //修改Arraylist
            bachItemList.set(lastIndex, newItem);



        }





        return bachItemList;
    }



    /**
     * 交叉
     */
    public  void crossCover(Papers papers){
        //System.out.println();
        System.out.println("================== cross ==================");
        //paperGenetic[1].length = 6
        //paperGenetic[1][0] = 36:(0,0,0,1,0):0.0:0.0:0.0:0.09500000000000001:0.0:0.0:0.0:0.0:0.805:0.0
        //需要修改
        //变异的单位是  试卷的试题，所以交叉试题即可， 不需要变化试题的属性类型及个数
        Integer point = paperGenetic[1].length;
        for (int i = 0; i < paperGenetic.length-1; i++) {
            if (Math.random() < papers.getPc()) {
                //单点交叉   不会导致多个属性比例变化，最多只有一个属性存在问题（重复导致）
                String [] temp1 = new String[point];
                int a = new Random().nextInt(point);

                for (int j = 0; j < a; j++) {
                    temp1[j] = paperGenetic[i][j];
                }

                for (int j = a; j < point; j++) {
                    temp1[j] = paperGenetic[i+1][j];
                }
                // 判断size，执行修补操作  只改变了tem1 每执行一次pc 则校验一次
                correct(i,temp1);

            }
        }
    }

    /**
     * 判断size，执行修补操作
     */
    public  void correct(int i,String[] temp1) {

        System.out.println(i+ " 开始交叉后校验 ..... ");

        //去重操作
        Set<String> setBegin = new HashSet<>(Arrays.asList(temp1));
        Set<Integer> setEnd = new HashSet<>();
        Set<Integer> oneSet = new HashSet<>();
        Set<Integer> twoSet = new HashSet<>();
        Set<Integer> threeSet = new HashSet<>();
        Set<Integer> fourSet = new HashSet<>();

        int one = 1;
        int two = 2;

        int size = setBegin.size();
        int oneAttNum = 0;
        int twoAttNum = 0;
        int threeAttNum = 0;
        int fourAttNum = 0;

        //分别将三张类型的数量进行统计   题库310道题  则50:100:100:50:10
        //在idea中，idea会为重新分配过地址的变量加上下划线，这是idea的设定，是为了快速发现那些变量被重新分配了地址
        Iterator<String> it = setBegin.iterator();
        while (it.hasNext()) {
            Integer num = Integer.valueOf(it.next().split(":")[0]);
            if (num < 51 ){
                oneAttNum = oneAttNum+1;
                oneSet.add(num);
            }else if (num < 151){
                twoAttNum = twoAttNum+1;
                twoSet.add(num);
            }else if(num < 251 ){
                threeAttNum = threeAttNum+1;
                threeSet.add(num);
            }else if(num < 301 ){
                fourAttNum = fourAttNum+1;
                fourSet.add(num);
            }
        }

        System.out.println(" 1个属性的题目量: "+oneAttNum+" 2个属性的题目量: "+twoAttNum+" 3个属性的题目量: "+threeAttNum+" 4个属性的题目量: "+fourAttNum);


        //目前只校验了题目数，未校验各个属性的所占比例  vs  个数
        // TODO  校验属性比例  （查看老师的文献，获取比例信息）
        //                    了解如何保证比例的代码公式
        //累加操作  获取type t 的所占比例，或者个数都可以的       这样的话，初始化难度加大了啊
        if (oneAttNum == one && twoAttNum == two && threeAttNum == two  && fourAttNum == one){
            System.out.println(i+ " 正常交叉,无需处理");
        }else{
            System.out.println(i+ " 交叉导致属性所占比例不匹配：开始进行交叉修补算子 ....");
            //1个属性的校验
            if(oneAttNum<one){
                while(oneSet.size() != one){
                    Integer key = new Random().nextInt(50);
                    oneSet.add(key);
                }
            }
            //2个属性的校验
            if(twoAttNum<two){
                while(twoSet.size() != two){
                    Integer key = Math.abs(new Random().nextInt()) % 100 + 50;
                    twoSet.add(key);
                }
            }
            //3个属性的校验
            if(threeAttNum<two){
                while(threeSet.size() != two){
                    Integer key = Math.abs(new Random().nextInt()) % 100 + 150;
                    threeSet.add(key);
                }
            }
            //4个属性的校验
            if(fourAttNum<one){
                while(fourSet.size() != one){
                    Integer key = Math.abs(new Random().nextInt()) % 50 + 250;
                    fourSet.add(key);
                }
            }
            setEnd.addAll(oneSet);
            setEnd.addAll(twoSet);
            setEnd.addAll(threeSet);
            setEnd.addAll(fourSet);

            setEnd.toArray(temp1);
            Arrays.sort(temp1);
            paperGenetic[i]=temp1;
            //打印选取的题目，打印的结果 应该是内存地址
            System.out.println("最终修补后的结果如下："+Arrays.toString(paperGenetic[i]));

            System.out.println(i+ " 结束交叉修补算子！！");

        }

    }


    /**
     * 变异
     */
    public  void mutate(Papers papers) throws SQLException {
        //System.out.println();
        System.out.println("=== mutate begin ===");
        JDBCUtils2 jdbcUtils = new JDBCUtils2();
        String key  ="";
        for (int i = 0; i < paperGenetic.length; i++) {
            if(Math.random() < papers.getPm()){
                Random random = new Random();
                int mutate_point = random.nextInt((paperGenetic[1].length)-1);
                Set<String> set = new HashSet<>(Arrays.asList( paperGenetic[i]));
                // 原试题的题目无序，导致添加的时候需要 做进一步判断
                String s = paperGenetic[i][mutate_point];
                System.out.println(i+" 原试卷: "+set);
                System.out.println("  remove element: "+ s);
                set.remove(s);
                String itemid = s.split(":")[0];
                System.out.println("  临时试卷：  "+set);

                String[] temp1 = new String[5];

                //生成一个合适的且不存在set中的key
                if (mutate_point<2){
                    while (set.size() != 5 ){
                        key = random.nextInt(10)+"";
                        if (!key.equals(itemid)){
                            ArrayList list = jdbcUtils.selectBachItem(key);
                            set.add(list.get(0)+"");
                        }
                    }
                }else if(mutate_point<4){
                    while (set.size() != 5 ){
                        key = new Random().nextInt(20) + 10+"";
                        if (!key.equals(itemid)){
                            ArrayList list = jdbcUtils.selectBachItem(key);
                            set.add(list.get(0)+"");
                        }
                    }
                }else if(mutate_point<6){

                    while (set.size() != 5 ){
                        key = new Random().nextInt(20) + 30+"";
                        if (!key.equals(s)){
                            ArrayList list = jdbcUtils.selectBachItem(key);
                            set.add(list.get(0)+"");
                        }
                    }
                }
                set.toArray(temp1);
                Arrays.sort(temp1);
                paperGenetic[i]=temp1;
            }
            System.out.println("  add element: "+ key);
            System.out.println("  最终试卷： "+Arrays.toString(paperGenetic[i]));
            //System.out.println();
        }
        System.out.println("=== mutate end ===");
    }



    /**
     * 选择: 根据轮盘赌选择适应度高的个体
     * 可以把适应度值打印出来看一下，方便后续比较
     *     ①适应度：如何去计算呢？选取的是试卷，然后重新计算一遍min即可
     *     ②或者直接从数据库中获取，初始化的时候直接将适应度值计算出来，并更新在数据库中  这种方式是最快的，但 试题选取构成试卷是随机的，且包括交叉变异，故不适合建立临时表
     *     如果需要可以，将最终的结果的汇总到数据库。
     *
     *     仅仅依靠选择和轮盘赌，其变化是很小的，故需要更大的外部作用(交叉+变异）
     *
     */
    public    void selection( ){
        System.out.println("====================== select ======================");
        //10套试卷   6道题目
        int paperSize = paperGenetic.length;

        //轮盘赌 累加百分比
        double[] fitPie = new double[paperSize];

        String[][] newPaperGenetic =new String[paperSize][];

        //执行计算适应度的操作
        ArrayList fitnessArray = getFitness(paperSize);

        //每套试卷的适应度占比
        double[] fitPro = (double[]) fitnessArray.get(2);

        //越大的适应度，其叠加时增长越快，即有更大的概率被选中   轮盘赌
        double accumulate = 0;

        //试卷占总试卷的适应度累加百分比
        for (int i = 0; i < paperSize; i++) {
            fitPie[i] = accumulate + fitPro[i];
            accumulate += fitPro[i];
            //System.out.println("试卷"+ i+"占目前总试卷的适应度累加百分比： "+fitPie[i]);
        }

        //累加的概率为1   数组下标从0开始
        fitPie[paperSize-1] = 1;

        //初始化容器
        double[] randomId = new double[paperSize];

        //不需要去重
        for (int i = 0; i < paperSize; i++) {
            randomId[i] = Math.random();
        }

        // 排序
        Arrays.sort(randomId);

        //把基本数据类型转化为列表 double[]转Double[]
        int num = randomId.length;
        Double [] arrDouble=new Double[num];
        for(int i=0;i<num;i++){
            arrDouble[i]=randomId[i];
        }

        //Double[]转List
        List<Double> list = Arrays.asList(arrDouble);
        //System.out.println("随机抽取的random概率值："+list);

        //轮盘赌
        int newSelectId = 0;
        for (int i = 0; i < paperSize; i++) {
            while (newSelectId < paperSize && randomId[newSelectId] < fitPie[i]){
                //需要确保fitPie[i] 和 paperGenetic[i] 对应的i 是同一套试卷
                newPaperGenetic[newSelectId]   = paperGenetic[i];
                newSelectId += 1;
            }
        }
        //输出老种群的适应度值
        System.out.print("老种群");
        getPaperFitness();

        //重新赋值种群的编码
        paperGenetic=newPaperGenetic;

        //输出新种群的适应度值
        System.out.print("新种群");
        getPaperFitness();
    }


    public ArrayList getFitness(int paperSize){

        //所有试卷的适应度总和
        double fitSum = 0.0;
        //每套试卷的适应度值
        double[] fitTmp = new double[paperSize];
        //每套试卷的适应度占比
        double[] fitPro = new double[paperSize];

        // 计算试卷的适应度值，即衡量试卷的指标之一 Fs
        for (int i = 0; i < paperSize; i++) {

            double adi1r =0;
            double adi2r =0;
            double adi3r =0;
            double adi4r =0;
            double adi5r =0;

            double adi1d =0;
            double adi2d =0;
            double adi3d =0;
            double adi4d =0;
            double adi5d =0;

            //ArrayList -> []
            String [] itemList = paperGenetic[i];
            for (int j = 0; j < itemList.length; j++) {

                String[] splits = itemList[j].split(":");
                adi1r = adi1r + Double.parseDouble(splits[2]);
                adi2r = adi2r + Double.parseDouble(splits[3]);
                adi3r = adi3r + Double.parseDouble(splits[4]);
                adi4r = adi4r + Double.parseDouble(splits[5]);
                adi5r = adi5r + Double.parseDouble(splits[6]);

                adi1d = adi1d + Double.parseDouble(splits[7]);
                adi2d = adi2d + Double.parseDouble(splits[8]);
                adi3d = adi3d + Double.parseDouble(splits[9]);
                adi4d = adi4d + Double.parseDouble(splits[10]);
                adi5d = adi5d + Double.parseDouble(splits[11]);

            }

            //均值 和 最小值
            double avgrum = (adi1r + adi2r + adi3r + adi4r + adi5r)/5 ;
            double minrum = Math.min(Math.min(Math.min(Math.min(adi1r,adi2r),adi3r),adi4r),adi5r) ;
            double avgdina = (adi1d + adi2d + adi3d + adi4d + adi5d)/5 ;
            double mindina = Math.min(Math.min(Math.min(Math.min(adi1d,adi2d),adi3d),adi4d),adi5d) ;

            //System.out.printf("avgrum=%s \t minrum=%s \t avgdina=%s \t mindina=%s \n", avgrum, minrum, avgdina,mindina);

            //System.out.println();

            fitTmp[i] = minrum ;

            fitSum = fitSum + minrum ;
        }


        for (int i = 0; i < paperSize; i++) {
            //各自的比例
            fitPro[i] = fitTmp[i] / fitSum;
        }

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(fitSum);
        arrayList.add(fitTmp);
        arrayList.add(fitPro);

        return  arrayList;
    }

    /**
     * 临时方法 测试专用
     */
    @Test
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

    /**
     * 计算每道试卷的适应度值
     *      遍历二维数组,根据第二维度来计算适应度值
     */
    public void getPaperFitness(){

        //遍历输出 试卷的适应度 矩阵
        System.out.println("试卷的适应度信息如下: ");

        // 计算试卷的适应度值，即衡量试卷的指标之一 Fs
        for (int i = 0; i < paperGenetic.length; i++) {

            double adi1r = 0;
            double adi2r = 0;
            double adi3r = 0;
            double adi4r = 0;
            double adi5r = 0;

            double adi1d = 0;
            double adi2d = 0;
            double adi3d = 0;
            double adi4d = 0;
            double adi5d = 0;

            //ArrayList -> []
            String[] itemList = paperGenetic[i];
            for (int j = 0; j < itemList.length; j++) {

                String[] splits = itemList[j].split(":");
                adi1r = adi1r + Double.parseDouble(splits[2]);
                adi2r = adi2r + Double.parseDouble(splits[3]);
                adi3r = adi3r + Double.parseDouble(splits[4]);
                adi4r = adi4r + Double.parseDouble(splits[5]);
                adi5r = adi5r + Double.parseDouble(splits[6]);

                adi1d = adi1d + Double.parseDouble(splits[7]);
                adi2d = adi2d + Double.parseDouble(splits[8]);
                adi3d = adi3d + Double.parseDouble(splits[9]);
                adi4d = adi4d + Double.parseDouble(splits[10]);
                adi5d = adi5d + Double.parseDouble(splits[11]);

            }

            //均值 和 最小值
            double avgrum = (adi1r + adi2r + adi3r + adi4r + adi5r) / 5;
            double minrum = Math.min(Math.min(Math.min(Math.min(adi1r, adi2r), adi3r), adi4r), adi5r);
            double avgdina = (adi1d + adi2d + adi3d + adi4d + adi5d) / 5;
            double mindina = Math.min(Math.min(Math.min(Math.min(adi1d, adi2d), adi3d), adi4d), adi5d);

            //System.out.printf("试卷%s \t avgrum=%s \t minrum=%s \t avgdina=%s \t mindina=%s \n", i, avgrum, minrum, avgdina, mindina);
            //部分试卷适应度变小，大部分适应度变大
            System.out.printf("试卷%s  minrum=%s \t", i, numbCohesion(minrum));
        }
        System.out.println();
    }



    /**
     * 格式转换工具
     */
    public Double numbCohesion(Double adi){

        return Double.valueOf(String.format("%.4f", adi));

    }

}




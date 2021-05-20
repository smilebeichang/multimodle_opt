package cn.edu.sysu.controller;

import cn.edu.sysu.pojo.Papers;
import cn.edu.sysu.pojo.Questions;
import cn.edu.sysu.utils.JDBCUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 *
 * @Author : songbeichang
 * @create 2021/05/18 0:17
 */
public class ADIController {


    /**
     * 容器
     */
    static Questions[] questions =new Questions[40];



    static  double[]   paper_fitness =new double[10];

    /* 10套试卷 5道题  */

    private static String[][] paperGenetic =new String[10][5];




    @Test
    public  void ori() {

        //抽取试卷  10套、每套试卷5题
        Papers papers = new Papers();
        papers.setPc(0.5);
        papers.setPm(0.5);

        //初始化试卷   从题库中选取题目构成试卷
        initItemBank();

        //计算适应度值  ①什么时候计算   可以初始化时计算，但和被试掌握属性有关 ==》 交叉变异的时候计算
        //            ②计算单位（单套试卷）  适应度值、交叉变异都以试卷为单位 取平均值，是按adi属性取列值的平均（横坐标代表pattern  纵坐标代表适应度值）
        //getPaperFitness();

        // i 迭代次数
        for (int i = 0; i < 2; i++) {
            selection();
            crossCover(papers);
            mutate(papers);
            //小生境环境的搭建
            //elitiststrategy();
        }
    }


    /**
     * 生成题库(以试卷为单位：id  需平衡试卷的整套试卷的属性数)
     * 如：1/4个属性的1题[1,10][51,60]  2个属性的2题[11,30]  3个属性的2题[31,50]
     * 5+10+10+5+1 = 31
     *
     */
    @Test
    public  void initItemBank(){

        /*  试卷数 */
        int paperNum = 10 ;
        /* 单张试卷每种属性的题目数量 */
        int oneAttNum = 1;
        int twoAttNum = 2;
        int threeAttNum = 2;

        JDBCUtils jdbcUtils = new JDBCUtils();


        String sql1 = "SELECT CEILING( RAND () * 10 ) + 1  AS id" ;
        String sql2 = "SELECT CEILING( RAND () * 20 ) + 10 AS id" ;
        String sql3 = "SELECT CEILING( RAND () * 20 ) + 30 AS id" ;
        String sql4 = "SELECT CEILING( RAND () * 10 ) + 50 AS id" ;

        System.out.println("====== 开始选题,构成试卷  ======");

        /*  j 选取的试卷数  */
        for (int j = 0; j < paperNum; j++) {
            ArrayList<Integer> idList = new ArrayList<>();
            int id ;
            //随机抽取1/4个属性的1题
            for (int i = 0; i < oneAttNum; i++) {
                if(Math.random() < 0.5){
                    id = jdbcUtils.selectItem(sql1);
                }else {
                    id = jdbcUtils.selectItem(sql4);
                }
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
            //通过加强for循环遍历HashSet
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
            //通过加强for循环遍历HashSet
            idList.addAll(id_set3);

            System.out.println(idList);
            String ids = idList.toString().substring(1,idList.toString().length()-1);
            System.out.println(ids);

            ArrayList bachItemList = jdbcUtils.selectBachItem(ids);

            //把题库提升为全局变量，方便整体调用
            //容器：二维数组
            //交叉变异的对象是 试题的题目
            // private static String[][] paperGenetic =new String[10][5];
            String[]   itemArray = new String[bachItemList.size()];
            for (int i = 0; i < bachItemList.size(); i++) {
                itemArray[i] = bachItemList.get(i).toString();
            }
            paperGenetic[j] = itemArray;
        }


    }




    /**
     * 交叉
     */
    public  void crossCover(Papers papers){
        System.out.println();
        System.out.println("=== crossCover begin ===");
        Integer point = paperGenetic[1].length;
        for (int i = 0; i < paperGenetic.length-1; i++) {
            if (Math.random() < papers.getPc()) {
                //单点交叉
                String [] temp1 = new String[point];
                int a = new Random().nextInt(point);

                for (int j = 0; j < a; j++) {
                    temp1[j] = paperGenetic[i][j];
                }

                for (int j = a; j < point; j++) {
                    temp1[j] = paperGenetic[i+1][j];
                }
                // 判断size，执行修补操作
                correct(i,temp1);

            }
        }
        System.out.println("=== crossCover end ===");
    }

    /**
     * 判断size，执行修补操作
     */
    public  void correct(int i,String[] temp1) {

        Set<String> set_begin = new HashSet<>(Arrays.asList(temp1));
        Set<Integer> set_end = new HashSet<>();
        Set<Integer> oneSet = new HashSet<>();
        Set<Integer> twoSet = new HashSet<>();
        Set<Integer> threeSet = new HashSet<>();
        int size = set_begin.size();
        int oneAttNum = 0;
        int twoAttNum = 0;
        int threeAttNum = 0;
        if (size == 5){
            System.out.println(i+ " 正常交叉,无需处理");
        }else{
            System.out.println(i+ " 交叉导致类型不匹配： "+set_begin.size());

            //分别将三张类型的数量进行统计
            Iterator<String> it = set_begin.iterator();
            while (it.hasNext()) {
                Integer num = Integer.valueOf(it.next().split(":")[0]);
                if (num < 11 || num > 50 ){
                    oneAttNum = oneAttNum+1;
                    oneSet.add(num);
                }else if (num < 31){
                    twoAttNum = twoAttNum+1;
                    twoSet.add(num);
                }else if(num < 51 ){
                    threeAttNum = threeAttNum+1;
                    threeSet.add(num);
                }
            }

            System.out.println(" 一个/四个属性的题目量: "+oneAttNum+" 两个属性的题目量: "+twoAttNum+" 三个属性的题目量: "+threeAttNum);

            if(oneAttNum<1){
                while(oneSet.size() != 1){
                    Integer key = new Random().nextInt(10);
                    oneSet.add(key);
                }
            }

            if(twoAttNum<2){
                while(twoSet.size() != 2){
                    Integer key = Math.abs(new Random().nextInt()) % 10 + 10;
                    twoSet.add(key);
                }
            }

            if(threeAttNum<2){
                while(threeSet.size() != 2){
                    Integer key = Math.abs(new Random().nextInt()) % 10 + 30;
                    threeSet.add(key);
                }
            }

            set_end.addAll(oneSet);
            set_end.addAll(twoSet);
            set_end.addAll(threeSet);

            set_end.toArray(temp1);
            Arrays.sort(temp1);
            paperGenetic[i]=temp1;
        }
        System.out.println("  "+Arrays.toString(paperGenetic[i]));
    }


    /**
     * 变异
     */
    public  void mutate(Papers papers){
        System.out.println();
        System.out.println("=== mutate begin ===");
        JDBCUtils jdbcUtils = new JDBCUtils();
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
            System.out.println();
        }
        System.out.println("=== mutate end ===");
    }



    @Test
    public void sss(){
//       String  key = (Math.abs(new Random().nextInt()) % 10 + 10)+"";
//       System.out.println(key);
//       key = (Math.abs(new Random().nextInt()) % 10 + 30)+"";
//       System.out.println(key);
//       key = new Random().nextInt(10)+"";
//       System.out.println(key);

        for (int i = 0; i < 10 ; i++) {
            int j = new Random().nextInt(20) + 10;
            System.out.println(j);
        }

    }
    /**
     * 交叉
     */
    public    void selection( ){
        //10套试卷   5道题目
        int paperSize = paperGenetic.length;
        double fitSum = 0;

        double[] fitTmp = new double[paperSize];
        double[] fitPro = new double[paperSize];
        double cumsum = 0;
        double[] fitPie = new double[paperSize];


        String[][] newPaperGenetic =new String[paperSize][];
        int randomSelectId = 0;

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

            System.out.printf("avgrum=%s \t minrum=%s \t avgdina=%s \t mindina=%s \n", avgrum, minrum, avgdina,mindina);

            System.out.println();

            fitTmp[i] = minrum ;

            fitSum = fitSum + minrum ;
        }

        for (int i = 0; i < paperSize; i++) {
            //各自的比例
            fitPro[i] = fitTmp[i] / fitSum;
        }

        //越大的适应度，其叠加时增长越快，所以有更大的概率被选中
        for (int i = 0; i < paperSize; i++) {
            fitPie[i] = cumsum + fitPro[i];
            cumsum += fitPro[i];
            System.out.println(i+"目前总试卷的适应度百分比： "+fitPie[i]);
        }

        //累加的概率为1
        fitPie[paperSize-1] = 1;

        //初始化容器
        double[] random_selection = new double[paperSize];

        for (int i = 0; i < paperSize; i++) {
            random_selection[i] = Math.random();
            //System.out.println(random_selection[i]);
        }
        //排序
        Arrays.sort(random_selection);

        //轮盘赌可能存在点问题 选举结束后[10][18]
        //随着random_selection_id的递增,random_selection[random_selection_id]逐渐变大
        for (int i = 0; i < paperSize; i++) {
            while (randomSelectId < paperSize && random_selection[randomSelectId] < fitPie[i]){
                newPaperGenetic[randomSelectId]   = paperGenetic[i];
                randomSelectId += 1;
            }
        }
        System.out.println();
        //输出老种群的适应度值
        //getPaperFitness();
        //重新赋值种群的编码
        paperGenetic=newPaperGenetic;
        //输出新种群的适应度值
        //getPaperFitness();
    }





}




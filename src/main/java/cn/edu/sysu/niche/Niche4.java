package cn.edu.sysu.niche;

import cn.edu.sysu.adi.TYPE;
import cn.edu.sysu.pojo.Papers;
import cn.edu.sysu.utils.JDBCUtils4;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author : song bei chang
 * @create 2021/7/3 10:22
 *
 *  复现 GA 和 Niche的代码，并实现多峰函数的曲线图
 *      1.稳态GA
 *      2.嵌入Niche
 *      3.替换适应度函数
 *      4.打印适应度值
 *
 * 《Messy genetic algorithms Motivation analysis and first results》Goldberg,Korb, Deb  1989
 *
 * 1. 稳态GA sizer =200，pc=0.4  迭代=100次 w=4c
 * 2. 已经将GA和小生境进行了初步融合
 * 3. 适应度函数的替换  使用文献中的多峰函数公式
 *        影响范围： 初始化部分+计算公式部分+选择部分
 *        初始化变化的  f(x) = sin6(5πx)    f(x) = e-2*ln(2)*((x-0.1)/0.8)2 * sin6(5π(x3/4-0.05))
 *        如何将函数嵌套进去呢？
 *        如何保证五个峰值，这是关键  ==>   保证0.1、0.3、0.5 这些极值点 ==>  即x是个体，且能满足交叉变异
 *             基因 如何变成0.1呢？
 *             1. 给每个基因片段赋予一个初始值，范围均为0~1 ，然后取平均值，仍然能得到0~1的值。
 *             2. 迭代过程中，使用f(x)来进行选择
 *             3. 交叉变异影响：判断相似时无论基因型还是表现型，需对double类型的数据进行转换
 *
 *
 * 4. 打印适应度值
 *
 *
 *
 */
public class Niche4 {

    /**
     * 容器
     */
    static  double[]   all_fitness =new double[200];
    static  double[]   paper_fitness =new double[200];
    static String[][]  paper_genetic =new String[200][15];
    JDBCUtils4 jdbcUtils = new JDBCUtils4();


    /**
     *  1.稳态GA
     */
    @Test
    public void main() throws FileNotFoundException, SQLException {

        //200套、每套试卷15题
        //初始化试卷
        init();

        // i 迭代次数
        for (int i = 0; i < 100; i++) {

            //选择
            selection();
            for (int j = 0; j < 200 - 1; j++) {
                //交叉
                crossCover(j);
                //变异
                mutate(j);
                //精英策略
                //elitistStrategy();
            }
            //统计相似个体的数目
            if(i%10==0){
                countCalculations(paper_genetic);
            }

        }
        System.out.println();


    }


    /**
     * 选择
     */
    public  void selection( ){

            System.out.println("====================== select ======================");

            //200套试卷
            int paperSize = paper_genetic.length;

            //轮盘赌 累加百分比
            double[] fitPie = new double[paperSize];

            //每套试卷的适应度占比  min*exp^1
            double[] fitPro = getFitness(paperSize);

            //累加初始值
            double accumulate = 0;

            //试卷占总试卷的适应度累加百分比
            for (int i = 0; i < paperSize; i++) {
                fitPie[i] = accumulate + fitPro[i];
                accumulate += fitPro[i];
            }

            //累加的概率为1 数组下标从0开始
            fitPie[paperSize-1] = 1;

            //初始化容器 随机生成的random概率值
            double[] randomId = new double[paperSize];

            //生成随机id
            for (int i = 0; i < paperSize; i++) {
                randomId[i] = Math.random();
            }

            // 排序
            Arrays.sort(randomId);

            //轮盘赌 越大的适应度，其叠加时增长越快，即有更大的概率被选中
            String[][] newPaperGenetic =new String[paperSize][];
            int newSelectId = 0;
            for (int i = 0; i < paperSize; i++) {
                while (newSelectId < paperSize && randomId[newSelectId] < fitPie[i]){
                    //需要确保fitPie[i] 和 paperGenetic[i] 对应的i 是同一套试卷
                    newPaperGenetic[newSelectId]   = paper_genetic[i];
                    newSelectId += 1;

                }
            }

            //重新赋值种群的编码
        paper_genetic=newPaperGenetic;


    }


    /**
     * 计算每张试卷的适应度
     */
    public static void getPaperFitness() {
        double sum = 0;
        for (int i = 0; i < 10; i++) {
            double tmp_value =0;
            String[] integers = paper_genetic[i];
            for (int j = 0; j < 20; j++) {
                tmp_value += all_fitness[Integer.parseInt(integers[j])];
            }
            double   f   =   tmp_value;
            BigDecimal b   =   new   BigDecimal(f);
            double   f1   =   b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            paper_fitness[i]=f1;
            sum = sum + f1;
        }
        System.out.println("总和："+sum);
    }


    /**
     * 交叉
     *      交叉后可能导致题目重复，解决方案：在变异后直接补全size
     *
     */
    public static void crossCover( int k){

        System.out.println("=== crossCover begin ===");
        Integer point = paper_genetic[0].length;

        if (Math.random() < 0.4) {
            //单点交叉  只保留一个个体
            String[] temp1 = new String[point];
            int a = new Random().nextInt(point);

            for (int j = 0; j < a; j++) {
                temp1[j] = paper_genetic[k][j];
            }
            for (int j = a; j < point; j++) {
                temp1[j] = paper_genetic[k+1][j];
            }
            paper_genetic[k] = temp1;

        }

        System.out.println("=== crossCover end ===");
    }


    /**
     * 变异
     */
    public static void mutate(int j) throws SQLException {

        System.out.println("=== mutate begin ===");

        //使用限制性锦标赛拥挤小生境的变异替换掉原有变异  需要将变异后的种群返回
        ArrayList<Object> rts = new  Niche3().RTS(paper_genetic, j);
        int similarPhenIndex = (int) rts.get(0);
        paper_genetic = (String[][]) rts.get(1);

        System.out.println("=== mutate end ===");
    }


    /**
     * 找出一个数组中一个数字出现次数最多的数字
     * 用HashMap的key来存放数组中存在的数字，value存放该数字在数组中出现的次数
     *
     * 将结果写到指定文件，便于后续统计
     *
     */
    private void countCalculations(String[][] paperGenetic) throws FileNotFoundException {


        //log.info("测试 log4j");

        String[] array = new String[paperGenetic.length];

        for (int i = 0; i < paperGenetic.length; i++) {
            //排序操作，为了保证检测出相似性
            String[] strings = sortPatch(paperGenetic[i]);
            StringBuilder idTmp = new StringBuilder();
            idTmp.append("[");
            for (String s : strings) {
                //将id抽取,拼接成新数组
                idTmp.append(s.split(":")[0]).append(",");
            }
            idTmp.append("]");
            array[i] = idTmp.toString();
        }


        //map的key数字，value出现的次数
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i < array.length; i++) {
            if(map.containsKey(array[i])) {
                int temp = map.get(array[i]);
                map.put(array[i], temp + 1);
            } else {
                map.put(array[i], 1);
            }
        }

        //输出每个个体出现的次数
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer count = entry.getValue();
            //log.info("试题编号："+ key+"  次数："+count);
        }

        //找出map的value中最大的数字，即数组中数字出现最多的次数
        //Collection<Integer> count = map.values();
        //int max = Collections.max(count);
        //System.out.println(max);

        String maxKey = "";
        int maxCount = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            //得到value为maxCount的key，也就是数组中出现次数最多的数字
            if(maxCount < entry.getValue()) {
                maxCount = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        //log.info("出现次数最多的数对象为：" + maxKey);
        //log.info("该数字一共出现" + maxCount + "次");

    }


    /**
     *
     * 排序
     *      1.获取id,重新据库查询一遍  返回的Array[]
     */
    public String[] sortPatch(String[] temp1) {


        //题型数量
        //int  typeNum = paperGenetic[0].length;
        int  typeNum = 15;

        //抽取id,封装成int[]
        int[] sortArray = new int[typeNum];
        for (int i = 0; i < temp1.length; i++) {
            sortArray[i] = Integer.parseInt(temp1[i].split(":")[0]);
        }
        Arrays.sort(sortArray);
        System.out.println("排序后的数组: "+Arrays.toString(sortArray));

        //根据id的位置，映射，重新排序 tmp2
        String[] temp2 = new String[typeNum];
        for (int i = 0; i < sortArray.length; i++) {
            int index = sortArray[i];
            for (String ts : temp1) {
                if(Integer.parseInt(ts.split(":")[0]) == index){
                    temp2[i] = ts;
                }
            }
        }

        return  temp2;
    }


    /**
     * 随机生成初代种群
     *      200个体  15基因
     */
    public  void  init( ) throws SQLException {
        System.out.println("=== init begin ===");
        for (int i = 0; i < 200; i++) {

            //随机生成取题目id序列
            String[] testGene= new String[15];
            Set<String> set = new HashSet<String>();
            for(int j = 0; j < 15; j++){
                //保证题目不重复,且满足长度约束
                while (set.size() == j ){
                    Integer key = new Random().nextInt(310)+1;
                    set.add(String.valueOf(key));
                }
            }

            set.toArray(testGene);


            //数组转字符串 org.apache.commons.lang3.StringUtils
            String idstr = StringUtils.join(testGene, ",");

            //利用id,查询题目
            ArrayList<String> arrayList = jdbcUtils.selectBachItem(idstr);
            String[] itemArray = new String[arrayList.size()];
            for (int k = 0; k < arrayList.size(); k++) {
                itemArray[k] = arrayList.get(k);
            }
            paper_genetic[i] = itemArray;
        }
        System.out.println("=== init end ===");

    }



    /**
     * 每套试卷的适应度占比
     *
     *     方案  进行乘以一个exp 来进行适应度值的降低，高等数学里以自然常数e为底的指数函数
     *     题型比例 选择[0.2,0.4]  填空[0.2,0.4]  简答[0.1,0.3]  应用[0.1,0.3]
     *     属性比例 第1属性[0.2,0.4]  第2属性[0.2,0.4]  第3属性[0.1,0.3] 第4属性[0.1,0.3] 第5属性[0.1,0.3]
     *
     */
    private double[] getFitness(int paperSize){

        //log.info("适应值 log4j")

        // 所有试卷的适应度总和
        double fitSum = 0.0;
        // 每套试卷的适应度值
        double[] fitTmp = new double[paperSize];
        // 每套试卷的适应度占比
        double[] fitPro = new double[paperSize];

        // 计算试卷的适应度值，即衡量试卷优劣的指标之一 Fs
        for (int i = 0; i < paperSize; i++) {

            double adi1r =0;
            double adi2r =0;
            double adi3r =0;
            double adi4r =0;
            double adi5r =0;

            // 获取原始adi
            String [] itemList = paper_genetic[i];
            for (int j = 0; j < itemList.length; j++) {

                String[] splits = itemList[j].split(":");
                adi1r = adi1r + Double.parseDouble(splits[3]);
                adi2r = adi2r + Double.parseDouble(splits[4]);
                adi3r = adi3r + Double.parseDouble(splits[5]);
                adi4r = adi4r + Double.parseDouble(splits[6]);
                adi5r = adi5r + Double.parseDouble(splits[7]);

            }


            // 题型个数
            String [] expList = paper_genetic[i];
            int typeChose  = 0;
            int typeFill   = 0;
            int typeShort  = 0;
            int typeCompre = 0;


            //此次迭代各个题型的数目
            for (String s:expList) {

                //计算每种题型个数
                if(TYPE.CHOSE.toString().equals(s.split(":")[1])){
                    typeChose += 1;
                }
                if(TYPE.FILL.toString().equals(s.split(":")[1])){
                    typeFill += 1;
                }
                if(TYPE.SHORT.toString().equals(s.split(":")[1])){
                    typeShort += 1;
                }
                if(TYPE.COMPREHENSIVE.toString().equals(s.split(":")[1])){
                    typeCompre += 1;
                }
            }

            // 题型比例
            double typeChoseRation  =  typeChose/10.0;
            double typeFileRation   =  typeFill/10.0;
            double typeShortRation  =  typeShort/10.0;
            double typeCompreRation =  typeCompre/10.0;

            // 题型比例 选择[0.2,0.4]  填空[0.2,0.4]  简答[0.1,0.3]  应用[0.1,0.3]
            // 先判断是否在范围内，在的话，为0，不在的话，然后进一步和上下限取差值，绝对值
            double td1 ;
            if(typeChoseRation>=0.2 && typeChoseRation<0.4){
                td1 = 0;
            }else if(typeChoseRation<0.2){
                td1 =  Math.abs(0.2 - typeChoseRation);
            }else {
                td1 =  Math.abs(typeChoseRation - 0.4);
            }

            double td2 ;
            if(typeFileRation>=0.2 && typeFileRation<0.4){
                td2 = 0;
            }else if(typeFileRation<0.2){
                td2 =  Math.abs(0.2 - typeFileRation);
            }else {
                td2 =  Math.abs(typeFileRation - 0.4);
            }

            double td3 ;
            if(typeShortRation>=0.1 && typeShortRation<0.3){
                td3 = 0;
            }else if(typeShortRation<0.1){
                td3 =  Math.abs(0.1 - typeShortRation);
            }else {
                td3 =  Math.abs(typeShortRation - 0.3);
            }

            double td4 ;
            if(typeCompreRation>=0.1 && typeCompreRation<0.3){
                td4 = 0;
            }else if(typeCompreRation<0.1){
                td4 =  Math.abs(0.1 - typeCompreRation);
            }else {
                td4 =  Math.abs(typeCompreRation - 0.3);
            }


            // 属性个数
            int exp1 = 0;
            int exp2 = 0;
            int exp3 = 0;
            int exp4 = 0;
            int exp5 = 0;

            for (int j = 0; j < expList.length; j++) {
                String[] splits = expList[j].split(":");
                exp1 = exp1 + Integer.parseInt(splits[2].split(",")[0].substring(1,2));
                exp2 = exp2 + Integer.parseInt(splits[2].split(",")[1]);
                exp3 = exp3 + Integer.parseInt(splits[2].split(",")[2]);
                exp4 = exp4 + Integer.parseInt(splits[2].split(",")[3]);
                exp5 = exp5 + Integer.parseInt(splits[2].split(",")[4].substring(0,1));
            }

            // 属性比例 第1属性[0.2,0.4]   第2属性[0.2,0.4]   第3属性[0.1,0.3]  第4属性[0.1,0.3]  第5属性[0.1,0.3]
            //先判断是否在范围内，在的话，为0，不在的话，然后进一步和上下限取差值，绝对值
            double ed1 ;
            double edx1 = exp1/23.0;
            if(edx1>=0.2 && edx1<0.4){
                ed1 = 0;
            }else if(edx1<0.2){
                ed1 =  Math.abs(0.2 - edx1);
            }else {
                ed1 =  Math.abs(edx1 - 0.4);
            }

            double ed2 ;
            double edx2 = exp2/23.0;
            if(edx2>=0.2 && edx2<0.4){
                ed2 = 0;
            }else if(edx2<0.2){
                ed2 =  Math.abs(0.2 - edx2);
            }else {
                ed2 =  Math.abs(edx2 - 0.4);
            }

            double ed3 ;
            double edx3 = exp3/23.0;
            if(edx3>=0.1 && edx3<0.3){
                ed3 = 0;
            }else if(edx3<0.1){
                ed3 =  Math.abs(0.1 - edx3);
            }else {
                ed3 =  Math.abs(edx3 - 0.3);
            }

            double ed4 ;
            double edx4 = exp4/23.0;
            if(edx4>=0.1 && edx4<0.3){
                ed4 = 0;
            }else if(edx4<0.1){
                ed4 =  Math.abs(0.1 - edx4);
            }else {
                ed4 =  Math.abs(edx4 - 0.3);
            }

            double ed5 ;
            double edx5 = exp5/23.0;
            if(edx5>=0.1 && edx5<0.3){
                ed5 = 0;
            }else if(edx5<0.1){
                ed5 =  Math.abs(0.1 - edx5);
            }else {
                ed5 =  Math.abs(edx5 - 0.3);
            }

            // 惩罚
            double expNum = -(td1 + td2 + td3 + td4 + ed1 + ed2 + ed3 + ed4 + ed5);

            //System.out.printf("exp(%.3f) 为 %.3f%n", expNum, Math.exp(expNum));

            //均值 和 最小值
            double avgrum = (adi1r + adi2r + adi3r + adi4r + adi5r)/5 ;
            double minrum = Math.min(Math.min(Math.min(Math.min(adi1r,adi2r),adi3r),adi4r),adi5r) * 100 ;

            //适应度值 (min * 惩罚系数)
            minrum = minrum * Math.exp(expNum);
            //个体、总和
            fitTmp[i] = minrum ;
            fitSum = fitSum + minrum ;

        }


        for (int i = 0; i < paperSize; i++) {
            //  各自的比例
            fitPro[i] = fitTmp[i] / fitSum;
        }

        //冒泡排序 打印top10
        //bubbleSort(fitTmp);

        return  fitPro;
    }


    /**
     * 实现多峰函数 f(x) = sin6(5πx)
     *
     */
    @Test
    public void sin1(){

        for (double i = 0; i < 1; i=i+0.001) {
            double degrees = 5 * 180 * i;
            //将角度转换为弧度
            double radians = Math.toRadians(degrees);
            //正弦
            //System.out.format("%.1f 度的正弦值为 %.4f%n", degrees, Math.sin(radians));
            //次方
            //System.out.format("pow(%.3f, 6) 为 %.10f%n", Math.sin(radians),  Math.pow(Math.sin(radians), 6))
            System.out.format("%f 为 %.10f%n", i,  Math.pow(Math.sin(radians), 6));
        }


    }


    /**
     * 实现多峰函数 f(x) = e-2*ln(2)*((x-0.1)/0.8)2 * sin6(5π(x3/4-0.05))
     *
     */
    @Test
    public void sin2(){

        for (double i = 0; i < 1; i=i+0.001) {


            //自然常数e的近似值
            double e = Math.E;
            //System.out.println("e="+e);

            //e次方数
            double y = -2 * Math.log(2) * (Math.pow((i-0.1)/0.8,2));

            //输出结果
            double d = Math.pow(e, y);
            //System.out.println("e^"+y+"="+d);


            double degrees = 5 * 180 * (Math.pow(i,0.75)-0.05);
            //将角度转换为弧度
            double radians = Math.toRadians(degrees);

            System.out.format("%f 为 %.10f%n", i,  d * Math.pow(Math.sin(radians), 6));

        }


    }



}




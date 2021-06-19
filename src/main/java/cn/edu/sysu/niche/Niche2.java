package cn.edu.sysu.niche;

import cn.edu.sysu.adi.TYPE;
import cn.edu.sysu.utils.JDBCUtils4;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;

/**
 * @Author : song bei chang
 * @create 2021/6/18 22:13
 *
 *
 *
 * Crossover interactions among niches
 * 确定性拥挤算法(W. Mahfoud,1994)
 * 1.有放回的随机选取两个父代个体p1 p2
 * 2.父代交叉、变异，产生新个体：c1 c2
 * 3.替换阶段(采用拥挤思想来决定下一代)：
 *        3.1 如果[d(p1,c1)+d(p2,c2)]<=[d(p1,c2)+d(p2,c1)]
 *                     如果f(c1)>f(p1),则用c1替换p1,否则保留p1;
 *                     如果f(c2)>f(p2),则用c2替换p2,否则保留p2;
 *         3.2 否则
 *                      如果f(c1)>f(p2),则用c1替换p2,否则保留p2;
 *                      如果f(c2)>f(p1),则用c2替换p1,否则保留p1;
 *
 *
 *
 * Finding multimodal solutions using restricted tournament selection
 * 限制锦标赛拥挤算法(Georges R. Harik,1995)
 *  1.有放回的随机选取两个父代个体p1 p2
 *  2.父代交叉、变异，产生新个体：c1 c2
 *  3.分别为个体c1/c2从当前种群中随机选取w个个体
 *  4.设d1/d2分别为w个个体中与c1/c2距离最近的两个个体
 *  5.如果f(c1)>f(d1),则用c1替换d1,否则保留d1;
 *    如果f(c2)>f(d2),则用c2替换d2,否则保留d2;
 *
 *
 */
public class Niche2 {

    private JDBCUtils4 jdbcUtils = new JDBCUtils4();

    /** 确定性拥挤算法 */
    public void deterministic_crowding(){


    }



    /** 选择锦标赛 */
    public void  RTS(String[][] paperGenetic) throws SQLException {

        //有放回的随机选取两个父代个体p1 p2
        Random random = new Random();
        String[] p1 = paperGenetic[random.nextInt(100)];
        String[] p2 = paperGenetic[random.nextInt(100)];


        //父代交叉、变异，产生新个体c1 c2
        ArrayList<String[]> cList = crossMutate(p1, p2);


        //分别为个体c1/c2从当前种群中随机选取w个个体
        ArrayList<ArrayList[]> cwList = championship();

        //5.如果f(c1)>f(d1),则用c1替换d1,否则保留d1;
        //  如果f(c2)>f(d2),则用c2替换d2,否则保留d2;
        closestResemble(cList,cwList);



    }

    private void closestResemble(ArrayList<String[]> cList, ArrayList<ArrayList[]> cwList) {
        //  表现型  适应度值，或者adi()
        //  基因型 解(2,3,56,24,4,6,89,98,200,23)
        String[] c1 = cList.get(0);
        String[] c2 = cList.get(1);

        ArrayList<String>[] cw1 = cwList.get(0);
        ArrayList<String>[] cw2 = cwList.get(1);

        // 选取表现型做相似性校验
        similarPhen(c1,cw1);


        //选取基因型做相似性校验
        similarGene(c1,cw1);



    }

    private void similarPhen(String[] c1, ArrayList<String>[] cw1) {
        //获取min adi
        //在cw1中寻找c1的近似解
        double minADI = getMinADI(c1);
        HashMap<Integer, Double> similarMap = new HashMap<>(1);
        similarMap.put(9999,9999999.99);
        double min = similarMap.get(9999);
        Map<Integer, Double> sortMapByValues = null;

        for (int i = 0; i < cw1.length; i++) {
            // arrayList 转 数组
            String[] itemArray = new String[cw1[i].size()];
            for (int j = 0; j < cw1[i].size(); j++) {
                itemArray[j] = cw1[i].get(j);
            }
            System.out.println(i+" : "+Math.abs(minADI - getMinADI(itemArray)));
            if(min > Math.abs(minADI - getMinADI(itemArray))){
                similarMap.put(i,Math.abs(minADI - getMinADI(itemArray)));
                min = Math.abs(minADI - getMinADI(itemArray));

            }
            sortMapByValues = sortMapByValues(similarMap);

        }

        // JDK1.5中,应用新特性For-Each循环
        // 遍历方法二
        int i = 0;
        Integer key = 9999;
        for (Map.Entry<Integer, Double> entry : sortMapByValues.entrySet()) {
            if(i == 0){
                key = entry.getKey();
                Double value = entry.getValue();
                System.out.println("key=" + key + " value=" + value);
                i ++ ;
            }
        }
        //输出最接近的元素  并做全局变量的替换
        ArrayList<String> list = cw1[key];

    }

    /**
     * 按照value倒序排序
     */
    private  <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues(Map<K, V> aMap) {
        HashMap<K, V> finalOut = new LinkedHashMap<>();
        aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
        return finalOut;
    }


    private double getMinADI(String[] c1){

        double adi1r =0;
        double adi2r =0;
        double adi3r =0;
        double adi4r =0;
        double adi5r =0;


        String [] itemList = c1;
        for (int j = 0; j < itemList.length; j++) {

            String[] splits = itemList[j].split(":");
            adi1r = adi1r + Double.parseDouble(splits[3]);
            adi2r = adi2r + Double.parseDouble(splits[4]);
            adi3r = adi3r + Double.parseDouble(splits[5]);
            adi4r = adi4r + Double.parseDouble(splits[6]);
            adi5r = adi5r + Double.parseDouble(splits[7]);

        }

        // 进行乘以一个exp 来进行适应度值的降低    高等数学里以自然常数e为底的指数函数
        //     题型比例 选择[0.2,0.4]  填空[0.2,0.4]  简答[0.1,0.3]  应用[0.1,0.3]
        //     属性比例 第1属性[0.2,0.4]   第2属性[0.2,0.4]   第3属性[0.1,0.3]  第4属性[0.1,0.3]  第5属性[0.1,0.3]


        // 题型个数
        String [] expList = c1;
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

        //System.out.println("目前题型和属性超额情况： td1:"+td1+" td2:"+td2+" td3:"+td3+" td4:"+td4 + "ed1:"+ed1+" ed2:"+ed2+" ed3:"+ed3+" ed4:"+ed4+" ed5:"+ed5);

        // 惩罚个数  只有比例不符合要求时才惩罚，故不会有太大的影响
        double expNum = -(td1 + td2 + td3 + td4 + ed1 + ed2 + ed3 + ed4 + ed5);


        //均值 和 最小值
        double minrum = Math.min(Math.min(Math.min(Math.min(adi1r,adi2r),adi3r),adi4r),adi5r) * 100 ;

        System.out.println("minrum: "+minrum);

        //适应度值 (min * 惩罚系数)
        minrum = minrum * Math.exp(expNum);

        return minrum;



    }

    private void similarGene(String[] c1, ArrayList[] cw1){

        HashSet c1Set = new HashSet<>(10);

        //获取c1 c2 的基因型  list.add(id+":"+type+":"+attributes)
        for (String s : c1) {
            String id = s.split(":")[0];
            c1Set.add(id);
        }

        //在cw1中寻找c1的近似解
        for (int i = 0; i < cw1.length; i++) {

            ArrayList cw = cw1[i];
            HashSet<String> idSet = new HashSet<>(10);

            for (int j = 0; j < cw.size(); j++) {
                String id = cw.get(j).toString().split(":")[0];
                idSet.add(id);
            }
            //计算相似性,并保存最优解  大面积出现相似性为0或者1 的情况
            idSet.retainAll(c1Set);
            System.out.println("t"+i+" : "+idSet.size());
        }
        System.out.println("********************");
    }

    private ArrayList<ArrayList[]> championship() throws SQLException {

        //9元锦标赛
        int num = 9 ;
        //ArrayList<String[]>
        ArrayList<String>[] cwList1 = new ArrayList[num];
        ArrayList<String>[] cwList2 = new ArrayList[num];

        //概率相等的选取n个体
        for (int i = 0; i < num; i++) {
            ArrayList<String> c1w = jdbcUtils.championshipSet(10);
            ArrayList<String> c2w = jdbcUtils.championshipSet(10);
            cwList1[i] = c1w;
            cwList2[i] = c2w;
        }


        ArrayList<ArrayList[]> cwList = new ArrayList<>(2);

        cwList.add(cwList1);
        cwList.add(cwList2);
        return cwList;

    }

    private ArrayList<String[]> crossMutate(String[] p1, String[] p2) throws SQLException {
        //  单点交叉
        int length = 10;
        int a = new Random().nextInt(length);
        String [] c1 = new String[length];
        String [] c2 = new String[length];

        if (a >= 0) {
            System.arraycopy(p1, 0, c1, 0, a);
        }

        if (length - a >= 0) {
            System.arraycopy(p2, a, c1, a, length - a);
        }

        if (a >= 0) {
            System.arraycopy(p2, 0, c2, 0, a);
        }

        if (length - a >= 0) {
            System.arraycopy(p1, a, c2, a, length - a);
        }

        //c1变异
        Random random = new Random();
        //length-1
        int mutatePoint = random.nextInt(length-1);
        //将Array 转 hashSet
        Set<String> set = new HashSet<>(Arrays.asList(c1));

        //将要变异的元素
        String s = c1[mutatePoint];
        set.remove(s);
        int removeId = Integer.parseInt(s.split(":")[0]);

        //单套试卷临时存储容器
        String[] c11 = new String[length];

        //生成一个不存在set中的key
        while (set.size() != length ){
            String key = random.nextInt(310)+1+"";
            if (!(key+"").equals(removeId)){
                ArrayList<String> list = jdbcUtils.selectBachItem(key);
                set.add(list.get(0)+"");
            }
        }
        set.toArray(c11);

        //c2变异
        int mutatePoint2 = random.nextInt(length-1);
        //将Array 转 hashSet
        Set<String> set2 = new HashSet<>(Arrays.asList(c2));

        //将要变异的元素
        String s2 = c1[mutatePoint2];
        set.remove(s2);
        int removeId2 = Integer.parseInt(s2.split(":")[0]);

        //单套试卷临时存储容器
        String[] c21 = new String[length];

        //生成一个不存在set中的key
        while (set2.size() != length ){
            String key = random.nextInt(310)+1+"";
            if (!(key+"").equals(removeId2)){
                ArrayList<String> list = jdbcUtils.selectBachItem(key);
                set2.add(list.get(0)+"");
            }
        }
        set2.toArray(c21);


        ArrayList<String[]> cList = new ArrayList<>(2);
        cList.add(c11);
        cList.add(c21);
        return  cList;

    }


}




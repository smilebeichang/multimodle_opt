package cn.edu.sysu.niche;

import cn.edu.sysu.utils.JDBCUtils4;

import java.sql.SQLException;
import java.util.*;

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
        ArrayList<ArrayList<String>> cwList = championship(cList);

        //5.如果f(c1)>f(d1),则用c1替换d1,否则保留d1;
        //  如果f(c2)>f(d2),则用c2替换d2,否则保留d2;
        closestResemble(cList,cwList);



    }

    private void closestResemble(ArrayList<String[]> cList, ArrayList<ArrayList<String>> cwList) {
        //  表现型  适应度值，或者adi()
        //  基因型 解(2,3,56,24,4,6,89,98,200,23)
        //  本次选取基因型做相似性校验
        String[] c1 = cList.get(0);
        String[] c2 = cList.get(1);

        ArrayList<String> cw1 = cwList.get(0);
        ArrayList<String> cw2 = cwList.get(1);

        //在cw1中寻找c1的近似解
        //array --> set  --> contain (保留全局最优)
        //list转hashSet
        HashSet<String> set1 = new HashSet<>(cw1);

        for (String cw : cw1) {}



    }

    private ArrayList<ArrayList<String>> championship(ArrayList<String[]> cList) throws SQLException {

        //9元锦标赛   当N的个数无限接近题库大小时,其和轮盘赌的前半部分是一致的
        int num = 9 ;

        //概率相等的选取n个体
        ArrayList<String> c1w = jdbcUtils.selectChampionship(num);
        ArrayList<String> c2w = jdbcUtils.selectChampionship(num);

        ArrayList<ArrayList<String>> cwList = new ArrayList<>(2);
        cwList.add(c1w);
        cwList.add(c2w);
        return cwList;

    }

    private ArrayList<String[]> crossMutate(String[] p1, String[] p2) throws SQLException {
        //  单点交叉 是否需要设置与a一个平行变量b
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
        System.out.println(" 原试卷: "+set);

        //将要变异的元素
        String s = c1[mutatePoint];
        System.out.println("  remove element: "+ s);
        set.remove(s);
        int removeId = Integer.parseInt(s.split(":")[0]);
        System.out.println("  临时试卷：  "+set);

        //单套试卷临时存储容器
        String[] c11 = new String[length];

        //生成一个不存在set中的key
        while (set.size() != length ){
            String key = random.nextInt(310)+1+"";
            if (!(key+"").equals(removeId)){
                ArrayList<String> list = jdbcUtils.selectBachItem(key);
                set.add(list.get(0)+"");
            }
            System.out.println("  add element: "+ key);
        }
        set.toArray(c11);

        //c2变异
        int mutatePoint2 = random.nextInt(length-1);
        //将Array 转 hashSet
        Set<String> set2 = new HashSet<>(Arrays.asList(c2));
        System.out.println(" 原试卷: "+set);

        //将要变异的元素
        String s2 = c1[mutatePoint2];
        System.out.println("  remove element: "+ s2);
        set.remove(s2);
        int removeId2 = Integer.parseInt(s2.split(":")[0]);
        System.out.println("  临时试卷：  "+set2);

        //单套试卷临时存储容器
        String[] c21 = new String[length];

        //生成一个不存在set中的key
        while (set2.size() != length ){
            String key = random.nextInt(310)+1+"";
            if (!(key+"").equals(removeId2)){
                ArrayList<String> list = jdbcUtils.selectBachItem(key);
                set2.add(list.get(0)+"");
            }
            System.out.println("  add element: "+ key);
        }
        set2.toArray(c21);


        ArrayList<String[]> cList = new ArrayList<>(2);
        cList.add(c11);
        cList.add(c21);
        return  cList;

    }


}




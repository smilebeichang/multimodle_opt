package cn.edu.sysu.adi;

import cn.edu.sysu.pojo.Questions;
import cn.edu.sysu.utils.JDBCUtils;
import cn.edu.sysu.utils.KLUtils;
import org.junit.Test;

import java.util.ArrayList;

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
     * 在掌握了试题j所考察的所有知识点的情况下做错的概率 ps
     * 在并不完全掌握试题j所考察的所有知识点下猜对的概率 pg
     * 容器：适应度值
     */
      String student_have_attribute = "abc";
      double ps = 0.2;
      double pg = 0.1;
      double[] all_fitness =new double[4];

    /**
     * 模仿RUM
     *       rum -- kl -- adi
     *       1.找相关文献，dina 如何定义 adi  ( 可能存在点难度，查找 + 翻阅 )
     *       2.计算出adi
     *       3.指标信息同步到同一套试卷上
     *       4.评价解的好坏--》试卷--》adi的avg/min
     *
     *       5.最直接的方式是 将计算方式    由 rum 换成 dina
     *       6.实现最佳性能的所需测试数量，且诊断性能平衡，同时满足重要在测试长度，项目类型分布和重叠比例
     *       7.evaluation  test quality: 1) index-oriented and 2) simulation-oriented.
     *       8.最大程度地提高整体测试质量，最小化测试之间的最大差异，或两者的加权组合。(基于ADI)
     *
     *
     */
    @Test
    public void main1(){

    }

    /**
     * 根据概率算出K_L矩阵
     *      1.生成题库，并保存到数据库
     *      2.从数据库中读取数据
     *      3.计算单个xueshenS\对每套pattern的作答概率
     *      4.形成K_L矩阵
     */
    @Test
    public void main2(){

        JDBCUtils jdbcUtils = new JDBCUtils();
        ArrayList<String> list = jdbcUtils.select();
        Questions[] questions = new Questions[4];

        //读取数据，格式清洗
        for (String s:list) {
            Questions question = new Questions();
            String[] split = s.split(":");
            question.setId(Integer.valueOf(split[0]));
            question.setAttributes(split[1]);
            questions[Integer.valueOf(split[0])-1]=question;
        }


        ArrayList<Double> list1 = calFitness(questions);
        new KLUtils().foreach(list1,list1);

    }



    /**
     * 根据属性掌握情况，计算每道题的适应度值
     */
    public  ArrayList<Double> calFitness(Questions[] questions){

        ArrayList<Double> list = new ArrayList<>();

        for (int i = 0; i < questions.length; i++){
            boolean a = true;
            boolean b = true;
            boolean c = true;
            String attSub = questions[i].getAttributes().substring(1,questions[i].getAttributes().length()-1);
            String[]  attArray = attSub.split(",");

            for (int j = 0; j < attArray.length; j++){
                switch (j){
                    case 0: a = student_have_attribute.contains(attArray[j].trim());break;
                    case 1: b = student_have_attribute.contains(attArray[j].trim());break;
                    case 2: c = student_have_attribute.contains(attArray[j].trim());break;
                    default:break;
                }
            }
            int potential_responses = 0;
            if (a & b & c){
                potential_responses = 1;
            }
            // DINA 的适应度值计算公式
            double que_fit = (Math.pow(pg,(1-potential_responses))) * (Math.pow((1-ps),potential_responses));

            all_fitness[i]=que_fit;
            list.add(que_fit);
        }
        //遍历输出
        System.out.println("试题的适应度容器大小："+all_fitness.length+"\n试题的适应度如下： ");
        for (int i = 0; i < all_fitness.length; i++) {
            System.out.print(all_fitness[i]+",  ");
        }
        System.out.println();
        return list;

    }




}




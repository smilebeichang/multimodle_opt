package cn.edu.sysu.adi;

import cn.edu.sysu.pojo.Questions;
import cn.edu.sysu.utils.JDBCUtils;
import cn.edu.sysu.utils.KLUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
     * 定义一个student,其掌握的属性  有误差
     * 在掌握了试题j所考察的所有知识点的情况下做错的概率 ps
     * 在并不完全掌握试题j所考察的所有知识点下猜对的概率 pg
     * 容器：适应度值
     */
      String student_have_attribute = "abc";
      double ps = 0.2;
      double pg = 0.1;
      double[] all_fitness =new double[4];

    /**
     * 模仿RUM计算出概率
     *
     */
    @Test
    public void main1(){
        Questions question = new Questions();
        question.setId(0);
        question.setAttributes("[a, b]");

        Questions question1 = new Questions();
        question1.setId(0);
        question1.setAttributes("[b, d]");

        Questions question2 = new Questions();
        question2.setId(0);
        question2.setAttributes("[h, f, g]");

        Questions question3 = new Questions();
        question3.setId(0);
        question3.setAttributes("[b,c]");


        Questions[] questions = new Questions[4];
        questions[0] = question;
        questions[1] = question1;
        questions[2] = question2;
        questions[3] = question3;
        calFitness(questions);
    }

    /**
     * 根据概率算出K_L矩阵
     *      1.生成题库，并保存到数据库
     *      2.从数据库中读取数据
     *      2.计算单个学生对每套试卷的作答概率
     *      3.形成K_L矩阵
     */
    @Test
    public void main2(){
        JDBCUtils jdbcUtils = new JDBCUtils();
        ArrayList<String> list = jdbcUtils.select();
        Questions[] questions = new Questions[4];

        for (String s:list) {
            Questions question = new Questions();
            String[] split = s.split(":");
            question.setId(Integer.valueOf(split[0]));
            question.setAttributes(split[1]);
            questions[Integer.valueOf(split[0])]=question;
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

            double que_fit = (Math.pow(pg,(1-potential_responses))) * (Math.pow((1-ps),potential_responses));
            all_fitness[i]=que_fit;
            list.add(que_fit);
        }
        System.out.println("试题的适应度容器大小："+all_fitness.length+"\n试题的适应度如下： ");
        for (int i = 0; i < all_fitness.length; i++) {
            System.out.print(all_fitness[i]+",  ");
        }
        System.out.println();
        return list;

    }




}




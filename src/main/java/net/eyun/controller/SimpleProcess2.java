package net.eyun.controller;

import net.eyun.pojo.Papers;
import net.eyun.pojo.Questions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : songbeichang
 * @create 2020/11/23 0:17
 */
public class SimpleProcess2 {

    /**
     * 初始化题库
     */
    private static Integer ATTRIBUTE_MAX = 3;
    private static Integer TYPE_CHOICE_NUM = 20;
    private static Integer TYPE_FILL_NUM  = 10;
    private static Integer TYPE_SUMMARY_NUM  = 10;

    private static Integer ACT_CHOICE_NUM = 10;
    private static Integer ACT_FILL_NUM  = 5;
    private static Integer ACT_SUMMARY_NUM  = 5;
    private static Integer[][] paper_genetic =new Integer[100][20];

    /**
     * 容器
     */
    static Questions[] questions =new Questions[40];
    static  double[] all_fitness =new double[40];
    static  double[] paper_fitness =new double[100];
    static Integer[] best_genetic =new Integer[20];
    static Integer[] best_genetic_one =new Integer[20];


    /**
     * 定义一个student,其掌握的属性
     */
    static  String student_have_attribute = "abc";

    static  double ps = 0.2;
    static  double pg = 0.5;
    static double best_one =0;



    public static void main(String[] args) {

        Papers papers = new Papers();
        papers.setPaperSize(100);
        papers.setQuestSize(20);
        papers.setPc(1);
        papers.setPm(1);
        initItemBank();
//        for (int i = 0; i < 20; i++) {
//            System.out.println(questions[i].toString());
//
//        }
        init(papers);
        for (int i = 0; i < 100; i++) {
            selection(paper_genetic);
            crossCover(papers);
            mutate(papers);
            getALLFitness(questions);
//        for (int i = 0; i < 40; i++) {
//            System.out.println(i+" 试题的适应度："+all_fitness[i]);
//        }
            getPaperFitness(paper_genetic);
            elitiststrategy();
        }

    }


    /**
     * 生成题库(类型、属性、id)
     */
    public static void initItemBank(){

        //选择题
//        System.out.println("====== 选择题  ======");
        for (int i = 0; i < TYPE_CHOICE_NUM; i++) {
            Questions question = new Questions();
            String attributes = "";
            int attNum = new Random().nextInt(ATTRIBUTE_MAX);
            for (int j = 0; j < attNum+1; j++) {
                attributes = attributes + (char)(Math.random()*5+'a');
            }
            question.setId(i);
            question.setAttributes(attributes);
            questions[question.getId()]=question;
//            System.out.println("id："+question.getId()+" 属性："+question.getAttributes());

        }
        System.out.println();

        //填空题
//        System.out.println("====== 填空题  ======");
        for (int i = 0; i < TYPE_FILL_NUM; i++) {
            Questions question = new Questions();
            String attributes = "";
            int attNum = new Random().nextInt(ATTRIBUTE_MAX);
            for (int j = 0; j < attNum+1; j++) {
                attributes = attributes + (char)(Math.random()*5+'a');
            }
            question.setId(i+TYPE_CHOICE_NUM);
            question.setAttributes(attributes);
            questions[question.getId()]=question;
//            System.out.println("id："+question.getId()+" 属性："+question.getAttributes());
            System.out.print("");
        }
        System.out.println();

        //简答题
//        System.out.println("====== 简答题  ======");
        for (int i = 0; i < TYPE_SUMMARY_NUM; i++) {
            Questions question = new Questions();
            String attributes = "" ;
            int attNum = new Random().nextInt(ATTRIBUTE_MAX);
            for (int j = 0; j < attNum+1; j++) {
                attributes = attributes + (char)(Math.random()*5+'a');
            }
            question.setId(i+TYPE_CHOICE_NUM+TYPE_FILL_NUM);
            question.setAttributes(attributes);
            questions[question.getId()]=question;
//            System.out.println("id："+question.getId()+" 属性："+question.getAttributes());
        }


    }

    /**
     * 随机生成初代种群
     */
    public static void  init(Papers papers) {
        System.out.println();
//        System.out.println("=== init begin ===");
        for (int i = 0; i < papers.getPaperSize(); i++) {
            //随机生成取题目id序列
            int a1=1;
            int a2=1;
            Integer[] testGene= new Integer[papers.getQuestSize()];
            Set<Integer> set = new HashSet<Integer>();
            for(int j = 0; j < ACT_CHOICE_NUM; j++){
                //保证题目不重复,且满足题型约束
                while (set.size() == j ){
                    Integer key = new Random().nextInt(TYPE_CHOICE_NUM);
                    set.add(key);
                }
            }
            for(int j = 0; j < ACT_FILL_NUM; j++){
                while (set.size()-ACT_CHOICE_NUM == j ){
                    Integer key = new Random().nextInt(TYPE_FILL_NUM)+TYPE_CHOICE_NUM;
                    set.add(key);
                }
            }
            for(int j = 0; j < ACT_SUMMARY_NUM; j++){
                while (set.size()-ACT_CHOICE_NUM-ACT_FILL_NUM == j ){
                    Integer key = new Random().nextInt(TYPE_SUMMARY_NUM)+TYPE_FILL_NUM+TYPE_CHOICE_NUM;
                    set.add(key);
                }
            }
            set.toArray(testGene);
            Arrays.sort(testGene);
//            System.out.println(i+" 初始化："+Arrays.toString(testGene));
            paper_genetic[i] = testGene;
        }
//        System.out.println("=== init end ===");

    }


    /**
     * 交叉
     */
    public static void crossCover(Papers papers){
        System.out.println();
//        System.out.println("=== crossCover begin ===");
        Integer point = papers.getQuestSize();
        for (int i = 0; i < papers.getPaperSize()-1; i++) {
            if (Math.random() < papers.getPc()) {
                //单点交叉
                Integer[] temp1 = new Integer[point];
                Integer[] temp2 = new Integer[point];
                int a = new Random().nextInt(point);

                for (int j = 0; j < a; j++) {
                    temp1[j] = paper_genetic[i][j];
                }
                for (int j = a; j < point; j++) {
                    temp1[j] = paper_genetic[i+1][j];
                }
                int a1=1;
                correct(i,temp1,temp2);
            }
        }
//        System.out.println("=== crossCover end ===");
    }

    /**
     * 判断size，执行修补操作
     */
    public static void correct(int i,Integer[] temp1,Integer[] temp2) {

        int a1=1;
        Set<Integer> set_begin = new HashSet<Integer>(Arrays.asList(temp1));
        Set<Integer> set_end = new HashSet<Integer>();
        Set<Integer> set_choice = new HashSet<Integer>();
        Set<Integer> set_fill = new HashSet<Integer>();
        Set<Integer> set_summary = new HashSet<Integer>();
        int size = set_begin.size();
        int num_choice = 0;
        int num_fill = 0;
        int num_summary = 0;
        if (size == 20){
//            System.out.println(i+ " 正常交叉");
        }else{
//            System.out.println(i+ " 交叉导致类型不匹配： "+set_begin.size());

            //分别将三张类型的数量进行统计
            Iterator<Integer> it = set_begin.iterator();
            while (it.hasNext()) {
                Integer num =  it.next();
                if (num<20){
                    num_choice = num_choice+1;
                    set_choice.add(num);
                }else if (num < 30){
                    int a3=1;
                    num_fill = num_fill+1;
                    set_fill.add(num);
                }else if(num < 40){
                    num_summary = num_summary+1;
                    set_summary.add(num);
                }
            }
            int a2=1;
//            System.out.println("  choice: "+num_choice+" fill: "+num_fill+" summary: "+num_summary);

            if(num_choice<10){
                while(set_choice.size() != 10){
                    Integer key = new Random().nextInt(20);
                    set_choice.add(key);
                }
            }

            if(num_fill<5){
                while(set_fill.size() != 5){
                    Integer key = Math.abs(new Random().nextInt()) % 10 + 20;
                    set_fill.add(key);
                }
            }

            if(num_summary<5){
                while(set_summary.size() != 5){
                    Integer key = Math.abs(new Random().nextInt()) % 10 + 30;
                    set_summary.add(key);
                }
            }


            set_end.addAll(set_choice);
            set_end.addAll(set_fill);
            set_end.addAll(set_summary);
            set_end.toArray(temp1);
            Arrays.sort(temp1);
            paper_genetic[i]=temp1;
        }
//        System.out.println("  "+Arrays.toString(paper_genetic[i]));
    }


    /**
     * 变异
     */
    public static void mutate(Papers papers){
        System.out.println();
//        System.out.println("=== mutate begin ===");
        Integer key = 0;
        for (int i = 0; i < papers.getPaperSize(); i++) {
            if(Math.random() < papers.getPm()){
                Random random = new Random();
                int mutate_point = random.nextInt(papers.getQuestSize()-1);
                Set<Integer> set = new HashSet<Integer>(Arrays.asList( paper_genetic[i]));
                Integer s = paper_genetic[i][mutate_point];
                int a=1;
//                System.out.println(i+" 原试卷: "+set);
//                System.out.println("  remove element: "+ s);
                set.remove(s);
//                System.out.println("  现试卷：  "+set);

                Integer[] temp1 = new Integer[20];

                if (mutate_point<10){
                    int a2=1;
                    //生成一个合适的且不存在set中的key
                    while (set.size() != 20 ){
                        key = random.nextInt(20);
                        if (!key.equals(s)){
                            set.add(key);
                        }
                    }
                }else if(mutate_point<15){
                    int a3=1;
                    while (set.size() != 20 ){
                        key = Math.abs(new Random().nextInt()) % 10 + 20;
                        if (!key.equals(s)){
                            set.add(key);
                        }
                    }
                }else if(mutate_point<20){
                    int a4=1;
                    while (set.size() != 20 ){
                        key = Math.abs(new Random().nextInt()) % 10 + 30;
                        if (!key.equals(s)){
                            set.add(key);
                        }
                    }
                }
                set.toArray(temp1);
                Arrays.sort(temp1);
                paper_genetic[i]=temp1;
            }
//            System.out.println("  add element: "+ key);
//            System.out.println("  最终试卷： "+Arrays.toString(paper_genetic[i]));
//            System.out.println();
        }
//        System.out.println("=== mutate end ===");
    }

    /**
     * 计算每道题目的适应度
     */
    public static void getALLFitness(Questions[] question) {
        double function_value ;
        int p_temp ;
        for (int i = 0; i < 40; i++) {
            boolean b = student_have_attribute.contains(question[i].getAttributes());
            p_temp = b ? 1 : 0;
            function_value = Math.pow(pg,(1-p_temp))*Math.pow((1-ps), p_temp);
            all_fitness[i]=function_value;
        }
    }

    /**
     * 计算每张试卷的适应度
     */
    public static void getPaperFitness(Integer[][] paper_genetic) {
        for (int i = 0; i < 100; i++) {
            double function_value =0;
            Integer[] integers = paper_genetic[i];
            for (int j = 0; j < 20; j++) {
                function_value += all_fitness[integers[j]];
            }
            paper_fitness[i]=function_value;
            System.out.println(i+"试卷的适应度： "+function_value);
        }
    }



    public static Object[] best_value(){
        Object[] objects = new Object[3];
        double max_fitness = paper_fitness[0];
        int max_index = 0;
        int max_index_2 = 0;

        for (int i = 0; i < 100; i++) {
            if(paper_fitness[i]>max_fitness){
                max_fitness = paper_fitness[i];
                max_index = i;
                best_genetic = paper_genetic[max_index];
            }
        }

        if (max_fitness>best_one){   //局部变量和全局变量的比较
            best_one = max_fitness;
            max_index_2 = max_index;    //随着每次交叉，轮盘赌，下标产生了变化，只要保存住最优解的基因编码就ok
            best_genetic_one = paper_genetic[max_index_2];       //全局最优解
        }

        //index,fitness,gentic
        objects[0]=max_index;
        objects[1]=max_fitness;
        objects[2]=best_genetic;

        return objects;
    }

    public static void  elitiststrategy(){
        getPaperFitness(paper_genetic);
        Object[] objects = best_value();
        // 替换掉局部最优解
        paper_genetic[(Integer) objects[0]]= best_genetic_one;

    }



    public static   void selection(Integer[][] paper_genetic){
        int population_size = 100;
        double fitness_sum = 0;
        double[] fitness_proportion = new double[population_size];
        double cumsum = 0;
        double[] pie_fitness = new double[100];


        Integer[][] new_genetic_population =new Integer[population_size][];
        int random_selection_id = 0;

        for (int i = 0; i < population_size; i++) {
            fitness_sum += paper_fitness[i];
        }
        //各自的比例
        for (int i = 0; i < population_size; i++) {
            fitness_proportion[i] = paper_fitness[i] / fitness_sum;
        }
        //越大的适应度，其叠加时增长越快，所以有更大的概率被选中
        for (int i = 0; i < population_size; i++) {
            pie_fitness[i] = cumsum + fitness_proportion[i];
            cumsum += fitness_proportion[i];
        }
        //累加的概率为1
        pie_fitness[population_size-1] = 1;


        double[] random_selection = new double[population_size];

        for (int i = 0; i < population_size; i++) {
            random_selection[i] = Math.random();
        }
        //排序
        Arrays.sort(random_selection);

        //随着random_selection_id的递增,random_selection[random_selection_id]逐渐变大
        for (int i = 0; i < population_size; i++) {
            while (random_selection_id < population_size && random_selection[random_selection_id] < pie_fitness[i]){
                new_genetic_population[random_selection_id]   =paper_genetic[i];
                random_selection_id += 1;
            }
        }

        //重新赋值种群的编码
        paper_genetic=new_genetic_population;

    }


}




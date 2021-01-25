package net.sysu.GA;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Created by songb
 */
public class GA {

    private static int population_size ;
    private static int generations ;
    static double pc ;
    static double pchange ;

    static Integer[][] genetic_population =new Integer[500][24];
    static Integer[][] genetic_population_1 =new Integer[500][8];  //genetic_population = []
    static Integer[][] genetic_population_2 =new Integer[500][8];
    static Integer[][] genetic_population_3 =new Integer[500][8];


    static int Glength = 24;        //Glength = 24
    static int singlelength = 8;          //singlelength = 8


    static double[]  population_1 =new double[500];        //population_1 = []   # x1
    static double[]  population_2 =new double[500];
    static double[]  population_3 =new double[500];

    static  double[] fitness =new double[500];             //fitness[],


    static Integer[] best_gentic =new Integer[24];

    static Integer[] best_gentic_one =new Integer[24];


    static double best_one =0;      //flag

    private int iteration;
    private double minnum2;



    public static double getPc() {
        return pc;
    }

    public static void setPc(double pc) {
        GA.pc = pc;
    }

    public static double getPchange() {
        return pchange;
    }

    public static void setPchange(double pchange) {
        GA.pchange = pchange;
    }

    public static int getPopulation_size() {
        return population_size;
    }

    public static void setPopulation_size(int population_size) {
        GA.population_size = population_size;
    }

    public  int getGenerations() {
        return generations;
    }

    public static void setGenerations(int generations) {
        GA.generations = generations;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public double getMinnum2() {
        return minnum2;
    }

    public void setMinnum2(double minnum2) {
        this.minnum2 = minnum2;
    }

    /**
     * @Description:
     * genetic_population[i]
     *
     */
    public  void  init() {
        for (int i = 0; i < population_size; i++) {
            //初始化
            Integer[] population_i = new Integer[Glength];
            for (int j = 0; j < Glength; j++) {
                population_i[j] = Math.random() >= 0.5?0:1;
            }
            genetic_population[i] = population_i;
        }
    }


    public   void spiltlist(){
        //切割
        for (int i = 0; i < genetic_population.length ; i++) {
            for (int j = 0; j < singlelength; j++) {
                genetic_population_1[i][j]=genetic_population[i][j];
            }
        }
        for (int i = 0; i < genetic_population.length ; i++) {
            for (int j = singlelength; j < singlelength*2; j++) {
                genetic_population_2[i][j-singlelength]=genetic_population[i][j];
            }
        }
        for (int i = 0; i < genetic_population.length ; i++) {
            for (int j = singlelength*2; j < Glength; j++) {
                genetic_population_3[i][j-(singlelength*2)]=genetic_population[i][j];
            }
        }

    }


    /**
     * Math.pow(a, b)
     */
    public   void decoding(){

        for (int i = 0; i < population_size; i++) {
            double value1 = 0;
            double value2 = 0;
            double value3 = 0;
            for (int j = 0; j < singlelength; j++) {
                value1 +=genetic_population_1[i][j]*Math.pow(2, (singlelength-1-j));
                value2 +=genetic_population_2[i][j]*Math.pow(2, (singlelength-1-j));
                value3 +=genetic_population_3[i][j]*Math.pow(2, (singlelength-1-j));
            }
            population_1[i] = value1 - 100;   //??????[-100,+155]
            population_2[i] = value2 - 100;
            population_3[i] = value3 - 100;
        }

    }


    public  void cfitness() {
        double funcion_value = 0;
        for (int i = 0; i < population_size; i++) {            //3000-?????????????????~~~????????

            //f2
            double a = Math.abs(population_1[i]/10);double b = Math.abs(population_2[i]/10);double c = Math.abs(population_3[i]/10);
            funcion_value = 30 + 1000 - a - b - c - a*b*c;



            if (funcion_value>0){           //?????????????????????????
                fitness[i]=funcion_value;   //?????????????????????0,
            }else{
                fitness[i]=0;
            }
        }

    }


    public  Object[] best_value(){
        Object[] objects = new Object[7];        //new????????????????
        double max_fitness = fitness[0];
        int max_number1 = 0;
        int max_number1_2 = 0;             //????????????????,???????

        for (int i = 0; i < population_size; i++) {                //???????????????????????
            if(fitness[i]>max_fitness){
                max_fitness = fitness[i];
                max_number1 = i;
                best_gentic = genetic_population[max_number1];       //?????????
            }
        }


        if (max_fitness>best_one){   //???????????????????
            best_one = max_fitness;
            max_number1_2 = max_number1;    //???????????????????????????????????????????????????ok
            best_gentic_one = genetic_population[max_number1_2];       //????????
        }


        objects[0]=max_number1;     //??????????????????   index,fitness,gentic
        objects[1]=max_fitness;
        objects[2]=best_gentic;

        return objects;
    }

    public   void selection(){
        double fitness_sum = 0;                     // 8?????????
        double[] fitness_proportion = new double[population_size];
        double cumsum = 0;
        double[] pie_fitness = new double[500];


        Integer[][] new_genetic_population =new Integer[population_size][];    //????????????
        int random_selection_id = 0;

        for (int i = 0; i < population_size; i++) {
            fitness_sum += fitness[i];          //??????????????????fitness_sum???????,fitness[]????????????????
        }
        for (int i = 0; i < population_size; i++) {
            fitness_proportion[i] = fitness[i] / fitness_sum;   //????????
        }
        for (int i = 0; i < population_size; i++) {
            pie_fitness[i] = cumsum + fitness_proportion[i];    //?????????????????????????????????????????
            cumsum += fitness_proportion[i];
        }
        pie_fitness[population_size-1] = 1;           //????????1


        double[] random_selection = new double[population_size];

        for (int i = 0; i < population_size; i++) {
            random_selection[i] = Math.random();
        }
        Arrays.sort(random_selection);          //????

        //????random_selection_id?????,random_selection[random_selection_id]????
        for (int i = 0; i < population_size; i++) {
            while (random_selection_id < population_size && random_selection[random_selection_id] < pie_fitness[i]){
                new_genetic_population[random_selection_id]   =genetic_population[i];
                random_selection_id += 1;
            }
        }

        //????????????????????????
        genetic_population=new_genetic_population;


    }


    public  void crosssover(){
        for (int i = 0 ; i < population_size-1; i++) {
            if (Math.random() < pc) {
                Random random = new Random();
                int a = random.nextInt(Glength);      //??????
                Integer[] temp1 = new Integer[Glength];
                Integer[] temp2 = new Integer[Glength];

                for (int j = 0; j < a; j++) {
                    temp1[j] = genetic_population[i][j];
                }
                for (int j = a; j < Glength; j++) {
                    temp1[j] = genetic_population[i+1][j];
                }
                for (int j = 0; j < a; j++) {
                    temp2[j] = genetic_population[i+1][j];
                }
                for (int j = a; j < Glength; j++) {
                    temp2[j] = genetic_population[i][j];
                }
                genetic_population[i]=temp1;
                genetic_population[i+1]=temp2;
            }

                /*if (Math.random() < pc) {
                    Random random = new Random();
                    int a = random.nextInt(24-1);      //?????
                    int b = random.nextInt(24-1);
                    int min = a > b ? b : a;
                    int max = a > b ? a : b;
                    //?????????????????????????a~b,?????????????????????????
                    int t;
                    for (int j = min; j <= max; j++) {
                        t = genetic_population[i][j];
                        genetic_population[i][j] = genetic_population[i + 1][j];
                        genetic_population[i + 1][j] = t;
                    }
                }*/
        }
    }


    public   void change(){
        for (int i = 0; i < population_size; i++) {
            if(Math.random() < pchange){
                Random random = new Random();
                int change_point = random.nextInt(Glength-1);
                if(genetic_population[i][change_point]==0){
                    genetic_population[i][change_point] = 1;
                }else{
                    genetic_population[i][change_point] = 0;
                }

            }
        }
    }

    public  void  elitiststrategy(){
        cfitness();                        //  ????????????? ????????
        Object[] objects = best_value();   //  ????????????????+????????+?????????
        // ??I??????????
        genetic_population[(Integer) objects[0]]= best_gentic_one;    //?????????????

    }


}

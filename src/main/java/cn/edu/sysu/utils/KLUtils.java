package cn.edu.sysu.utils;

import jeasy.analysis.MMAnalyzer;
import org.junit.Test;


import java.io.*;
import java.util.ArrayList;

/**
 * @Author : song bei chang
 * @create 2021/5/6 19:09
 */
public class KLUtils {


    /**
     *  主程序
     */
    public static void KLCal(String path_one,String path_two) throws FileNotFoundException, IOException {

        // TODO Auto-generated method stub;
        ArrayList<Entity> enList1;
        enList1=CalcuP(path_one);
        System.out.println(" size "+enList1.size());

        ArrayList<Entity> enList2;
        enList2=CalcuP(path_two);


        double f1=CalKL(enList1,enList2);
        double f2=CalKL(enList2,enList1);
        double f7=CalKL(enList1,enList1);
        System.out.println("《PATH_ONE》与《PATH_TWO》的KL距离:   "+f1);
        System.out.println("《PATH_TWO》与《PATH_ONE》的KL距离:   "+f2);
        System.out.println("《PATH_ONE》与《PATH_ONE》的KL距离:   "+f7);


    }

    /**
     * 读取文件
     *
     **/
    public static String GetFileText(String path) throws  FileNotFoundException,IOException
    {
        InputStreamReader inStreamReader=new InputStreamReader(new FileInputStream(path),"UTF-8");
        BufferedReader bufReader=new BufferedReader(inStreamReader);
        String line;
        StringBuilder sb=new StringBuilder();
        while((line=bufReader.readLine())!=null) {
            sb.append(line+"　");
        }
        inStreamReader.close();
        bufReader.close();
        String strFile=sb.toString();
        return strFile;

    }

    /**
     * 将一段文本切成单词序列
     **/
    public static String CutText(String path)throws FileNotFoundException,IOException {

        String fileText=GetFileText(path);

        MMAnalyzer analyzer=new MMAnalyzer();
        String result =null;
        String spliter="|";
        try {
            result = analyzer.segment(fileText, spliter);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }




    /**
     * 计算文本中每个词语出现的相对频率代替概率
     *
     */
    public static ArrayList<Entity> CalcuP(String path) throws IOException
    {    //以词为单位计算相对熵
        String result=CutText(path);
        //以字为单位计算相对熵
        //String result=CutTextSingleCharacter(path);
        String []words=result.split("\\|");


        ArrayList<Entity> enList=new ArrayList();
        for(String w: words) {  w=w.trim();
            Entity en=new Entity();
            en.word=w;
            en.pValue=1;
            enList.add(en);
        }

        float total=enList.size();
        for(int i=0;i<enList.size()-1;i++) {
            if(!enList.get(i).word.isEmpty())
            {
                for(int j=i+1;j<enList.size();j++)
                {
                    if(enList.get(i).word.equals(enList.get(j).word))
                    {
                        enList.get(i).pValue++;
                        enList.get(j).pValue=0;
                        enList.get(j).word="";
                    }
                }
            }
        }
        for(int i=enList.size()-1;i>=0;i--)
        {
            if(enList.get(i).pValue<1.0) {
                enList.remove(i);
            }
        }
        for(int i=0;i<enList.size();i++)
        {
            enList.get(i).pValue=enList.get(i).pValue/total;
        }
        return enList;
    }


    /**
     * 用于计算两段文本的相对熵
     *
     **/
    public static float CalKL(ArrayList<Entity>p,ArrayList<Entity>q) {
        float kl=0;
        float infinity=10000000;//无穷大
        double accretion=infinity;//设置熵增加量的初始值为无穷大。
        //从q中找出与p中相对应词的概率，如果找到了，就将accretion的值更新，并累加到相对熵上面；如果没找到，则增加了为无穷大
        for(int i=0;i<p.size();i++) {
            if(q.size()!=0)
            {   for(int j=q.size()-1;j>=0;j--) {
                if(p.get(i).word.equals(q.get(j).word)) {
                    accretion=p.get(i).pValue*Math.log(p.get(i).pValue/q.get(j).pValue);
                    break;
                }
            }
                kl+=accretion;
                accretion=infinity;
            }
        }
        return kl;
    }


    /**
     * 计算 K_L 矩阵
     * 并使用foreach方法对二维数组进行遍历
     *
     */
    public   Double[][] foreach( ArrayList<Double> lists1,ArrayList<Double> lists2) {

        //计算 K_L
        System.out.println();
        Double[][] klArray =new Double[lists1.size()][lists2.size()];
        for (int i = 0;i<lists1.size();i++){
            for (int j = 0; j < lists2.size(); j++) {
                //(0,0) vs (0,0)   K_L 的计算公式
                double v = lists1.get(i) * Math.log(lists1.get(i) / lists2.get(j)) +
                        (1 - lists1.get(i)) * Math.log((1 - lists1.get(i)) / (1 - lists2.get(j)));

                v = Double.valueOf((v+"0000").substring(0,4));
                klArray[i][j] = v;
            }
        }

        return klArray;
    }

    public  void arrayPrint(Double[][] klArray) {
        //遍历输出 K_L 矩阵
        System.out.println("K_L information矩阵如下: ");
        for (Double[] fs:klArray) {
            for (Double fss:fs) {
                //相当于arr[i][j]
                System.out.print(fss+"  ");
            }
            System.out.println();
        }
    }



    /**
     * 返回 rum 的 集合 ArrayList<ArrayList<Double>>
     * @param base  基线系数
     * @param penalty 惩罚系数
     */
    public ArrayList<ArrayList<Double>> GetRumListsRandom(Double base,Double penalty){

        ArrayList<ArrayList<Double>> patternLists = new ArrayList<>();

        //试题试卷_pattern ips
        ArrayList<String> ips = new ArrayList<String>(){{
            add("(0,0,0)");
            add("(0,0,1)");
            add("(0,1,0)");
            add("(1,0,0)");
            add("(0,1,1)");
            add("(1,0,1)");
            add("(1,1,0)");
            add("(1,1,1)");
        }};

        //定义学生pattern
        ArrayList<String> sps = ips;

        for (String pattern : ips) {
            //根据学生pattern vs 题目pattern 获取答对此题的rum
            int b1 = Integer.parseInt(pattern.substring(1, 2));
            int b2 = Integer.parseInt(pattern.substring(3, 4));
            int b3 = Integer.parseInt(pattern.substring(5, 6));

            ArrayList<Double> lists0 = new ArrayList<>();

            for (String sp : sps) {
                int a1 = Integer.parseInt(sp.substring(1, 2));
                int a2 = Integer.parseInt(sp.substring(3, 4));
                int a3 = Integer.parseInt(sp.substring(5, 6));

                //a>=b则*1, a<b 则*penalty^1
                boolean ab1 = a1 >= b1;
                boolean ab2 = a2 >= b2;
                boolean ab3 = a3 >= b3;
                int num1 = ab1?0:1;
                int num2 = ab2?0:1;
                int num3 = ab3?0:1;
                int sum = num1 + num2 + num3;

                double p = base * Math.pow(penalty, sum);

                lists0.add(p);
            }
            patternLists.add(lists0);
        }
        return  patternLists;

    }


}




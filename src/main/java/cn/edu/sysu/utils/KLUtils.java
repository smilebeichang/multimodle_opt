package cn.edu.sysu.utils;

import jeasy.analysis.MMAnalyzer;


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
                //System.out.println(lists1.get(i) + " * " +  Math.log(lists1.get(i) / lists2.get(j)) +" " +(1 - lists1.get(i)) + " * " + Math.log((1 - lists1.get(i)) / (1 - lists2.get(j))));
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
                System.out.print(fss+"  ");//相当于arr[i][j]
            }
            System.out.println();
        }
    }


    /**
     *  定义一个返回能够返回所有元素的子集
     */
     ArrayList<ArrayList<Integer>> sub(ArrayList<Integer> arr, int index) {
        //声明一个装子集的集合
        ArrayList<ArrayList<Integer>> all = new ArrayList<>();
        //判断:如果传入的集合长度==传入的元素索引(实质上是要求子集的元素个数),即前面的所有元素都安排完了
        if(arr.size() == index){
            //添加一个空的集合
            all.add(new ArrayList<>());
        }else{
            //递归调用:从索引为0的元素开始将索引增加不断调用
            all = sub(arr, index+1);
            //获得当前索引的元素
            int item = arr.get(index);
            //声明一个装所有(index-1)个元素的所有子集元素+当前索引元素的集合
            ArrayList<ArrayList<Integer>> subsets =  new ArrayList<>();
            //遍历包含index-1的所有子集和的集合,将其中的子集输出
            for(ArrayList<Integer> s: all){
                //声明一个新的数组来装(index-1)个元素的所有子集元素+当前索引index元素的集合
                ArrayList<Integer> newSubset = new ArrayList<>();
                //先将(index-1)个元素的每一个子集添加到新的集合中
                newSubset.addAll(s);
                //再将index位置的元素添加进去
                newSubset.add(item);
                //最后将新的子集添加到集合subsets中
                subsets.add(newSubset);
            }
            //最后将加入新的元素后的所有子集添加到包含(index-1)个元素的所有子集的集合当中当中
            all.addAll(subsets);
        }
         System.out.println(all.size());
        return all;
    }

    public  void Combin() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        System.out.println(arrayList);

        System.out.println(sub(arrayList,0));

    }




}




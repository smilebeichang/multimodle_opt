package cn.edu.sysu.adi;

import cn.edu.sysu.utils.KLUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author : song bei chang
 * @create 2021/6/9 23:13
 */
public abstract class Pseudocode {


    /**
     *
     *  1.保留选择和精英策略  （基数太小、交叉变异未能生效）
     *  2.优化长度和题型的校验设计
     *  3.比例不要写死,out 取并集
     *
     *
     */
    abstract  void correct();
    abstract  void correctLength();
    abstract  void correctType();
    abstract  void correctAttribute();



    public  void main(String[] args) {

        // 迭代3次  出现只有三套试卷可选
        // 迭代6次  大概率十套试卷一模一样
        // 有了评估的适应度函数，便可以根据适者生存法则将优秀者保留下来了。选择则是根据新个体的适应度进行，但同时不意味着完全以适应度高低为导向（选择top k个适应度最高的个体，容易陷入局部最优解），因为单纯选择适应度高的个体将可能导致算法快速收敛到局部最优解而非全局最优解，我们称之为早熟。作为折中，遗传算法依据原则：适应度越高，被选择的机会越高，而适应度低的，被选择的机会就低。
        // 局部最优？ 导致过早成熟了吗 34次迭代就完全一致了
        //          交叉变异没能生效,忘记赋值给全局变量了


        //修补流程:
        //    1. 长度校验
        //    2. 题型校验
        //    3. 属性校验
        correct();
        {
            //1.长度校验
            correctLength();
            {
                if (size() == 10){
                    System.out.println("size正常,退出长度校验");
                }else{
                    System.out.println("size不匹配：进行长度修补 ");
                    //依次校验各个题型的比例信息，并修补    eg 选择题
                    while(typeChoseRation<expectedRatio && size() != 10){
                        //where type = Chose
                        String sql = " type = 'CHOSE' order by RAND() limit 1 ";
                        ArrayList<String> tmp = jdbcUtils.selectBySql(sql);
                        //动态更新比例信息
                        if(!set.contains(tmp)){
                            set.addAll(tmp);
                            typeChoseNum += 1;
                            typeChoseRation  =  typeChoseNum/10.0;
                        }
                    }

                    //随机选题
                    while(size() != 10){
                        //  where 1=1
                        String sql = " 1=1 order by RAND() limit 1 ";
                        ArrayList<String> arrayList = jdbcUtils.selectBySql(sql);
                        set.addAll(arrayList);
                    }
                }
            }

            //2.题型校验
            correctType();
            {
                //=========================  1.0 指标统计   ================================
                String typeFlag = "(1,0,0,-1)";

                //=========================  2.0 解集统计   ================================
                Set<String> outMore = new HashSet<>();
                Set<String> outLess = new HashSet<>();

                //=========================  3.0 执行修补操作   ================================
                /*
                 *  outMore outLess 的关系判断
                 *      1.outLess有  outLess无  则取 outLess
                 *      2.outLess无  outMore有  则取 outMore
                 *      3.outLess有  outMore有  ①不可能存在交集 ②做两次选取
                 *
                 */

            }


            //3.属性校验
            correctAttribute();
            {

                /*
                 * =========================  1.0 指标统计   ================================
                 * 数量-->比例-->flag  例如 attributeFlag = "(-1,1,0,0,0)"
                 *
                 */
                String attributeFlag = getAttributeFlag(paperGenetic[w]);

                int af1 = Integer.parseInt(attributeFlag.split(",")[0]);
                int af2 = Integer.parseInt(attributeFlag.split(",")[1]);
                int af3 = Integer.parseInt(attributeFlag.split(",")[2]);
                int af4 = Integer.parseInt(attributeFlag.split(",")[3]);
                int af5 = Integer.parseInt(attributeFlag.split(",")[4]);

                /*
                 * =========================  2.0 解集统计   ================================
                 *  ①取出属性比例过多的集合的并集
                 *  ②取出属性比例不足的集合的并集
                 */
                Set<String> outMore = getoutMore(bachItemList,af1,af2,af3,af4,af5);

                Set<String> outLess = getoutLess(bachItemList,af1,af2,af3,af4,af5);


                /*
                 * =========================  3.0 执行修补操作   ================================
                 *  outMore outLess 的关系判断
                 *      1.outLess有  outMore无  则取 outLess
                 *      2.outLess无  outMore有  则取 outMore
                 *      3.outLess有  outMore有  则①取交集  ②按权重取
                 *
                 */

                if(outMore.size()==0 && outLess.size()>0){
                    bachItemList = correctAttributeLess(outLess,jdbcUtils,bachItemList,af1,af2,af3,af4,af5);
                }


                if(outMore.size()>0 && outLess.size()==0){
                    bachItemList = correctAttributeMore(outMore,jdbcUtils,bachItemList,af1,af2,af3,af4,af5);
                }

                if(outMore.size()>0 && outLess.size()>0){
                    bachItemList = correctAttributeMoreAndLess(outMore,outLess,jdbcUtils,bachItemList,af1,af2,af3,af4,af5);
                }


                paperGenetic[w] = bachItemList;


            }
        }


    }



    /**
     * 指标信息初步统计
     *
     */
    private String getAttributeFlag(HashSet<String> itemSet){


        //属性个数
        int attributeNum1  = 0;
        int attributeNum2  = 0;
        int attributeNum3  = 0;
        int attributeNum4  = 0;
        int attributeNum5  = 0;

        //各个属性的数目
        for (String s:itemSet) {

            if("1".equals(s.split(":")[2].substring(1,2))){
                attributeNum1 += 1;
            }
            if("1".equals(s.split(":")[2].substring(3,4))){
                attributeNum2 += 1;
            }
            if("1".equals(s.split(":")[2].substring(5,6))){
                attributeNum3 += 1;
            }
            if("1".equals(s.split(":")[2].substring(7,8))){
                attributeNum4 += 1;
            }
            if("1".equals(s.split(":")[2].substring(9,10))){
                attributeNum5 += 1;
            }
        }


        //属性比例
        double attributeRatio1 = attributeNum1/23.0;
        double attributeRatio2 = attributeNum2/23.0;
        double attributeRatio3 = attributeNum3/23.0;
        double attributeRatio4 = attributeNum4/23.0;
        double attributeRatio5 = attributeNum5/23.0;


        int af1 ;
        if(attributeRatio1>=0.2 && attributeRatio1<=0.4){
            af1 = 0;
        }else if(attributeRatio1<0.2){
            af1 = -1;
        }else {
            af1 = 1;
        }

        int af2 ;
        if(attributeRatio2>=0.2 && attributeRatio2<=0.4){
            af2 = 0;
        }else if(attributeRatio2<0.2){
            af2 = -1;
        }else {
            af2 = 1;
        }

        int af3 ;
        if(attributeRatio3>=0.1 && attributeRatio3<=0.3){
            af3 = 0;
        }else if(attributeRatio3<0.1){
            af3 = -1;
        }else {
            af3 = 1;
        }

        //输出 attributeFlag
        String attributeFlag = af1+","+af2+","+af3+","+af4+","+af5;
        System.out.println("目前属性占比情况： attributeFlag:("+attributeFlag+")");

        return attributeFlag;

    }




    /**
     *  修补算子
     *  适用场景: att less
     *
     */
    private ArrayList<String> correctAttributeLess() throws SQLException {


        //SQL 均用and没影响  影响范围:inList  and条件使得解集变少，但更高效
        StringBuilder sb = new StringBuilder();
        if(af1>0){
            sb.append(" p1=0 and ");
        }else if (af1<0){
            sb.append(" p1=1 and ");
        }

        if(af2>0){
            sb.append(" p2=0 and ");
        }else if (af2<0){
            sb.append(" p2=1 and ");
        }


        //获取新解的集合
        String sql = sb.toString().substring(0, sb.toString().length() - 4);
        ArrayList<String> inList = jdbcUtils.selectBySql(sql);


        // ori解集 - out解 +  in解 = 新解(拿新解去再次校验)
        List<String> outList = new ArrayList<>(outLess);

        Boolean b = false;

        // 寻找完美解
        for (int i = 0; i < outList.size(); i++) {

            // inList 按照type排序  解决方法: String.contain()
            String type = outList.get(i).split(":")[1];
            ArrayList<String> inListRe = rearrange(type, inList);

            for (int j = 0; j < inListRe.size(); j++) {

                b = propCheck(bachItemList,outList.get(i),inListRe.get(j));

                if(b){
                    // 删除out解，添加in解
                    for (int k = 0; k < bachItemList.size(); k++) {
                        if (bachItemList.get(k).equals(outList.get(i))){
                            bachItemList.set(k,inListRe.get(j));
                        }
                    }
                    // 输出
                    System.out.println("已找到符合要求的解，现退出循环,目前的解集为："+bachItemList.toString());
                    break;
                }
            }
            if (b){
                break;
            }
        }

        // 寻找替补解
        if(!b) {

            for (int i = 0; i < outList.size(); i++) {

                //out解信息
                String t1 = " and type = '" + outList.get(i).split(":")[1] +"'";
                String[] arr = outList.get(i).split(":")[2].split(",");
                String a1 = "0".equals(arr[0].substring(1,2))?"": " and p1 = 1 ";
                String a2 = "0".equals(arr[1])?"": " and p2 = 1 ";
                String a3 = "0".equals(arr[2])?"": " and p3 = 1 ";
                String a4 = "0".equals(arr[3])?"": " and p4 = 1 ";
                String a5 = "0".equals(arr[4].substring(0,1))?"": " and p5 = 1 ";

                String outString =  a1 + a2 + a3 + a4 + a5;

                //目前空缺解信息  取出-1的解,使用集合接收,并直接转换为 p1 p2 p3 p4 p5
                ArrayList<String> lessTemp = new ArrayList<>();
                if(af1==-1){
                    lessTemp.add("p1");
                }
                if(af2==-1){
                    lessTemp.add("p2");
                }
                if(af3==-1){
                    lessTemp.add("p3");
                }
                if(af4==-1){
                    lessTemp.add("p4");
                }
                if(af5==-1){
                    lessTemp.add("p5");
                }


                String[] lessArray = new String[lessTemp.size()];
                for (int j = 0; j < lessTemp.size(); j++) {
                    lessArray[j] = lessTemp.get(j);
                }

                //递归遍历 （先取全解，再取部分解，最后取一个解）
                Set<Set<String>> lessSet = new KLUtils().getSubCollection(lessArray);

                //重新定义排序方法
                ArrayList<Set<String>> lessFinally = new ArrayList<>(lessSet);


                // 倒序取出  [p1, p2]、[p2]、[p1]   [p1]
                for (int i1 = lessFinally.size() -1 ; i1 >= 1; i1--) {
                    String tmp1 = lessFinally.get(i1).toString().substring(1);
                    String tmp2=tmp1.replaceAll(","," = 1 and ");
                    String tmp3=tmp2.replace("]"," = 1 ");
                    System.out.println(tmp3);

                    //拼接 less ori type
                    String sqlFinally = tmp3 + outString + t1;
                    System.out.println(sqlFinally);
                    ArrayList<String> arrayList = jdbcUtils.selectBySql(sqlFinally);

                    if(arrayList.size()>0){

                        System.out.println("out解："+outList.get(i));
                        System.out.println("in解："+arrayList.get(0));

                        // 删除out解，添加in解
                        for (int k = 0; k < bachItemList.size(); k++) {
                            if (bachItemList.get(k).equals(outList.get(i))){
                                bachItemList.set(k,arrayList.get(0));
                            }
                        }

                        System.out.println("找到合适解,退出循环");
                        b = true;
                        break;
                    }else {
                        System.out.println("未找到合适解,继续递归查找");
                    }
                }
                if(b){
                    break;
                }
            }
        }

        System.out.println("校验后的集合:"+bachItemList.toString());

        return  bachItemList;


    }




}












package net.sysu.adi;

import net.sysu.utils.KLUtils;

import java.io.IOException;
import java.time.Year;

/**
 * @Author : song bei chang
 * @create 2021/5/2 17:52
 *
 *          1.属性级别认知诊断的适应度值代码实现 ADI
 *              1.1 K_L information的实现 抽取为KL utils
 *              1.2 ADI
 *          2.验证
 */
public class AttributeLevel {

    public static String PATH_ONE = "F:\\song\\SYSU\\multimodal_optimization\\src\\main\\java\\net\\sysu\\adi\\KL\\zhangailing.txt";

    public static String PATH_TWO = "F:\\song\\SYSU\\multimodal_optimization\\src\\main\\java\\net\\sysu\\adi\\KL\\zhangailing2.txt";

    public static void main(String[] args) throws IOException {
        // 基于分词的K_L
        //KLUtils.KLCal(PATH_ONE, PATH_TWO);

        // 最终的计算公式
        double v = (0.8) * Math.log((0.8) / (0.1))+(0.2) * Math.log((0.2) / (0.9));

        System.out.println(v);

    }







}




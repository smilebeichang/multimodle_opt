package net.sysu.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Created by songb.
 */
@Data
public class Papers implements Serializable{
    /**
     * 试卷编号
     */
	private int id;

    /**
     * 难度分布
     */
    private List<String> difDistribute;
    /**
     * 章节覆盖度
     */
    private List<String> chapterCoverage;

    private  int paperSize;
    private  int questSize;
    private double pc;
    private double pm;

    private  double[] fitness =new double[400];
    private Integer[] bestGene =new Integer[20];



}

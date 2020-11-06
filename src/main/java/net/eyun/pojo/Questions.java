package net.eyun.pojo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by songb
 */
public class Questions implements Serializable{
    /**
     * 编号
     */
	private int id;
    /**
     * 类型
     */
	private int types;
    /**
     * 章节
     */
	private int chapter;
    /**
     * 难度
     */
	private String difficult;
    /**
     * 曝光度
     */
	private String exposure;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getExposure() {
        return exposure;
    }

    public void setExposure(String exposure) {
        this.exposure = exposure;
    }
}

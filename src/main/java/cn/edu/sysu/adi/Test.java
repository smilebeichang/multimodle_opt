package cn.edu.sysu.adi;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author : song bei chang
 * @create 2021/5/26 0:18
 */
public class Test {

    @org.junit.Test
    public void test1() {
        Set<String> result = new HashSet<>();
        Set<String> set1 = new HashSet<String>() {
            {
                add("王者荣耀");
                add("英雄联盟");
                add("穿越火线");
                add("地下城与勇士");
            }
        };

        Set<String> set2 = new HashSet<String>() {
            {
                add("王者荣耀");
                add("地下城与勇士");
                add("魔兽世界");
            }
        };

        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        System.out.println("交集：" + result);

    }
}




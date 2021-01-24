package net.eyun.test;

/**
 * @Author : songbeichang
 * @create 2021/1/24 23:29
 */
import lombok.Getter;
import lombok.Setter;
import org.junit.*;
public class Test01 {
    @Test
    public void test01(){
        System.out.println("1111111");
        System.out.println("1111111");
        System.out.println("2222111");
        System.out.println("1111111");
        System.out.println("1111111");

        try {
            System.out.println("333333");
        }finally {
            System.out.println("444444");
        }

        try {
            System.out.println("333333");
        }finally {
            System.out.println("444444");
        }

        System.out.println();

        "sout".equals(111);


        Dog dog = new Dog();
        dog.getAge();
        dog.getName();
    }



}

@Getter
@Setter
class Animal{
    String name;
    int age;
}
@Getter
@Setter
class Dog extends Animal{
    String name;
    String address;
}



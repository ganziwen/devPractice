package oop;

/**
 * 汽车类
 *
 * @author steven01.gan
 * @version 1.0
 * @date 2021/7/2-16:00
 */
public class Car {
    //    属性&字段:就是这个类本身拥有的东西
    //    里程数
    int mile = 110;
    //    车门数
    int doorNumber = 4;

    //    行为&方法&函数
    void run(int number) {
//        跑的方法,每次跑加里程
        System.out.println("汽车这次跑的里程数 number = " + number);
        mile += number;
    }

    int getSumMile() {

        return mile;
    }
}

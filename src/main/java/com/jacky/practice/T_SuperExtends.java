package com.jacky.practice;

import com.jacky.common.util.Md5Util;
import lombok.Data;

import java.util.*;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/14 14:40
 * @since jdk1.8
 */

interface Behaviour {
    void print();

    String getInfo();
}

enum Color implements Behaviour {
    RED("红色", 1), GREEN("绿色", 2), BLANK("白色", 3), YELLO("黄色", 4);
    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private Color(String name, int index) {
        this.name = name;
        this.index = index;
    }

    //接口方法
    @Override
    public String getInfo() {
        return this.name;
    }

    //接口方法
    @Override
    public void print() {
        System.out.println(this.index + ":" + this.name);
    }
}

public class T_SuperExtends {


    public static void main(String[] args) {

        String str = "xxx";
        System.out.println("fff测试sssss" + Md5Util.encode("FFFFFFF") + (args.length > 0 ? args[0] : ""));

        // Number "extends" Number (in this Context)
        List<? extends Number> foo1 = new ArrayList<Number>();

        // Integer extends Number
        List<? extends Number> foo2 = new ArrayList<Integer>();

        // Double extends Number
        List<? extends Number> foo3 = new ArrayList<Double>();

        /**
         * 扩展说一下 PECS(Producer Extends Consumer Super)原则
         * extends 子类继承 编译器不知道？具体是哪个子类，为了类型安全，只好阻止向其中加入任何子类 事实上，不能往使用了? extends的数据结构里写入任何的值
         * super 超类 编译器并不知道？具体是哪个超类，为了类型安全，不允许加入特定的任何超类型
         *
         * 如果要从集合中读取类型T的数据，并且不能写入，可以使用 ? extends 通配符；(Producer Extends) 上界通配符 (?的上界是Self)，只能传入本类和子类，只能get，不能add
         * 如果要从集合中写入类型T的数据，并且不需要读取，可以使用 ? super 通配符；(Consumer Super)   下界通配符(?的下界是Self)，只能传入本类和父类，只能add，不能get
         * 如果既要存又要取，那么就不要使用任何通配符
         */
        // 第一、频繁往外读取内容的，适合用<? extends T>  水果商，无籽西瓜，有籽西瓜
        // List<? extends Frut> 表示 “具有任何从Fruit继承类型的列表”，【*****编译器无法确定List所持有的类型*****】，所以无法安全的向其中添加对象，会造成数据丢失。可以添加null,因为null 可以表示任何类型。所以List 的add 方法不能添加任何有意义的元素，但是可以接受现有的子类型List<Apple> 赋值。
        List<? extends Self> listExtends = new ArrayList<>();
        List<? extends Self> listExtends1 = new ArrayList<Son>();
        List<? extends Self> listExtends2 = new ArrayList<Baby>();
        //listExtends2.add(new Son());// Baby有的,Son不一定有,Super也不一定有，所以干脆不支持

        // 编译器知道它总是 Self或其其继承的超类
        // 由于，其中放置是从Self中继承的类型，所以可以安全地取出Self类型，至少List中的对象数据不会丢失，可以明确转换出来。
        //Son son = (Son) listExtends1.get(0);

        // 写用super,读用extends
        Collections.copy(null,null);

        // 第二、经常往里插入的，适合用<? super T>。
        // List<? super Fruit> 表示“具有任何Fruit超类型的列表”，【*****列表的类型至少是一个 Fruit 类型******】，因此可以安全的向其中添加Fruit 及其子类型，不会有任何数据丢失。
        List<? super Self> listSuper = new ArrayList<>();
        List<? super Self> listSuper1 = new ArrayList<Super>();
        listSuper.add(new Son());
        listSuper1.add(new Son());// Self/Super有的，Son都有

        // 编译器在不知道这个超类具体是什么类，只能返回Object对象，因为Object是任何Java类的最终祖先类
        // 由于List<? super Self>中的类型可能是任何Self的超类型，无法赋值为Self的子类型Son，数据不明确，转换已丢失.
        //Self son1= (Self)listSuper.get(0);
        Object super1 = listSuper.get(0);

        /**
         * extends 可用于的返回类型限定，不能用于参数类型限定。
         * super 可用于参数类型限定，不能用于返回类型限定。
         * 带有super超类型限定的通配符可以向泛型对易用写入，带有extends子类型限定的通配符可以向泛型对象读取。
         */

        // SingletonTemplate
        Singleton single = Singleton.getInstance();

        System.out.println(single.getTestA());

        Singleton single2 = Singleton.getInstance();
        System.out.println(single2.getTestA());

        Singleton single3 = Singleton.getInstance2();
    }
}

class Singleton {
    LinkedList linkedList;
    AbstractSet set;
    WeakHashMap  weakHashMap;
    // 在一个线程的工作内存中修改了该变量的值，该变量的值立即能回显到主内存中，从而保证所有的线程看到这个变量的值是一致的。
    // 所以在处理同步问题上它大显作用，而且它的开销比synchronized小、使用成本更低
    public volatile static Singleton instance = null;

    // 实现线程安全的单例模式方式1：双重检查加锁机制?
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                    instance.setTestA("xxxxxx");

                    System.out.println("正在实例化");
                }
            }
        }

        return instance;
    }

    // 实现线程安全的单例模式方式2：利用私有的内部工厂类（线程安全，内部类也可以换成内部接口，不过工厂类变量的作用域要改为public了。）
    public static Singleton getInstance2() {
        return SingleFactory.instance;
    }

    private String testA;

    public String getTestA() {
        return testA;
    }

    public void setTestA(String testA) {
        this.testA = testA;
    }

    private static class SingleFactory {
        private static Singleton instance = new Singleton();
    }
}

@Data
class Super {
    private String as;
}

class Self extends Super {
}

class Son extends Self {

    private String son_1 = "son_1";
}

class Baby extends Son {
}
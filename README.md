# practice

###### 1. @Configuration、@Bean 以及 @Component、@Controller、@Service、@Repository区别

1.1 ***启动阶段***
Spring 容器在启动时，会加载默认的一些 PostPRocessor，其中就有 ConfigurationClassPostProcessor，
这个后置处理程序专门处理带有 @Configuration 注解的类，这个程序会在 bean 定义加载完成后，在 bean 初始化前进行处理。
主要处理的过程就是使用 cglib 动态代理增强类，而且是对其中带有 @Bean 注解的方法进行处理（在方法里面实现bean对象的构造并发返回）。

1.2 ***注册阶段***
Spring 2.5 中除了提供 @Component 注释外，还定义了几个拥有特殊语义的注释，它们分别是：@Repository、@Service 和 @Controller。
在目前的 Spring 版本中，这 3 个注释和 @Component 是等效的，但是从注释类的命名上，很容易看出这 3 个注释分别和持久层、业务层和控制层（Web 层）相对应。

使用@Controller注解标识UserAction之后，就表示要把UserAction交给Spring容器管理，在Spring容器中会存在一个名字为"userAction"的action，这个名字是根据UserAction类名来取的。
这里的UserAction还使用了@Scope注解，@Scope("prototype")表示将Action的范围声明为原型，可以利用容器的scope="prototype"来保证每一个请求有一个单独的Action来处理，避免struts中Action的线程安全问题。
spring 默认scope 是单例模式(scope="singleton")，这样只会创建一个Action对象，每次访问都是同一Action对象，数据不安全，struts2 是要求每次次访问都对应不同的Action，scope="prototype" 可以保证当有请求的时候都创建一个Action对象

@Service("userService")注解是告诉Spring，当Spring要创建UserServiceImpl的的实例时，bean的名字必须叫做"userService"，这样当Action需要使用UserServiceImpl的的实例时,就可以由Spring创建好的"userService"（如果是单例，有就取，没有就创建），
然后注入给Action：在Action只需要声明一个名字叫“userService”的变量来接收由Spring注入的"userService"即可.

PS:@Component + @Scope singleton模式时，为启动时创建, 类似@Bean初始化注入的方式

1.3***使用阶段***
@Autowired @Resource 自动装配，类似于用context.getBean()的方法自定义获取对应注册的bean对象。

###### 2.HashMap和HashTable区别
1. HashMap是非线程安全的，HashTable是线程安全的；HashTable内部的方法基本都经过synchronized修饰。
2. 因为线程安全的问题，HashMap要比HashTable效率高一点，HashTable基本被淘汰。
3. HashMap允许有null值的存在，而在HashTable中put进的键值只要有一个null，直接抛出NullPointerException。

Hashtable和HashMap有几个主要的不同：线程安全以及速度。仅在你需要完全的线程安全的时候使用Hashtable，而如果你使用Java5或以上的话，请使用ConcurrentHashMap吧


## <font face="楷体">Arraylist 与 LinkedList 区别</font>
Arraylist底层使用的是数组（存读数据效率高，插入删除特定位置效率低），LinkedList底层使用的是双向循环链表数据结构（插入，删除效率特别高）。学过数据结构这门课后我们就知道采用链表存储，插入，删除元素时间复杂度不受元素位置的影响，都是近似O（1）而数组为近似O（n），因此当数据特别多，而且经常需要插入删除元素时建议选用LinkedList.一般程序只用Arraylist就够用了，因为一般数据量都不会蛮大，Arraylist是使用最多的集合类。


## <font face="楷体">HashSet如何检查重复</font>
当你把对象加入HashSet时，HashSet会先计算对象的hashcode值来判断对象加入的位置，同时也会与其他加入的对象的hashcode值作比较，如果没有相符的hashcode，HashSet会假设对象没有重复出现。但是如果发现有相同hashcode值的对象，这时会调用equals（）方法来检查hashcode相等的对象是否真的相同。如果两者相同，HashSet就不会让加入操作成功。（摘自我的Java启蒙书《Head fist java》第二版）

**hashCode（）与equals（）的相关规定：**
1. 如果两个对象相等，则hashcode一定也是相同的
2. 两个对象相等,对两个equals方法返回true
3. 两个对象有相同的hashcode值，它们也不一定是相等的
4. 综上，equals方法被覆盖过，则hashCode方法也必须被覆盖
5. hashCode()的默认行为是对堆上的对象产生独特值。如果没有重写hashCode()，则该class的两个对象无论如何都不会相等（即使这两个对象指向相同的数据）。

**==与equals的区别**

1. ==是判断两个变量或实例是不是指向同一个内存空间    equals是判断两个变量或实例所指向的内存空间的值是不是相同
2. ==是指对内存地址进行比较    equals()是对字符串的内容进行比较3.==指引用是否相同    equals()指的是值是否相同

###  Java多线程学习（六）Lock锁的使用
1. [xxxxx](http://www.baidu.com)
2. ![](https://user-gold-cdn.xitu.io/2018/8/4/16504e0cb6bac32e?w=758&h=772&f=jpeg&s=247210)
3. [xxxxx](http://www.baidu.com)
4. [xxxxx](http://www.baidu.com)

### 无序排列

* xxx
* xxxx


*斜体xieti*

**粗体cuti**

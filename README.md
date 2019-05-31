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

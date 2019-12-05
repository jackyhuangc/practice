package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.ThreadPoolUtil;
import com.rabbitmq.client.*;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class T_RabbitMQ {

    public final static String EXCHANGE_NAME = "rabbitMQ.exchange"; // 交换器
    public final static String ROUTING_KEY = "rabbitMQ.key"; // 路由键
    public final static String QUEUE_NAME = "rabbitMQ.queue"; // 队列
    // 队列持久化 我们需要确认RabbitMQ永远不会丢失我们的队列。为了这样，我们需要声明它为持久化
    public final static Boolean DURABLE = true;
    // 消息持久化 我们需要标识我们的信息为持久化的
    public final static com.rabbitmq.client.AMQP.BasicProperties MESSAGE_PERSISTENT = MessageProperties.PERSISTENT_TEXT_PLAIN;

//    public static final BasicProperties MINIMAL_BASIC = new BasicProperties((String)null, (String)null, (Map)null, (Integer)null, (Integer)null, (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);
//    public static final BasicProperties MINIMAL_PERSISTENT_BASIC = new BasicProperties((String)null, (String)null, (Map)null, Integer.valueOf(2), (Integer)null, (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);
//    public static final BasicProperties BASIC = new BasicProperties("application/octet-stream", (String)null, (Map)null, Integer.valueOf(1), Integer.valueOf(0), (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);
//    public static final BasicProperties PERSISTENT_BASIC = new BasicProperties("application/octet-stream", (String)null, (Map)null, Integer.valueOf(2), Integer.valueOf(0), (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);
//    public static final BasicProperties TEXT_PLAIN = new BasicProperties("text/plain", (String)null, (Map)null, Integer.valueOf(1), Integer.valueOf(0), (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);
//    public static final BasicProperties PERSISTENT_TEXT_PLAIN = new BasicProperties("text/plain", (String)null, (Map)null, Integer.valueOf(2), Integer.valueOf(0), (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);

    public static void main(String[] args) throws IOException, TimeoutException {

        // 模拟开启10个消费者
        for (int i = 0; i < 10; i++) {

            ThreadPoolUtil.execute(() -> {
                try {
                    consumer();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });
        }

        producer();
    }

    public static void producer() throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("127.0.0.1");

        factory.setConnectionTimeout(30000);
        factory.setUsername("admin");
        factory.setPassword("123456");
        factory.setPort(5672);
        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        // 创建一个type=direct的  持久化的 非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", DURABLE, false, null);

        // 声明一个队列 队列持久化非自动删除 QUEUE_DURABLE=true
        // 第二篇有介绍当exchange的名称为空字符串的时候，创建queue的时候用到queue的名字和Producer的BasicPublish方法或Consuner的BasicConsume方法的routing key的名字可以是相同的。即queue的名字和routing key的名字是相同的。
        channel.queueDeclare(QUEUE_NAME, DURABLE, false, false, null);

        // 通过路由键将交换机和队列绑定，这样使得通过 exchange+routing key 过来的消息可以转到queue
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        String message = "";

        //发送消息到队列中
        // 标准的输入流对象 --读取操作
        InputStream is = System.in;
        // 标准的输出流对象---写的操作
        OutputStream os = System.out;

        try {
            // System.in是一个很原始、很简陋的输入流对象，通常不直接使用它来读取用户的输入。
            // 一般会在外面封装过滤流：
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // 然后调用br.readLine()方法进行读取。
            String inputStr = "";
            do {
                inputStr = br.readLine();

                if ("exit".equals(inputStr)) {
                    break;
                } else if ("test".equals(inputStr)) {
                    inputStr = "{'xxx':'abc'}";
                }

                message = DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss:SSSS") + "***" + inputStr;

                // exchange the exchange to publish the message to  相当于分组
                // routingKey the routing key
                // props other properties for the message - routing headers etc
                // body the message body
                //channel.basicPublish("", QUEUE_NAME, MESSAGE_PERSISTENT, message.getBytes("UTF-8"));
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MESSAGE_PERSISTENT, message.getBytes("UTF-8"));

                System.out.println("Producer Send +'" + message + "'");
            }
            while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭通道和连接
        channel.close();
        connection.close();
    }

    public static void consumer() throws IOException, TimeoutException {

        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("127.0.0.1");
        factory.setPort(5672);

        factory.setConnectionTimeout(30000);
        factory.setUsername("admin");
        factory.setPassword("123456");

        //创建一个新的连接
        Connection connection = factory.newConnection();


        Channel channel = connection.createChannel();

        //声明要关注的队列
        //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 客户端只需要声明一个队列即可，用于消费消息
        channel.queueDeclare(QUEUE_NAME, DURABLE, false, false, null);

        System.out.println("Customer Waiting Received messages");
        //DefaultConsumer类实现了Consumer接口，通过传入一个频道，
        // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Customer Received '" + message + "'" + Thread.currentThread().getName());

                if (new Random().nextInt(10) == 9) {
                    // 若有异常，或未确认，该数据将重新分发给其他消费者
                    throw new IOException("模拟随机异常");
                }

                // FIXME 不要在消费/接收时处理耗时的任务，应该将消息入库，结合XXL-JOB异步调用执行，否则可能会导致RabbitMQ转发服务超时，
                // FIXME 增加系统消耗和负担，降低系统可靠性。

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        //自动回复队列应答 -- RabbitMQ中的消息确认机制  关闭自动应答，才能保证消息处理异常时不会丢失，而会转发到下一个消费者
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}

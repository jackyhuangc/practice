package com.jacky.practice;

import com.jacky.common.util.DateUtil;
import com.jacky.common.util.ThreadPoolUtil;
import com.rabbitmq.client.*;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class T_RabbitMQ {

    public final static String EXCHANGE_NAME = "rabbitMQ1.exchange"; // 交换器
    public final static String ROUTING_KEY = "rabbitMQ1.*"; // 路由键
    public final static String QUEUE_NAME = "rabbitMQ1.queue"; // 队列
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

        /**
         *  四种交换器类型
         *
         *  DIRECT("direct"), 处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “test”，则只有被标记为“test”的消息才被转发，不会转发test.aaa，也不会转发dog.123，只会转发test。  比较适用
         *
         *  FANOUT("fanout"), 不处理路由键。你只需要简单的将队列绑定到交换机上。一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消息。Fanout交换机转发消息是最快的。
         *  适用场景：exchange a 与 exchange b(fanout类型)后使用，所有转发到exchange a+ routing key的数据将转发到 exchange b的所有队列queue中
         *
         *
         *  TOPIC("topic"), 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
         *  使用场景：QBUS，比较适用
         *
         *  HEADERS("headers"), Headers类型的exchange使用的比较少，它也是忽略routingKey的一种路由方式。是使用Headers来匹配的。Headers是一个键值对，可以定义成Hashtable。发送者在发送的时候定义一些键值对，接收者也可以再绑定时候传入一些键值对，两者匹配的话，则对应的队列就可以收到消息。匹配有两种方式all和any
         */
        // 创建一个type=direct的  持久化的 非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", DURABLE, false, null);

        // 声明一个队列 队列持久化非自动删除 QUEUE_DURABLE=true
        // 第二篇有介绍当exchange的名称为空字符串的时候，创建queue的时候用到queue的名字和Producer的BasicPublish方法或Consuner的BasicConsume方法的routing key的名字可以是相同的。即queue的名字和routing key的名字是相同的。
        channel.queueDeclare(QUEUE_NAME, DURABLE, false, false, null);

        // 通过路由键将交换机和队列绑定，这样使得通过 exchange+routing key 过来的消息可以转到queue
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        channel.addConfirmListener(new ConfirmListener() {
            //消息失败处理
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //deliveryTag；唯一消息标签
                //multiple：是否批量
                System.err.println("-------no ack!-----------");
            }
            //消息成功处理
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("-------ack!-----------");
            }
        });

        channel.addReturnListener(new ReturnCallback() {
            @Override
            public void handle(Return aReturn) {
                System.out.println("err code :" + aReturn.getReplyCode());
                System.out.println("错误消息的描述 :" +  aReturn.getReplyText());
                System.out.println("错误的交换机是 :" +  aReturn.getExchange());
                System.out.println("错误的路右键是 :" +  aReturn.getRoutingKey());
            }
        });
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
                //channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MESSAGE_PERSISTENT, message.getBytes("UTF-8"));
                channel.basicPublish(EXCHANGE_NAME, "rabbitMQ.key", MESSAGE_PERSISTENT, message.getBytes("UTF-8"));

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
        // 告诉服务器我们需要那个通道的消息，如果通道中有消息，就会执行回调函数handleDelivery
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

                //deliveryTag（唯一标识 ID）：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel ，RabbitMQ 会用 basic.deliver 方法向消费者推送消息，这个方法携带了一个 delivery tag， 它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID，是一个单调递增的正整数，delivery tag 的范围仅限于 Channel
                //multiple：为了减少网络流量，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        //自动回复队列应答 -- RabbitMQ中的消息确认机制  关闭自动应答，才能保证消息处理异常时不会丢失，而会转发到下一个消费者
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);

        com.jacky.common.util.LogUtil.info("");
    }
}

//package com.jacky.practice;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//import kafka.consumer.Consumer;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.consumer.ConsumerConfig;
//import kafka.javaapi.producer.Producer;
//import kafka.producer.KeyedMessage;
//import kafka.producer.ProducerConfig;
//import kafka.serializer.StringEncoder;
//
//import java.text.SimpleDateFormat;
//import java.util.Properties;
//
///**
// * Description Here...
// *
// * @author Jacky Huang
// * @date 2018/3/19 12:41
// * @since jdk1.8
// */
//
//class KafkaProducer extends Thread {
//    @Override
//    public void run() {
//        Producer producer = createProducer();
//        int i = 0;
//        while (true) {
//            producer.send(new KeyedMessage<Integer, String>("test2", "message: KAFKA" + i++));
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss SSS");
//            Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
//            String str = formatter.format(curDate);
//            System.out.println("����������" + "KAFKA" + (i - 1) + "***" + str);
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private Producer createProducer() {
//        Properties properties = new Properties();
//        properties.put("zookeeper.connect", "192.168.99.100:2181");//����zk,�ɶ��
//        properties.put("serializer.class", StringEncoder.class.getName());
//        properties.put("metadata.broker.list", "192.168.99.100:9092");// ����kafka broker
//
//        // A.��֤����
//        // -1,����ζ��producer��follower����ȷ�Ͻ��յ����ݺ����һ�η�����ɡ���ѡ���ṩ��õ��;��ԣ����Ǳ�֤û����Ϣ����ʧ��ֻҪ����һ��ͬ���������ִ��
//        // Ĭ����0������ζ��������producer���ȴ�����brokerͬ����ɵ�ȷ�ϼ���������һ����������Ϣ��1����ζ��producer��leader�ѳɹ��յ������ݲ��õ�ȷ�Ϻ�����һ��message��-1��ʾ�ȴ�����leader������һ��follower�յ����ݲ������
//        properties.put("request.required.acks", "-1");// ACK����, ��Ϣ������Ҫkafka�����ȷ��
//        return new Producer<Integer, String>(new ProducerConfig(properties));
//    }
//}
//
//class KafkaComsumer extends Thread {
//    @Override
//    public void run() {
//        ConsumerConnector consumer = createConsumer();
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        // ����ÿ��topic�������߳�
//        topicCountMap.put("test2", 1); // һ�δ������л�ȡһ������
//        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
//        KafkaStream<byte[], byte[]> stream = messageStreams.get("test2").get(0);// ��ȡÿ�ν��յ����������
//
//        // ���Խ����·������߳�����
//        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
//        while (iterator.hasNext()) {
//            String message = new String(iterator.next().message());
//            System.out.println("���յ�: " + message);
//        }
//    }
//
//    private ConsumerConnector createConsumer() {
//        Properties properties = new Properties();
//        properties.put("zookeeper.connect", "192.168.99.100:2181");//����zk
//        properties.put("zookeeper.session.timeout.ms", "10000");
//        properties.put("group.id", "8");// ����Ҫʹ�ñ�������ƣ� ��������ߺ������߶���ͬһ�飬���ܷ���ͬһ���ڵ�topic����
//
//        // B.��ͷ����
//        /*�������ã�����ͷ��ȡ�����е�������Ϣ*/
//        //properties.put("auto.commit.enable","false");//auto.commit.enable	true	���true,consumer���ڵ���zookeeperд��ÿ��������offset
//        //properties.put("auto.offset.reset","smallest");// ָ��consumer����Kafka���ݵ�ʱ��offset��ʼֵ��ɶ����ѡsmallest(��ͷ��ʼ),largest(�ӵ�ǰ��ʼ)
//        // offsetλ���Ǽ�¼��ǰ������topic���������ѵ���һ����(��������������ʱ???���������Ӧ��Ҳ���ԣ��ؼ��������Ƿ����ڻ��棬Ӱ����Խ��)����group.id=x��smallest��ʾ��ͷ��ʼ��ȡ�������ݣ���ʱ����largest��ʾ�ӵ�ǰ��ʼ��ȡ��������
//        //properties.put("group.id", "9");
//        /**************************************/
//
//        // C.����ظ�����
//        /*һ������£�kafka�ظ����Ѷ�������δ�����ύoffset�����޸����ã������ύoffset���ɽ��*/
//        /* �Զ�ȷ��offset��ʱ����  */
//        properties.put("auto.commit.interval.ms", "1000");
//        properties.put("session.timeout.ms", "30000");
//        //��Ϣ���͵���ȴ�ʱ��.�����session.timeout.ms���ʱ��
//        properties.put("request.timeout.ms", "40000");
//
//        //һ�δ�kafka��poll��������������
//        //max.poll.records��������Ҫ����session.timeout.ms���ʱ���ڴ�����
//        properties.put("max.poll.records", "100");
//        //server���͵����Ѷ˵���С���ݣ����ǲ����������ֵ���ȴ�ֱ������ָ����С��Ĭ��Ϊ1��ʾ�������ա�
//        properties.put("fetch.min.bytes", "1");
//        //���ǲ�����fetch.min.bytesʱ���ȴ����Ѷ��������ȴ�ʱ��
//        properties.put("fetch.wait.max.ms", "1000");
//
//        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
//    }
//}
//
//public class T_Kafka {
//    public static void main(String[] args) throws InterruptedException {
//
//        new KafkaProducer().start();
//
//        //Thread.sleep(5000);
//
//        new KafkaComsumer().start();
//    }
//}

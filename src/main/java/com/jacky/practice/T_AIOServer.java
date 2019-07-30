package com.jacky.practice;


import com.jacky.common.util.LogUtil;
import com.sun.mail.iap.ByteArray;
import javafx.util.Builder;

import javax.management.StringValueExp;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Set;
import java.util.concurrent.*;

public class T_AIOServer {

    public final static int PORT = 8001;
    public final static String IP = "127.0.0.1";


    private AsynchronousServerSocketChannel server = null;

    public T_AIOServer() {
        try {
            //同样是利用工厂方法产生一个通道，异步通道 AsynchronousServerSocketChannel
            server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(IP, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //使用这个通道(server)来进行客户端的接收和处理
    public void start() {
        System.out.println("Server listen on " + PORT);

        //注册事件和事件完成后的处理器，这个CompletionHandler就是事件完成后的处理器
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {

                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult = null;

                try {
                    buffer.clear();
                    Integer size = result.read(buffer).get(100, TimeUnit.SECONDS);

                    System.out.println("In server: " + new String(buffer.array(), 0, size));

                    //将数据写回客户端
                    buffer.flip();

                    ByteBuffer respBuffer = ByteBuffer.allocate(1024);

                    String str = new String(buffer.array(), 0, size) + "的响应结果";
                    writeResult = result.write(ByteBuffer.wrap((str).getBytes()));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                } finally {
                    server.accept(null, this);
                    try {
                        writeResult.get();
                        result.close();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed:" + exc);
            }

        });
    }

    public static void server() throws IOException {
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(IP, PORT));

        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {

                final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                try {

                    StringBuilder stringBuilder = new StringBuilder();
                    Integer total = 0;
                    Integer passed = 0;
                    Integer length = 0;
                    do {

                        //position将被设回0，limit设置成capacity，换句话说，Buffer被清空了
                        byteBuffer.clear();

                        // get 将阻塞，配合while持续读取
                        length = result.read(byteBuffer).get(60, TimeUnit.SECONDS);

                        // 开始帧头
                        if (byteBuffer.array()[0] == 0x0A) {
                            // 内容长度
                            total = (int) byteBuffer.array()[1];
                        }

                        stringBuilder.append(new String(byteBuffer.array(), 0, length));

                        LogUtil.warn(String.format("接收到%s个字符：%s", length, new String(byteBuffer.array(), 0, length)));

                        //position设回0，并将limit设成之前的position的值
                        //将数据写回客户端
                        //byteBuffer.flip();
                        //writeResult = result.write(byteBuffer);

                        //compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。
                        // 现在Buffer准备好写数据了，但是不会覆盖未读的数据
                        //byteBuffer.compact();

                        passed += length;
                    }
                    while (passed < total);

                    /**
                     * FIXME 使用具有消息头和长度的报文格式，解决粘包问题
                     * 使用带消息头的协议、消息头存储消息开始标识及消息长度信息，服务端获取消息头的时候解析出消息长度，然后向后读取该长度的内容。
                     * 设置定长消息，服务端每次读取既定长度的内容作为一条完整消息。
                     * 设置消息边界，服务端从网络流中按消息编辑分离出消息内容。
                     */

                    LogUtil.warn(String.format("接收到%s个字符：%s", stringBuilder.length(), stringBuilder.toString()));
                    LogUtil.info(new String(byteBuffer.array(), 0, length));

                    // byteBuffer.flip();

                    length = result.write(ByteBuffer.wrap(stringBuilder.toString().getBytes())).get();
                    LogUtil.warn(String.format("返回到%s个字符：%s", stringBuilder.length(), stringBuilder.toString()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    // 继续接收
                    server.accept(null, this);
                    try {
                        result.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                LogUtil.error(new Exception(exc));
            }
        });
    }

    public static void client() throws IOException {

        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8001);

        CompletionHandler<Void, ? super Object> handler = new CompletionHandler<Void, Object>() {

            @Override
            public void completed(Void result, Object attachment) {

                System.out.println(attachment);

                byte[] body = "测试数据".getBytes();
                byte[] bytes = new byte[body.length + 3];
                bytes[0] = 0x0A;
                bytes[1] = Byte.valueOf(String.valueOf(body.length + 3));
                System.arraycopy(body, 0, bytes, 2, body.length);
                bytes[bytes.length - 1] = 0x0B;

                client.write(ByteBuffer.wrap(bytes), null,
                        new CompletionHandler<Integer, Object>() {

                            @Override
                            public void completed(Integer result,
                                                  Object attachment) {

                                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

                                    @Override
                                    public void completed(Integer result,
                                                          ByteBuffer attachment) {
                                        attachment.flip();
                                        System.out.println(new String(attachment.array(), 0, result));
                                        try {
                                            client.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void failed(Throwable exc,
                                                       ByteBuffer attachment) {
                                    }

                                });
                            }

                            @Override
                            public void failed(Throwable exc, Object attachment) {
                            }

                        });
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
            }

        };

        client.connect(serverAddress, "xxx", handler);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // new T_AIOServer().start();
        server();
//
        client();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
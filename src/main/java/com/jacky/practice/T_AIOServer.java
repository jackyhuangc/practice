package com.jacky.practice;


import com.jacky.common.util.LogUtil;
import javafx.util.Builder;

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
                    Integer length = 0;
                    //do
                    {
                        // get 将阻塞，配合while持续读取
                        length = result.read(byteBuffer).get(1000, TimeUnit.SECONDS);
                        stringBuilder.append(new String(byteBuffer.array(), 0, length));

                        LogUtil.warn(String.format("接收到%s个字符：%s", length, new String(byteBuffer.array(), 0, length)));
                        byteBuffer.clear();
                    }
                    //while (true);

                    LogUtil.warn(String.format("接收到%s个字符：%s", stringBuilder.length(), stringBuilder.toString()));
                    LogUtil.info(new String(byteBuffer.array(), 0, length));

                    byteBuffer.flip();

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
                client.write(ByteBuffer.wrap("测试数据".getBytes()), null,
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
//        //client();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
package com.xiang.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * xiang 2018/6/10
 **/
public class ZKDemo {

    public static void main(String[] args) throws IOException {

        try {
        final CountDownLatch countDownLatch=new CountDownLatch(1);
        //建立连接
        final ZooKeeper zooKeeper=new ZooKeeper("192.168.19.128:2181,192.168.19.129:2181,192.168.19.132:2181", 4000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("默认事件:"+watchedEvent.getType()+"---->"+watchedEvent.getPath());
               countDownLatch.countDown();
            }
        });

        countDownLatch.await();//等连接成功之后再释放,就可以保证state的状态是CONENTEND


            /**
             *  第三个参数为节点的权限,ZooDefs.Ids.OPEN_ACL_UNSAFE表示开放式的 任何人都可以访问
             *  ZooDefs.Ids.CREATOR_ALL_ACL表示创建者有所有的权限,如果其他人想访问 要设置权限
             *  ZooDefs.Ids.READ_ACL_UNSAFE表示只读
             *  第四个参数为节点类型 CreateMode.PERSISTENT为持久化节点
             */
            //创建节点
            zooKeeper.create("/node1","1".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            //通过exists去注册监听   exists判断节点是否存在
            //注册监听有三种方式  getData, exists getChildren
           Stat stat= zooKeeper.exists("/node1", new Watcher() {
                //也可以通过默认的wachter,第二个参数传true就行
                @Override
                public void process(WatchedEvent watchedEvent) {
                    //打印wachter类型 和节点名称
                    System.out.println(watchedEvent.getType()+"---->"+watchedEvent.getPath());

                    try {
                        //再次通过exists注册监听,达到永久监听的效果
                        //这里wathch用的是默认的wachter,  也就是建立连接的时候的wachter
                        zooKeeper.exists("/node1",true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            //修改节点的值 触发监听事件
           stat= zooKeeper.setData("/node1","2".getBytes(),stat.getVersion());

            //删除节点
            zooKeeper.delete("/node1",stat.getVersion());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

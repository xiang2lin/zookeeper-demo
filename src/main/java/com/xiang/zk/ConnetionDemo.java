package com.xiang.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * xiang 2018/6/10
 **/
public class ConnetionDemo {
    public static void main(String[] args) {
        try {

          final   CountDownLatch countDownLatch=new CountDownLatch(1);
            //客户端和服务端建立连接
            ZooKeeper zooKeeper=new ZooKeeper("192.168.19.128:2181,192.168.19.129:2181,192.168.19.132:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    //如果连接成功
                    if (Event.KeeperState.SyncConnected==watchedEvent.getState()){
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();//等连接成功之后再释放,就可以保证state的状态是CONENTEND
            System.out.println(zooKeeper.getState());

            //创建节点
            /**
             *  第三个参数为节点的权限,ZooDefs.Ids.OPEN_ACL_UNSAFE表示开放式的 任何人都可以访问
             *  ZooDefs.Ids.CREATOR_ALL_ACL表示创建者有所有的权限,如果其他人想访问 要设置权限
             *  ZooDefs.Ids.READ_ACL_UNSAFE表示只读
             *  第四个参数为节点类型 CreateMode.PERSISTENT为持久化几点
             */
            zooKeeper.create("/test","1".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            //获取节点的值   Stat当前节点的信息
            Stat stat=new Stat();
            byte[]bytes= zooKeeper.getData("/test",null,stat);
            System.out.println(new String(bytes));
            //修改节点的值
            zooKeeper.setData("/test","2".getBytes(),stat.getVersion());
            //获取修改后的值
            byte[]bytes1= zooKeeper.getData("/test",null,stat);
            System.out.println(new String(bytes1));
            //删除节点
            zooKeeper.delete("/test",stat.getVersion());
            //关闭连接
            zooKeeper.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}

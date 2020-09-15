package com.example.consistent.hash;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author: zhangrongqing
 * @Date: 2020/9/15 18:35
 * 一致性hash带虚拟节点
 */
public class ConsistentHashWithVirtualNode {

    /**
     * 服务器列表
     */
    private String[] serverList = {"192.168.1.1", "192.168.1.2", "192.168.1.3", "192.168.1.4"};

    /**
     * key为服务器的hash值，value为服务器
     */
    private SortedMap<Integer, String> virtualNode = new TreeMap<Integer, String>();

    private final static String VIRTUAL_NODE_FLAG = "&&";

    /**
     * 一个节点对应5个虚拟节点
     */
    private int virtualNodeNum = 5;

    public ConsistentHashWithVirtualNode() {
        //记录hash值
        int hash;
        //加入真实节点
        for (String server : serverList) {
            hash = getHash(server);
            virtualNode.put(hash, server);
            System.out.println("节点：" + server + "加入，hashCode为" + hash);

            //添加虚拟节点
            for (int i = 0; i < virtualNodeNum; i++) {
                //虚拟节点的标识server+ "&&VN" + i
                hash = getHash(server + VIRTUAL_NODE_FLAG + i);
                virtualNode.put(hash, server);
                System.out.println("节点：" + server + VIRTUAL_NODE_FLAG + i + "加入，hashCode为" + hash);
            }
        }
    }

    private int getHash(String str) {
        final int p = 16777619;

        int hash = (int) 2166136261L;

        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }

        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }

        return hash;
    }

    public String getServer(String key) {

        int hash = getHash(key);

        SortedMap<Integer, String> subMap = virtualNode.tailMap(hash);

        //找不到对应的节点
        if (subMap == null || subMap.size() <= 0) {
            subMap = virtualNode;
        }

        //返回第一个
        return virtualNode.get(virtualNode.firstKey());
    }


    public static void main(String[] args) {
        ConsistentHashWithVirtualNode consistentHash = new ConsistentHashWithVirtualNode();
        String[] keyList = new String[]{"1a11", "2d22", "333x", "444", "555", "6666"};

        for (String key : keyList) {
            System.out.println(key + "的hash值为" + consistentHash.getHash(key) + "匹配到了server：" + consistentHash.getServer(key));
        }
    }

}

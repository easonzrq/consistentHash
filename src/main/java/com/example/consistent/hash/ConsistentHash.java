package com.example.consistent.hash;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author: zhangrongqing
 * @Date: 2020/9/15 17:03
 * 一致性hash算法，不带虚拟节点
 */
public class ConsistentHash {

    /**
     * 服务器列表
     */
    private String[] serverList = {"192.168.1.1", "192.168.1.2", "192.168.1.3", "192.168.1.4"};

    /**
     * key为服务器的hash值，value为服务器
     */
    private SortedMap<Integer, String> virtualNode = new TreeMap<Integer, String>();

    /**
     * 默认hash算法
     */
    private Boolean defaultHash = true;

    public ConsistentHash(Boolean defaultHash) {
        this.defaultHash = defaultHash;
        for (String server : serverList) {
            int hash = getHash(server);
            System.out.println("[" + server + "]加入集合中, 其Hash值为" + hash);
            virtualNode.put(hash, server);
        }
    }

    public ConsistentHash() {
        for (String server : serverList) {
            int hash = getHash(server);
            System.out.println("[" + server + "]加入集合中, 其Hash值为" + hash);
            virtualNode.put(hash, server);
        }
    }

    /**
     * 获取hash值
     *
     * @param str
     * @return
     */
    private int getHash(String str) {
        if (defaultHash) {
            return getDefaultHash(str);
        }
        return getFNVLHash(str);
    }

    /**
     * 获取hash值
     * 默认的hash算法，hash阵列不是很散开，导致hash聚拢
     *
     * @param str
     * @return
     */
    private int getDefaultHash(String str) {
        int hashCode = str.hashCode();
        if (hashCode <= 0) {
            hashCode = Math.abs(hashCode);
        }
        return hashCode;
    }

    /**
     * FNVL_32_HASH算法
     *
     * @param str
     * @return
     */
    private int getFNVLHash(String str) {
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

    /**
     * 获取到应得的key的节点
     *
     * @param key
     * @return
     */
    private String getServer(String key) {
        int hash = getHash(key);
        SortedMap<Integer, String> subMap = virtualNode.tailMap(hash);

        //未查询到map,说明只有根节点最大
        if (subMap == null || subMap.size() <= 0) {
            subMap = virtualNode;
        }

        //返回获取的第一台机器
        return subMap.get(subMap.firstKey());
    }

    public Boolean getDefaultHash() {
        return defaultHash;
    }

    public void setDefaultHash(Boolean defaultHash) {
        this.defaultHash = defaultHash;
    }

    public static void main(String[] args) {
        ConsistentHash consistentHash = new ConsistentHash(false);
        String[] keyList = new String[]{"111", "222", "333", "444", "555", "6666"};

        for (String key : keyList) {
            System.out.println(key + "的hash值为" + consistentHash.getHash(key) + "匹配到了server：" + consistentHash.getServer(key));
        }
    }
}

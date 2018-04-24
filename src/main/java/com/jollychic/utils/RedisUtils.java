package com.jollychic.utils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisUtils {

    /** centos 上的redis 地址 **/
    private static final Jedis jedis = new Jedis("ip", 6379);

    private RedisUtils() {

    }

    public static Jedis getRedis() {
        return jedis;
    }

//    //序列化
//    public static byte [] serialize(Object obj){
//        ObjectOutputStream obi = null;
//        ByteArrayOutputStream bai=null;
//        try {
//            bai = new ByteArrayOutputStream();
//            obi = new ObjectOutputStream(bai);
//
//            obi.writeObject(obj);
//            return bai.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    //反序列化
//    public static Object unserizlize(byte[] byt){
//        ObjectInputStream oii = null;
//        ByteArrayInputStream bis = null;
//        bis=new ByteArrayInputStream(byt);
//        try {
//            oii = new ObjectInputStream(bis);
//            return oii.readObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis("172.31.11.171", 6379);
        log.debug(jedis.ping());

        jedis.set("a", "b");
        jedis.set("a", "c");

        log.debug("" + jedis.del("a"));
    }
}

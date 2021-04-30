package com.needayeah.elastic.common;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Twitter的分布式自增ID雪花算法snowflake
 * @author MENG
 * @create 2018-08-23 10:21
 **/
@Slf4j
public class SnowFlake {

    private static final Logger LOGGER = Logger.getLogger(SnowFlake.class.getCanonicalName());

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;

    private static final int BYTE_MASK = 0xFF;

    private static final long twepoch = 1288834974657L;

    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private static final long sequenceBits = 12L;

    private static final long workerIdShift = sequenceBits;
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static int getPID() {
        String pid;
        String name = ManagementFactory.getRuntimeMXBean().getName();
        pid = name.substring(0, name.indexOf("@"));
        return Integer.valueOf(pid);
    }

    public synchronized long createId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            LOGGER.warning(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static final long LOCAL_NODE_ID;

    private static final long LOCAL_WORK_ID;

    static {
        byte[] bytes = getLocalAddress().getAddress();
        int tmp = (bytes[0] & BYTE_MASK) << 24 | (bytes[1] & BYTE_MASK) << 16 | (bytes[2] & BYTE_MASK) << 8 | bytes[3] & BYTE_MASK;
        LOCAL_NODE_ID = Math.abs(tmp % maxDatacenterId);
        LOCAL_WORK_ID = getPID() % maxWorkerId;
    }

    private static SnowFlake ID_WORKER = new SnowFlake(LOCAL_WORK_ID, LOCAL_NODE_ID);

    public synchronized static void resetSnowFlake(SnowFlake snowFlake) {
        ID_WORKER = snowFlake;
    }


    public static long nextId() {
        return ID_WORKER.createId();
    }

    /**
     * 获取id生成的时间戳
     *
     * @param id IDGenerator生成的ID
     * @return timestamp
     */
    public static long getIDGeneratorTimestamp(long id) {
        return (id >> timestampLeftShift) + twepoch;
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warning("Failed to retrieving ip address, " + e.getMessage());
        }
        LOGGER.warning("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public SnowFlake(long workerId, long dataCenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = dataCenterId;
        LOGGER.info(String.format("IDGenerator : DataCenterId %d, WorkerId %d", this.datacenterId, this.workerId));
    }

    public static String generateId() {
        return String.valueOf(ID_WORKER.createId());
    }

    public static String strNextId(){
        return String.valueOf(ID_WORKER.createId());
    }

//    public static void main(String[] args) {
//        for (int i = 0;i<20;i++){
//            String s = SnowFlake.strNextId();
//            System.out.println(s);
//        }
//    }
}



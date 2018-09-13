package com.cmx.shiroservice.genernator;

import com.cmx.shiroapi.enums.SerialNumEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

@Slf4j
public class CommonIdGenerator implements IdGenerator {


    /** 单例模式 */
    private static volatile CommonIdGenerator instance;

    /**序列在id中占的位数*/
    private static final long SEQUENCE_BITS = 10L;
    /**生成序列的掩码，这里为1023 (0b1111111111=0x3ff=1023)*/
    private final long sequenceMask = -1L ^ (-1L << SEQUENCE_BITS);

    /**机器id所占位数，最多支持一个机房32台机器*/
    private static final long WORKER_ID_BITS = 5L;
    /**支持的最大机器id，结果是31 (该移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)*/
    public static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    /**数据中心识所占位数，可支持最多8地机房*/
    private static final long DATACENTER_ID_BITS = 3L;
    /**支持的最大数据标识id，结果是31*/
    public static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);

    /**业务标识id所占的位数*/
    private static final long BUSINESS_ID_BITS = 6L;
    /**6位 分表建 */
    private static final long APP_ID_BITS = 6L;

    /**支持的最大业务标识id，结果是63*/
    public static final long MAX_BUSINESS_ID = -1L ^ (-1L << BUSINESS_ID_BITS);

    /**工作机器ID(0~31)*/
    @Getter
    private static long workerId;
    /**数据中心ID(0~7)*/
    // TODO: 2018-01-16 目前默认0，要改成读配置
    private static long datacenterId = 0;
    /**开始时间截 (2015-01-01)*/
    private final long twepoch = 1420041600000L;
    /**毫秒内序列(0~1023)*/
    private long sequence = 0L;


    /**机器ID向左移10位*/
    private final long workerIdShift = SEQUENCE_BITS;
    /**数据标识id向左移17位(12+5)*/
    private final long datacenterIdShift = SEQUENCE_BITS  + WORKER_ID_BITS;
    /**业务标识标识id向左移17位(12+5+3)*/
    private final long businessIdShift = SEQUENCE_BITS  + WORKER_ID_BITS + DATACENTER_ID_BITS;
    /**时间戳向左偏移18位(10+5+3)*/
    private final long timestampLeftShift = SEQUENCE_BITS  + WORKER_ID_BITS + DATACENTER_ID_BITS + BUSINESS_ID_BITS;

    /**上次生成ID的时间截*/
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param workerId 工作ID (0~31)
     */
    private CommonIdGenerator(long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                    MAX_WORKER_ID));
        }
    }

    public static CommonIdGenerator getInstance() {
        if (instance == null) {
            synchronized (CommonIdGenerator.class) {
                if (instance != null) {
                    return instance;
                }
                instance = new CommonIdGenerator(workerId);
            }

        }
        return instance;
    }

    /**
     * 设置工作进程Id.
     *
     * @param workerId 工作进程Id
     */
    public static void setWorkerId(final Long workerId) {
        if(workerId >= 0L && workerId < MAX_WORKER_ID){

        }
        CommonIdGenerator.workerId = workerId;
    }

    /**
     * 生成唯一序列号
     * 生成规则：模块名简写+Long型，例如：
     *
     * @param serialNumberType
     * @return
     */
    @Override
    public String generateId(SerialNumEnum serialNumberType, Integer appId) {
        String baseId = String.valueOf(generateIdLong(serialNumberType, appId));
        StringBuffer stringBuffer = new StringBuffer(baseId);
        stringBuffer.insert(10, appId);
        return stringBuffer.toString();
    }

    @Override
    public long generateIdLong(SerialNumEnum serialNumberType, Integer appId) {
        CommonIdGenerator idWorker = CommonIdGenerator.getInstance();
        return  idWorker.nextId(serialNumberType.getBusinessId());
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId(int businessId) {
        long timestamp = timeGen();

        //业务id不能越界
        if (businessId > MAX_BUSINESS_ID || businessId < 0) {
            throw new IllegalArgumentException(String.format("businessId can't be greater than %d or less than 0",
                    MAX_BUSINESS_ID));
        }

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            //毫秒内序列从0-9中的随机数开始，避免大多情况都是0，都是偶数
            sequence = RandomUtils.nextInt(0, 10);
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (businessId << businessIdShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}

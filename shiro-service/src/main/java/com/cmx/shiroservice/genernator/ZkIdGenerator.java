package com.cmx.shiroservice.genernator;

import com.cmx.shiroapi.enums.SerialNumEnum;
import com.cmx.shiroservice.config.ZkConfig;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ZkIdGenerator implements IdGenerator {

    private static final String CHARSET = "utf-8";

    public static final int MAX_SIZE = 1024;

    private static String zkNodes;

    private static String appName;

    private static String authority;

    private static String root;
    /**单例模式 */
    private static volatile ZkIdGenerator instance;

    @Autowired
    private ZkConfig zkConfig;

    private ZkIdGenerator() {
    }

    public static ZkIdGenerator getInstance() {
        if (instance == null) {
            synchronized (CommonIdGenerator.class) {
                if (instance != null) {
                    return instance;
                }
                instance = new ZkIdGenerator();
            }

        }
        return instance;
    }

    private static void initWorkerId() {
        try {
            String zkNodes = instance.zkConfig.getZkNodes();
            String appName = instance.zkConfig.getAppName();
            root = (root == null || root.isEmpty()) ? ZkClient.ZK_ROOT : root;
            if (!Strings.isNullOrEmpty(zkNodes) && !Strings.isNullOrEmpty(appName)) {
                ZkClient.initZkClient(zkNodes, authority, root + "/" + appName);
                return;
            }

        } catch (final Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        throw new IllegalArgumentException("zkNodes and appName can not be empty.");
    }

    @PostConstruct
    public void init() {
        instance = this;
        instance.zkConfig = this.zkConfig;
        initWorkerId();
    }

    @Override
    public String generateId(SerialNumEnum serialNumberType, Integer appId) {
        return CommonIdGenerator.getInstance().generateId(serialNumberType, appId);
    }

    @Override
    public long generateIdLong(SerialNumEnum serialNumberType, Integer appId) {
        return CommonIdGenerator.getInstance().generateIdLong(serialNumberType, appId);
    }

    private static class ZkClient {

        /**向Zookeeper注册的根节点*/
        private static final String ZK_ROOT = "/dtpay";

        /**向Zookeeper注册的dtpay center节点*/
        private static final String ZK_NODE = "/node";

        /**向Zookeeper注册的dtpay config节点*/
        private static final String ZK_CONFIG = "/config";

        /**向Zookeeper注册的dtpay order节点*/
        private static final String ZK_ORDER = "/core";

        /**授权方式*/
        private static final String AUTHORIZATION = "digest";

        /**workId最大值*/
        private static final long WORKER_ID_MAX_VALUE = CommonIdGenerator.MAX_WORKER_ID;

        /**Session过期时间*/
        private static final int SESSION_TIMEOUT_MS = 60 * 1000;

        /**连接过期时间*/
        private static final int CONNECTION_TIMEOUT_MS = 10000;

        private static final int BASE_SLEEP_TIME_MS = 1000;

        private static final int LOCK_WAIT_TIME_MS = 30000;

        /**Curator客户端*/
        private static CuratorFramework client;

        /**注册节点监听*/
        private static TreeCache treeCache;

        /**节点创建时间*/
        private static volatile long pathCreatedTime;

        private static void closeClient() {
            if (null != client && null == client.getState()) {
                client.close();
            }
            client = null;
        }

        private static void initZkClient(final String zkNodes, final String authority, final String appNode) throws
                Exception {
            if (null != client) {
                closeClient();
            }
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(zkNodes)
                    .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                    .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                    .canBeReadOnly(false)
                    .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, Integer.MAX_VALUE))
                    .namespace(null)
                    .defaultData(null);
            if (authority != null && authority.length() > 0) {
                ACLProvider aclProvider = new ACLProvider() {
                    private List<ACL> acls;

                    @Override
                    public List<ACL> getDefaultAcl() {
                        if (acls == null) {
                            ArrayList<ACL> acls = ZooDefs.Ids.CREATOR_ALL_ACL;
                            acls.add(new ACL(ZooDefs.Perms.ALL, new Id(AUTHORIZATION, authority)));
                            this.acls = acls;
                        }
                        return acls;
                    }

                    @Override
                    public List<ACL> getAclForPath(final String path) {
                        return acls;
                    }
                };
                builder.aclProvider(aclProvider).authorization(AUTHORIZATION, authority.getBytes(CHARSET));
            }
            client = builder.build();
            client.start();
            doRegister(appNode);
        }

        private static void doRegister(final String appPath) throws Exception {
            if (null == client) {
                log.warn("Client closed, node: {}.", appPath);
                return;
            }
            if (null == client.checkExists().forPath(appPath)) {
                try {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(appPath);
                } catch (final KeeperException.NodeExistsException ignored) {
                }
            }
            InterProcessMutex lock = new InterProcessMutex(client, appPath);
            try {
                if (!lock.acquire(LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException(String.format("acquire lock failed after %s ms.", LOCK_WAIT_TIME_MS));
                }
                if (null == client.checkExists().forPath(appPath + ZK_NODE)) {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(appPath
                            + ZK_NODE);
                }
                if (null == client.checkExists().forPath(appPath + ZK_CONFIG + ZK_ORDER)) {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(appPath
                            + ZK_CONFIG + ZK_ORDER);
                }
                Set<Integer> orderIdSet = checkAndInitOrder(new String(client.getData().forPath(appPath + ZK_CONFIG
                        + ZK_ORDER), CHARSET));
                Set<Integer> nodeIdSet = client.getChildren().forPath(appPath
                        + ZK_NODE).stream().map(Integer::parseInt).collect(Collectors.toCollection(LinkedHashSet::new));
                for (Integer each : orderIdSet) {
                    if (!nodeIdSet.contains(each)) {
                        orderIdSet.remove(each);
                        orderIdSet.add(each);
                        final String nodePath = appPath + ZK_NODE + "/" + each;
                        String nodeDate = String.format("{\"ip\":\"%s\",\"hostName\":\"%s\",\"pid\":\"%s\"}",
                                InetAddress.getLocalHost().getHostAddress(), InetAddress.getLocalHost().getHostName(),
                                ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
                        log.info("nodePath: {}", nodePath);
                        log.info("nodeDate: {}", nodeDate);
                        client.inTransaction().create().withMode(CreateMode.EPHEMERAL).forPath(nodePath)
                                .and().setData().forPath(nodePath, nodeDate.getBytes(CHARSET))
                                .and().setData().forPath(appPath + ZK_CONFIG + ZK_ORDER, orderIdSet.toString()
                                .getBytes(CHARSET))
                                .and().commit();
                        pathCreatedTime = client.checkExists().forPath(nodePath).getCtime();
                        ZkClient.treeCache = new TreeCache(client, nodePath);
                        final TreeCache treeCache = ZkClient.treeCache;
                        treeCache.getListenable().addListener(
                                new TreeCacheListener() {
                                    @Override
                                    public void childEvent(final CuratorFramework curatorFramework, final
                                    TreeCacheEvent treeCacheEvent) throws Exception {
                                        long pathTime;
                                        try {
                                            pathTime = curatorFramework.checkExists().forPath(nodePath).getCtime();
                                        } catch (final Exception e) {
                                            pathTime = 0;
                                        }
                                        if (pathCreatedTime != pathTime) {
                                            doRegister(appPath);
                                            treeCache.close();
                                        }
                                    }
                                }
                        );
                        treeCache.start();
                        log.info("workId: {}", Long.valueOf(each));
                        CommonIdGenerator.setWorkerId(Long.valueOf(each));
                        return;
                    }
                }
                throw new IllegalStateException(String.format("The dtpay node is full! The max node amount is %s.",
                        WORKER_ID_MAX_VALUE));
            } catch (final Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                lock.release();
            }
        }

        private static Set<Integer> checkAndInitOrder(final String orderIdSetStr) {
            Set<Integer> result;
            try {
                String[] orderIdSplit = orderIdSetStr.replace("[", "").replace("]", "")
                        .replace(" ", "").split(",");
                result = Arrays.stream(orderIdSplit).map(Integer::parseInt).collect(Collectors.toCollection
                        (LinkedHashSet::new));
                if (result.size() != MAX_SIZE) {
                    throw new IllegalArgumentException();
                }
                for (Integer each : result) {
                    if (each < 0 || each >= WORKER_ID_MAX_VALUE) {
                        throw new IllegalArgumentException();
                    }
                }
                return result;
            } catch (final IllegalArgumentException ignored) {
            }
            result = new LinkedHashSet<>();
            for (int i = 0; i < WORKER_ID_MAX_VALUE; i++) {
                result.add(i);
            }
            return result;
        }
    }
}

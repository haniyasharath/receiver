package com.event.receiver.configurations;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    public static final String FUEL_COST_CACHE_STORE = "fuelCostCacheStore";

    @Value("${hazelcast.tcpipenabled}")
    private Boolean tcpIp;

    @Value("#{'${hazelcast.group.members}'.split(',')}")
    private String[] groupMembers;

    @Value("${hazelcast.maxdepth}")
    private int maxDepth;

    @Value("${hazelcast.idle.ttl}")
    private int idleTtl;

    @Bean
    public HazelcastInstance hazelcastInstance() {

        Config config = new Config();
        config.getPartitionGroupConfig().setGroupType(PartitionGroupConfig.MemberGroupType.PER_MEMBER);

        config.setProperty("hazelcast.logging.type", "slf4j");

        NetworkConfig network = config.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(getTcpIp());

        for (String member : getGroupMembers()) {
            join.getTcpIpConfig().addMember(member);
        }

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName(HazelcastConfiguration.FUEL_COST_CACHE_STORE);
        mapConfig.setBackupCount(1);
        mapConfig.getEvictionConfig().setSize(getMaxDepth());
        mapConfig.setMaxIdleSeconds(getIdleTtl());
        mapConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.LRU);

        config.addMapConfig(mapConfig);
        return Hazelcast.newHazelcastInstance(config);
    }

    public Boolean getTcpIp() {
        return tcpIp;
    }

    public String[] getGroupMembers() {
        return groupMembers;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getIdleTtl() {
        return idleTtl;
    }

    public void setTcpIp(Boolean tcpIp) {
        this.tcpIp = tcpIp;
    }

    public void setGroupMembers(String[] groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setIdleTtl(int idleTtl) {
        this.idleTtl = idleTtl;
    }
}

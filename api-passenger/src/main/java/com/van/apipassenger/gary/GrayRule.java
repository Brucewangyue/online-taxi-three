package com.van.apipassenger.gary;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class GrayRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        ILoadBalancer loadBalancer = getLoadBalancer();

        Map<String, Object> paramsMap = GrayParameter.get();
        String userId = paramsMap.get("userId").toString();

        // 根据用户id查询用户的灰度规则
        List<Server> allServers = loadBalancer.getAllServers();
        DiscoveryEnabledServer selectedServer = null;
        for (int i = 0; i < allServers.size(); i++) {
            selectedServer = (DiscoveryEnabledServer) allServers.get(i);
            InstanceInfo instanceInfo = selectedServer.getInstanceInfo();
            String grayVersion = instanceInfo.getMetadata().get("grayVersion");
            if (userId.equals("1") && grayVersion.equals("v1"))
                break;
        }

        return selectedServer;
    }
}

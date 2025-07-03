package com.chenzhihao.shopcommon.filter;

import com.chenzhihao.shopcommon.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Slf4j
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = -1)
public class DubboUserContextFilter implements Filter {

    // 常量Key
    private static final String CTX_KEY_USER_ID = "user_id";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // Consumer端逻辑
        if (RpcContext.getServiceContext().isConsumerSide()) {
            Long userId = UserContext.getUserId();
            if (userId != null) {
                RpcContext.getClientAttachment().setAttachment(CTX_KEY_USER_ID, userId.toString());
                log.info("Consumer的Dubbo_filter获得解析的userId:{}，发送给下游Provider", userId);
            }

        }

        try {
            // Provider端逻辑
            if (RpcContext.getServiceContext().isProviderSide()) {
                // 获取userId
                String userId = RpcContext.getServerAttachment().getAttachment(CTX_KEY_USER_ID);
                if (userId != null) {
                    UserContext.setUserId(Long.parseLong(userId));
                    log.info("Provider的Dubbo_filter获得解析的userId:{}，存入UserContext", userId);
                }
            }
            return invoker.invoke(invocation);
        } finally {
            // 清理线程，避免数据污染
            if (RpcContext.getServiceContext().isProviderSide()) {
                UserContext.removeUserId();
            }
        }
    }

}

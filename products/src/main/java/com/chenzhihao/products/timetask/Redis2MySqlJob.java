package com.chenzhihao.products.timetask;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class Redis2MySqlJob {
    /**
     * 将Redis中的库存数据同步到Mysql
     */
    @XxlJob("StockUpdateJobHandler")
    public void stockUpdateJobHandler() throws Exception {
        System.out.println("执行库存更新任务");
    }

}

package com.chenzhihao.products.controller;


import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.vo.PageResult;


/**
 * 商品模块 前端控制器
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    @Autowired
    private ICommodityService commodityService;

    /**
     * 根据id查找商品信息
     * @param id 商品id
     * @return {@link Result }<{@link Commodity }>
     */
    @GetMapping("/get/{id}")
    public Result<CommodityRedisVo> getCommodityById(@PathVariable Long id) {
        CommodityRedisVo commodity = commodityService.getCommodityFromCache(id);
        return Result.success(commodity);
    }


    /**
     * 商品搜索接口
     * @param queryDTO SpringBoot会自动将URL中的查询参数封装到DTO对象中
     * @return 统一的分页查询结果
     */
    @GetMapping("/search")
    public PageResult<CommodityEsDoc> search(CommodityQueryDTO queryDTO) {
        return commodityService.search(queryDTO);
    }


    /**
     * 测试经过网关之后是否获取到userId
     * @return {@link Result }<{@link Long 用户userId}>
     */
    @GetMapping("/testId")
    public Result<Long> testId() {
        Long id = UserContext.getUserId();
        return Result.success(id);
    }

    /*
     * 根据商品信息生成订单
     * @param comPayDto 商品信息
     * @return {@link Result }<{@link ComPayVo }>
     **/

    @PostMapping("/pay")
    public Result<ComPayVo> crePay(@RequestBody ComPayDto comPayDto){
       return commodityService.crePay(comPayDto);
    }

}

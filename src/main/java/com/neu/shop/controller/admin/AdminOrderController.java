package com.neu.shop.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neu.shop.pojo.*;
import com.neu.shop.service.GoodsService;
import com.neu.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 文辉 on 2017/7/26.
 */
@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/send")
    public String sendOrder(@RequestParam(value = "page",defaultValue = "1")Integer pn, Model model) {

        //一页显示几个数据
        PageHelper.startPage(pn, 2);


        //查询未发货订单
        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(false);
        List<Order> orderList = orderService.selectOrderByExample(orderExample);
        model.addAttribute("sendOrder", orderList);


        //查询该订单中的商品
        for (int i = 0; i < orderList.size(); i++) {
            //获取订单项中的goodsid
            Order order = orderList.get(i);
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.or().andOrderidEqualTo(order.getOrderid());
            List<OrderItem> orderItemList = orderService.getOrderItemByExample(orderItemExample);
            List<Integer> goodsIdList = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                goodsIdList.add(orderItem.getGoodsid());
            }

            //根据goodsid查询商品
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.or().andGoodsidIn(goodsIdList);
            List<Goods> goodsList = goodsService.selectByExample(goodsExample);
            order.setGoodsInfo(goodsList);

            //查询地址
            Address address = orderService.getAddressByKey(order.getAddressid());
            order.setAddress(address);

            orderList.set(i, order);
        }

        //显示几个页号
        PageInfo page = new PageInfo(orderList,5);
        model.addAttribute("pageInfo", page);

        return "adminAllOrder";
    }

    @RequestMapping("/sendGoods")
    public String sendGoods(Integer orderid) {
        Order order = new Order();
        order.setOrderid(orderid);
        order.setIssend(true);
        orderService.updateOrderByKey(order);
        return "redirect:/admin/order/send";
    }

    @RequestMapping("/receiver")
    public String receiveOrder() {
        return "adminAllOrder";
    }

    @RequestMapping("/complete")
    public String completeOrder() {
        return "adminAllOrder";
    }
}
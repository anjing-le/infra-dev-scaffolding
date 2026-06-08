package com.anjing.example.statemachine;

import com.anjing.annotation.ScaffoldSample;
import com.anjing.model.response.APIResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 🔄 状态机使用示例服务
 * 
 * <p>演示如何在实际业务中使用状态机进行订单状态管理</p>
 * 
 * <h3>🎯 示例场景：</h3>
 * <ul>
 *   <li><b>订单创建</b> - 创建订单并初始化状态</li>
 *   <li><b>订单支付</b> - 处理支付并转换状态</li>
 *   <li><b>订单发货</b> - 商家发货并更新状态</li>
 *   <li><b>订单完成</b> - 用户确认收货完成订单</li>
 *   <li><b>订单取消/退货</b> - 处理取消和退货流程</li>
 * </ul>
 * 
 * <h3>💡 最佳实践：</h3>
 * <ul>
 *   <li>✅ <b>状态检查</b> - 操作前先检查当前状态是否支持</li>
 *   <li>🔒 <b>条件验证</b> - 使用上下文进行业务条件验证</li>
 *   <li>📋 <b>日志记录</b> - 记录所有状态转换操作</li>
 *   <li>🛡️ <b>异常处理</b> - 优雅处理状态转换异常</li>
 * </ul>
 * 
 * @author Backend Template Team
 * @version 1.0
 * @since 1.0.0
 */
@ScaffoldSample("状态机服务示例")
@Service
@Slf4j
public class StateMachineExampleService {

    /**
     * 示例1: 创建订单
     * 场景：用户下单，订单进入CREATED状态
     */
    public APIResponse<OrderInfo> createOrder(CreateOrderRequest request) {
        log.info("=== 示例1: 创建订单 ===");
        
        // 创建订单信息
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId("ORDER_" + System.currentTimeMillis());
        orderInfo.setUserId(request.getUserId());
        orderInfo.setProductId(request.getProductId());
        orderInfo.setAmount(request.getAmount());
        orderInfo.setCurrentState(OrderState.CREATED);
        orderInfo.setShippingAddress(request.getShippingAddress());
        orderInfo.setCreateTime(System.currentTimeMillis());
        
        log.info("📋 订单创建成功: {} | 状态: {} | 金额: {}", 
                orderInfo.getOrderId(), 
                orderInfo.getCurrentState().getDisplayName(), 
                orderInfo.getAmount());
        
        return APIResponse.success(orderInfo, "订单创建成功");
    }

    /**
     * 示例2: 确认订单
     * 场景：用户确认订单信息，状态从CREATED转为CONFIRMED
     */
    public APIResponse<OrderInfo> confirmOrder(String orderId) {
        log.info("=== 示例2: 确认订单 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(orderId, OrderState.CREATED);
        
        try {
            // 准备状态转换上下文
            Map<String, Object> context = buildTransitionContext(orderInfo);
            
            // 执行状态转换
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.CONFIRM, 
                    context
            );
            
            // 更新订单状态
            orderInfo.setCurrentState(newState);
            
            return APIResponse.success(orderInfo, "订单确认成功");
            
        } catch (Exception e) {
            log.error("订单确认失败: {}", e.getMessage());
            return APIResponse.error("订单确认失败: " + e.getMessage());
        }
    }

    /**
     * 示例3: 支付订单
     * 场景：用户完成支付，状态转为PAID（带条件验证）
     */
    public APIResponse<OrderInfo> payOrder(PayOrderRequest request) {
        log.info("=== 示例3: 支付订单 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(request.getOrderId(), OrderState.CREATED);
        
        try {
            // 检查当前状态是否支持支付
            if (!orderInfo.getCurrentState().isPayable()) {
                return APIResponse.error("当前订单状态不支持支付: " + orderInfo.getCurrentState().getDisplayName());
            }
            
            // 准备支付上下文（包含支付条件）
            Map<String, Object> context = buildTransitionContext(orderInfo);
            context.put("amount", request.getAmount());
            context.put("paymentMethod", request.getPaymentMethod());
            
            // 执行状态转换（会自动验证支付条件）
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.PAY, 
                    context
            );
            
            // 更新订单状态和支付信息
            orderInfo.setCurrentState(newState);
            orderInfo.setPaymentMethod(request.getPaymentMethod());
            orderInfo.setPayTime(System.currentTimeMillis());
            
            return APIResponse.success(orderInfo, "订单支付成功");
            
        } catch (Exception e) {
            log.error("订单支付失败: {}", e.getMessage());
            return APIResponse.error("订单支付失败: " + e.getMessage());
        }
    }

    /**
     * 示例4: 发货
     * 场景：商家发货，状态从PAID转为SHIPPED（带条件验证）
     */
    public APIResponse<OrderInfo> shipOrder(ShipOrderRequest request) {
        log.info("=== 示例4: 发货 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(request.getOrderId(), OrderState.PAID);
        
        try {
            // 检查当前状态是否支持发货
            if (!orderInfo.getCurrentState().isShippable()) {
                return APIResponse.error("当前订单状态不支持发货: " + orderInfo.getCurrentState().getDisplayName());
            }
            
            // 准备发货上下文（包含发货条件）
            Map<String, Object> context = buildTransitionContext(orderInfo);
            context.put("shippingAddress", orderInfo.getShippingAddress());
            context.put("hasStock", request.getHasStock());
            
            // 执行状态转换（会自动验证发货条件）
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.SHIP, 
                    context
            );
            
            // 更新订单状态和发货信息
            orderInfo.setCurrentState(newState);
            orderInfo.setTrackingNumber(request.getTrackingNumber());
            orderInfo.setShipTime(System.currentTimeMillis());
            
            return APIResponse.success(orderInfo, "订单发货成功");
            
        } catch (Exception e) {
            log.error("订单发货失败: {}", e.getMessage());
            return APIResponse.error("订单发货失败: " + e.getMessage());
        }
    }

    /**
     * 示例5: 确认收货
     * 场景：用户确认收货，订单完成
     */
    public APIResponse<OrderInfo> confirmReceipt(String orderId) {
        log.info("=== 示例5: 确认收货 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(orderId, OrderState.DELIVERED);
        
        try {
            // 准备状态转换上下文
            Map<String, Object> context = buildTransitionContext(orderInfo);
            
            // 执行状态转换
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.CONFIRM_RECEIPT, 
                    context
            );
            
            // 更新订单状态
            orderInfo.setCurrentState(newState);
            orderInfo.setFinishTime(System.currentTimeMillis());
            
            return APIResponse.success(orderInfo, "订单完成");
            
        } catch (Exception e) {
            log.error("确认收货失败: {}", e.getMessage());
            return APIResponse.error("确认收货失败: " + e.getMessage());
        }
    }

    /**
     * 示例6: 取消订单
     * 场景：用户取消未支付的订单
     */
    public APIResponse<OrderInfo> cancelOrder(CancelOrderRequest request) {
        log.info("=== 示例6: 取消订单 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(request.getOrderId(), OrderState.CREATED);
        
        try {
            // 检查当前状态是否支持取消
            if (!orderInfo.getCurrentState().isCancellable()) {
                return APIResponse.error("当前订单状态不支持取消: " + orderInfo.getCurrentState().getDisplayName());
            }
            
            // 准备状态转换上下文
            Map<String, Object> context = buildTransitionContext(orderInfo);
            context.put("cancelReason", request.getCancelReason());
            
            // 执行状态转换
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.CANCEL, 
                    context
            );
            
            // 更新订单状态
            orderInfo.setCurrentState(newState);
            orderInfo.setCancelReason(request.getCancelReason());
            orderInfo.setCancelTime(System.currentTimeMillis());
            
            return APIResponse.success(orderInfo, "订单取消成功");
            
        } catch (Exception e) {
            log.error("订单取消失败: {}", e.getMessage());
            return APIResponse.error("订单取消失败: " + e.getMessage());
        }
    }

    /**
     * 示例7: 申请退货
     * 场景：用户申请退货（带条件验证）
     */
    public APIResponse<OrderInfo> requestReturn(ReturnOrderRequest request) {
        log.info("=== 示例7: 申请退货 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(request.getOrderId(), OrderState.DELIVERED);
        
        try {
            // 检查当前状态是否支持退货
            if (!orderInfo.getCurrentState().isReturnable()) {
                return APIResponse.error("当前订单状态不支持退货: " + orderInfo.getCurrentState().getDisplayName());
            }
            
            // 准备退货上下文（包含退货条件）
            Map<String, Object> context = buildTransitionContext(orderInfo);
            context.put("returnReason", request.getReturnReason());
            context.put("orderTime", orderInfo.getCreateTime());
            
            // 执行状态转换（会自动验证退货条件）
            OrderState newState = OrderStateMachine.INSTANCE.transition(
                    orderInfo.getCurrentState(), 
                    OrderEvent.REQUEST_RETURN, 
                    context
            );
            
            // 更新订单状态
            orderInfo.setCurrentState(newState);
            orderInfo.setReturnReason(request.getReturnReason());
            orderInfo.setReturnTime(System.currentTimeMillis());
            
            return APIResponse.success(orderInfo, "退货申请成功");
            
        } catch (Exception e) {
            log.error("退货申请失败: {}", e.getMessage());
            return APIResponse.error("退货申请失败: " + e.getMessage());
        }
    }

    /**
     * 示例8: 查询订单支持的操作
     * 场景：根据当前状态查询可执行的操作
     */
    public APIResponse<OrderOperationInfo> getOrderOperations(String orderId) {
        log.info("=== 示例8: 查询订单支持的操作 ===");
        
        // 模拟获取订单信息
        OrderInfo orderInfo = mockGetOrderInfo(orderId, OrderState.CREATED);
        
        // 获取当前状态支持的事件
        Set<OrderEvent> supportedEvents = OrderStateMachine.INSTANCE.getSupportedEvents(orderInfo.getCurrentState());
        
        // 构建操作信息
        OrderOperationInfo operationInfo = new OrderOperationInfo();
        operationInfo.setOrderId(orderId);
        operationInfo.setCurrentState(orderInfo.getCurrentState());
        operationInfo.setSupportedEvents(supportedEvents);
        
        // 根据业务逻辑判断具体可执行的操作
        operationInfo.setCanPay(orderInfo.getCurrentState().isPayable());
        operationInfo.setCanCancel(orderInfo.getCurrentState().isCancellable());
        operationInfo.setCanShip(orderInfo.getCurrentState().isShippable());
        operationInfo.setCanReturn(orderInfo.getCurrentState().isReturnable());
        
        return APIResponse.success(operationInfo, "查询成功");
    }

    /**
     * 示例9: 状态机信息查询
     * 场景：查询状态机的配置信息和统计数据
     */
    public APIResponse<String> getStateMachineInfo() {
        log.info("=== 示例9: 状态机信息查询 ===");
        
        StringBuilder info = new StringBuilder();
        
        // 基础统计信息
        info.append(OrderStateMachine.INSTANCE.getOrderStateStatistics()).append("\n\n");
        
        // 状态转换图
        info.append("详细转换图:\n");
        info.append(OrderStateMachine.INSTANCE.getTransitionGraph());
        
        return APIResponse.success(info.toString(), "查询成功");
    }

    // ==================== 辅助方法 ====================
    
    /**
     * 构建状态转换上下文
     */
    private Map<String, Object> buildTransitionContext(OrderInfo orderInfo) {
        Map<String, Object> context = new HashMap<>();
        context.put("orderId", orderInfo.getOrderId());
        context.put("userId", orderInfo.getUserId());
        return context;
    }
    
    /**
     * 模拟获取订单信息
     */
    private OrderInfo mockGetOrderInfo(String orderId, OrderState currentState) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderId);
        orderInfo.setUserId(12345L);
        orderInfo.setProductId(67890L);
        orderInfo.setAmount(new BigDecimal("199.99"));
        orderInfo.setCurrentState(currentState);
        orderInfo.setShippingAddress("北京市朝阳区某某街道某某小区");
        orderInfo.setCreateTime(System.currentTimeMillis() - 3600000); // 1小时前创建
        return orderInfo;
    }

    // ==================== 请求响应DTO类 ====================
    
    @Data
    public static class CreateOrderRequest {
        private Long userId;
        private Long productId;
        private BigDecimal amount;
        private String shippingAddress;
    }
    
    @Data
    public static class PayOrderRequest {
        private String orderId;
        private BigDecimal amount;
        private String paymentMethod;
    }
    
    @Data
    public static class ShipOrderRequest {
        private String orderId;
        private String trackingNumber;
        private Boolean hasStock = true;
    }
    
    @Data
    public static class CancelOrderRequest {
        private String orderId;
        private String cancelReason;
    }
    
    @Data
    public static class ReturnOrderRequest {
        private String orderId;
        private String returnReason;
    }
    
    @Data
    public static class OrderInfo {
        private String orderId;
        private Long userId;
        private Long productId;
        private BigDecimal amount;
        private OrderState currentState;
        private String shippingAddress;
        private String paymentMethod;
        private String trackingNumber;
        private String cancelReason;
        private String returnReason;
        private Long createTime;
        private Long payTime;
        private Long shipTime;
        private Long finishTime;
        private Long cancelTime;
        private Long returnTime;
    }
    
    @Data
    public static class OrderOperationInfo {
        private String orderId;
        private OrderState currentState;
        private Set<OrderEvent> supportedEvents;
        private Boolean canPay;
        private Boolean canCancel;
        private Boolean canShip;
        private Boolean canReturn;
    }
}

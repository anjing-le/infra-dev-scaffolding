package com.anjing.example.statemachine;

import com.anjing.annotation.ScaffoldSample;
import com.anjing.statemachine.BaseStateMachine;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 📋 订单状态机实现
 * 
 * <p>定义订单完整的状态转换规则，包括基础转换和带条件的转换</p>
 * 
 * <h3>🔄 状态转换图：</h3>
 * <pre>
 * ┌─────────┐    CONFIRM    ┌───────────┐      PAY      ┌──────┐
 * │ CREATED │──────────────>│ CONFIRMED │──────────────>│ PAID │
 * └─────────┘               └───────────┘               └──────┘
 *      │                           │                        │
 *      │ PAY (直接支付)              │ CANCEL                 │ SHIP
 *      └───────────────────────────┼────────────────────────┼──────┐
 *                                  │                        │      │
 *                                  ▼                        ▼      ▼
 *      ┌──────────┐           ┌───────────┐           ┌─────────┐ ┌─────────┐
 *      │ TIMEOUT  │           │ CANCELLED │           │ SHIPPED │ │ CLOSED  │
 *      │    │     │           └───────────┘           └─────────┘ └─────────┘
 *      │    ▼     │                                        │
 *      │ CLOSED   │                                        │ DELIVER
 *      └──────────┘                                        ▼
 *                                                    ┌───────────┐
 *                                    REQUEST_RETURN  │ DELIVERED │  CONFIRM_RECEIPT
 *                              ┌─────────────────────┤           ├─────────────────┐
 *                              │                     └───────────┘                 │
 *                              ▼                                                   ▼
 *                        ┌──────────┐                                        ┌──────────┐
 *                        │ RETURNED │                                        │ FINISHED │
 *                        └──────────┘                                        └──────────┘
 * </pre>
 * 
 * <h3>🎯 业务规则：</h3>
 * <ul>
 *   <li><b>支付条件</b> - 订单金额必须大于0</li>
 *   <li><b>取消条件</b> - 只能在未支付状态下取消</li>
 *   <li><b>发货条件</b> - 必须已支付且有收货地址</li>
 *   <li><b>退货条件</b> - 必须在可退货期限内</li>
 * </ul>
 * 
 * @author Backend Template Team
 * @version 1.0
 * @since 1.0.0
 */
@ScaffoldSample("订单状态机示例")
@Slf4j
public class OrderStateMachine extends BaseStateMachine<OrderState, OrderEvent> {

    /**
     * 单例实例
     */
    public static final OrderStateMachine INSTANCE = new OrderStateMachine();

    /**
     * 私有构造函数，在初始化块中配置状态转换规则
     */
    private OrderStateMachine() {
        initTransitionRules();
        initTransitionListeners();
    }

    /**
     * 🔧 初始化状态转换规则
     */
    private void initTransitionRules() {
        
        // ==================== CREATED 状态的转换 ====================
        
        // 创建 -> 确认
        putTransition(OrderState.CREATED, OrderEvent.CONFIRM, OrderState.CONFIRMED);
        
        // 创建 -> 支付（直接支付，跳过确认）
        putTransition(OrderState.CREATED, OrderEvent.PAY, OrderState.PAID, 
                this::validatePaymentCondition);
        
        // 创建 -> 取消
        putTransition(OrderState.CREATED, OrderEvent.CANCEL, OrderState.CANCELLED);
        
        // 创建 -> 超时关闭
        putTransition(OrderState.CREATED, OrderEvent.TIMEOUT, OrderState.CLOSED);

        // ==================== CONFIRMED 状态的转换 ====================
        
        // 确认 -> 支付
        putTransition(OrderState.CONFIRMED, OrderEvent.PAY, OrderState.PAID, 
                this::validatePaymentCondition);
        
        // 确认 -> 取消
        putTransition(OrderState.CONFIRMED, OrderEvent.CANCEL, OrderState.CANCELLED);
        
        // 确认 -> 超时关闭
        putTransition(OrderState.CONFIRMED, OrderEvent.TIMEOUT, OrderState.CLOSED);

        // ==================== PAID 状态的转换 ====================
        
        // 已支付 -> 确认（幂等操作，状态不变）
        putTransition(OrderState.PAID, OrderEvent.CONFIRM, OrderState.PAID);
        
        // 已支付 -> 发货
        putTransition(OrderState.PAID, OrderEvent.SHIP, OrderState.SHIPPED, 
                this::validateShippingCondition);
        
        // 已支付 -> 自动发货（虚拟商品）
        putTransition(OrderState.PAID, OrderEvent.AUTO_SHIP, OrderState.SHIPPED);

        // ==================== SHIPPED 状态的转换 ====================
        
        // 已发货 -> 送达
        putTransition(OrderState.SHIPPED, OrderEvent.DELIVER, OrderState.DELIVERED);
        
        // 已发货 -> 申请退货
        putTransition(OrderState.SHIPPED, OrderEvent.REQUEST_RETURN, OrderState.RETURNED, 
                this::validateReturnCondition);

        // ==================== DELIVERED 状态的转换 ====================
        
        // 已送达 -> 确认收货完成
        putTransition(OrderState.DELIVERED, OrderEvent.CONFIRM_RECEIPT, OrderState.FINISHED);
        
        // 已送达 -> 自动完成（超过确认期限）
        putTransition(OrderState.DELIVERED, OrderEvent.AUTO_FINISH, OrderState.FINISHED);
        
        // 已送达 -> 申请退货
        putTransition(OrderState.DELIVERED, OrderEvent.REQUEST_RETURN, OrderState.RETURNED, 
                this::validateReturnCondition);

        // ==================== 退货相关转换 ====================
        
        // 退货完成
        putTransition(OrderState.RETURNED, OrderEvent.RETURN_COMPLETED, OrderState.RETURNED);
        
        log.info("订单状态机初始化完成，共配置 {} 个状态转换规则", getAllTransitions().size());
        log.debug("状态转换图:\n{}", getTransitionGraph());
    }

    /**
     * 🔧 初始化状态转换监听器
     */
    private void initTransitionListeners() {
        // 添加状态转换日志监听器
        addListener(new StateTransitionListener<OrderState, OrderEvent>() {
            @Override
            public void beforeTransition(OrderState fromState, OrderEvent event, OrderState toState, 
                                       Map<String, Object> context) {
                String orderId = (String) context.get("orderId");
                log.info("📋 订单状态即将转换: 订单[{}] {} --[{}]--> {}", 
                        orderId, fromState.getDisplayName(), event.getDisplayName(), toState.getDisplayName());
            }

            @Override
            public void afterTransition(OrderState fromState, OrderEvent event, OrderState toState, 
                                      Map<String, Object> context) {
                String orderId = (String) context.get("orderId");
                log.info("✅ 订单状态转换完成: 订单[{}] 当前状态: {}", orderId, toState.getDisplayName());
                
                // 可以在这里添加状态转换后的业务逻辑
                // 例如：发送通知、更新数据库、触发其他业务流程等
                handlePostTransitionBusiness(fromState, event, toState, context);
            }
        });
    }

    /**
     * 💰 验证支付条件
     */
    private boolean validatePaymentCondition(OrderState fromState, OrderEvent event, Map<String, Object> context) {
        // 检查订单金额
        BigDecimal amount = (BigDecimal) context.get("amount");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("支付条件不满足: 订单金额必须大于0, 当前金额: {}", amount);
            return false;
        }
        
        // 检查支付方式
        String paymentMethod = (String) context.get("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            log.warn("支付条件不满足: 支付方式不能为空");
            return false;
        }
        
        log.debug("支付条件验证通过: 金额={}, 支付方式={}", amount, paymentMethod);
        return true;
    }

    /**
     * 🚚 验证发货条件
     */
    private boolean validateShippingCondition(OrderState fromState, OrderEvent event, Map<String, Object> context) {
        // 检查收货地址
        String shippingAddress = (String) context.get("shippingAddress");
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            log.warn("发货条件不满足: 收货地址不能为空");
            return false;
        }
        
        // 检查库存（可选）
        Boolean hasStock = (Boolean) context.get("hasStock");
        if (hasStock != null && !hasStock) {
            log.warn("发货条件不满足: 商品库存不足");
            return false;
        }
        
        log.debug("发货条件验证通过: 收货地址={}", shippingAddress);
        return true;
    }

    /**
     * 🔄 验证退货条件
     */
    private boolean validateReturnCondition(OrderState fromState, OrderEvent event, Map<String, Object> context) {
        // 检查退货期限
        Long orderTime = (Long) context.get("orderTime");
        if (orderTime != null) {
            long currentTime = System.currentTimeMillis();
            long daysDiff = (currentTime - orderTime) / (24 * 60 * 60 * 1000);
            
            // 假设退货期限为7天
            if (daysDiff > 7) {
                log.warn("退货条件不满足: 超过退货期限，已过{}天", daysDiff);
                return false;
            }
        }
        
        // 检查退货原因
        String returnReason = (String) context.get("returnReason");
        if (returnReason == null || returnReason.trim().isEmpty()) {
            log.warn("退货条件不满足: 退货原因不能为空");
            return false;
        }
        
        log.debug("退货条件验证通过: 退货原因={}", returnReason);
        return true;
    }

    /**
     * 🎯 处理状态转换后的业务逻辑
     */
    private void handlePostTransitionBusiness(OrderState fromState, OrderEvent event, OrderState toState, 
                                            Map<String, Object> context) {
        String orderId = (String) context.get("orderId");
        
        switch (toState) {
            case PAID:
                log.info("📧 发送支付成功通知: 订单[{}]", orderId);
                // 这里可以调用通知服务
                break;
                
            case SHIPPED:
                log.info("📦 发送发货通知: 订单[{}]", orderId);
                // 这里可以调用物流服务
                break;
                
            case DELIVERED:
                log.info("🚚 发送送达通知: 订单[{}]", orderId);
                // 这里可以启动自动确认收货定时器
                break;
                
            case FINISHED:
                log.info("🎉 订单完成，发送完成通知: 订单[{}]", orderId);
                // 这里可以触发积分奖励、评价提醒等
                break;
                
            case CANCELLED:
            case CLOSED:
                log.info("❌ 订单关闭，处理退款: 订单[{}]", orderId);
                // 这里可以调用退款服务
                break;
                
            case RETURNED:
                log.info("🔄 处理退货流程: 订单[{}]", orderId);
                // 这里可以调用退货处理服务
                break;
                
            default:
                // 其他状态暂不处理
                break;
        }
    }

    /**
     * 📊 获取订单状态统计信息（扩展功能）
     */
    public String getOrderStateStatistics() {
        return String.format(
                "订单状态机统计:\n" +
                "- 状态总数: %d\n" +
                "- 事件总数: %d\n" +
                "- 转换规则总数: %d\n" +
                "- 终态数量: %d\n" +
                "- 可支付状态数量: %d",
                OrderState.values().length,
                OrderEvent.values().length,
                getAllTransitions().size(),
                (int) java.util.Arrays.stream(OrderState.values()).filter(OrderState::isFinalState).count(),
                (int) java.util.Arrays.stream(OrderState.values()).filter(OrderState::isPayable).count()
        );
    }
}

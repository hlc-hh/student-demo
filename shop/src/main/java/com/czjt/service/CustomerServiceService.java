package com.czjt.service;

import com.czjt.mapper.ChatHistoryMapper;
import com.czjt.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 流式对话（逐字输出）
     */
    public void chatStream(String message, String sessionId, Long userId, Consumer<String> onChunk) {
        try {
            log.info("收到用户消息(流式): sessionId={}, userId={}, message={}", sessionId, userId, message);

            // 构建上下文
            String context = buildContext(sessionId, userId);

            // 构建知识库
            String knowledge = retrieveKnowledge(message, userId);

            // 构建提示词
            String prompt = buildPrompt(message, context, knowledge);

            log.info("提示词长度: {}", prompt.length());

            AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());

            // 使用流式输出
            Flux<String> flux = chatClient.prompt()
                    .user(prompt)
                    .stream()
                    .content();

            flux.doOnNext(chunk -> {
                        if (chunk != null && !chunk.isEmpty()) {
                            fullResponse.get().append(chunk);
                            onChunk.accept(chunk);
                        }
                    })
                    .doOnComplete(() -> {
                        String response = fullResponse.get().toString();
                        log.info("AI回复完成，长度: {}", response.length());

                        // 保存聊天记录
                        ChatHistory chatHistory = new ChatHistory();
                        chatHistory.setSessionId(sessionId);
                        chatHistory.setUserId(userId);
                        chatHistory.setMessage(message);
                        chatHistory.setResponse(response);
                        chatHistoryMapper.insert(chatHistory);
                    })
                    .doOnError(error -> {
                        log.error("流式响应出错", error);
                        onChunk.accept("抱歉，我暂时遇到了一些问题，请稍后再试。");
                    })
                    .blockLast();

        } catch (Exception e) {
            log.error("AI客服处理消息失败", e);
            onChunk.accept("抱歉，我暂时遇到了一些问题，请稍后再试。");
        }
    }

    // 非流式版本（保留兼容）
    public String chat(String message, String sessionId, Long userId) {
        try {
            log.info("收到用户消息: sessionId={}, userId={}, message={}", sessionId, userId, message);

            String context = buildContext(sessionId, userId);
            String knowledge = retrieveKnowledge(message, userId);
            String prompt = buildPrompt(message, context, knowledge);

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setSessionId(sessionId);
            chatHistory.setUserId(userId);
            chatHistory.setMessage(message);
            chatHistory.setResponse(response);
            chatHistoryMapper.insert(chatHistory);

            log.info("AI回复: {}", response);
            return response;

        } catch (Exception e) {
            log.error("AI客服处理消息失败", e);
            return "抱歉，我暂时遇到了一些问题，请稍后再试。";
        }
    }

    private String buildContext(String sessionId, Long userId) {
        StringBuilder context = new StringBuilder();

        List<ChatHistory> history = chatHistoryMapper.findBySessionId(sessionId);

        if (!history.isEmpty()) {
            context.append("对话历史：\n");
            int recentCount = Math.min(history.size(), 5);
            List<ChatHistory> recentHistory = history.subList(history.size() - recentCount, history.size());

            for (ChatHistory chat : recentHistory) {
                context.append("用户：").append(chat.getMessage()).append("\n");
                context.append("助手：").append(chat.getResponse()).append("\n");
            }
        }

        return context.toString();
    }

    private String retrieveKnowledge(String query, Long userId) {
        StringBuilder knowledge = new StringBuilder();

        if ((query.contains("商品") || query.contains("产品") || query.contains("价格") ||
                query.contains("推荐") || query.contains("有什么")) && !query.contains("订单")) {
            knowledge.append("【商品信息】\n");
            try {
                List<Product> products = productService.findAll();
                if (products != null && !products.isEmpty()) {
                    knowledge.append("当前商城共有 ").append(products.size()).append(" 款商品：\n");

                    List<Product> topProducts = products.stream()
                            .limit(10)
                            .collect(Collectors.toList());

                    for (Product product : topProducts) {
                        knowledge.append("- ").append(product.getName());
                        if (product.getSubName() != null) {
                            knowledge.append("(").append(product.getSubName()).append(")");
                        }
                        knowledge.append("，价格：¥").append(product.getPrice());
                        knowledge.append("，库存：").append(product.getStock());
                        knowledge.append("，销量：").append(product.getSales()).append("\n");
                    }

                    if (products.size() > 10) {
                        knowledge.append("... 还有 ").append(products.size() - 10).append(" 款商品\n");
                    }
                } else {
                    knowledge.append("暂无商品信息\n");
                }
            } catch (Exception e) {
                log.error("获取商品信息失败", e);
                knowledge.append("商品信息暂时无法获取\n");
            }
            knowledge.append("\n");
        }

        if (query.contains("分类") || query.contains("类别")) {
            knowledge.append("【商品分类】\n");
            try {
                List<Category> categories = categoryService.findAll();
                if (categories != null && !categories.isEmpty()) {
                    knowledge.append("商品分类包括：\n");
                    for (Category category : categories) {
                        knowledge.append("- ").append(category.getName()).append("\n");
                    }
                }
            } catch (Exception e) {
                log.error("获取分类信息失败", e);
            }
            knowledge.append("\n");
        }

        if (query.contains("订单") || query.contains("我的订单") || query.contains("订单状态")) {
            knowledge.append("【订单信息】\n");
            if (userId != null) {
                try {
                    List<Order> orders = orderService.getUserOrders(userId);
                    if (orders != null && !orders.isEmpty()) {
                        knowledge.append("您共有 ").append(orders.size()).append(" 个订单：\n");

                        List<Order> recentOrders = orders.stream()
                                .limit(5)
                                .collect(Collectors.toList());

                        for (Order order : recentOrders) {
                            knowledge.append("- 订单号：").append(order.getOrderNo());
                            knowledge.append("，金额：¥").append(order.getPayPrice());
                            String statusDesc = orderService.getStatusDescription(order.getStatus());
                            knowledge.append("，状态：").append(statusDesc);
                            if (order.getCreateTime() != null) {
                                knowledge.append("，下单时间：").append(order.getCreateTime().toString());
                            }
                            knowledge.append("\n");

                            if (order.getRefundStatus() != null && order.getRefundStatus() != 0) {
                                String refundDesc = orderService.getRefundStatusDescription(order.getRefundStatus());
                                knowledge.append("  退款状态：").append(refundDesc);
                                if (order.getRefundReason() != null) {
                                    knowledge.append("，原因：").append(order.getRefundReason());
                                }
                                knowledge.append("\n");
                            }
                        }

                        if (orders.size() > 5) {
                            knowledge.append("... 还有 ").append(orders.size() - 5).append(" 个订单\n");
                        }
                    } else {
                        knowledge.append("您暂无订单记录\n");
                    }
                } catch (Exception e) {
                    log.error("获取订单信息失败", e);
                    knowledge.append("订单信息暂时无法获取\n");
                }
            } else {
                knowledge.append("请先登录后再查询订单\n");
            }
            knowledge.append("\n");
        }

        if (query.contains("物流") || query.contains("发货") || query.contains("收货")) {
            knowledge.append("【物流信息】\n");
            knowledge.append("- 一般下单后24小时内发货\n");
            knowledge.append("- 发货后会更新订单状态为'待收货'\n");
            knowledge.append("- 您可以在订单详情中查看物流信息\n\n");
        }

        if (query.contains("退款") || query.contains("退货") || query.contains("售后")) {
            knowledge.append("【售后服务】\n");
            knowledge.append("- 支持7天无理由退换货\n");
            knowledge.append("- 商品质量问题可申请退款\n");
            knowledge.append("- 退款将在1-3个工作日内处理\n");
            if (userId != null) {
                try {
                    List<Order> orders = orderService.getUserOrders(userId);
                    if (orders != null) {
                        long refundableCount = orders.stream()
                                .filter(o -> o.getStatus() != null && o.getStatus() != 40)
                                .filter(o -> o.getRefundStatus() == null || o.getRefundStatus() == 0)
                                .count();
                        if (refundableCount > 0) {
                            knowledge.append("- 您当前有 ").append(refundableCount).append(" 个订单可以申请退款\n");
                        }
                    }
                } catch (Exception e) {
                    log.error("检查可退款订单失败", e);
                }
            }
            knowledge.append("\n");
        }

        if (query.contains("支付") || query.contains("付款")) {
            knowledge.append("【支付信息】\n");
            knowledge.append("- 支持微信支付、支付宝等多种支付方式\n");
            knowledge.append("- 支付超时订单将自动取消\n\n");
        }

        if (query.contains("购物车") || query.contains(" cart ")) {
            knowledge.append("【购物车】\n");
            knowledge.append("- 您可以将心仪的商品加入购物车\n");
            knowledge.append("- 在购物车中可以修改数量或删除商品\n");
            knowledge.append("- 结算时会从购物车生成订单\n\n");
        }

        if (query.contains("地址") || query.contains("收货地址")) {
            knowledge.append("【收货地址】\n");
            knowledge.append("- 您可以管理多个收货地址\n");
            knowledge.append("- 下单时可以选择或添加新的收货地址\n\n");
        }

        return knowledge.toString();
    }

    private String buildPrompt(String userMessage, String context, String knowledge) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一个专业的电商客服助手，请根据以下信息回答用户的问题。\n");
        prompt.append("重要提示：请优先使用下面提供的真实商城数据来回答，不要编造信息。\n\n");

        if (knowledge != null && !knowledge.isEmpty()) {
            prompt.append("=== 商城真实数据 ===\n");
            prompt.append(knowledge);
            prompt.append("\n");
        }

        if (context != null && !context.isEmpty()) {
            prompt.append("=== 对话历史 ===\n");
            prompt.append(context);
            prompt.append("\n");
        }

        prompt.append("=== 用户当前问题 ===\n");
        prompt.append(userMessage).append("\n\n");

        prompt.append("回答要求：\n");
        prompt.append("1. 请基于上面提供的真实商城数据进行回答，不要编造商品、订单等信息\n");
        prompt.append("2. 如果没有相关数据，诚实地告知用户，并建议联系人工客服\n");
        prompt.append("3. 用简洁、友好、专业的语气回答，适当使用emoji让对话更亲切\n");
        prompt.append("4. 如果用户询问具体商品或订单，尽量提供详细信息（如价格、状态等）\n");
        prompt.append("5. 对于常见问题（退款、物流等），结合商城政策给出准确回答");

        return prompt.toString();
    }

    public List<ChatHistory> getChatHistory(String sessionId) {
        return chatHistoryMapper.findBySessionId(sessionId);
    }
}
package com.czjt.controller;

import com.czjt.pojo.Cart;
import com.czjt.pojo.Result;
import com.czjt.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            if (!params.containsKey("productId") || !params.containsKey("quantity")) {
                return Result.error("参数不完整");
            }

            Long productId = Long.valueOf(params.get("productId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());

            if (quantity <= 0) {
                return Result.error("数量必须大于0");
            }

            cartService.addToCart(userId, productId, quantity);
            return Result.success("添加购物车成功", null);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("添加购物车失败: " + e.getMessage());
        }
    }

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> list(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            List<Cart> cartList = cartService.getCartList(userId);
            return Result.success(cartList);
        } catch (Exception e) {
            return Result.error("获取购物车列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        try {
            if (!params.containsKey("cartId") || !params.containsKey("quantity")) {
                return Result.error("参数不完整");
            }

            Long cartId = Long.valueOf(params.get("cartId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());

            cartService.updateQuantity(cartId, quantity);
            return Result.success("更新购物车成功", null);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("更新购物车失败: " + e.getMessage());
        }
    }

    /**
     * 更新购物车商品选中状态
     */
    @PutMapping("/check")
    public Result<Void> check(@RequestBody Map<String, Object> params) {
        try {
            if (!params.containsKey("cartId") || !params.containsKey("checked")) {
                return Result.error("参数不完整");
            }

            Long cartId = Long.valueOf(params.get("cartId").toString());
            Integer checked = Integer.valueOf(params.get("checked").toString());

            cartService.updateChecked(cartId, checked);
            return Result.success("更新选中状态成功", null);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("更新选中状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/remove/{cartId}")
    public Result<Void> remove(@PathVariable Long cartId) {
        try {
            cartService.removeById(cartId);
            return Result.success("删除购物车商品成功", null);
        } catch (Exception e) {
            return Result.error("删除购物车商品失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除购物车商品
     */
    @DeleteMapping("/batch-remove")
    public Result<Void> batchRemove(@RequestBody List<Long> cartIds) {
        try {
            cartService.batchRemove(cartIds);
            return Result.success("批量删除成功", null);
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<Void> clear(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            cartService.clearCart(userId);
            return Result.success("清空购物车成功", null);
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    /**
     * 获取选中的购物车商品
     */
    @GetMapping("/checked")
    public Result<List<Cart>> checked(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            List<Cart> checkedItems = cartService.getCheckedItems(userId);
            return Result.success(checkedItems);
        } catch (Exception e) {
            return Result.error("获取选中商品失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新选中状态
     */
    @PutMapping("/batch-check")
    public Result<Void> batchCheck(@RequestBody Map<String, Object> params) {
        try {
            if (!params.containsKey("cartIds") || !params.containsKey("checked")) {
                return Result.error("参数不完整");
            }

            @SuppressWarnings("unchecked")
            List<Long> cartIds = (List<Long>) params.get("cartIds");
            Integer checked = Integer.valueOf(params.get("checked").toString());

            cartService.batchUpdateChecked(cartIds, checked);
            return Result.success("批量更新选中状态成功", null);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("批量更新选中状态失败: " + e.getMessage());
        }
    }

    /**
     * 全选/取消全选
     */
    @PutMapping("/check-all")
    public Result<Void> checkAll(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("用户未登录");
            }

            if (!params.containsKey("checked")) {
                return Result.error("参数不完整");
            }

            Integer checked = Integer.valueOf(params.get("checked").toString());
            cartService.checkAll(userId, checked);
            return Result.success("全选操作成功", null);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("全选操作失败: " + e.getMessage());
        }
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        try {
            return Long.valueOf(userId.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

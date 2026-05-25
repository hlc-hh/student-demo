package com.czjt.controller;

import com.czjt.pojo.Address;
import com.czjt.pojo.Result;
import com.czjt.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result<List<Address>> getUserAddresses(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询用户地址列表, userId: {}", userId);
        List<Address> addresses = addressService.getUserAddresses(userId);

        return Result.success(addresses);
    }

    @GetMapping("/{id}")
    public Result<Address> getAddressDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询地址详情, addressId: {}, userId: {}", id, userId);

        try {
            Address address = addressService.getAddressById(id, userId);
            return Result.success(address);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        }
    }

    @GetMapping("/default")
    public Result<Address> getDefaultAddress(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询默认地址, userId: {}", userId);
        Address address = addressService.getDefaultAddress(userId);

        if (address == null) {
            return Result.success("暂无默认地址", null);
        }

        return Result.success(address);
    }

    @PostMapping
    public Result<Void> addAddress(@RequestBody Address address, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("添加地址, userId: {}", userId);

        if (!validateAddress(address)) {
            return Result.error("地址信息不完整");
        }

        try {
            addressService.addAddress(userId, address);
            logger.info("地址添加成功, userId: {}", userId);
            return Result.success("地址添加成功", null);
        } catch (Exception e) {
            logger.error("地址添加失败, userId: {}", userId, e);
            return Result.error("地址添加失败");
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id,
                                     @RequestBody Address address,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("更新地址, addressId: {}, userId: {}", id, userId);

        address.setId(id);

        if (!validateAddress(address)) {
            return Result.error("地址信息不完整");
        }

        try {
            addressService.updateAddress(userId, address);
            logger.info("地址更新成功, addressId: {}", id);
            return Result.success("地址更新成功", null);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            logger.error("地址更新失败, addressId: {}", id, e);
            return Result.error("地址更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("删除地址, addressId: {}, userId: {}", id, userId);

        try {
            addressService.deleteAddress(id, userId);
            logger.info("地址删除成功, addressId: {}", id);
            return Result.success("地址删除成功", null);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            logger.error("地址删除失败, addressId: {}", id, e);
            return Result.error("地址删除失败");
        }
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("设置默认地址, addressId: {}, userId: {}", id, userId);

        try {
            addressService.setDefaultAddress(id, userId);
            logger.info("默认地址设置成功, addressId: {}", id);
            return Result.success("默认地址设置成功", null);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        } catch (Exception e) {
            logger.error("默认地址设置失败, addressId: {}", id, e);
            return Result.error("默认地址设置失败");
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private boolean validateAddress(Address address) {
        if (address == null) {
            return false;
        }

        if (address.getContactName() == null || address.getContactName().trim().isEmpty()) {
            return false;
        }

        if (address.getContactPhone() == null || address.getContactPhone().trim().isEmpty()) {
            return false;
        }

        if (address.getDetailAddress() == null || address.getDetailAddress().trim().isEmpty()) {
            return false;
        }

        return true;
    }
}

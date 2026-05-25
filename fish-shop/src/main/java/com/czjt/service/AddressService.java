package com.czjt.service;

import com.czjt.mapper.AddressMapper;
import com.czjt.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> getUserAddresses(Long userId) {
        List<Address> addresses = addressMapper.findByUserId(userId);

        addresses.forEach(address -> {
            address.setFullAddress(buildFullAddress(address));
        });

        return addresses;
    }

    public Address getAddressById(Long id, Long userId) {
        Address address = addressMapper.findById(id);

        if (address != null && !address.getUserId().equals(userId)) {
            throw new SecurityException("无权访问此地址");
        }

        if (address != null) {
            address.setFullAddress(buildFullAddress(address));
        }

        return address;
    }

    @Transactional
    public void addAddress(Long userId, Address address) {
        address.setUserId(userId);

        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefault(userId);
        }

        addressMapper.insert(address);
    }

    @Transactional
    public void updateAddress(Long userId, Address address) {
        Address existingAddress = addressMapper.findById(address.getId());

        if (existingAddress == null) {
            throw new IllegalArgumentException("地址不存在");
        }

        if (!existingAddress.getUserId().equals(userId)) {
            throw new SecurityException("无权修改此地址");
        }

        address.setUserId(userId);
        addressMapper.update(address);
    }

    @Transactional
    public void deleteAddress(Long id, Long userId) {
        Address existingAddress = addressMapper.findById(id);

        if (existingAddress == null) {
            throw new IllegalArgumentException("地址不存在");
        }

        if (!existingAddress.getUserId().equals(userId)) {
            throw new SecurityException("无权删除此地址");
        }

        addressMapper.deleteById(id, userId);
    }

    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        Address existingAddress = addressMapper.findById(id);

        if (existingAddress == null) {
            throw new IllegalArgumentException("地址不存在");
        }

        if (!existingAddress.getUserId().equals(userId)) {
            throw new SecurityException("无权操作此地址");
        }

        addressMapper.cancelDefault(userId);
        addressMapper.setDefault(id, userId);
    }

    public Address getDefaultAddress(Long userId) {
        Address address = addressMapper.findDefaultByUserId(userId);

        if (address != null) {
            address.setFullAddress(buildFullAddress(address));
        }

        return address;
    }

    private String buildFullAddress(Address address) {
        StringBuilder sb = new StringBuilder();

        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            sb.append(address.getProvince());
        }

        if (address.getCity() != null && !address.getCity().isEmpty()) {
            sb.append(address.getCity());
        }

        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            sb.append(address.getDistrict());
        }

        if (address.getDetailAddress() != null && !address.getDetailAddress().isEmpty()) {
            sb.append(address.getDetailAddress());
        }

        return sb.toString();
    }
}

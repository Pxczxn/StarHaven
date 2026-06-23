package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final BookingOrderMapper bookingOrderMapper;

    /**
     * 分页查询客户
     */
    public Page<Customer> page(Integer pageNum, Integer pageSize, String keyword) {
        Page<Customer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（姓名、手机号、证件号）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                    .or().like(Customer::getPhone, keyword)
                    .or().like(Customer::getIdNumber, keyword));
        }

        // 按 ID 升序，保持列表稳定展示
        wrapper.orderByAsc(Customer::getId);

        return customerMapper.selectPage(page, wrapper);
    }

    /**
     * 根据 ID 查询客户
     */
    public Customer getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw BusinessException.notFound("客户不存在");
        }
        return customer;
    }

    /**
     * 根据手机号查询客户
     */
    public Customer getByPhone(String phone) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone);
        return customerMapper.selectOne(wrapper);
    }

    /**
     * 新增客户
     */
    @Transactional(rollbackFor = Exception.class)
    public Customer create(Customer customer) {
        // 检查手机号是否已存在
        if (customer.getPhone() != null) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getPhone, customer.getPhone());
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("手机号已存在");
            }
        }

        // 检查证件号是否已存在
        if (customer.getIdNumber() != null && !customer.getIdNumber().trim().isEmpty()) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getIdNumber, customer.getIdNumber());
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("证件号已存在");
            }
        }

        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        customerMapper.insert(customer);
        log.info("创建客户成功: {} - {}", customer.getName(), customer.getPhone());
        return customer;
    }

    /**
     * 更新客户
     */
    @Transactional(rollbackFor = Exception.class)
    public Customer update(Long id, Customer customer) {
        Customer existing = getById(id);

        // 检查手机号是否与其他客户重复
        if (customer.getPhone() != null && !existing.getPhone().equals(customer.getPhone())) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getPhone, customer.getPhone());
            wrapper.ne(Customer::getId, id);
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("手机号已存在");
            }
        }

        // 检查证件号是否与其他客户重复
        if (customer.getIdNumber() != null && !customer.getIdNumber().trim().isEmpty()
                && !customer.getIdNumber().equals(existing.getIdNumber())) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getIdNumber, customer.getIdNumber());
            wrapper.ne(Customer::getId, id);
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("证件号已存在");
            }
        }

        customer.setId(id);
        customer.setCreatedAt(existing.getCreatedAt());
        customer.setUpdatedAt(LocalDateTime.now());

        customerMapper.updateById(customer);
        log.info("更新客户成功: {} - {}", customer.getName(), customer.getPhone());
        return customer;
    }

    /**
     * 删除客户
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Customer customer = getById(id);

        // 检查是否有订单关联该客户
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getCustomerId, id);
        long count = bookingOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.badRequest("该客户有 " + count + " 个订单，无法删除");
        }

        customerMapper.deleteById(id);
        log.info("删除客户成功: {} - {}", customer.getName(), customer.getPhone());
    }

    /**
     * 查询客户的历史订单
     */
    public List<BookingOrder> getOrdersByCustomerId(Long customerId) {
        getById(customerId); // 检查客户是否存在

        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getCustomerId, customerId);
        wrapper.orderByDesc(BookingOrder::getCreatedAt);

        return bookingOrderMapper.selectList(wrapper);
    }

    /**
     * 根据手机号自动创建或获取客户
     */
    @Transactional(rollbackFor = Exception.class)
    public Customer getOrCreateByPhone(String phone, String name, String idNumber) {
        Customer customer = getByPhone(phone);
        if (customer == null) {
            customer = new Customer();
            customer.setPhone(phone);
            customer.setName(name);
            customer.setIdNumber(idNumber);
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customerMapper.insert(customer);
            log.info("自动创建客户: {} - {} - {}", name, phone, idNumber);
        } else {
            // 如果客户已存在但身份证号为空，则更新身份证号
            if (idNumber != null && !idNumber.trim().isEmpty() &&
                (customer.getIdNumber() == null || customer.getIdNumber().trim().isEmpty())) {
                customer.setIdNumber(idNumber);
                customer.setUpdatedAt(LocalDateTime.now());
                customerMapper.updateById(customer);
                log.info("更新客户身份证号: {} - {}", name, idNumber);
            }
        }
        return customer;
    }
}

package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.CustomerRequest;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.mapper.BookingOrderMapper;
import com.xingqi.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务
 */
@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BookingOrderMapper bookingOrderMapper;

    /**
     * 分页查询客户
     */
    public PageResponse<Customer> list(long page, long pageSize, String keyword) {
        Page<Customer> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（姓名、手机号、证件号）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Customer::getName, keyword)
                    .or().like(Customer::getPhone, keyword)
                    .or().like(Customer::getIdNumber, keyword)
            );
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Customer::getCreatedAt);

        IPage<Customer> result = customerMapper.selectPage(pageParam, wrapper);

        return new PageResponse<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
    }

    /**
     * 获取客户详情
     */
    public Customer getById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw BusinessException.notFound("客户不存在");
        }
        return customer;
    }

    /**
     * 创建客户
     */
    public Customer create(CustomerRequest request) {
        // 检查手机号是否已存在
        if (StringUtils.hasText(request.getPhone())) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getPhone, request.getPhone());
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("该手机号已存在");
            }
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);

        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        customerMapper.insert(customer);

        log.info("创建客户成功: {} - {}", customer.getName(), customer.getPhone());
        return customer;
    }

    /**
     * 更新客户
     */
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = getById(id);

        // 检查手机号是否与其他客户重复
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(customer.getPhone())) {
            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Customer::getPhone, request.getPhone());
            wrapper.ne(Customer::getId, id);
            if (customerMapper.selectCount(wrapper) > 0) {
                throw BusinessException.badRequest("该手机号已存在");
            }
        }

        BeanUtils.copyProperties(request, customer);
        customer.setUpdatedAt(LocalDateTime.now());

        customerMapper.updateById(customer);

        log.info("更新客户成功: {} - {}", customer.getName(), customer.getPhone());
        return customer;
    }

    /**
     * 删除客户
     */
    public void delete(Long id) {
        Customer customer = getById(id);

        // 检查是否有关联订单
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getCustomerId, id);
        long count = bookingOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.conflict("该客户有 " + count + " 个关联订单，无法删除");
        }

        customerMapper.deleteById(id);

        log.info("删除客户成功: {} - {}", customer.getName(), customer.getPhone());
    }

    /**
     * 获取客户的历史订单
     */
    public List<BookingOrder> getOrders(Long customerId) {
        Customer customer = getById(customerId);

        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookingOrder::getCustomerId, customerId);
        wrapper.orderByDesc(BookingOrder::getCreatedAt);

        return bookingOrderMapper.selectList(wrapper);
    }

    /**
     * 根据手机号查找或创建客户
     * 用于订单创建时的客户沉淀
     */
    public Customer findOrCreateByPhone(String phone, String name) {
        // 先查找是否已存在
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone);
        Customer customer = customerMapper.selectOne(wrapper);

        if (customer != null) {
            // 如果客户已存在，更新姓名（以最新信息为准）
            if (StringUtils.hasText(name) && !name.equals(customer.getName())) {
                customer.setName(name);
                customer.setUpdatedAt(LocalDateTime.now());
                customerMapper.updateById(customer);
                log.info("更新客户姓名: {} -> {}", customer.getPhone(), name);
            }
            return customer;
        }

        // 不存在则创建新客户
        customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setSource("h5");
        customer.setLevel("normal");

        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        customerMapper.insert(customer);

        log.info("自动创建客户: {} - {}", name, phone);
        return customer;
    }
}

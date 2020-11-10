package com.example.demo.web;

import com.example.demo.dto.Country;
import com.example.demo.dto.MyOrder;
import com.example.demo.repositories.CountryRepository;
import com.example.demo.repositories.CustomerRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.util.SaasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author huang
 */
@RestController
public class CountryController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/country")
    public Object testListener() {
        Country country = new Country();
        country.setId(10000L);
        country.setName("aa");
        Random random = new Random();
        int tenantId = random.nextInt(100);
        SaasUtils.setTenantId(tenantId);
        return countryRepository.save(country);
    }

    @GetMapping("/customer")
    public Object testCustomer() {
        return customerRepository.findOne(1L);
    }

    @GetMapping("/one")
    public Object testOne() {
        SaasUtils.setTenantId(31);
        return countryRepository.findOne(10L);
    }

    @GetMapping("/order")
    public Object testOrder() {
        SaasUtils.setTenantId(56);
        return countryRepository.findByOrder();
    }

    @GetMapping("/left")
    public Object testLeftJoin() {
        SaasUtils.setTenantId(56);
        return customerRepository.findByLeftJoin();
    }

    @GetMapping("/right")
    public Object testRightJoin() {
        SaasUtils.setTenantId(56);
        return customerRepository.findByRightJoin();
    }

    @GetMapping("/in")
    public Object testIn() {
        SaasUtils.setTenantId(56);
        return customerRepository.findByInCountryId();
    }

    /**
     * select cu.first_name from customer cu, country co where cu.country_id = co.id
     * @return
     */
    @GetMapping("/inner")
    public Object test1() {
        SaasUtils.setTenantId(56);
        return customerRepository.findFirstNameByT();
    }

    /**
     * select * from customer where country_id = (select id from country)
     * @return
     */
    @GetMapping("/child")
    public Object testChild() {
        SaasUtils.setTenantId(56);
        return customerRepository.findByCountryId();
    }

    @GetMapping("/save")
    public Object testListenerSave() {
        new Thread(() -> {
            Random random = new Random();
            int tenantId = random.nextInt(100);
            SaasUtils.setTenantId(tenantId);
            Country country = new Country();
            country.setName("aa");
            countryRepository.save(country);
        }).start();

        new Thread(() -> {
            Random random = new Random();
            int tenantId = random.nextInt(100);
            SaasUtils.setTenantId(tenantId);
            MyOrder myOrder = new MyOrder();
            myOrder.setCode("123");
            orderRepository.save(myOrder);
        }).start();

        return "ok";
    }

    @GetMapping("/update")
    public Object testListenerUpdate() {
        MyOrder myOrder = new MyOrder();
        myOrder.setId(1L);
        myOrder.setCode("update");
        orderRepository.save(myOrder);
        return "ok";
    }
}

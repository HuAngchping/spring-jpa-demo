package com.example.demo.dto;

import com.example.demo.util.SaasUtils;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import java.lang.reflect.Field;

/**
 * @author huang
 * @create 2020/10/30
 */
public class TenantEntityListener {

    /**
     * 在保存之前调用
     */
    @PrePersist
    public void prePersist(Object source) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("@PrePersist: " + source.toString());
        source = setValue(source);
        System.out.println("@PrePersist: " + source.toString());
    }

    private static Object setValue(Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField("tenantId");
        field.setAccessible(true);
        field.set(obj, String.valueOf(SaasUtils.getTenantId()));
        return obj;
    }
}

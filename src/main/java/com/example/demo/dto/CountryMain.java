package com.example.demo.dto;

import java.lang.reflect.Field;

/**
 * @author huang
 * @create 2020/10/30
 */
public class CountryMain {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Country country = new Country();
        country.setId(1L);
        country.setName("aa");
        country.setTenantId("bb");

        Country obj = (Country) setValue(country);
        System.out.println(obj.getId());
        System.out.println(obj.getName());
        System.out.println(obj.getTenantId());

    }

    private static Object setValue(Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField("code");
        field.setAccessible(true);
        field.set(obj, "2L");
        return obj;
    }

}

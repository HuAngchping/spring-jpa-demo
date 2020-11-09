package com.example.demo.util;

import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huang
 * @create 2020/11/3
 */
public class SaasUtils {
    private static final ThreadLocal<SaasUtils> local = new ThreadLocal<SaasUtils>() {
        @Override
        protected SaasUtils initialValue() {
            return new SaasUtils();
        }
    };
    private final Map<String, String> map = new HashMap<>();

    public static Integer getTenantId() {
        SaasUtils saasUtils = local.get();
        String tenantId = saasUtils.map.get("tenant-id");
        if (StringUtils.isEmpty(tenantId)) {
            return 1001;
        } else {
            return Integer.parseInt(tenantId);
        }
    }

    public static void setTenantId(Integer tenantId) {
        setAttachment("tenant-id", String.valueOf(tenantId));
    }

    public static String getAcceptLanguage() {
        SaasUtils saasUtils = local.get();
        return saasUtils.map.get("accept-language");
    }

    public static String getAttachment(String key) {
        SaasUtils saasUtils = local.get();
        return saasUtils.map.get(key);
    }

    public static void setAttachment(String key, String value) {
        SaasUtils saasUtils = local.get();
        if (value == null) {
            saasUtils.map.remove(key);
        } else {
            saasUtils.map.put(key, value);
        }
    }
}

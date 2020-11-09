package com.example.demo.config;

import com.example.demo.dto.TenantInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.stereotype.Component;

import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author huang
 * @create 2020/11/6
 */
@Component
public class TenantTableListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            ApplicationContext applicationContext = event.getApplicationContext();
            JpaMetamodelMappingContext jpaMappingContext = (JpaMetamodelMappingContext) applicationContext.getBean("jpaMappingContext");
            Collection entityCollection = jpaMappingContext.getManagedTypes();
            Iterator it = entityCollection.iterator();
            while (it.hasNext()) {
                ClassTypeInformation classTypeInformation = (ClassTypeInformation) it.next();
                Class clazz = classTypeInformation.getType();
                if (clazz.getAnnotation(EntityListeners.class) != null) {
                    Annotation annotation = clazz.getAnnotation(Table.class);
                    InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
                    try {
                        Field field = invocationHandler.getClass().getDeclaredField("memberValues");
                        field.setAccessible(true);
                        Map<String, Object> memberValues = (Map<String, Object>) field.get(invocationHandler);
                        String tableName = memberValues.get("name").toString();
                        TenantInterceptor.tenantTables.add(tableName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}

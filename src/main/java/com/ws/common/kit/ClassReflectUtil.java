package com.ws.common.kit;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * @author wangshuo
 * @version 2017-12-06
 */
public class ClassReflectUtil {

    /**
     * 获取类的所有属性（包括其父类的）
     *
     * @param clazz        指定类
     * @return             该类的所有属性
     */
    public static List<Field> getClassFields(Class clazz) {
        List<Field> fieldList = Lists.newArrayList();
        Class tempClazz = clazz;
        while (tempClazz != null) {
            Field[] fields = tempClazz.getDeclaredFields();
            List<Field> tempFields = Arrays.asList(fields);
            if (!CollectionUtils.isEmpty(tempFields)) {
                fieldList.addAll(tempFields);
            }
            tempClazz = tempClazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 将对象的属性赋值
     *
     * @param field       类属性
     * @param object      类对象
     * @param value       属性值
     * @throws Exception  如果调用方法给属性赋值失败时
     */
    public static void setValue(Field field, Object object, String value) throws Exception {
        String name = field.getName();
        String type = field.getGenericType().toString();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        if (type.equals("class java.lang.String")) {
            Method m = object.getClass().getMethod("set" + name, String.class);
            m.invoke(object, value);
            return;
        }

        if (type.equals("class java.lang.Integer")) {
            Method m = object.getClass().getMethod("set" + name, Integer.class);
            m.invoke(object, Integer.parseInt(value));
            return;
        }

        if (type.equals("class java.lang.Boolean")) {
            Method m = object.getClass().getMethod("set" + name, Boolean.class);
            m.invoke(object, Boolean.parseBoolean(value));
            return;
        }

        if (type.equals("class java.math.BigDecimal")) {
            Method m = object.getClass().getMethod("set" + name, BigDecimal.class);
            m.invoke(object, new BigDecimal(value));
            return;
        }

        throw new BizException(MessageFormat.format("属性赋值失败，暂不支持{0}类型的属性赋值", type));
    }
}

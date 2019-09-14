package com.blog.util;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateJsonValueProcessor implements JsonValueProcessor {
    private String format;

    public DateJsonValueProcessor(String format) {
        this.format = format;
    }

    @Override
    public Object processArrayValue(Object o, JsonConfig jsonConfig) {
        return null;
    }

    //该功能实现：当执行下面这个方法的时候JSONArray jsonArray = JSONArray.fromObject(list,config);
    //在list中如果遇到有某条数据项的某字段是属于时间类型的，或者是属于时间的Object类，就会按照构造器
    //传来的format的格式进行转换，最后写入到jsonObject中以时间的形式。
    @Override
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        if (value == null){
            return "";
        }
        if (value instanceof Timestamp){
            return new SimpleDateFormat(this.format).format((Timestamp)value);
        }
        //执行的这个Date:import java.util.Date;
        if (value instanceof Date){
            return new SimpleDateFormat(this.format).format((Date) value);
        }
        //如果非时间的字符串则返回object的toString，也就是原先的字符串。如果有toString方法则返回字符串
        return value.toString();
    }
}

package com.muern.framework.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author gegeza
 * @date 2022/03/28
 */
public class Json {

    private static final Logger LOGGER = LoggerFactory.getLogger(Json.class);
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /** 默认日期时间格式 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /** 默认时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {
        //处理时间格式数据
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        module.addSerializer(LocalDate.class,new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        module.addSerializer(LocalTime.class,new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        module.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        module.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        module.addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        //Date序列化和反序列化
        module.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
                String formattedDate = formatter.format(date);
                jsonGenerator.writeString(formattedDate);
            }
        });
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
                String date = jsonParser.getText();
                try {
                    return format.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        MAPPER.registerModule(module);
        //序列化时对象的所有字段
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    
    /** 将Java对象转化为JSON字符串 */
    public static String toStr(Object object) {
        try {
            return object == null ? null
                    : object instanceof String ? (String) object : MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /** 将JSON字符串转化为Java对象 非集合 */
    public static <T> T parse(String str, Class<T> clazz) {
        try {
            return StringUtils.isEmpty(str) || clazz == null ? null
                    : String.class.equals(clazz) ? (T) str : MAPPER.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /** 将JSON字符串转化为Java集合对象 */
    public static <T> T parse(String str, TypeReference<T> typeReference) {
        try {
            return StringUtils.isEmpty(str) || typeReference == null ? null
                    : String.class.equals(typeReference.getType()) ? (T) str : MAPPER.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /** 将JSON字符串转化为JsonNode对象 */
    public static JsonNode parseNode(String str) {
        try {
            return StringUtils.isEmpty(str) ? null : MAPPER.readTree(str);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}

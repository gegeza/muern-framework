package com.muern.framework.controller;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.muern.framework.common.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * 日志级别查询及修改
 * @author gegeza
 * @date 2022-04-06
 */
@RestController
@RequestMapping("muern/logger")
public class LoggerLevelController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerLevelController.class);

    /** logback日志框架对应的Binder */
    private static final String LOGBACK_BINDER = "ch.qos.logback.classic.util.ContextSelectorStaticBinder";

    /**
     * 获取当前日志的Map集合
     * @return Map<String, Logger>
     */
    private static Map<String, Logger> getLoggerMap() {
        //获取SLF4J绑定的具体的日志框架
        String loggerBinder = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
        //此处以SpringBoot默认的Logback为例，其它日志框架参考 https://tech.meituan.com/2017/02/17/change-log-level.html
        if (LOGBACK_BINDER.equals(loggerBinder)) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            return loggerContext.getLoggerList().stream().collect(Collectors.toMap(Logger::getName, p -> {return p;}));
        } else {
            //其他日志框架暂不支持
            return null;
        }
    }

    /**
     * 查询日志包名及日志级别的对应关系集合
     * @return Map<String, String> key:日志包名 value:日志级别
     */
    @GetMapping(value = "maps")
    public Result<Map<String, String>> maps() {
        return Result.ins(getLoggerMap().entrySet().stream().filter(e -> {
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) e.getValue();
            return logger.getLevel() != null;
        }).collect(
            Collectors.toMap(Entry<String, Logger>::getKey, e -> {
                ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) e.getValue();
                return logger.getLevel().toString();
            })
        ));
    }

    /**
     * 修改日志级别
     * @author gegeza
     * @date 2022-04-07
     * @param  loggerDto name:日志包名(例如com.muern.paying.mapper) level:日志级别(例如INFO/DEBUG)
     * @return 修改结果
     */
    @PostMapping(value = "change")
    public Result<Void> change(@RequestBody LoggerDto loggerDto) {
        Map<String, Logger> loggerMap = getLoggerMap();
        for (Map.Entry<String, Logger> entry : loggerMap.entrySet()) {
            if (entry.getKey().equals(loggerDto.getName())) {
                ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) entry.getValue();
                Level level = Level.toLevel(loggerDto.getLevel());
                LOGGER.info("change Logger[{}] Level[{}]", entry.getKey(), level.toString());
                logger.setLevel(level);
                return Result.succ();
            }
        }
        LOGGER.warn("Logger[{}] not found", loggerDto.getName());
        return Result.fail();
    }

    public static class LoggerDto {
        private String name;
        private String level;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        
    }
}
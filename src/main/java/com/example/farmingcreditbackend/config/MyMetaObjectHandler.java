//package com.example.farmingcreditbackend.config;
//
//import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.reflection.MetaObject;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
///**
// * 元数据处理器 - 自动填充字段
// */
//@Component
//@Slf4j
//public class MyMetaObjectHandler implements MetaObjectHandler {
//
//    @Override
//    public void insertFill(MetaObject metaObject) {
//        log.info("开始插入填充...");
//        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
//        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//    }
//
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        log.info("开始更新填充...");
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//    }
//}

//后续查看需不需要

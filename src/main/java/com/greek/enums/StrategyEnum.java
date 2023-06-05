package com.greek.enums;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.*;

/**
 * @author Zhaofeng Zhou
 * @date 2023/6/5
 */
public enum StrategyEnum {

    ENTITY() {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
            builder.entityBuilder()
                    .superClass("")
                    .disableSerialVersionUID()
                    .enableChainModel()
                    .enableLombok()
                    .enableRemoveIsPrefix()
                    .enableTableFieldAnnotation()
                    .idType(IdType.AUTO)
                    .formatFileName("%sEntity");
        }
    },
    MAPPER {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
            builder.mapperBuilder()
                    .enableMapperAnnotation()
                    .enableBaseResultMap()
                    .enableBaseColumnList()
                    .formatMapperFileName("%sMapper")
                    .formatXmlFileName("%sMapper")
                    .build();
        }
    },
    SERVICE {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
            builder.serviceBuilder()
                    .formatServiceFileName("%sService")
                    .formatServiceImplFileName("%sServiceImp");
        }
    },
    SERVICE_IMPL {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
        }
    },
    CONTROLLER {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
            builder.controllerBuilder()
                    .enableHyphenStyle()
                    .enableRestStyle()
                    .formatFileName("%sController");
        }
    };


    public abstract void strategyConfig(StrategyConfig.Builder builder);
}


package com.greek.enums;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

/**
 * @author Zhaofeng Zhou
 * @date 2023/6/5
 */
public enum StrategyEnum {

    ENTITY() {
        @Override
        public void strategyConfig(StrategyConfig.Builder builder) {
            builder.entityBuilder()
                    .enableFileOverride()
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
                    .enableFileOverride()
                    .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)
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
                    .enableFileOverride()
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
                    .enableFileOverride()
                    .enableHyphenStyle()
                    .enableRestStyle()
                    .formatFileName("%sController");
        }
    };


    public abstract void strategyConfig(StrategyConfig.Builder builder);
}


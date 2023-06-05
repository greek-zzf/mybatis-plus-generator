package com.greek.mojo;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

import java.util.Collections;

import static com.greek.enums.StrategyEnum.*;

public class CodeGenerator {

    public static void main(String[] args) {
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig
                .Builder("jdbc:mysql://127.0.0.1:3306/rainng_course", "root", "123456");

        // 代码生成器
        FastAutoGenerator.create(dataSourceConfigBuilder)
                .globalConfig(CodeGenerator::configGlobal)
                .packageConfig(CodeGenerator::configPackage)
                .strategyConfig(CodeGenerator::configStrategy)
                .injectionConfig(CodeGenerator::configInject)
                .execute();
    }

    private static GlobalConfig.Builder configGlobal(GlobalConfig.Builder builder) {
        return builder.fileOverride()
                .outputDir(System.getProperty("user.dir") + "/src/main/java") // TODO: 用户手动指定
                .dateType(DateType.TIME_PACK);
    }

    private static PackageConfig.Builder configPackage(PackageConfig.Builder builder) {
        return builder.parent("com.baomidou.mybatisplus.samples.generator")
                .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"))
                .entity("entity");
    }

    private static StrategyConfig.Builder configStrategy(StrategyConfig.Builder builder) {
        ENTITY.strategyConfig(builder);
        MAPPER.strategyConfig(builder);
        SERVICE.strategyConfig(builder);
        CONTROLLER.strategyConfig(builder);

        return builder.enableCapitalMode()
                .enableSkipView()
                .disableSqlFilter()
                .addInclude("rc_person")
                .addTablePrefix("t_", "rc_");
    }

    private static InjectionConfig.Builder configInject(InjectionConfig.Builder builder) {
        return builder.beforeOutputFile((tableInfo, objectMap) -> {
            System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
        });
    }

}

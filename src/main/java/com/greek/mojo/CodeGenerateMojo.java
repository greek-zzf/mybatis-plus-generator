package com.greek.mojo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Collections;
import java.util.Scanner;
import java.util.function.Function;

import static com.greek.enums.StrategyEnum.*;

/**
 * @author Zhaofeng Zhou
 * @date 2022/2/16 10:09
 */
@Mojo(name = "generate")
public class CodeGenerateMojo extends AbstractMojo {

    private static final Scanner input = new Scanner(System.in);
    private static String host = "127.0.0.1";
    private static int port = 3306;
    private static String scheme = "rainng_course";
    private static String username = "root";
    private static String password = "123456";
    private static String defaultOutputDir = System.getProperty("user.dir") + "/src/main/java";

    @Override
    public void execute() throws MojoExecutionException {
        initDatabaseInfo();

        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig
                .Builder(StrUtil.format("jdbc:mysql://{}:{}/{}", host, port, scheme), username, password);

        // 代码生成器
        FastAutoGenerator.create(dataSourceConfigBuilder)
                .globalConfig(this::configGlobal)
                .packageConfig(this::configPackage)
                .strategyConfig((scanner, builder) -> configStrategy(builder, scanner))
                .injectionConfig(this::configInject)
                .execute();

    }

    private  GlobalConfig.Builder configGlobal(GlobalConfig.Builder builder) {
        System.out.println("请输入生成代码位置，默认为当前目录的 /src/main/java 文件夹");
        String inputValue = input.nextLine();
        return builder.outputDir(StrUtil.isEmpty(inputValue) ? defaultOutputDir : inputValue) // TODO: 用户手动指定
                .dateType(DateType.TIME_PACK);
    }

    private  PackageConfig.Builder configPackage(PackageConfig.Builder builder) {
        return builder.parent("com.baomidou.mybatisplus.samples.generator")
                .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"))
                .entity("entity");
    }

    private  StrategyConfig.Builder configStrategy(StrategyConfig.Builder builder, Function<String, String> scanner) {
        ENTITY.strategyConfig(builder);
        MAPPER.strategyConfig(builder);
        SERVICE.strategyConfig(builder);
        CONTROLLER.strategyConfig(builder);

        return builder.enableCapitalMode()
                .enableSkipView()
                .disableSqlFilter()
                .addInclude(scanner.apply("请输入要生成的表名,多个表名以逗号分隔:"))
                .addTablePrefix(scanner.apply("请输入表前缀，例t_,rc_"));
    }

    private  InjectionConfig.Builder configInject(InjectionConfig.Builder builder) {
        return builder.beforeOutputFile((tableInfo, objectMap) -> {
            System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
        });
    }

    private  Scanner initDatabaseInfo() {
        System.out.println("请输入数据库 ip，不填默认为 127.0.0.1");
        host = StrUtil.emptyToDefault(input.nextLine(), host);

        System.out.println("请输入数据库端口，不填默认为 3306");
        String portStr = input.nextLine();
        port = StrUtil.isEmpty(portStr) ? port : Integer.parseInt(portStr);

        System.out.println("请输入数据库名称");
        String schemeName = input.nextLine();
        while (StrUtil.isEmpty(schemeName)) {
            System.out.println("错误的数据库名称,请重新输入！");
            schemeName = input.nextLine();
        }
        scheme = schemeName;

        System.out.println("请输入数据库账号，不填默认为 root");
        username = StrUtil.emptyToDefault(input.nextLine(), username);

        System.out.println("请输入数据库密码，不填默认为 123456");
        password = StrUtil.emptyToDefault(input.nextLine(), password);
        return input;
    }

}




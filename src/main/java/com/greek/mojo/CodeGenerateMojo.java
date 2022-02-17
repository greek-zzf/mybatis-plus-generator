package com.greek.mojo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.greek.enums.XmlTagEnum.*;

/**
 * @author Zhaofeng Zhou
 * @date 2022/2/16 10:09
 */
@Mojo(name = "generate")
public class CodeGenerateMojo extends AbstractMojo {


    @Parameter(property = "author")
    private String author = "";

    @Parameter(property = "generator.configurationFile",
            defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml", required = true)
    private File configurationFile;

    @Parameter(readonly = true, required = true, defaultValue = "${project.build.sourceDirectory}")
    private String project_path;

    @Parameter(readonly = true, required = true, defaultValue = "${project.build.resources[0].directory}")
    private String resourcePath;

    @Parameter(required = true, defaultValue = "${project.package.name}")
    private String packageName;


    // 配置文件属性
    private static Properties configProperties;

    // 模板配置文件属性
    private static Properties templatePathProperties;

    // 数据库配置
    private static String username;
    private static String password;
    private static String url;
    private static DbType db_type;
    private static String driverClassName;

    //生成的实体类时，忽略表的前缀名: 不需要则置空
    private static String[] entity_ignore_prefix = {""};
    //需要生成的表名
    private static String[] tables = {
            "vehicle_io_declare"
    };
    //需要排除的表名
    private static String[] excludes = {
//            "SYS_USER"
    };


    private String packagePath;


    private String entity_path;
    private String mapper_path;
    private String service_path;
    private String service_impl_path;
    private String controller_path;
    private String xml_path;

    //文件输出模板
    private static String entity_template;
    private static String xml_template;
    private static String mapper_template;
    private static String service_template;
    private static String service_impl_template;
    private static String controller_template;

    @Override
    public void execute() throws MojoExecutionException {
        init();

        AutoGenerator mpg = new AutoGenerator()
                .setGlobalConfig(globalConfig())
                .setDataSource(dataSourceConfig())
                .setStrategy(strategyConfig())
                .setPackageInfo(packageConfig())
                // 因为使用了自定义模板,所以需要把各项置空否则会多生成一次
                .setTemplate(templateConfig())
                // 使用的模板引擎，如果不是默认模板引擎则需要添加模板依赖到pom
                .setTemplateEngine(new FreemarkerTemplateEngine())
                .setCfg(injectionConfig());
        mpg.execute();
    }

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig() {
        return new GlobalConfig()
                .setAuthor(author) // 作者
                .setOpen(false)// 生成文件后打开目录
                .setFileOverride(true)// 文件覆盖
                .setActiveRecord(true)// 开启activeRecord模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(true)// XML ResultMap: mapper.xml生成查询映射结果
                .setBaseColumnList(true)// XML ColumnList: mapper.xml生成查询结果列
                //.setSwagger2(true)// swagger注解; 须添加swagger依赖
                .setKotlin(false) //是否生成 kotlin 代码
                .setServiceName("%sService");//默认是I%sService
    }

    /**
     * 数据源配置
     */
    private DataSourceConfig dataSourceConfig() {
        return new DataSourceConfig()
                .setDbType(db_type)// 数据库类型
                .setDriverName(driverClassName)// 连接驱动
                .setUrl(url)// 地址
                .setUsername(username)// 用户名
                .setPassword(password);// 密码
    }

    private void init() throws MojoExecutionException {
        if (!configurationFile.exists()) {
            getLog().error("配置文件: " + configurationFile + "不存在");
            throw new MojoExecutionException("配置文件: " + configurationFile + "不存在");
        }

        String xmlContent = readFileAsString(configurationFile);
        JSONObject jsonObject = JSONUtil.parseFromXml(xmlContent);

        parseGenerateConfig(jsonObject);

        loadDatabaseConfig();

        loadTemplateConfig();

        loadGenerateFilePathConfig();
    }

    private void loadGenerateFilePathConfig() {
        packagePath = convertPath(packageName);
        String javaFilePath = combineWithFileSeparator(project_path, packagePath);

        entity_path = combinePath(javaFilePath, "/entity");
        mapper_path = combinePath(javaFilePath, "/mapper");
        service_path = combinePath(javaFilePath, "/service");
        service_impl_path = combinePath(javaFilePath, "/service/impl");
        controller_path = combinePath(javaFilePath, "/controller");
        xml_path = resourcePath + "/mybatis-mapper" + File.separator + packagePath + "/mapper";
    }

    /**
     * 生成策略配置，自定义命名，生成结构
     */
    private StrategyConfig strategyConfig() {
        return new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setInclude(tables)
                // 去除表前缀,此处可以修改为您的表前缀
                .setTablePrefix(entity_ignore_prefix)
                .setChainModel(true)
                // 生成实体类字段注解
                .setEntityTableFieldAnnotationEnable(true)
                // controller映射地址：驼峰转连字符
                .setControllerMappingHyphenStyle(false)
                // 生成RestController
                .setRestControllerStyle(true);
    }

    /**
     * 包配置，设置包路径用于导包时使用，路径示例：com.path
     */
    private PackageConfig packageConfig() {
        String parent = packageName;
        String entity = convertPackage(entity_path);
        String mapper = convertPackage(mapper_path);
        String xml = convertPackage(xml_path);
        String service = convertPackage(service_path);
        String service_impl = convertPackage(service_impl_path);
        String controller = convertPackage(controller_path);

        return new PackageConfig()
                //父包名
                .setParent(parent)
                .setModuleName("")
                //结构包
                .setEntity(entity)
                .setMapper(mapper)
                .setXml(xml)
                .setService(service)
                .setServiceImpl(service_impl)
                .setController(controller);
    }

    /**
     * 模板配置
     */
    private TemplateConfig templateConfig() {
        // 自定义模板配置，模板可以参考源码 /mybatis-plus/src/main/resources/template
        // 使用 copy至您项目 src/main/resources/template 目录下，模板名称也可自定义如下配置：
        // 置空后方便使用自定义输出位置
        return new TemplateConfig()
                .setEntity(null)
                .setXml(null)
                .setMapper(null)
                .setService(null)
                .setServiceImpl(null)
                .setController(null);
    }

    /**
     * 自定义配置
     */
    private InjectionConfig injectionConfig() {
        return new InjectionConfig() {
            @Override
            public void initMap() {
                // 注入配置
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        }.setFileCreate(new IFileCreate() {
            // 自定义输出文件
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 检查文件目录，不存在自动递归创建
                File file = new File(filePath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                //自定义指定需要覆盖的文件 - 文件结尾名字参照 全局配置 中对各层文件的命名,未修改为默认值

                if (new File(filePath).exists() &&
                        (filePath.endsWith("Controller.java") || filePath.endsWith("Service.java") || filePath.endsWith("ServiceImpl.java"))) {
                    return false;
                }
                return true;
            }
        }).setFileOutConfigList(
                //指定模板、输出路径
                Arrays.asList(
                        new FileOutConfig(entity_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return entity_path + File.separator + tableInfo.getEntityName() + StringPool.DOT_JAVA;
                            }
                        }, new FileOutConfig(xml_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return xml_path + File.separator + tableInfo.getMapperName() + StringPool.DOT_XML;
                            }
                        }, new FileOutConfig(mapper_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return mapper_path + File.separator + tableInfo.getMapperName() + StringPool.DOT_JAVA;
                            }
                        }, new FileOutConfig(service_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return service_path + File.separator + tableInfo.getServiceName() + StringPool.DOT_JAVA;
                            }
                        }, new FileOutConfig(service_impl_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return service_impl_path + File.separator + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
                            }
                        }, new FileOutConfig(controller_template) {
                            @Override
                            public String outputFile(TableInfo tableInfo) {
                                return controller_path + File.separator + tableInfo.getControllerName() + StringPool.DOT_JAVA;
                            }
                        })
        );
    }

    private void loadDatabaseConfig() throws MojoExecutionException {
        JSONObject databaseConfig = JSONUtil.parseObj(configProperties.get(JDBCCONNECTION.getTag()));
        getLog().debug("数据库配置信息: " + databaseConfig.toJSONString(4));


        databaseConfig.getByPath("dbType");
        String dbTypeStr = databaseConfig.getStr("dbType");
        getLog().debug("获取的数据库类型: " + dbTypeStr);

        DbType dbType = DbType.getDbType(dbTypeStr);

        if (dbType == DbType.OTHER) {
            getLog().error("不支持的数据库类型: " + dbTypeStr);
            throw new MojoExecutionException("不支持的数据库类型: " + dbTypeStr);
        }

        username = databaseConfig.getStr("username");
        db_type = dbType;
        password = databaseConfig.getStr("password");
        url = databaseConfig.getStr("connectionURL");
        driverClassName = databaseConfig.getStr("driverClass");
    }

    private void loadTemplateConfig() {
        JSONObject templatePath = JSONUtil.parseObj(configProperties.get(TEMPLATEPATH.getTag()));
        templatePathProperties = parseProperty(JSONUtil.parseArray(templatePath.get("property")));

        entity_template = templatePathProperties.getProperty("entityPath");
        mapper_template = templatePathProperties.getProperty("mapperPath");
        service_template = templatePathProperties.getProperty("servicePath");
        service_impl_template = templatePathProperties.getProperty("serviceImplPath");
        controller_template = templatePathProperties.getProperty("controllerPath");
        xml_template = templatePathProperties.getProperty("mapperXmlPath");
    }


    private static String readFileAsString(File file) {
        InputStream in;
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(isr);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    protected void parseGenerateConfig(JSONObject rootConfig) {
        Properties result = new Properties();

        JSONObject jsonObject = JSONUtil.parseObj(rootConfig.get(ROOT.getTag()));
        result.put(JDBCCONNECTION.getTag(), jsonObject.get(JDBCCONNECTION.getTag()));
        result.put(TEMPLATEPATH.getTag(), jsonObject.get(TEMPLATEPATH.getTag()));

        configProperties = result;
    }


    protected static Properties parseProperty(JSONArray elements) {
        Properties result = new Properties();
        elements.stream()
                .map(JSONUtil::parseObj)
                .forEach(e -> result.put(e.get("name"), e.get("value")));
        return result;
    }

    private String convertPath(String packageName) {
        return packageName.replace(".", File.separator);
    }

    private String convertPackage(String path) {
        return path.replace(File.separator, ".");
    }


    private String combineWithFileSeparator(String path, String anotherPath) {
        return StrUtil.format("{}{}{}", path, File.separator, anotherPath);
    }

    private String combinePath(String path, String anotherPath) {
        return StrUtil.format("{}{}", path, anotherPath);
    }

}




package com.greek.mojo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.*;

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

    @Parameter(property = "template.file", required = true)
    private File templateFile;

    // 配置文件属性
    private static Properties configProperties;

    // 模板配置文件属性
    private static Properties templatePathProperties;

    //输出路径
    private static String out_put_dir = "F:\\project\\mybatistools\\src\\main\\java";

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
    //自定义实体类父类
    private static String super_entity = "com.yzj.mybatistools.module";
    //自定义实体类父类的公共字段
    private static String[] super_entity_columns = {"test_id"};
    // 自定义需要填充的字段
    private static List<TableFill> table_fill_list = new ArrayList<>();

    //包名，导入使用
    private static String package_path = "/com/yzj/mybatistools";

    //文件输出路径
    private static String project_path = System.getProperty("user.dir");

    private static String java_path = project_path + "/src/main/java" + package_path;
    private static String resource_path = project_path + "/src/main";

    private static String entity_path = java_path + "/entity";
    private static String mapper_path = java_path + "/mapper";
    private static String service_path = java_path + "/service";
    private static String service_impl_path = java_path + "/service/impl";
    private static String controller_path = java_path + "/controller";
    private static String xml_path = resource_path + "/resources/mybatis-mapper" + package_path + "/mapper";

    //文件输出模板
    private static String entity_template;
    private static String xml_template;
    private static String mapper_template;
    private static String service_template;
    private static String service_impl_template;
    private static String controller_template;

    public static void main(String[] args) throws MojoExecutionException {
        loadTemplateConfig();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        init();

//        AutoGenerator mpg = new AutoGenerator()
//                .setGlobalConfig(globalConfig())
//                .setDataSource(dataSourceConfig())
//                .setStrategy(strategyConfig())
//                .setPackageInfo(packageConfig())
//                // 因为使用了自定义模板,所以需要把各项置空否则会多生成一次
//                .setTemplate(templateConfig())
//                // 使用的模板引擎，如果不是默认模板引擎则需要添加模板依赖到pom
//                .setTemplateEngine(new FreemarkerTemplateEngine())
//                .setCfg(injectionConfig());
//        mpg.execute();
//        // 打印注入设置，这里演示模板里面怎么获取注入内容【可无】
//        System.err.println(mpg.getCfg().getMap().get("abc"));
    }

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig() {
        return new GlobalConfig()
                .setAuthor(author) // 作者
                .setOutputDir(out_put_dir)//输出目录
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

        configProperties = parseGenerateConfig(jsonObject);

        loadDatabaseConfig();

        loadTemplateConfig();
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
        String parent = package_path.replace('/', '.').substring(1);
        String entity = entity_path.substring(java_path.length()).replace('/', '.').substring(1);
        String mapper = mapper_path.substring(java_path.length()).replace('/', '.').substring(1);
        String xml = xml_path.substring(resource_path.length()).replace('/', '.').substring(1);
        String service = service_path.substring(java_path.length()).replace('/', '.').substring(1);
        String service_impl = service_impl_path.substring(java_path.length()).replace('/', '.').substring(1);
        String controller = controller_path.substring(java_path.length()).replace('/', '.').substring(1);
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
        JSONObject databaseConfig = JSONUtil.parseObj(configProperties.getProperty(JDBCCONNECTION.getTag()));

        databaseConfig.getByPath("dbType");
        String dbTypeStr = databaseConfig.getStr("dbType");
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

    private static void loadTemplateConfig() throws MojoExecutionException {
        JSONArray templatePath = JSONUtil.parseArray(configProperties.getProperty(TEMPLATEPATH.getTag()));
        templatePathProperties = parseProperty(templatePath);

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

    protected Properties parseGenerateConfig(JSONObject rootConfig) {
        Properties result = new Properties();

        JSONObject jsonObject = JSONUtil.parseObj(rootConfig.get(ROOT.getTag()));
        result.put(JDBCCONNECTION.getTag(), jsonObject.get(JDBCCONNECTION.getTag()));
        result.put(TEMPLATEPATH.getTag(), jsonObject.get(TEMPLATEPATH.getTag()));

        return result;
    }


    protected static Properties parseProperty(JSONArray elements) {
        Properties result = new Properties();
        elements.stream()
                .map(JSONUtil::parseObj)
                .forEach(e -> result.put(e.get("name"), e.get("value")));
        return result;
    }

}




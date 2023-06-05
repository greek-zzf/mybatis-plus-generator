// package com.greek.mojo;
//
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.hutool.json.JSONArray;
// import cn.hutool.json.JSONObject;
// import cn.hutool.json.JSONUtil;
// import com.baomidou.mybatisplus.annotation.DbType;
// import com.baomidou.mybatisplus.generator.FastAutoGenerator;
// import com.baomidou.mybatisplus.generator.config.*;
// import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
// import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
// import com.baomidou.mybatisplus.generator.config.rules.DateType;
// import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
// import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
// import org.apache.maven.plugin.AbstractMojo;
// import org.apache.maven.plugin.MojoExecutionException;
// import org.apache.maven.plugins.annotations.Mojo;
// import org.apache.maven.plugins.annotations.Parameter;
//
// import java.io.*;
// import java.nio.charset.StandardCharsets;
// import java.util.Properties;
//
// import static com.greek.enums.XmlTagEnum.*;
//
// /**
//  * @author Zhaofeng Zhou
//  * @date 2022/2/16 10:09
//  */
// @Mojo(name = "generate")
// public class CodeGenerateMojo extends AbstractMojo {
//
//
//     @Parameter(property = "author")
//     private String author = "";
//
//     @Parameter(property = "generator.configurationFile",
//             defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml", required = true)
//     private File configurationFile;
//
//     @Parameter(readonly = true, required = true, defaultValue = "${project.build.sourceDirectory}")
//     private String project_path;
//
//     @Parameter(readonly = true, required = true, defaultValue = "${project.build.resources[0].directory}")
//     private String resourcePath;
//
//     @Parameter(required = true, defaultValue = "com.generate", property = "package.name")
//     private String packageName;
//
//
//     // 配置文件属性
//     private static Properties configProperties;
//
//     // 模板配置文件属性
//     private static Properties templatePathProperties;
//
//     // 数据库配置
//     private static String username;
//     private static String password;
//     private static String url;
//     private static String scheme;
//     private static DbType db_type;
//     private static String driverClassName;
//
//     //生成的实体类时，忽略表的前缀名: 不需要则置空
//     private static String[] entity_ignore_prefix = {""};
//     //需要生成的表名
//     private static String[] tables = {
//             "vehicle_io_declare"
//     };
//     //需要排除的表名
//     private static String[] excludes = {
// //            "SYS_USER"
//     };
//
//
//     private String packagePath;
//
//
//     private String entity_path;
//     private String mapper_path;
//     private String service_path;
//     private String service_impl_path;
//     private String controller_path;
//     private String xml_path;
//
//     //文件输出模板
//     private static String entity_template;
//     private static String xml_template;
//     private static String mapper_template;
//     private static String service_template;
//     private static String service_impl_template;
//     private static String controller_template;
//
//     @Override
//     public void execute() throws MojoExecutionException {
//         init();
//
//         FastAutoGenerator.create(dataSourceConfig())
//                 // 全局配置
//                 .globalConfig(this::globalConfig)
//                 // 包配置
//                 .packageConfig(this::packageConfig)
//                 // 策略配置
//                 .strategyConfig(this::strategyConfig)
//                 // 模板引擎配置
//                 .templateEngine(new FreemarkerTemplateEngine())
//
//                 .execute();
//
//     }
//
//     /**
//      * 全局配置
//      */
//     private void globalConfig(GlobalConfig.Builder builder) {
//         builder.fileOverride()
//                 .outputDir(project_path)
//                 .author(author)
//                 .dateType(DateType.TIME_PACK)
//                 .commentDate("yyyy-MM-dd").build();
//     }
//
//     /**
//      * 数据源配置
//      */
//     private DataSourceConfig.Builder dataSourceConfig() {
//         return new DataSourceConfig.Builder(url, username, password)
//                 .dbQuery(new MySqlQuery())
//                 .schema(scheme)
//                 .typeConvert(new MySqlTypeConvert())
//                 .keyWordsHandler(new MySqlKeyWordsHandler());
//     }
//
//     private void init() throws MojoExecutionException {
//         if (!configurationFile.exists()) {
//             getLog().error("配置文件: " + configurationFile + "不存在");
//             throw new MojoExecutionException("配置文件: " + configurationFile + "不存在");
//         }
//
//         String xmlContent = readFileAsString(configurationFile);
//         JSONObject jsonObject = JSONUtil.parseFromXml(xmlContent);
//
//         parseGenerateConfig(jsonObject);
//
//         loadDatabaseConfig();
//
//         loadTemplateConfig();
//
//         loadGenerateFilePathConfig();
//     }
//
//     private void loadGenerateFilePathConfig() {
//         packagePath = convertPath(packageName);
//         String javaFilePath = combineWithFileSeparator(project_path, packagePath);
//
//         entity_path = combinePath(javaFilePath, "/entity");
//         mapper_path = combinePath(javaFilePath, "/mapper");
//         service_path = combinePath(javaFilePath, "/service");
//         service_impl_path = combinePath(javaFilePath, "/service/impl");
//         controller_path = combinePath(javaFilePath, "/controller");
//         xml_path = resourcePath + "/mybatis-mapper" + File.separator + packagePath + "/mapper";
//     }
//
//     /**
//      * 生成策略配置，自定义命名，生成结构
//      */
//     private void strategyConfig(StrategyConfig.Builder builder) {
//         builder.enableCapitalMode()
//                 .enableSkipView()
//                 .disableSqlFilter()
//                 .addInclude(tables)
//                 .addTablePrefix(entity_ignore_prefix)
//                 .build();
//     }
//
//     /**
//      * 生成的类包名配置，只需要设置父包名即可
//      * 默认的子包名为： entity, service, service.impl, mapper, mapper.xml, controller
//      */
//     private void packageConfig(PackageConfig.Builder builder) {
//         builder.parent(packageName).build();
//     }
//
//     /**
//      * 模板配置
//      */
//     private TemplateConfig templateConfig() {
//
//         return new TemplateConfig.Builder()
//                 .entity(entity_template)
//                 .service(service_template)
//                 .serviceImpl(service_impl_template)
//                 .mapper(mapper_template)
//                 .mapperXml(mapper_template)
//                 .controller(controller_template)
//                 .build();
//     }
//
//
//     private void loadDatabaseConfig() throws MojoExecutionException {
//         JSONObject databaseConfig = JSONUtil.parseObj(configProperties.get(JDBCCONNECTION.getTag()));
//         getLog().debug("数据库配置信息: " + databaseConfig.toJSONString(4));
//
//
//         databaseConfig.getByPath("dbType");
//         String dbTypeStr = databaseConfig.getStr("dbType");
//         getLog().debug("获取的数据库类型: " + dbTypeStr);
//
//         DbType dbType = DbType.getDbType(dbTypeStr);
//
//         if (dbType == DbType.OTHER) {
//             getLog().error("不支持的数据库类型: " + dbTypeStr);
//             throw new MojoExecutionException("不支持的数据库类型: " + dbTypeStr);
//         }
//
//         username = databaseConfig.getStr("username");
//         db_type = dbType;
//         password = databaseConfig.getStr("password");
//         scheme = databaseConfig.getStr("scheme");
//         url = databaseConfig.getStr("connectionURL");
//         driverClassName = databaseConfig.getStr("driverClass");
//     }
//
//     private void loadTemplateConfig() {
//         JSONObject templatePath = JSONUtil.parseObj(configProperties.get(TEMPLATEPATH.getTag()));
//         templatePathProperties = parseProperty(JSONUtil.parseArray(templatePath.get("property")));
//
//         entity_template = templatePathProperties.getProperty("entityPath");
//         mapper_template = templatePathProperties.getProperty("mapperPath");
//         service_template = templatePathProperties.getProperty("servicePath");
//         service_impl_template = templatePathProperties.getProperty("serviceImplPath");
//         controller_template = templatePathProperties.getProperty("controllerPath");
//         xml_template = templatePathProperties.getProperty("mapperXmlPath");
//     }
//
//
//     private static String readFileAsString(File file) {
//         return FileUtil.readString(file, StandardCharsets.UTF_8);
//     }
//
//     protected void parseGenerateConfig(JSONObject rootConfig) {
//         Properties result = new Properties();
//
//         JSONObject jsonObject = JSONUtil.parseObj(rootConfig.get(ROOT.getTag()));
//         result.put(JDBCCONNECTION.getTag(), jsonObject.get(JDBCCONNECTION.getTag()));
//         result.put(TEMPLATEPATH.getTag(), jsonObject.get(TEMPLATEPATH.getTag()));
//
//         configProperties = result;
//     }
//
//
//     protected static Properties parseProperty(JSONArray elements) {
//         Properties result = new Properties();
//         elements.stream()
//                 .map(JSONUtil::parseObj)
//                 .forEach(e -> result.put(e.get("name"), e.get("value")));
//         return result;
//     }
//
//     private String convertPath(String packageName) {
//         return packageName.replace(".", File.separator);
//     }
//
//     private String convertPackage(String path) {
//         return path.replace(File.separator, ".");
//     }
//
//
//     private String combineWithFileSeparator(String path, String anotherPath) {
//         return StrUtil.format("{}{}{}", path, File.separator, anotherPath);
//     }
//
//     private String combinePath(String path, String anotherPath) {
//         return StrUtil.format("{}{}", path, anotherPath);
//     }
//
// }
//
//
//

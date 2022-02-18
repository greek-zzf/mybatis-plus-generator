# MyBatis-Plus-Generator

以往使用 MyBatis-Plus 代码生成，都需要自己新建一个类，根据官方文档的 samples 实现相应配置。每次生成代码都需要手动执行该类，较为繁琐
，且大多配置都使用不上。

因此，自己实现了一个 Easy 版本的代码生成插件。内部使用了 Mybatis-Plus 最新的代码生成配置，遵循「约定大于配置」，简化了大多不必要配置。将核心配置信息
抽取成 xml 和 系统属性配置，常用配置写入 xml 配置中，需要动态更改可以使用系统属性配置。

## 使用

### 安装
由于未发布到 Maven 仓库，因此需要克隆本项目，使用命令安装到本地 Maven 仓库
```shell script
mvn install
```
### 配置
在自己的项目中引用该插件依赖，version 版本以该项目实际版本为准
```xml
    <plugin>
                 <groupId>com.greek</groupId>
                 <artifactId>mybatis-plus-generator-maven-plugin</artifactId>
                 <version>1.0-SNAPSHOT</version>
    </plugin>
```
为了简化命令，建议在 Maven setting.xml 文件中配置如下信息（可选）

```xml
<pluginGroups>
  <pluginGroup>com.greek</pluginGroup>
</pluginGroups>
```
在项目的 resources 文件夹下引入需要生成的模板，目前仅支持 Freemarker 模板。

在项目的 resources 文件夹下引入配置文件 generatorConfig.xml，[配置文件](#配置文件)参考。

执行命令，即可生成代码
```shell script
mvn mybatis-plus-generator:generate
```


## 默认约定
 
## 配置文件
 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                    connectionURL="jdbc:mysql://192.168.100.203:3306/visible_port?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=true&amp;serverTimezone=UTC"
                    username="root"
                    password="123456"
                    dbType="MYSQL">
    </jdbcConnection>

    <templatePath>
        <property name="entityPath" value="freemarker/entity.java.ftl"/>
        <property name="mapperPath" value="freemarker/mapper.xml.ftl"/>
        <property name="servicePath" value="freemarker/mapper.java.ftl"/>
        <property name="serviceImplPath" value="freemarker/service.java.ftl"/>
        <property name="controllerPath" value="freemarker/serviceImpl.java.ftl"/>
        <property name="mapperXmlPath" value="freemarker/controller.java.ftl"/>
    </templatePath>

</generatorConfiguration>

```



## 后续实现功能及优化
- [ ] 支持更多数据库
- [ ] 简化并重构部分代码
- [ ] 优化文档（参考 Vue 文档）
- [ ] 解决部分 bug
- [ ] 补充后续文档
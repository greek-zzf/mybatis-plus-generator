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

执行命令，根据控制台提示操作即可生成代码（默认覆盖同名文件）
```shell script
mvn mybatis-plus-generator:generate
```

## 后续实现功能及优化
- [ ] 支持排除不需要生成代码的表
- [ ] 简化并重构部分代码
- [ ] 代码格式优化
- [ ] 简化插件，移除部分依赖包
- [ ] 指定 MySQL 驱动版本
- [ ] 发布到 maven 中央仓库
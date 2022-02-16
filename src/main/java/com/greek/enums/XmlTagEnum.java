package com.greek.enums;

/**
 * @author Zhaofeng Zhou
 * @date 2022/2/16 18:00
 */
public enum XmlTagEnum {

    ROOT("generatorConfiguration", "配置文件根节点标签"),
    JDBCCONNECTION("jdbcConnection", "jdbc 连接信息标签"),
    TEMPLATEPATH("templatePath", "模板文件地址标签");

    private String tag;
    private String desc;

    XmlTagEnum(String tag, String desc) {
        this.tag = tag;
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public String getDesc() {
        return desc;
    }
}

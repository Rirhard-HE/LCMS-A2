package com.example.backend;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Arrays;
import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        String url  = "jdbc:mysql://ifn636server.mysql.database.azure.com:3306/ifn636_db"
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true"
                + "&sslMode=REQUIRED";
        String user = "ifn636admin";
        String pass = "123321qq!";

        String projectDir = System.getProperty("user.dir");

        FastAutoGenerator.create(url, user, pass)
                .globalConfig(builder -> builder
                        .author("Kian")
                        .outputDir(projectDir + "/src/main/java")
                        .disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .parent("com.example.backend")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml, projectDir + "/src/main/resources/mapper"
                        ))
                )
                .strategyConfig(builder -> builder
                        .addInclude(Arrays.asList(
                                "categories",
                                "lawyers",
                                "cases",
                                "evidence",
                                "case_categories"
                        ))
                        // case_categories -> CaseCategory
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .naming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                        .columnNaming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                        .controllerBuilder().enableRestStyle().enableHyphenStyle().disable()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sMapper")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}

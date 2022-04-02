package com.muern.framework.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.muern.framework.utils.DateUtil;

/**
 * @author gegeza
 * @date 2019-12-17 17:40 AM
 */
public class GenerateMapper {
    /** 生成Mapper类 */
    public static void generate(String entityName, String basePackage, String basePath) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ".concat(basePackage).concat(".mapper;\r\n\r\n"));
        //import code
        sb.append("import cn.d0x.bootstarter.common.Mappers;\r\n");
        sb.append("import ".concat(basePackage).concat(".entity.").concat(entityName).concat(";\r\n"));
        sb.append("import org.apache.ibatis.annotations.Mapper;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis generate for imms\r\n");
        sb.append(" * @date ".concat(DateUtil.formatNowDateTime()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@Mapper\r\n");
        //interface code
        sb.append("public interface ".concat(entityName).concat("Mapper extends Mappers<").concat(entityName).concat("> {\r\n"));
        sb.append("}\r\n");
        File file = new File(basePath.concat("/mapper/"));
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "Mapper.java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println(sb.toString());
        pw.flush();
        pw.close();
        System.out.println("Generate Successful :" + file.getPath());
    }
}

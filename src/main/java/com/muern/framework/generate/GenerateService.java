package com.muern.framework.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.muern.framework.utils.DateUtil;

/**
 * @author gegeza
 * @date 2019-12-17 17:45 AM
 */
public class GenerateService {

    /** 生成Service类 */
    public static void generate(String entityName, String basePackage, String basePath) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ".concat(basePackage).concat(".service;\r\n\r\n"));
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis generate for imms\r\n");
        sb.append(" * @date ".concat(DateUtil.formatNowDateTime()).concat("\r\n"));
        sb.append(" */\r\n");
        //interface code
        sb.append("public interface ").append(entityName).append("Service {\r\n\r\n");
        sb.append("}\r\n");
        String content = sb.toString();
        File file = new File(basePath.concat("/service/"));
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "Service.java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println(content);
        pw.flush();
        pw.close();
        System.out.println("Generate Successful :" + file.getPath());
    }
}

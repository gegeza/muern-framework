package com.muern.framework.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.muern.framework.utils.DateUtil;

/**
 * @author gegeza
 * @date 2019-12-17 17:48 AM
 */
public class GenerateServiceImpl {

    /** 生成ServiceImpl类 */
    public static void generate(String entityName, String basePackage, String basePath) throws IOException {
        StringBuilder sb;
        //package code
        sb = new StringBuilder().append("package ".concat(basePackage).concat(".service.impl;\r\n\r\n"));
        //import code
        sb.append("import ".concat(basePackage).concat(".mapper.").concat(entityName).concat("Mapper;\r\n"));
        sb.append("import ".concat(basePackage).concat(".service.").concat(entityName).concat("Service;\r\n"));
        sb.append("import org.springframework.stereotype.Service;\r\n");
        sb.append("import javax.annotation.Resource;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis generate for imms\r\n");
        sb.append(" * @date ".concat(DateUtil.formatNowDateTime()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@Service\r\n");
        //class code
        sb.append("public class ".concat(entityName).concat("ServiceImpl implements ").concat(entityName).concat("Service {\r\n\r\n"));
        sb.append("\t@Resource private ".concat(entityName).concat("Mapper mapper;\r\n\r\n"));
        sb.append("}\r\n");
        String content = sb.toString();
        //write to file
        File file = new File(basePath.concat("/service/impl/"));
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "ServiceImpl.java");
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

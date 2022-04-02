package com.muern.framework.generate;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.muern.framework.utils.DateUtil;

/**
 * @author gegeza
 * @date 2019-12-17 17:30 AM
 */
public class GenerateEntity {

    private static final Pattern PATTERN= Pattern.compile("([A-Za-z\\d]+)(_)?");

    /** 生成实体类 */
    public static void generate(String entityName, String basePackage, String basePath, String databaseName, String tableName,
                                List<String> colnames, List<String> colTypes, List<String> colComment, boolean importDate,
                                boolean importDateTime, boolean importBigDecimal) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ".concat(basePackage).concat(".entity;\r\n\r\n"));
        //import code
        if (importDate) {
            sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\r\n");
        }
        sb.append("import cn.d0x.bootstarter.common.Json;\r\n");
        sb.append("import lombok.*;\r\n\r\n");
        sb.append("import javax.persistence.Id;\r\n");
        sb.append("import javax.persistence.Column;\r\n");
        sb.append("import javax.persistence.Table;\r\n");
        sb.append("import java.io.Serializable;\r\n");
        if (importBigDecimal) {
            sb.append("import java.math.BigDecimal;\r\n");
        }
        if (importDate) {
            sb.append("import java.time.LocalDate;\r\n");
        }
        if (importDateTime) {
            sb.append("import java.time.LocalDateTime;\r\n");
        }
        sb.append("\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis generate for imms\r\n");
        sb.append(" * @date ".concat(DateUtil.formatNowDateTime()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@Data\r\n");
        sb.append("@Builder\r\n");
        sb.append("@NoArgsConstructor\r\n");
        sb.append("@AllArgsConstructor\r\n");
        sb.append("@Table(schema = \"`".concat(databaseName).concat("`\", name = \"").concat(tableName).concat("\")\r\n"));
        //class code
        sb.append("public class ".concat(entityName).concat(" implements Serializable {\r\n\r\n"));
        sb.append("\tprivate static final long serialVersionUID = ").append(generateSerialVersionUid()).append("L;\r\n\r\n");
        for (int i = 0; i < colnames.size(); i++) {
            if ("Date".equals(getType(colTypes.get(i)))) {
                sb.append("\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")\r\n");
            }
            sb.append("\t/** ").append(colComment.get(i)).append(" */\r\n");
            if (i == 0) {
                sb.append("\t@Id\r\n");
            }
            sb.append("\t@Column(name = \"").append(colnames.get(i)).append("\")\r\n");
            sb.append("\tprivate ").append(getType(colTypes.get(i))).append(" ").append(underline2Camel(colnames.get(i)))
                    .append(";\r\n\r\n");
        }
        sb.append("\t@Override\r\n");
        sb.append("\tpublic String toString() {\r\n");
        sb.append("\t\treturn Json.format(this);\r\n");
        sb.append("\t}\r\n\r\n");
        sb.append("}\r\n");
        File file = new File(basePath.concat("/entity/"));
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + ".java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println(sb.toString());
        pw.flush();
        System.out.println("Generate Successful :" + file.getPath());
    }

    /** 根据SQL类型获取java类型 */
    private static String getType(String sqlType) {
        switch (sqlType) {
            case "tinyint":
            case "smallint":
            case "int":
            case "integer":
                return "Integer";
            case "bigint":
                return "Long";
            case "float":
            case "double":
            case "numeric":
                return "Double";
            case "decimal":
                return "BigDecimal";
            case "varchar":
            case "char":
            case "text":
            case "mediumtext":
                return "String";
            case "date":
                return "LocalDate";
            case "time":
            case "datetime":
            case "timestamp":
                return "LocalDateTime";
            default:
                System.out.println("ERROR DATA TYPE : "+ sqlType);
                return null;
        }
    }

    /** 下划线转首字母大写 */
    private static String underline2Camel(String line){
        if(StringUtils.isEmpty(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN.matcher(line);
        while(matcher.find()){
            String word = matcher.group();
            sb.append(matcher.start()==0? Character.toLowerCase(word.charAt(0)): Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if(index > 0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static long generateSerialVersionUid() {
        return Double.valueOf(Math.random() * 1000000000000000000L).longValue();
    }
}

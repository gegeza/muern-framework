package com.muern.framework.controller;

import java.io.File;
import java.util.Base64;
import java.util.UUID;

import com.muern.framework.common.ECode;
import com.muern.framework.common.Result;
import com.muern.framework.encrypt.Hash;
import com.muern.framework.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("muern/upload")
@ConditionalOnProperty(name = "muern.upload.dir", havingValue = "true")
public class FileUploadController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);
    
    @Value("${muern.upload.dir}") private String fileDir;
    @Value("${muern.upload.uri}") private String fileUri;

    private static final String SIGN_KEY = "muern_upload";

    @PostMapping(value = "upload")
    public Result<String> upload(@RequestBody UploadDto dto) {
        //验证签名
        if (!dto.getSign().equalsIgnoreCase(Hash.sha256(dto.getTimestamp() + SIGN_KEY))){
            return Result.ins(ECode.ERR_SIGN);
        }
        //定义文件名称
        String randomName = UUID.randomUUID().toString().replace("-", "");
        if (!StringUtils.isEmpty(dto.getSuffix())) {
            randomName = dto.getSuffix().startsWith(".") ? randomName.concat(dto.getSuffix()) : randomName.concat(".").concat(dto.getSuffix());
        }
        //创建本地文件
        File localFile = new File(fileDir.concat(randomName));
        try {
            if (!localFile.exists() && !localFile.createNewFile()) {
                LOGGER.error("createNewFile Error:" + localFile.getPath());
                return Result.fail();
            }
        } catch (Exception e) {
            LOGGER.error("createNewFile Error:" + localFile.getPath());
            LOGGER.error(e.getMessage(), e);
            return Result.fail();
        }
        //写入文件并返回文件路径
        FileUtil.bytes2File(Base64.getDecoder().decode(dto.getFile()), localFile);
        return Result.ins(fileUri.concat(randomName));
    }

    public static class UploadDto {
        private Long timestamp;
        private String sign;
        private String file;
        private String suffix;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
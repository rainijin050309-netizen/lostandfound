package com.lostandfound.lostandfound.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("请选择文件");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!suffix.equals(".jpg") && !suffix.equals(".jpeg")
                && !suffix.equals(".png") && !suffix.equals(".gif")) {
            return Result.fail("只支持 jpg、png、gif 格式");
        }

        // 生成唯一文件名
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 创建目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        try {
            file.transferTo(new File(uploadPath + filename));
        } catch (IOException e) {
            return Result.fail("上传失败");
        }

        // 返回可访问的URL
        String url = "http://localhost:8080/uploads/" + filename;
        return Result.success(url);
    }
}

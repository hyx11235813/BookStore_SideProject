package yixian.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yixian.website.common.Result;
import yixian.website.service.FileStorageService;

/**
 * 文件上傳到本地
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private static final String DOWNLOAD_PATH = "/api/file/download/";

    private final FileStorageService fileStorageService;

    //返回調用api的路徑，讓前端能夠透過api獲取圖片
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.store(file);
        return Result.ok(DOWNLOAD_PATH + fileName, "文件上傳到本地成功");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource file = fileStorageService.load(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileStorageService.contentTypeOf(fileName)))
                .body(file);
    }
}

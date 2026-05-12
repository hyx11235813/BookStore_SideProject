package yixian.website.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yixian.website.common.Result;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 文件上傳到本地
 */
@RestController
@RequestMapping("/file")
public class FIleController {
    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";

    //返回調用api的路徑，讓前端能夠透過api獲取圖片
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file) {
        if (file == null) {
            return Result.getError("文件為空");
        }
        String fileName = generateFileName(file);
        String filePath = getFilePath(fileName);
        saveFile(file, filePath);
        //api路徑加上圖片名稱
        String URL =  "/api/file/download/" + filePath.substring(filePath.lastIndexOf(File.separator) + 1);//拼裝文件的下載路徑

        return Result.saveSuccess(URL, "文件上傳到本地成功 ");
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();//獲取文件名
        String fileName = originalFilename.substring(0, originalFilename.lastIndexOf("."));  //獲取不包含後綴的文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //獲取文件後綴名
        return fileName + "_" + System.currentTimeMillis() + suffix; //文件名稱加上時間戳避免文件重名
    }

    private String getFilePath(String fileName) {
        return ROOT_PATH + File.separator + fileName;
    }

    private void saveFile(MultipartFile file, String filePath) {
        File saveFile = new File(filePath);
        if (!saveFile.getParentFile().exists()) { //判斷File的父文件(files)存不存在，不存在則創建
            if (!saveFile.getParentFile().mkdirs()) {
                System.err.println("儲存的文件夾創建失敗");
            }
        }
        try {
            file.transferTo(saveFile);//存到本地
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download/{fileName}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String filePath = ROOT_PATH + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        //讀取文件
        byte[] bytes = Files.readAllBytes(file.toPath());
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLength(bytes.length);
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream()); //透過複製，直接將文件寫入response的輸出流
        response.flushBuffer();
    }
}

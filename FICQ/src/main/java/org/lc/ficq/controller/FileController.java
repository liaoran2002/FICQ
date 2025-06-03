package org.lc.ficq.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.result.Result;
import org.lc.ficq.result.ResultUtils;
import org.lc.ficq.service.thirdparty.FileService;
import org.lc.ficq.vo.UploadImageVO;
import org.lc.ficq.vo.UploadVideoVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "文件上传")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传图片", description = "上传图片,上传后返回原图和缩略图的url")
    @PostMapping("/image/upload")
    public Result<UploadImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResultUtils.success(fileService.uploadImage(file));
    }

    @CrossOrigin
    @Operation(summary = "上传文件", description = "上传文件，上传后返回文件url")
    @PostMapping("/file/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResultUtils.success(fileService.uploadFile(file), "");
    }

    @CrossOrigin
    @Operation(summary = "上传视频", description = "上传视频，上传后返回视频和缩略图的url")
    @PostMapping("/video/upload")
    public Result<UploadVideoVO> uploadVideo(@RequestParam("videoFile") MultipartFile videoFile,@RequestParam("imageFile") MultipartFile imageFile) {
        return ResultUtils.success(fileService.uploadVideo(videoFile,imageFile), "");
    }

}
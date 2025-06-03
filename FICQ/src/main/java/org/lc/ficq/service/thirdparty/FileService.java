package org.lc.ficq.service.thirdparty;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.contant.Constant;
import org.lc.ficq.enums.FileType;
import org.lc.ficq.enums.ResultCode;
import org.lc.ficq.exception.GlobalException;
import org.lc.ficq.session.SessionContext;
import org.lc.ficq.util.FileUtil;
import org.lc.ficq.util.ImageUtil;
import org.lc.ficq.vo.UploadImageVO;
import org.lc.ficq.vo.UploadVideoVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * todo 通过校验文件MD5实现重复文件秒传
 * 文件上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.baseUploadDir}")
    private String baseUploadDir;

    @PostConstruct
    public void init() {
        // Create base directories if they don't exist
        try {
            Files.createDirectories(Paths.get(baseUploadDir, "files"));
            Files.createDirectories(Paths.get(baseUploadDir, "images"));
            Files.createDirectories(Paths.get(baseUploadDir, "videos"));
        } catch (IOException e) {
            log.error("Failed to create upload directories", e);
            throw new RuntimeException("Failed to initialize file service", e);
        }
    }


    public String uploadFile(MultipartFile file) {
        Long userId = SessionContext.getSession().getUserId();
        // Size validation
        if (file.getSize() > Constant.MAX_FILE_SIZE) {
            throw new GlobalException(ResultCode.PROGRAM_ERROR, "文件大小不能超过20M");
        }
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = null;
        if (originalFilename != null) {
            fileExtension = FileUtil.getFileExtension(originalFilename);
        }
        String uniqueFileName = UUID.randomUUID() + (fileExtension != null ? "." + fileExtension : "");
        // Save file
        Path filePath = Paths.get(baseUploadDir, "files", uniqueFileName);
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new GlobalException(ResultCode.PROGRAM_ERROR, "文件上传失败");
        }
        String url = generUrl(FileType.FILE, uniqueFileName);
        log.info("文件上传成功，用户id:{}, url:{}", userId, url);
        return url;
    }

    public UploadImageVO uploadImage(MultipartFile file) {
        try {
            Long userId = SessionContext.getSession().getUserId();
            // Size validation
            if (file.getSize() > Constant.MAX_IMAGE_SIZE) {
                throw new GlobalException(ResultCode.PROGRAM_ERROR, "图片大小不能超过5M");
            }
            String originalFilename = file.getOriginalFilename();
            // Image format validation
            if (!FileUtil.isImage(originalFilename) || originalFilename == null) {
                throw new GlobalException(ResultCode.PROGRAM_ERROR, "图片格式不合法");
            }
            // Generate unique filename
            String fileExtension =  FileUtil.getFileExtension(originalFilename);
            String uniqueFileName = UUID.randomUUID() + "." + fileExtension;
            UploadImageVO vo = new UploadImageVO();
            // Save original image
            Path imagePath = Paths.get(baseUploadDir, "images", uniqueFileName);
            file.transferTo(imagePath);
            vo.setOriginUrl(generUrl(FileType.IMAGE, uniqueFileName));
            // Create thumbnail if image is larger than 30KB
            if (file.getSize() > 30 * 1024) {
                byte[] imageByte = ImageUtil.compressForScale(file.getBytes(), 30);
                String thumbFileName = "thumb_" + uniqueFileName;
                Path thumbPath = Paths.get(baseUploadDir, "images", thumbFileName);
                Files.write(thumbPath, imageByte);
                vo.setThumbUrl(generUrl(FileType.IMAGE, thumbFileName));
            } else {
                vo.setThumbUrl(vo.getOriginUrl());
            }
            log.info("图片上传成功，用户id:{}, url:{}", userId, vo.getOriginUrl());
            return vo;
        } catch (IOException e) {
            log.error("上传图片失败，{}", e.getMessage(), e);
            throw new GlobalException(ResultCode.PROGRAM_ERROR, "图片上传失败");
        }
    }

    public UploadVideoVO uploadVideo(MultipartFile videoFile, MultipartFile imageFile) {
        try {
            Long userId = SessionContext.getSession().getUserId();
            // Size validation
            if (videoFile.getSize() > Constant.MAX_FILE_SIZE) {
                throw new GlobalException(ResultCode.PROGRAM_ERROR, "视频大小不能超过20M");
            }
            String videoFileOriginalFilename = videoFile.getOriginalFilename();
            // Image format validation
            if (!FileUtil.isVideo(videoFileOriginalFilename) || videoFileOriginalFilename == null) {
                throw new GlobalException(ResultCode.PROGRAM_ERROR, "视频格式不合法");
            }
            String imageFileOriginalFilename = imageFile.getOriginalFilename();
            if (!FileUtil.isImage(imageFileOriginalFilename) || imageFileOriginalFilename == null) {
                throw new GlobalException(ResultCode.PROGRAM_ERROR, "图片格式不合法");
            }
            String uniqueFileName = UUID.randomUUID()+".";
            UploadVideoVO vo = new UploadVideoVO();
            String uniqueVideoFileName = uniqueFileName+FileUtil.getFileExtension(videoFileOriginalFilename);
            Path videoPath = Paths.get(baseUploadDir, "videos", uniqueVideoFileName);
            videoFile.transferTo(videoPath);
            vo.setOriginUrl(generUrl(FileType.VIDEO, uniqueVideoFileName));
            byte[] videoByte = ImageUtil.compressForScale(imageFile.getBytes(), 30);
            String thumbFileName = "thumb_" + uniqueFileName+FileUtil.getFileExtension(imageFileOriginalFilename);
            Path thumbPath = Paths.get(baseUploadDir, "images", thumbFileName);
            Files.write(thumbPath, videoByte);
            vo.setThumbUrl(generUrl(FileType.IMAGE, thumbFileName));
            log.info("视频上传成功，用户id:{}, url:{}", userId, vo.getOriginUrl());
            return vo;
        }catch (Exception e) {
            log.error("上传视频失败，{}", e.getMessage(), e);
            throw new GlobalException(ResultCode.PROGRAM_ERROR, "视频上传失败");
        }
    }

    public String generUrl(FileType fileTypeEnum, String fileName) {
        String url = "";
        switch (fileTypeEnum) {
            case FILE:
                url += "/files/";
                break;
            case IMAGE:
                url += "/images/";
                break;
            case VIDEO:
                url += "/videos/";
                break;
            default:
                break;
        }
        url += fileName;
        return url;
    }
}

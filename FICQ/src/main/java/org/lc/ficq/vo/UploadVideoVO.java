package org.lc.ficq.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频上传VO")
public class UploadVideoVO {

    @Schema(description = "原视频")
    private String originUrl;

    @Schema(description = "缩略图")
    private String thumbUrl;
}

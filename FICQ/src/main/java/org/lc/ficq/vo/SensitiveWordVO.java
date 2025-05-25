package org.lc.ficq.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.lc.ficq.serializer.DateToLongSerializer;

import java.util.Date;

@Data
@Schema(description = "敏感词VO")
public class SensitiveWordVO {
    @NotNull(message = "敏感词id不能为空")
    @Schema(description = "敏感词id")
    private Long id;

    @NotNull(message = "敏感词内容不能为空")
    @Schema(description = "敏感词内容")
    private String content;

    @Schema(description = "创建时间")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;

    @Schema(description = "是否启用")
    private Boolean enabled;
}

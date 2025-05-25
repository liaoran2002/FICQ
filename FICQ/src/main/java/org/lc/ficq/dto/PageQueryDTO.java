package org.lc.ficq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分页查询DTO")
public class PageQueryDTO {
    @Schema(description = "封禁状态", example = "false")
    private Boolean banned;

    @NotNull(message = "页码不能为空")
    @Schema(description = "页码", example = "1")
    private Integer pageNum;

    @NotNull(message = "每页数量不能为空")
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize;

    @Schema(description = "搜索词", example = "")
    private String searchWord;
}

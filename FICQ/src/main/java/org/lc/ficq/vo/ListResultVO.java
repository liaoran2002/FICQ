package org.lc.ficq.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
@Schema(description = "分页信息VO")
public class ListResultVO<T> {
    @NotNull(message = "列表不可为空")
    @Schema(description = "列表")
    private List<T> list;

    @NotNull(message = "总数不可为空")
    @Schema(description = "总数")
    private Long total;
}

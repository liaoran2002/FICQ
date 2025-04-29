package org.lc.ficq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "群组解锁")
public class GroupUnbanDTO {
    @NotNull(message = "群组id不可为空")
    @Schema(description = "群组id")
    private Long id;

}

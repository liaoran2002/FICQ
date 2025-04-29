package org.lc.ficq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "用户解锁")
public class UserUnbanDto {

    @NotNull(message = "用户id不可为空")
    @Schema(description = "用户id")
    private Long id;

}
package org.lc.ficq.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "群组封禁")
public class GroupBanDTO {
    @NotNull(message = "群组id不可为空")
    @Schema(description = "群组id")
    private Long id;

    @Length(max = 128, message = "封禁原因长度不能超过128")
    @Schema(description = "封禁原因")
    private String reason;
}

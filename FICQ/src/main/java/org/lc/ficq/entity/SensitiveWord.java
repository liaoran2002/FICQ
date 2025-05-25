package org.lc.ficq.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 敏感词
 *
 * @author Blue 
 * @since 1.0.0 2024-07-20
 */

@Data
@TableName("sensitive_word")
public class SensitiveWord {
	/**
	* id
	*/
	@TableId
	private Long id;

	/**
	* 敏感词内容
	*/
	private String content;

	/**
	* 是否启用
	*/
	private Boolean enabled;

	/**
	 * 创建者
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long creator;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;


}
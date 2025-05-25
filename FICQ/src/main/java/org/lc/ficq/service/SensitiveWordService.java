package org.lc.ficq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.entity.SensitiveWord;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.SensitiveWordVO;


import java.util.List;

public interface SensitiveWordService extends IService<SensitiveWord> {

    /**
     * 查询所有开启的敏感词
     * @return enableWords
     */
    List<String> findAllEnabledWords();

    ListResultVO<SensitiveWordVO> findSensitiveWordList(@Valid PageQueryDTO dto);
}

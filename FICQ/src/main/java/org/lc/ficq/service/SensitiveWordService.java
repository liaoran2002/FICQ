package org.lc.ficq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lc.ficq.entity.SensitiveWord;


import java.util.List;

public interface SensitiveWordService extends IService<SensitiveWord> {

    /**
     * 查询所有开启的敏感词
     * @return enableWords
     */
    List<String> findAllEnabledWords();
}

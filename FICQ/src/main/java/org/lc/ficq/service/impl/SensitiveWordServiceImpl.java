package org.lc.ficq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lc.ficq.dto.PageQueryDTO;
import org.lc.ficq.entity.SensitiveWord;
import org.lc.ficq.entity.User;
import org.lc.ficq.mapper.SensitiveWordMapper;
import org.lc.ficq.service.SensitiveWordService;
import org.lc.ficq.util.BeanUtils;
import org.lc.ficq.vo.ListResultVO;
import org.lc.ficq.vo.SensitiveWordVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements
        SensitiveWordService {

    @Override
    public List<String> findAllEnabledWords() {
        LambdaQueryWrapper<SensitiveWord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SensitiveWord::getEnabled,true);
        wrapper.select(SensitiveWord::getContent);
        List<SensitiveWord> words = this.list(wrapper);
        return words.stream().map(SensitiveWord::getContent).collect(Collectors.toList());
    }

    @Override
    public ListResultVO<SensitiveWordVO> findSensitiveWordList(PageQueryDTO dto) {
        LambdaQueryWrapper<SensitiveWord> wrapper = Wrappers.lambdaQuery();
        List<SensitiveWord> sensitiveWords = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()),wrapper).getRecords();
        ListResultVO<SensitiveWordVO> vo = new ListResultVO<>();
        vo.setList(sensitiveWords.stream().map(sensitiveWord -> BeanUtils.copyProperties(sensitiveWord,SensitiveWordVO.class)).toList());
        vo.setTotal(this.count(wrapper));
        return vo;
    }
}

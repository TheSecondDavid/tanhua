package com.zhouhao.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhouhao.api.QuestionApi;
import com.zhouhao.dao.QuestionMapper;
import com.zhouhao.entity.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi {
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLambdaQueryWrapper.eq(Question::getUserId, userId);
        return questionMapper.selectOne(questionLambdaQueryWrapper);
    }

    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void update(Question question) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getUserId, question.getUserId());
        questionMapper.update(question, queryWrapper);
    }
}

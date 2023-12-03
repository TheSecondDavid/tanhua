package com.zhouhao.api;

import com.zhouhao.entity.Question;

public interface QuestionApi {
    Question findByUserId(Long userId);

    void save(Question question);

    void update(Question question);
}

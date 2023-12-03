package com.zhouhao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhouhao.api.QuestionApi;
import com.zhouhao.api.SettingsApi;
import com.zhouhao.entity.BlackList;
import com.zhouhao.entity.Question;
import com.zhouhao.entity.Settings;
import com.zhouhao.utils.UserHolder;
import com.zhouhao.vo.PageResult;
import com.zhouhao.vo.SettingsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {
    @DubboReference
    SettingsApi settingsApi;
    @DubboReference
    QuestionApi questionApi;

    public SettingsVo settings() {
        SettingsVo settingsVo = new SettingsVo();
        Long userId = UserHolder.getUserId();
        Settings settings = settingsApi.settings(userId);
        BeanUtils.copyProperties(settings, settingsVo);
        Question question = questionApi.findByUserId(userId);
        if(question!= null)
            settingsVo.setStrangerQuestion(question.getTxt());
        return settingsVo;
    }

    public void saveQuestion(String content) {
        Question question = new Question();
        question.setTxt(content);
        question.setUserId(UserHolder.getUserId());

        Question byUserId = questionApi.findByUserId(UserHolder.getUserId());
        if (byUserId == null) {
            questionApi.save(question);
        } else {
            questionApi.update(question);
        }
    }

    public void saveSettings(Map map) {
        boolean likeNotification = (Boolean) map.get("likeNotification");
        boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");

        Long userId = UserHolder.getUserId();
        Settings settings = settingsApi.findById(userId);

        if (settings != null) {
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setUserId(userId);
            settingsApi.updateById(settings);
        }else {
            Settings settingsInsert = new Settings();
            settingsInsert.setUserId(userId);
            settingsInsert.setLikeNotification(likeNotification);
            settingsInsert.setPinglunNotification(pinglunNotification);
            settingsInsert.setGonggaoNotification(gonggaoNotification);

            settingsApi.insertSettings(settingsInsert);
        }
    }

    public void deleteBlackList(Long blackUserId) {
        settingsApi.deleteBlackList(blackUserId, UserHolder.getUserId());
    }

    public PageResult blcaklist(int page, int size) {
        Page<BlackList> pageMp = settingsApi.blacklist(page, size, UserHolder.getUserId());
        List<BlackList> records = pageMp.getRecords();
        return new PageResult(page, size, records.size(), records);
    }
}

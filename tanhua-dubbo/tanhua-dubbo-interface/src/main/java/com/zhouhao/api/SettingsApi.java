package com.zhouhao.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhouhao.entity.BlackList;
import com.zhouhao.entity.Settings;

public interface SettingsApi {

    Settings settings(Long userId);

    Settings findById(Long userId);

    void insertSettings(Settings settingsInsert);

    void updateById(Settings settings);

    void deleteBlackList(Long blackUserId, Long userId);

    Page<BlackList> blacklist(int page, int size, Long userId);

}

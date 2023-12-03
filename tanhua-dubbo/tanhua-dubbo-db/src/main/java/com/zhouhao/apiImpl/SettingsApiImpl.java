package com.zhouhao.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhouhao.api.SettingsApi;
import com.zhouhao.dao.BlackListMapper;
import com.zhouhao.dao.SettingsMapper;
import com.zhouhao.entity.BlackList;
import com.zhouhao.entity.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@DubboService
public class SettingsApiImpl implements SettingsApi {
    @Autowired
    private SettingsMapper settingsMapper;
    @Autowired
    BlackListMapper blackListMapper;

    @Override
    public Settings settings(Long userId) {
        LambdaQueryWrapper<Settings> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(Settings::getUserId, userId);
        return settingsMapper.selectOne(objectLambdaQueryWrapper);
    }

    @Override
    public Settings findById(Long userId) {
        LambdaQueryWrapper<Settings> settingsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        settingsLambdaQueryWrapper.eq(Settings::getUserId, userId);
        return settingsMapper.selectOne(settingsLambdaQueryWrapper);
    }

    @Override
    public void insertSettings(Settings settingsInsert) {
        settingsMapper.insert(settingsInsert);
    }

    @Override
    public void updateById(Settings settings) {
        LambdaQueryWrapper<Settings> settingsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        settingsLambdaQueryWrapper.eq(Settings::getUserId, settings.getUserId());

        settingsMapper.update(settings, settingsLambdaQueryWrapper);
    }

    @Override
    public void deleteBlackList(Long blackUserId, Long userId) {
        LambdaQueryWrapper<BlackList> blackListLambdaQueryWrapper = new LambdaQueryWrapper<>();
        blackListLambdaQueryWrapper.eq(BlackList::getBlackUserId, blackUserId).eq(BlackList::getUserId, userId);
        blackListMapper.delete(blackListLambdaQueryWrapper);
    }

    @Override
    public Page<BlackList> blacklist(int page, int size, Long userId) {
        Page pages = new Page(page, size);
        LambdaQueryWrapper<BlackList> blackListLambdaQueryWrapper = new LambdaQueryWrapper<>();
        blackListLambdaQueryWrapper.eq(BlackList::getUserId, userId);
        List list = blackListMapper.selectList(pages, blackListLambdaQueryWrapper);
        return pages.setRecords(list);
    }
}

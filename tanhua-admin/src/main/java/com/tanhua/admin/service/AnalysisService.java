package com.tanhua.admin.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.zhouhao.admin.Analysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService extends ServiceImpl<AnalysisMapper, Analysis> {
    @Autowired
    LogMapper logMapper;
    @Autowired
    AnalysisMapper analysisMapper;
    /**
     * 定时统计日志数据到统计表中
     *    1、查询tb_log表中的数 （每日注册用户数，每日登陆用户，活跃的用户数据，次日留存的用户）
     *    2、构造AnalysisByDay对象
     *    3、完成统计数据的更新或者保存
     */
    public void analysis() throws ParseException {

        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yestodayStr =  DateUtil.yesterday().toString("yyyy-MM-dd"); //工具类
        //1、统计每日注册用户数
        Integer numRegistered = logMapper.queryByTypeAndLogTime("0102",todayStr);
        //2、统计每日登陆用户
        Integer numLogin = logMapper.queryByTypeAndLogTime("0101",todayStr);
        //3、统计活跃的用户数
        Integer numActive = logMapper.queryByLogTime(todayStr);
        //4、统计次日留存的用户数
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yestodayStr);
        //5、根据当前时间查询AnalysisByDay数据
        QueryWrapper<Analysis> qw = new QueryWrapper<>();
        Date todatDate = new SimpleDateFormat("yyyy-MM-dd").parse(todayStr);
        qw.eq("record_date", todatDate);

        Analysis analysis = analysisMapper.selectOne(qw);
        if(analysis == null) {
            //7、如果不存在，保存
            analysis = new Analysis();
            analysis.setRecordDate(todatDate);
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setCreated(new Date());
            analysisMapper.insert(analysis);
        }else{
            //8、如果存在，更新
            analysis.setNumRegistered(numRegistered);
            analysis.setNumLogin(numLogin);
            analysis.setNumActive(numActive);
            analysis.setNumRetention1d(numRetention1d);
            analysisMapper.updateById(analysis);
        }
    }

    /**
     * 查询活跃用户的数量
     */
    public Long queryActiveUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_active");
    }

    /**
     * 查询注册用户的数量
     */
    public Long queryRegisterUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_registered");
    }

    private Long queryUserCount(DateTime today, int offset, String num) {
        return analysisMapper.queryCount(today, offset, num);
    }

    /**
     * 查询登录用户的数量
     */
    public Long queryLoginUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_login");
    }

    private Long queryAnalysisCount(String column,String today,String offset){
        return analysisMapper.sumAnalysisData(column,offset,today);
    }
}

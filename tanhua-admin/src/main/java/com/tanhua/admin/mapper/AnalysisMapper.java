package com.tanhua.admin.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouhao.admin.Analysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnalysisMapper extends BaseMapper<Analysis> {
    @Select("select sum(${column}) from tb_analysis where record_date > #{leDate} and record_date < #{gtDate}")
    Long sumAnalysisData(@Param("column") String column, @Param("leDate") String leDate, @Param("gtDate") String gtDate);

    @Select("select count(${num}) from tb_analysis where created <= #{today}")
    Long queryCount(DateTime today, int offset, String num);
}

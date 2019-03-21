package com.example.demo.mapper;

import com.example.demo.entity.Happiness;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

/**
 * Created by libin on 2019/3/21.
 */
@Mapper
public interface HappinessDao {
    Happiness findHappinessByCity(String city);
    int insertHappiness(HashMap<String, Object> map);
}


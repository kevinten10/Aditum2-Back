package com.ten.aditum2.back.mapper;

import com.ten.aditum2.back.model.entity.DeviceAccessTotal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceAccessTotalDao {

    int insert(@Param("pojo") DeviceAccessTotal pojo);

    int insertList(@Param("pojos") List<DeviceAccessTotal> pojo);

    List<DeviceAccessTotal> select(@Param("pojo") DeviceAccessTotal pojo);

    int update(@Param("pojo") DeviceAccessTotal pojo);

}

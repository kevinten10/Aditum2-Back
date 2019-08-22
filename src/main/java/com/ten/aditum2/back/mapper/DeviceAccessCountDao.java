package com.ten.aditum2.back.mapper;

import com.ten.aditum2.back.entity.DeviceAccessCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceAccessCountDao {

    int insert(@Param("pojo") DeviceAccessCount pojo);

    int insertList(@Param("pojos") List<DeviceAccessCount> pojo);

    List<DeviceAccessCount> select(@Param("pojo") DeviceAccessCount pojo);

    int update(@Param("pojo") DeviceAccessCount pojo);

}

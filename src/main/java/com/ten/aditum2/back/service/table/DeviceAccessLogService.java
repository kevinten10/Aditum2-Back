package com.ten.aditum2.back.service.table;

import com.ten.aditum2.back.model.entity.DeviceAccessLog;
import com.ten.aditum2.back.mapper.DeviceAccessLogDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeviceAccessLogService {

    @Resource
    private DeviceAccessLogDao deviceAccessLogDao;

    public int insert(DeviceAccessLog pojo) {
        return deviceAccessLogDao.insert(pojo);
    }

    public int insertList(List<DeviceAccessLog> pojos) {
        return deviceAccessLogDao.insertList(pojos);
    }

    public List<DeviceAccessLog> select(DeviceAccessLog pojo) {
        return deviceAccessLogDao.select(pojo);
    }

    public int update(DeviceAccessLog pojo) {
        return deviceAccessLogDao.update(pojo);
    }

}

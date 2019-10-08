package com.ten.aditum2.back.service.data;

import com.ten.aditum2.back.model.entity.Device;
import com.ten.aditum2.back.mapper.DeviceDao;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.util.TimeGenerator;
import com.ten.aditum2.back.util.UidGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeviceService {

    @Resource
    private DeviceDao deviceDao;

    public int insert(Device pojo) {
        log.debug("Device [POST] : {}", vo);
        Device entity = new Device()
                .setImei(UidGenerator.generateUid())
                .setAlias(vo.getAlias())
                .setCommunityId(vo.getCommunityId())
                .setDeviceStatus(0)
                .setCreateTime(TimeGenerator.currentTime())
                .setUpdateTime(TimeGenerator.currentTime())
                .setIsDeleted(NO_DELETED);

        int result = deviceService.insert(entity);
        if (result < 1) {
            log.warn("Device [POST] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Device [POST] SUCCESS : {}", entity);
        return new ResultModel(AditumCode.OK);
    }

    public int insertList(List<Device> pojos) {
        return deviceDao.insertList(pojos);
    }

    public List<Device> select(Device pojo) {
        log.debug("Device [GET] : {}", vo);
        List<Device> deviceList = deviceService.select(vo);
        if (deviceList == null) {
            log.warn("Device [GET] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Device [GET] SUCCESS : {} -> {}", vo, deviceList);
        return new ResultModel(AditumCode.OK, deviceList);
    }

    public int update(Device pojo) {
        return deviceDao.update(pojo);
    }

}

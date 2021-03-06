package com.ten.aditum2.back.route.controller.table;

import com.ten.aditum2.back.model.entity.*;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.service.table.DeviceAccessCountService;
import com.ten.aditum2.back.service.table.DeviceAccessHeatService;
import com.ten.aditum2.back.service.table.DeviceAccessLogService;
import com.ten.aditum2.back.service.table.DeviceAccessTotalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/access/device")
public class AccessDeviceController {

    private static final int NO_DELETED = 0;
    private static final int IS_DELETED = 1;

    private final DeviceAccessCountService deviceAccessCountService;
    private final DeviceAccessHeatService deviceAccessHeatService;
    private final DeviceAccessLogService deviceAccessLogService;
    private final DeviceAccessTotalService deviceAccessTotalService;

    @Autowired
    public AccessDeviceController(DeviceAccessCountService deviceAccessCountService,
                                  DeviceAccessHeatService deviceAccessHeatService,
                                  DeviceAccessLogService deviceAccessLogService,
                                  DeviceAccessTotalService deviceAccessTotalService) {
        this.deviceAccessCountService = deviceAccessCountService;
        this.deviceAccessHeatService = deviceAccessHeatService;
        this.deviceAccessLogService = deviceAccessLogService;
        this.deviceAccessTotalService = deviceAccessTotalService;
    }

    /**
     * 根据IMEI获取设备的所有访问记录
     */
    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public ResultModel getLog(Device device) {
        log.debug("AccessDeviceLog [GET] : {}", device);

        if (device.getImei() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        DeviceAccessLog logEntity = new DeviceAccessLog()
                .setImei(device.getImei())
                .setIsDeleted(NO_DELETED);

        List<DeviceAccessLog> deviceAccessLogs = deviceAccessLogService.select(logEntity);
        if (deviceAccessLogs.size() < 1) {
            log.warn("AccessDeviceLog [GET] FAILURE : {}", device);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("AccessDeviceLog [GET] SUCCESS : {} -> {}", device, deviceAccessLogs);
        return new ResultModel(AditumCode.OK, deviceAccessLogs);
    }

    /**
     * 根据IMEI获取设备最近二十四小时的访问热度
     */
    @RequestMapping(value = "/heat", method = RequestMethod.GET)
    public ResultModel getHeat(Device device) {
        log.debug("AccessDeviceHeat [GET] : {}", device);

        if (device.getImei() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        DeviceAccessHeat heatEntity = new DeviceAccessHeat()
                .setImei(device.getImei())
                .setIsDeleted(NO_DELETED);

        List<DeviceAccessHeat> deviceAccessHeats = deviceAccessHeatService.select(heatEntity);
        if (deviceAccessHeats.size() < 1) {
            log.warn("AccessDeviceHeat [GET] FAILURE : {}", device);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("AccessDeviceHeat [GET] SUCCESS : {} -> {}", device, deviceAccessHeats);

        // 获取最近二十四条
        List<DeviceAccessHeat> deviceAccessHeatList = deviceAccessHeats.stream()
                .sorted(((o1, o2) -> o2.getCurrentHourTime().compareTo(o1.getCurrentHourTime())))
                .collect(Collectors.toList());

        if (deviceAccessHeatList.size() > 24) {
            deviceAccessHeatList = deviceAccessHeatList.subList(0, 24);
        }

        return new ResultModel(AditumCode.OK, deviceAccessHeatList);
    }

    /**
     * 根据IMEI获取设备按天计算的访问热度(最近三十天)
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResultModel getCountHeat(DeviceAccessCount device) {
        log.debug("AccessDeviceCount [GET] : {}", device);

        if (device.getImei() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        DeviceAccessCount countEntity = new DeviceAccessCount()
                .setImei(device.getImei())
                .setIsDeleted(NO_DELETED);

        List<DeviceAccessCount> deviceAccessCounts = deviceAccessCountService.select(countEntity);
        if (deviceAccessCounts.size() < 1) {
            log.warn("AccessDeviceCount [GET] FAILURE : {}", device);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("AccessDeviceCount [GET] SUCCESS : {} -> {}", device, deviceAccessCounts);

        // 获取最近三十天
        List<DeviceAccessCount> deviceAccessCountList = deviceAccessCounts.stream()
                .sorted(((o1, o2) -> o2.getLogDate().compareTo(o1.getLogDate())))
                .collect(Collectors.toList());

        if (deviceAccessCountList.size() > 30) {
            deviceAccessCountList = deviceAccessCountList.subList(0, 30);
        }


        return new ResultModel(AditumCode.OK, deviceAccessCountList);
    }

    /**
     * 根据日期logDate获取此日期所有的count热度信息
     */
    @RequestMapping(value = "/countByDay", method = RequestMethod.GET)
    public ResultModel getCountByDay(DeviceAccessCount device) {
        log.debug("AccessDeviceCount ByDay [GET] : {}", device);

        if (device.getLogDate() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        device.setIsDeleted(NO_DELETED);

        List<DeviceAccessCount> deviceAccessCounts = deviceAccessCountService.select(device);
        if (deviceAccessCounts.size() < 1) {
            log.warn("AccessDeviceCount ByDay [GET] FAILURE : {}", device);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("AccessDeviceCount ByDay [GET] SUCCESS : {} -> {}", device, deviceAccessCounts);
        return new ResultModel(AditumCode.OK, deviceAccessCounts);
    }

    /**
     * 根据IMEI获取设备总访问次数和总使用天数
     */
    @RequestMapping(value = "/total", method = RequestMethod.GET)
    public ResultModel getTotal(Device device) {
        log.debug("AccessDeviceTotal [GET] : {}", device);

        if (device.getImei() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        DeviceAccessTotal totalEntity = new DeviceAccessTotal()
                .setImei(device.getImei())
                .setIsDeleted(NO_DELETED);

        List<DeviceAccessTotal> deviceAccessTotals = deviceAccessTotalService.select(totalEntity);
        if (deviceAccessTotals.size() < 1) {
            log.warn("AccessDeviceTotal [GET] FAILURE : {}", device);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("AccessDeviceTotal [GET] SUCCESS : {} -> {}", device, deviceAccessTotals.get(0));
        return new ResultModel(AditumCode.OK, deviceAccessTotals.get(0));
    }

}

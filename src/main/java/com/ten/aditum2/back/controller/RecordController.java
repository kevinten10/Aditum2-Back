package com.ten.aditum2.back.controller;

import com.ten.aditum2.back.entity.Record;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.service.RecordService;
import com.ten.aditum2.back.util.TimeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/record")
public class RecordController extends BaseController<Record> {

    private final RecordService service;

    @Autowired
    public RecordController(RecordService service) {
        this.service = service;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public ResultModel get(Record record) {
        log.debug("Record [GET] : {}", record);
        List<Record> recordList = service.select(record);
        if (recordList == null) {
            log.warn("Record [GET] FAILURE : {}", record);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Record [GET] SUCCESS : {} -> {}", record, recordList);
        return new ResultModel(AditumCode.OK, recordList);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public ResultModel post(@RequestBody Record record) {
        log.debug("Record [POST] : {}", record);
        Record entity = new Record()
                .setImei(record.getImei())
                .setPersonnelId(record.getPersonnelId())
                .setVisiteTime(TimeGenerator.currentTime())
                .setVisiteStatus(record.getVisiteStatus())
                .setIsDeleted(NO_DELETED);

        int result = service.insert(entity);
        if (result < 1) {
            log.warn("Record [POST] FAILURE : {}", record);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("New record : " + entity);

        log.debug("Record [POST] SUCCESS : {}", entity);
        return new ResultModel(AditumCode.OK);
    }

    @Override
    public ResultModel update(Record record) {
        return null;
    }

    @Override
    public ResultModel delete(Record record) {
        return null;
    }
}

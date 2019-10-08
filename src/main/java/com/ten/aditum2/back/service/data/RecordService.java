package com.ten.aditum2.back.service.data;

import com.ten.aditum2.back.model.entity.Record;
import com.ten.aditum2.back.mapper.RecordDao;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.util.TimeGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RecordService {

    @Resource
    private RecordDao recordDao;

    public int insert(Record pojo) {
        log.debug("Record [POST] : {}", vo);
        Record entity = new Record()
                .setImei(vo.getImei())
                .setPersonnelId(vo.getPersonnelId())
                .setVisiteTime(TimeGenerator.currentTime())
                .setVisiteStatus(vo.getVisiteStatus())
                .setIsDeleted(NO_DELETED);

        int result = service.insert(entity);
        if (result < 1) {
            log.warn("Record [POST] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }

        log.debug("New record : " + entity);

        log.debug("Record [POST] SUCCESS : {}", entity);
        return new ResultModel(AditumCode.OK);
    }

    public int insertList(List<Record> pojos) {
        return recordDao.insertList(pojos);
    }

    public List<Record> select(Record pojo) {
        log.debug("Record [GET] : {}", vo);
        List<Record> recordList = service.select(vo);
        if (recordList == null) {
            log.warn("Record [GET] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Record [GET] SUCCESS : {} -> {}", vo, recordList);
        return new ResultModel(AditumCode.OK, recordList);
    }

    /**
     * 获取数量
     */
    public int selectCount(Record pojo) {
        return recordDao.selectCount(pojo);
    }

    /**
     * 获取某段时间内的数量
     */
    public int selectCountBetweenDateTime(Record pojo, String startTime, String endTime) {
        return recordDao.selectCountBetweenDateTime(pojo, startTime, endTime);
    }

    /**
     * 获取某段时间内的数量
     */
    public int selectCountAfterDateTime(Record pojo, String startTime) {
        return recordDao.selectCountAfterDateTime(pojo, startTime);
    }

    /**
     * 获取此ID之后的数据
     */
    public List<Record> selectAfterTheId(Record pojo) {
        if (pojo.getId() == null) {
            return null;
        }
        return recordDao.selectAfterTheId(pojo);
    }

    /**
     * 当前时间对应的时刻之后的数据
     */
    public List<Record> selectAfterTheDateTime(Record pojo) {
        if (pojo.getVisiteTime() == null) {
            return null;
        }
        return recordDao.selectAfterTheVisitTime(pojo);
    }

    public int update(Record pojo) {
        return recordDao.update(pojo);
    }

}

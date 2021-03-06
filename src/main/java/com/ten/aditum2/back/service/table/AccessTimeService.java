package com.ten.aditum2.back.service.table;

import com.ten.aditum2.back.model.entity.AccessTime;
import com.ten.aditum2.back.mapper.AccessTimeDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AccessTimeService {

    @Resource
    private AccessTimeDao accessTimeDao;

    public int insert(AccessTime pojo) {
        return accessTimeDao.insert(pojo);
    }

    public int insertList(List<AccessTime> pojos) {
        return accessTimeDao.insertList(pojos);
    }

    public List<AccessTime> select(AccessTime pojo) {
        return accessTimeDao.select(pojo);
    }

    public int update(AccessTime pojo) {
        return accessTimeDao.update(pojo);
    }

}

package com.ten.aditum2.back.service.data;

import com.ten.aditum2.back.model.entity.Person;
import com.ten.aditum2.back.mapper.PersonDao;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.util.TimeGenerator;
import com.ten.aditum2.back.util.UidGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PersonService {

    @Resource
    private PersonDao personDao;

    public int insert(Person pojo) {
        log.debug("Person [POST] : {}", vo);
        Person entity = new Person()
                .setPersonnelId(UidGenerator.generateUid())
                .setPersonnelName(vo.getPersonnelName())
                .setCommunityId(vo.getCommunityId())
                .setPersonnelAddress(vo.getPersonnelAddress())
                .setPersonnelPhone(vo.getPersonnelPhone())
                .setCreateTime(TimeGenerator.currentTime())
                .setUpdateTime(TimeGenerator.currentTime())
                .setIsDeleted(NO_DELETED);

        int result = service.insert(entity);
        if (result < 1) {
            log.warn("Person [POST] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Person [POST] SUCCESS : {}", entity);
        return new ResultModel(AditumCode.OK);
    }

    public int insertList(List<Person> pojos) {
        return personDao.insertList(pojos);
    }

    public List<Person> select(Person pojo) {
        log.debug("Person [GET] : {}", vo);
        List<Person> personList = service.select(vo);
        if (personList == null) {
            log.warn("Person [GET] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Person [GET] SUCCESS : {} -> {}", vo, personList);
        return new ResultModel(AditumCode.OK, personList);
    }

    public int update(Person pojo) {
        return personDao.update(pojo);
    }

}

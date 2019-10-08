package com.ten.aditum2.back.service.table;

import com.ten.aditum2.back.model.entity.PersonasPortrait;
import com.ten.aditum2.back.mapper.PersonasPortraitDao;
import com.ten.aditum2.back.util.TimeGenerator;
import com.ten.aditum2.back.model.vo.Personas;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PersonasPortraitService {

    @Resource
    private PersonasPortraitDao personasPortraitDao;

    public int insert(PersonasPortrait pojo) {
        return personasPortraitDao.insert(pojo);
    }

    public int insertList(List<PersonasPortrait> pojos) {
        return personasPortraitDao.insertList(pojos);
    }

    public List<PersonasPortrait> select(PersonasPortrait pojo) {
        return personasPortraitDao.select(pojo);
    }

    public int update(PersonasPortrait pojo) {
        return personasPortraitDao.update(pojo);
    }

    /**
     * 根据标签创建或者更新用户画像
     */
    private void createOrUpdatePortrait(Personas personas,
                                        List<String> labelList,
                                        boolean existed,
                                        int portraitId) {
        if (existed) {
            PersonasPortrait update = new PersonasPortrait()
                    .setId(portraitId)
                    .setPersonasExt(String.join(",", labelList))
                    .setUpdateTime(TimeGenerator.currentTime());
            update(update);
        }
        // 未存在，创建
        else {
            PersonasPortrait create = new PersonasPortrait()
                    .setPersonnelId(personas.getPersonnelId())
                    .setPersonasExt(String.join(",", labelList))
                    .setCreateTime(TimeGenerator.currentTime())
                    .setIsDeleted(0);
            insert(create);
        }
    }
}

package com.ten.aditum2.back.service.data;

import com.ten.aditum2.back.model.entity.Community;
import com.ten.aditum2.back.mapper.CommunityDao;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.util.TimeGenerator;
import com.ten.aditum2.back.util.UidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Community reactive data service
 *
 * @author shihaowang
 * @date 2019/10/7
 */
@Slf4j
@Service
public class CommunityService {

    @Resource
    private CommunityDao communityDao;

    public int insert(Community pojo) {
        log.debug("Community [POST] : {}", vo);
        Community entity = new Community()
                .setCommunityId(UidGenerator.generateUid())
                .setCommunityName(vo.getCommunityName())
                .setCommunityCity(vo.getCommunityCity())
                .setCommunityAddress(vo.getCommunityAddress())
                .setDeviceCount(NO_DELETED)
                .setDeviceOnlineCount(NO_DELETED)
                .setCreateTime(TimeGenerator.currentTime())
                .setUpdateTime(TimeGenerator.currentTime())
                .setIsDeleted(NO_DELETED);

        int result = service.insert(entity);
        if (result < 1) {
            log.warn("Community [POST] FAILURE : {}", vo);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Community [POST] SUCCESS : {}", entity);
        return new ResultModel(AditumCode.OK);
    }

    public int insertList(List<Community> pojos) {
        return communityDao.insertList(pojos);
    }

    public List<Community> select(Community community) {
        log.debug("Community [GET] : {}", community);
        List<Community> communityList = communityDao.select(community);
        if (communityList == null) {
            log.warn("Community [GET] FAILURE : {}", community);
            return new ResultModel(AditumCode.ERROR);
        }
        log.debug("Community [GET] SUCCESS : {} -> {}", vo, communityList);
        return new ResultModel(AditumCode.OK, communityList);
    }

    public int update(Community pojo) {
        return communityDao.update(pojo);
    }

}

package com.van.serviceorder.dao;

import com.van.serviceorder.entity.LocalEvent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LocalEventDao {
    int deleteByPrimaryKey(String id);

    int insert(LocalEvent record);

    int insertSelective(LocalEvent record);

    LocalEvent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(LocalEvent record);

    int updateByPrimaryKey(LocalEvent record);

    int updateStatus(String id, int status);

    List<LocalEvent> selectAllUnSendEvent();
}
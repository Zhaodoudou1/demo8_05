package com.zdd.login.dao;

import com.zdd.pojo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<UserInfo,Integer> {
    @Query(value = "SELECT * FROM base_user u WHERE u.`loginName` = ?1",nativeQuery = true)
    public UserInfo findAllByUserName(String loginName);
}

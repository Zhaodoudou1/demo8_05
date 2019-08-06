package com.zdd.login.server;

import com.zdd.login.dao.UserDao;
import com.zdd.pojo.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServer {

    @Autowired
    private UserDao userDao;

    public UserInfo findUserByName(String loginName){
        UserInfo allByUserName = userDao.findAllByUserName(loginName);
        return allByUserName;
    }
}

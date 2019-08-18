import com.zdd.common.util.MD5;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
@RunWith(SpringRunner.class)

public class Test {



    /*@org.junit.Test
    public void test(){
        String log = MD5.encryptPassword("1234567", "lcg");
        System.out.println("加密后的"+log);

        HashMap<String, Object> map = new HashMap<>();

        map.put("password","1234567");

        String s = MD5.encryptPassword(map.get("password").toString(), "lcg");
        System.out.println("解密后:"+s);

    }*/



   @Autowired
    private RedisTemplate redisTemplate;
    @org.junit.Test
    public void redisLoginDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前时间
        String format = sdf.format(new Date());
        //自增1
        redisTemplate.opsForHash().increment("number",format,1l);
        redisTemplate.opsForList().leftPush("date",format);
    }

}

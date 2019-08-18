package com.zdd.business.config;

import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    public static void sendEmail(String email,String code) throws AddressException, MessagingException {

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", "smtp.qq.com");// 主机名
        properties.put("mail.smtp.port", 465);// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接
        properties.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
// 得到回话对象
        Session session = Session.getInstance(properties);
// 获取邮件对象
        Message message = new MimeMessage(session);
// 设置发件人邮箱地址
        message.setFrom(new InternetAddress("953920730@qq.com"));
// 设置收件人地址
        message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(email) });
// 设置邮件标题
        message.setSubject("找回密码验证");
// 设置邮件内容
        message.setText(code);
// 得到邮差对象
        Transport transport = session.getTransport();
// 连接自己的邮箱账户
        transport.connect("953920730@qq.com", "vpayblqcusbwbedh");// 密码为刚才得到的授权码
// 发送邮件
        transport.sendMessage(message, message.getAllRecipients());
    }




}

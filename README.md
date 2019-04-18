# Spring-Oauth2-Demo
1.token存储在redis中，需要先安装redis
2.使用mysql数据库，需要安装mysql，并执行resource db下 rbac_db.sql文件
3.使用mybatis
4.运行demo中oauth-server，oauth-resource-server
5.测试密码登陆模式
获取token
http://localhost:8081/oauth/token?username=user_1&password=123456&grant_type=password&scope=select&client_id=client_2&client_secret=123456
获取受保护资源
http://localhost:8080/user/hello?access_token=1c15831c-2c38-4b89-acdd-23b515bc4070

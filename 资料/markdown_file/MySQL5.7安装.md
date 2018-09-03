# CentOS 6.5/6.6 安装（install）mysql 5.7  
<p><br /> </p>  
##一：安装mysql  In CentOS6.5 / 7.0
**Step1:** 检测系统是否自带安装mysql 
 
`# yum list installed | grep mysql`  

**Step2:** 删除系统自带的mysql及其依赖  
命令：

`# yum -y remove mysql-libs.x86_64`  

**Step3:** 给CentOS添加rpm源，并且选择较新的源
命令：

`# wget dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm`      
`# yum localinstall mysql-community-release-el6-5.noarch.rpm`     
`# yum repolist all | grep mysql`   
`# yum-config-manager --disable mysql55-community`      
`# yum-config-manager --disable mysql56-community`   
`# yum-config-manager --enable mysql57-community-dmr`   
`# yum repolist enabled | grep mysql` 

**Step4:** 安装mysql 服务器   
命令：

`# yum install mysql-community-server`
  
**Step5:** 启动mysql  
命令:

`# service mysqld start`
  
**Step6:** 查看mysql是否自启动,并且设置开启自启动  
命令:

`# chkconfig --list | grep mysqld`
`# chkconfig mysqld on`
  
**Step7:** mysql安全设置  
命令：

`# mysql_secure_installation`   
<p><br /> </p>
##二：下面介绍下 Linux MySQL的几个重要目录  
1. 数据库目录  
/var/lib/mysql/
2. 配置文件  
/usr/share /mysql（mysql.server命令及配置文件）
3. 相关命令  
/usr/bin（mysqladmin mysqldump等命令）
4. 启动脚本  
/etc/rc.d/init.d/（启动脚本文件mysql的目录）
<p><br /> </p>
##三：Linux MySQL修改登录密码  
命令初始化root账户密码 

`mysql -u root -p,出现enter password`  
  
 
下面我们查看MySQL中root的默认密码并修改

**MySQL 5.7以上 root用户默认密码问题**  

在安装的过程里，将仅创建一个root账户——'root'@'localhost'，同时将自动生成一个随机密码给它，并标记此密码已过期。MySQL管理员必须使用随机密码登陆root账户，并使用SET PASSWORD去设置一个新的密码。（随机密码可以在运行安装脚本的有效用户其主目录中的.mysql_secret文件中找到。）安装时不创建匿名用户账户。 安装时不创建测试数据库。    
其实MySQL在运行的时候会有一个日志文件，它存在于/var/log/mysqld.log，我们在安装时生成的随机密码就在这个文件里，我们可以直接用下面这条命令显示出这个文件里的所有内容：

`cat /var/log/mysqld.log`

我们这里可以用个更方便的命令可以找到密码：

`grep "temporary password" /var/log/mysqld.log`  

怎么样，现在密码是不是很清楚了？被设置为了“m6qVLN8kYQ?d”。

现在就可以用随机密码登陆MySQL控制台了。


现在好不容易进入了控制台，接下来要做的就是修改密码了，毕竟这么一串密码谁都记不住吧？设置新密码的命令是：

`SET PASSWORD = PASSWORD('你的新密码');`

不过需要注意的是现在MySQL已经强制要求强密码，已经不能再用弱密码比如“123456”了。如果你设置的密码过于简单，会提示错误：

ERROR 1819 (HY000): Your password does not satisfy the current policy requirements
<p><br /> </p>



<p><br /> </p>
<p><br /> </p>
参考相关文档地址:  
[MySQL 5.7 安装](https://segmentfault.com/a/1190000003049498)  
[MySQL 5.7.15 安装最完整版教程](https://my.oschina.net/dyyweb/blog/737200)  
[MySQL 5.7 以上 root用户默认密码问题](https://www.cnblogs.com/arraylist/p/6726614.html)


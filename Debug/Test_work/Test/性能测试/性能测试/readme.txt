
e.g.:
java -jar OBWriteTest.jar config.txt

config.txt中的配置项说明见obwrite1.txt示例

可测数据库：MySQL|PostgreSQL|Cedar

支持的测试任务：
insert|update|replace|select|delete等，见示例配置文件

###分隔符是用来支持分组测试的

测试程序会自动统计吞吐和时延

测试前先建一个数据表

create table t1 (c2 int primary key, c2 int, c3 varchar(100));

使用中如有疑问，可咨询张小磊

源码见zip文件
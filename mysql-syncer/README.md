## 工程简介

后端使用 SpringBoot+Mybatis 实现,能够简单实现数据库**结构**同步

### 实现

系统分层

* 基础
    * 常用的工具类
        * ListUtil
    * 封装各种 db 层的操作,主要是在 DaoFacade 内
        * show create database
        * show create table
        * exec sql (单条/批量)
* 框架
    * 核心能力
        * table diff
        * database diff
        * instance diff
    * 扩展能力
        * 基于 diff 结果生成 sql
        * 输出差异信息进行告警
* 工具
    * 就是抽象出来一个场景的使用。比如：线下环境有多套,每个人在执行测试修改数据结构需要人工传递消息
    * 工具的目的是去做对比分析,并执行差异结构的同步
    * controller
        * /sync/instance
        * /sync/database
        * /sync/table
* 服务
    * 对外提供的一种或者几种能力的集合
    * 除了刚提到的工具之外,将与我们的框架能力进行最大化扩展和设计
    * 在框架层面向外输出对应能力
    * 其实就是理解提供口子给到外部进行扩展,外部调用的其他人可以尽心重新扩展其功能
        * 比如说 sql 输出之后,是直接执行还是告警或者是其他的功能
        * 基于 schedule 配置,一个源,多个目的,周期时间内,进行自动化对比。为每个目的进行配置加提醒用户,有对比到差异则发送邮件提醒用户并且带上执行的链接进行确认,确认完毕之后再进行执行
* 需求
    * 指定库的同步
    * 指定同步结果的输出方式
        * 生成 sql 预览
        * 报警提示
        * 定时周期运行,并最终以任务形式给到用户确认
        * ……

实际分层
* sync-common:基础层,比如 utils 等
* sync-framework：框架层
* sync-service：服务层


* 系统架构

  ![img.png](https://gitee.com/xiaowenshu7/devPractice/raw/master/mysql-schema-sync/img.png)

## 功能

### 已实现功能列表

* 实例级别同步
    * 默认支持两个实例的所有库进行同步,已排除 mysql 的基本库：'information_schema', 'mysql', 'performance_schema', 'sys'
    * 支持自定义的排除同步列表
* 库级别同步
    * 默认支持两个库内的所有表进行同步
    * 支持自定义库内的表进行排除同步
* 表级别同步
    * 先同步字段。只会对目标数据表进行新增列操作,不会进行删减列操作
    * 再同步索引

### 待实现功能列表

* 前端页面实现
* 支持异步处理,先生成 sql 再执行 sql,同步操作容易阻塞

## 延伸阅读

### 问题
1. 设计到实现到应用使用了多久
* 发现问题,结合现状,收集需求,调研,整合方案等。大概一个月
* 编码的时间大概两周时间,写和拆
* 测试 1-2 天
* 推广使用

2. 编写以及实施遇到哪些问题？
* 建表语句 diff 出来的 sql 没问题有问题但是执行出来的表 ddl 中文字符有问题。因为 url 没有加字符集编码`useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC` 
    * 如果用户输入的 url 内有 `?` 后面这部分,则直接使用
    * 如果输入的部分没有则程序默认追加
* 程序运行过程中,发现有时候对比出来索引去执行会报字段不存在
    * 说明需要先 diff 出 column 不一样,而且要执行一下否则去执行索引会报列不存在
* 线下库做了分库分表,执行耗时。库有 128 个,每个库有很多表。执行起来会很慢,在执行 sql 语句这一步就加了线性池优化
* 还有就是数据库连接池的优化,之前是来个池就去连,后来我们根据连接信息去重则不会有多余的连接
    * 参见 LocalDataSourceFactory 的 `newConcurrentMap` 缓存优化

3. 提升了多少效率,节省多少人力
* controller 内可以打上 log,这个接口的 qps 可以塞到 redis内,接口的调用次数等都可以被统计出来,这样肯定就能统计到时间
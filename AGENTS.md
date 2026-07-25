# AGENTS.md

## 项目定位

这是用于复习传统 Spring Boot 分层开发和学习 Spring AI 的练习项目。

## 开发原则

- 保持 Controller、Service、Mapper、Entity 的传统分层。
- 第一版使用 MyBatis-Plus、MySQL、HttpSession 和 Spring AI `ChatClient`。
- 非必要不引入 Spring Security、JWT、Redis、消息队列、微服务等额外组件。
- 类名、方法名和变量名可使用英文或拼音，不强制统一风格；同一业务概念保持一致。
- 先完成简单、可运行、可理解的实现，再考虑扩展。
- 本项目不使用Git相关流程。

## 操作约束

- 默认只检查、讲解和给出代码示例。
- 未获得用户明确授权，不直接修改源码、配置或IDE文件，不运行、编译或测试项目。
- 用户明确要求修改文档时，只修改指定文档范围。
- 不把密码、API Key等秘密写入文档、日志或对外输出。

## 环境

- Windows + IntelliJ IDEA。
- JDK 17。
- Maven Wrapper。
- Docker Compose运行MySQL。
- 不混用WSL与Windows的Java、Maven和虚拟环境。

## 设计文档

详细设计位于 `docs/`：

1. `1-技术版本基线.md`
2. `2-开发总览.md`
3. `3-项目总需求.md`
4. `4-实体类与数据库设计.md`
5. `5-接口设计.md`
6. `6-后端公共机制.md`
7. `7-人工智能对话逻辑设计.md`

# AI记录助手

传统 Spring Boot + Spring AI 学习项目。

## 运行

1. 启动 Docker Desktop。
2. 在项目根目录启动MySQL和Redis：

```powershell
docker compose up -d
```

3. 使用 IntelliJ IDEA 打开项目，选择 JDK 17，等待Maven依赖加载完成。
4. 确认 `application.yaml` 中的MySQL、Redis和AI配置可用。
5. 在IDEA中运行 `linggu.JiluzhushouApplication`。

应用地址：`http://localhost:8080`

停止MySQL和Redis容器：

```powershell
docker compose down
```

MySQL和Redis数据保存在Docker卷中，执行 `down` 不会删除数据。

开发环境中的MySQL和Redis端口均绑定到 `127.0.0.1`，只允许本机访问：

```text
MySQL：127.0.0.1:3306
Redis：127.0.0.1:6379
```

详细设计见 [docs/1.开发总览.md](./docs/1.开发总览.md)，当前进度和问题见 [docs/开发过程.md](./docs/开发过程.md)。

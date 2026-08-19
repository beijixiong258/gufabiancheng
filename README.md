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
5. 首次运行前，在项目根目录执行 `mvnw.cmd generate-resources` 构建前端资源；完整打包使用 `mvnw.cmd clean package`。
6. 在IDEA中运行 `linggu.JiluzhushouApplication`。

应用地址：`http://localhost:8080`

## 单 Jar 构建

前端源码位于 `src/main/frontend`。执行：

```powershell
mvnw.cmd clean package
```

Maven 会自动执行前端的 `npm ci` 和 `npm run build`，并将构建产物装入 Spring Boot Jar。打包完成后只需运行：

```powershell
java -jar target/jiluzhushou-0.0.1-SNAPSHOT.jar
```

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

详细设计见 [docs/1.开发总览.md](./docs/1.开发总览.md)，接口见 [docs/3.接口设计.md](./docs/3.接口设计.md)，当前进度和问题见 [docs/开发过程.md](./docs/开发过程.md)。

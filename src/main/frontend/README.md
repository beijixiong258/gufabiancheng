# 北极熊记录助手 · 前端

Vue 3 + Vite + Vue Router + Pinia + Axios + Element Plus 前端，源码位于 `src/main/frontend`，由 Maven 与 Spring Boot 一起打包为单个 Jar。

## 开发

1. 先启动 MySQL、Redis 和 Spring Boot 后端，后端端口为 8080。
2. 在本目录执行：

```powershell
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。Vite 会把 `/api` 请求代理到 `http://localhost:8080`。

前端只注册当前页面实际使用的 Element Plus 组件和指令，避免从全量入口引入全部组件 JavaScript；主题 CSS 仍统一引入。

## 构建

通常直接在项目根目录执行：

```powershell
.\mvnw.cmd clean package
```

Maven 会在 `generate-resources` 阶段依次执行：

```powershell
npm ci
npm run build
```

Vite 产物输出到项目根目录的 `target/classes/static`，随后由 Spring Boot Maven 插件装入 Jar。运行 Jar 后，前端页面和 `/api` 后端接口使用同一个 8080 端口，不需要单独部署静态服务器，也不需要配置 CORS。生产构建当前会把 Element Plus JavaScript 拆为约 360 KB 的按需组件块；具体文件名由 Vite 每次构建生成。

## 路由

前端使用 HTML5 History 路由。Spring Boot 的 `QianDuanController` 会把 `/` 和 `/login` 转发到 `index.html`，因此直接访问或刷新登录页仍可加载前端应用。

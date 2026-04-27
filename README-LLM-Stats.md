# LLM 与 Stats 运行说明

本文档说明如何在本项目中运行并验证以下功能：
- LLM 查询（前端 -> Java -> Flask）
- 统计模块 Stats（前端 -> Java -> MySQL）

## 1. 目录与模块关系

- Java 后端：src/main/java
- Java 配置文件：src/main/resources/application.yml
- Flask LLM 服务：app.py
- Flask LLM 逻辑：llm_service.py
- 前端：lostandfound-frontend

调用链路：
- LLM：前端 /api/llm-query -> Java /api/llm-query -> Flask /llm-query -> MySQL item 表
- Stats：前端 /api/stats/* -> Java /api/stats/* -> MySQL

## 2. 环境要求

建议版本：
- Python 3.11+
- Java 21
- Node.js 18+（已验证 Node 24 可用）
- MySQL 8.x

## 3. 配置说明（统一读取）

核心配置文件：src/main/resources/application.yml

当前与 LLM/Stats 相关配置：

- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- server.port
- llm.service.port
- llm.openai.api-key
- llm.openai.model
- llm.proxy.url
- llm.proxy.connect-timeout-ms
- llm.proxy.read-timeout-ms

说明：
- Python 端（app.py / llm_service.py）会从同一个 application.yml 读取 MySQL 与 LLM 配置。
- 如需自定义配置文件路径，可设置环境变量 APP_CONFIG_PATH。
- llm.openai.api-key 为空时，系统会自动走规则 SQL 生成（不调用 OpenAI）。

## 4. 首次安装依赖

在项目根目录运行：

Windows PowerShell

python -m pip install -r requirements-llm.txt

在前端目录运行：

cd lostandfound-frontend
npm.cmd install

说明：
- 如果 PowerShell 执行策略拦截 npm，可使用 npm.cmd 代替 npm。

## 5. 启动顺序（推荐）

建议开 3 个终端。

终端 A：启动 Flask LLM 服务

cd 项目根目录
python app.py

成功后应看到类似输出：
- Running on http://127.0.0.1:5000

终端 B：启动 Java 后端

cd 项目根目录
.\mvnw.cmd spring-boot:run

成功后应看到类似输出：
- Tomcat started on port 8080

终端 C：启动前端

cd lostandfound-frontend
npm.cmd run dev

成功后应看到类似输出：
- Local: http://localhost:5173/

## 6. 功能验证

### 6.1 LLM 验证

1) 直接测 Flask 服务：

curl -X POST http://127.0.0.1:5000/llm-query -H "Content-Type: application/json" -d "{\"query\":\"在图书馆丢了书\"}"

2) 测 Java 代理接口（前端实际调用的是这个）：

curl -X POST http://127.0.0.1:8080/api/llm-query -H "Content-Type: application/json" -d "{\"query\":\"在图书馆丢了书\"}"

期望：
- 返回 code=200
- data 内含 sql、count、results

### 6.2 Stats 验证

Stats 接口：
- GET http://127.0.0.1:8080/api/stats/category
- GET http://127.0.0.1:8080/api/stats/location
- GET http://127.0.0.1:8080/api/stats/monthly
- GET http://127.0.0.1:8080/api/stats/overview

期望：
- 返回 code=200
- data 为统计结果

## 8. 常见问题

1) 前端请求失败（网络错误）
- 确认 Java 是否启动在 8080
- 前端请求基地址在 lostandfound-frontend/src/utils/request.js，默认是 http://localhost:8080

2) Java 调用 LLM 报错
- 确认 Flask 是否启动在 5000
- 确认 application.yml 中 llm.proxy.url 正确

3) Flask 报数据库连接错误
- 确认 spring.datasource 配置的 MySQL 地址、账号密码是否可用
- 确认数据库有 item 表以及相关字段

4) npm 在 PowerShell 被拦截
- 改用 npm.cmd

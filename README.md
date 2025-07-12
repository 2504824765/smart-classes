# 📚 智慧课堂（Smart-Classes）

一个面向高校与教育机构的智能教学管理平台，融合用户管理、课程管理、知识图谱、任务发布、学习分析与 AI 智能辅助等功能，全面提升教学与学习的数字化、智能化水平。

> 🌟 本项目深度集成 Dify 大语言模型，实现 AI 讲解、问答、组题、批改与能力分析等多种智能教学辅助功能。

---

## 🔧 项目功能概览

### 🧑‍🏫 多角色支持

- 学生 / 教师 / 管理员三种角色，登录后分配不同权限与功能入口

### 📘 教学管理模块

- 用户 / 课程 / 组织（院系班级）管理
- 课程任务发布（支持多种任务类型）
- 资源上传与管理（支持PPT、视频、文档等）
- 知识图谱构建与展示

### 📊 学习与分析模块

- 学生任务完成情况记录与分析
- 课程成绩趋势图 / 学习进度图
- 教师成绩管理与批量评分

### 🤖 智能模块（Dify 接入）

- AI 知识讲解与问答助手
- 自动化智能组题
- 作业智能批改与评语生成
- 学生学习能力智能分析

---

## ⚙️ 技术架构

### 前端

- Vue 3 + Vite 4 + TypeScript
- Element Plus UI 组件库
- Pinia 状态管理、Vue Router 路由
- Echarts 图表库、Unocss 样式引擎
- 模板基于 [vue-element-plus-admin](https://github.com/tookit/vue-element-admin)

### 后端

- Spring Boot 3.x + Spring Data JPA
- Java 17 + Maven
- RESTful API 接口风格
- KingbaseES 数据库
- 阿里云 OSS 对象存储

---

## 🧱 数据库设计（核心表）

| 表名                        | 描述                 |
| --------------------------- | -------------------- |
| `tb_user`                   | 用户表（账号、角色） |
| `tb_student` / `tb_teacher` | 学生 / 教师信息表    |
| `tb_class`                  | 课程信息表           |
| `tb_student_classes`        | 学生-课程关联        |
| `tb_class_mission`          | 课程任务             |
| `tb_student_mission`        | 学生任务完成情况     |
| `tb_resource`               | 教学资源表           |
| `tb_dept`                   | 组织结构（支持树状） |

> 📌 详细表结构请参考文档或实体类注释。

---

## 📈 系统亮点

- ✅ 前后端分离，接口清晰规范，易于维护与扩展
- ✅ 支持知识图谱与任务自动化关联
- ✅ 提供丰富的教学资源上传与管理功能
- ✅ 支持课程任务与学生任务评分的闭环分析
- ✅ Dify 智能模块深度集成，赋能教学效率与质量

---

## 🚀 快速启动

### 环境要求

- Node.js >= 16.x
- JDK >= 17
- Maven >= 3.8+
- KingbaseES 数据库（可修改为 MySQL）
- 配置 OSS 存储账号

### 前端运行

```bash
cd frontend
pnpm install
pnpm run dev
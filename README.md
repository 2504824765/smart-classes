# 数据库设计

学生信息

| 学号(id,long) | 姓名(name,varchar(255)) | 性别(gender,(varchar(10))) | 组织(dept_id,Integer) | 绩点(gpa, double) | username(String) |      |      |      |      |
| ------------- | ----------------------- | -------------------------- | --------------------- | ----------------- | ---------------- | ---- | ---- | ---- | ---- |
| 1             | 张三                    | 男1                        | 1                     | 5.0               |                  |      |      |      |      |

教师信息

| 工号(id,long) | 姓名(name) | 性别(gender) | 组织(dept_id) | username(String) |      |      |      |      |      |
| ------------- | ---------- | ------------ | ------------- | ---------------- | ---- | ---- | ---- | ---- | ---- |
| 1             | 李四       | 男           | 1             |                  |      |      |      |      |      |

课程信息

| 编号(id.Long) | 名字(name.String) | 教师(teacher_id.Long) | 学分(credit.double) | 学时(class_hours.double) | 图谱(graph.String)         |      |      |      |      |
| ------------- | ----------------- | --------------------- | ------------------- | ------------------------ | -------------------------- | ---- | ---- | ---- | ---- |
| 1             | web开发           | 王五                  |                     |                          | 知识图谱json文件的文件路径 |      |      |      |      |

学生课程关联

| 编号(id.Long) | 课程编号(cid.Long) | 学号(sid.Long) | 成绩(grade.double) |
| ------------- | ------------------ | -------------- | ------------------ |
| 1             | C1001              | S1001          | 100                |

组织表		

| 编号（id.Long） | 名称(name.String) | 上属部门(parent_id.Long) | 级别(department_level.Integer)                        |      |      |      |      |      |      |
| --------------- | ----------------- | ------------------------ | ----------------------------------------------------- | ---- | ---- | ---- | ---- | ---- | ---- |
| 1               | 软件2301班        | 1                        | 1(1是0的下级，级别指代的树状图的同一层级，如所有班级) |      |      |      |      |      |      |

课程学习任务

| 编号(id.Long) | 课程编号(cid.Long) | 类型(type.String)                               | 说明(description.String) | 截止时间(deadline，varchar(100)) | 提交方式(submit_method.String) | 得分(score.dounle) |      |      |      |
| ------------- | ------------------ | ----------------------------------------------- | ------------------------ | -------------------------------- | ------------------------------ | ------------------ | ---- | ---- | ---- |
| 1             | 1                  | PPT浏览/章节资料阅读/视频观看/测试答题/实践项目 | 任务说明                 | YY-MM-DD-HH-MM-SS                | 暂定                           | 每个任务的具体得分 |      |      |      |

学生学习数据

| 编号(id.Long) | 学号(sid.Long) | 待定 |      |      |      |      |      |      |      |
| :-----------: | -------------- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
|       1       | 1              |      |      |      |      |      |      |      |      |

资源表

| 编号(id.Long) | 文件名(name.String) | 路径(path.String)     | 类型(type.String)        |      |      |      |      |      |      |
| ------------- | ------------------- | --------------------- | ------------------------ | ---- | ---- | ---- | ---- | ---- | ---- |
| 1             | 陈赞写真            | 在oss对象存储库的路径 | pptx/pdf/docx/doc/mp4... |      |      |      |      |      |      |

用户表

| 编号(id.Long) | 用户名(username.String) | 密码(password.String) | 角色(role.String)                     |      |      |      |      |      |      |
| ------------- | ----------------------- | --------------------- | ------------------------------------- | ---- | ---- | ---- | ---- | ---- | ---- |
| 1             | :D                      | 111                   | student/teacher/admin(表示前端的路径) |      |      |      |      |      |      |

# 模块设计

### 前端

付 吴

### 后端

陈 王 武

信息管理模块（包括用户、课程）

​	|----用户管理模块（管理学生、教师的数据库信息）

​	|----课程管理模块（管理课程的数据库信息，）

​		|---**-知识图谱模块**

教学模块（包括知识图谱、测验、任务发布等教学相关内容）

​	|----学生端

​		|----**学习模块（记录学生学习数据）**

​		|----**分析模块（暂定只展示课程成绩）**

​	|----教师端

​		|----**发布任务模块**(包括测验的生成)

开发环境

JDK 17

IDEA 24及以上

# **接口文档**

统一前缀http://localhost:8080/api

| **说明**                                      | **方法** | **URL**                                  | **请求参数**                                                 | **返回参数**                                                 |
| --------------------------------------------- | -------- | ---------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| User                                          |          |                                          |                                                              |                                                              |
| User登陆                                      | POST     | /user/login                              | {username: String, password: String, role: String(Student/Teacher/Admin)} | bool                                                         |
| User注册（新增User）                          | POST     | /user/register                           | {username: String,password: String,role: String(Student/Teacher/Admin)} | bool                                                         |
| User更改密码                                  | POST     | /user/changePassword                     | {username: String,newPassword: String}                       | bool                                                         |
| 根据id获取User                                | GET      | /user/getUserById{id}                    | id: Long                                                     | {id: Long, username: String, role: String(Student/Teacher/Admin), imageURL: String} |
| 根据username获取User                          | GET      | /user/getUserByUsername/{username}       | username: String                                             | {id: Long, username: String, role: String(Student/Teacher/Admin), imageURL: String} |
| 获取所有User                                  | GET      | /user/all                                | \                                                            | [{id: Long, username: String, role: String(Student/Teacher/Admin), imageURL: String}] |
| 根据id删除User                                | DELETE   | /user/deleteUser/{id}                    | id: Long                                                     | bool                                                         |
| Student                                       |          |                                          |                                                              |                                                              |
| 新增Student信息                               | POST     | /student/add                             | {name: String, username: String, gender: String(Male/Female/Other/UNK), deptId: Long, gpa: Double} | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}, gpa: Double} |
| 根据id获取Student                             | GET      | /student/getStudentById/{id}             | id: Long                                                     | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}, gpa: Double} |
| 根据username获取Student                       | GET      | /student/getStudentByUsername/{username} | username: String                                             | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}, gpa: Double} |
| 获取所有Student                               | GET      | /student/all                             | \                                                            | [{id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}, gpa: Double}] |
| 更新Student                                   | POST     | /student/update                          | {id: Long, name: String, gender: String, deptId: Long, gpa: Double} | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}, gpa: Double} |
| 删除Student                                   | DELETE   | /student/{id}                            | id: Long                                                     | bool                                                         |
| Teacher                                       |          |                                          |                                                              |                                                              |
| 新增teacher                                   | POST     | /teacher/add                             | {name: String, username: String, gender: String(Male/Female/Other/UNK), deptId: Long} | {name: String, username: String, gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer} |
| 更新teacher                                   | POST     | /teacher/update                          | {id: Long, name: String, gender: String, deptId: Long, }     | {id: Long, name: String, username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}Teacher |
| 根据工号id删除teacher                         | DELETE   | /teacher/{id}                            | id: Long                                                     | bool                                                         |
| 根据工号id获取teacher                         | GET      | /teacher/getTeacherById/{id}             | id: Long                                                     | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer} }Teacher |
| 根据username获取teacher                       | GET      | /teacher/getTeacherByUsername/{username} | username: String                                             | {id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer} }Teacher |
| 获取所有teacher                               | GET      | /teacher/all                             | \\\\\\\\\\\\                                                 | [{id: Long,name: String,username: String,gender: String, department: {id: Long, name: String, parentId: Long, departmentLevel: Integer}}]List<Teacher> |
| Class                                         |          |                                          |                                                              |                                                              |
| 新增class                                     | POST     | /class/add                               | {name: String,teacherId: Long,credit: double,classHours: double,graph: String} | {id: Long, name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}Class |
| 根据课程id删除class                           | DELETE   | /class/delete/{id}                       | \                                                            | bool                                                         |
| 更新class信息                                 | POST     | /class/update                            | {id: Long,name: String,teacherId: Long,credit: double,classHours: double,graph: String} | {id: Long,name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}Class |
| 获取所有class                                 | GET      | /class/all                               | \\\\\\\\\\\\\\\\\\\\                                         | [{id: Long,name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}]List<Class> |
| 根据课程id获取class                           | GET      | /class/getClassById/{id}                 | id: Long                                                     | {id: Long,name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}Class |
| 根据课程名name获取class                       | GET      | /class/getClassByName/{name}             | name: String                                                 | {id: Long,name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}Class |
| 根据教师名teacherName获取class                | GET      | /class/getClassByTeacher/{teacherId}     | teacherId: Long                                              | [{id: Long,name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}]List<Class> |
| Student_Class_Associated                      |          |                                          |                                                              |                                                              |
| 新增选课记录                                  | POST     | /scAssociated/add                        | {sid: Long,cid: Long}                                        | {id: Long,sid: Long,cid: Longgrade: Double}Student_Class_Associated |
| 根据id删除选课记录                            | DELETE   | /scAssociated/delete/{id}                | id: Long                                                     | bool                                                         |
| 更新某门课课程成绩                            | POST     | /scAssociated/update                     | {sid: Long,cid: Long,grade: double}                          | {id: Long,sid: Long,cid: Long,grade: Double}Student_Class_Associated |
| 根据编号id查询选课记录                        | GET      | /scAssociated/getAssociatedById/{id}     | id: Long                                                     | {id: Long,sid: Long,cid: Long,grade: Double}Student_Class_Associated |
| 根据学号sid查询其所有选课记录                 | GET      | /scAssociated/getAssociatedBySid/{sid}   | sid: Long                                                    | [{id: Long,sid: Long,cid: Long,grade: Double}]List<Student_Class_Associated> |
| 根据课程编号cid查询此课程下的所有学生课程记录 | GET      | /scAssociated/getAssociatedByCid/{cid}   | cid: Long                                                    | [{id: Long,sid: Long,cid: Long,grade: Double}]List<Student_Class_Associated> |
|                                               |          |                                          |                                                              |                                                              |
| Department                                    |          |                                          |                                                              |                                                              |
| 新增dept                                      | POST     | /dept/add                                | {name: String,parentId: LongdepartmentLevel: Integer}        | {id: Long,name: String,parentId: LongdepartmentLevel: Integer} |
| 根据组织id删除dept                            | DELETE   | /dept/delete/{id}                        | id: Long                                                     | bool                                                         |
| 更新dept信息                                  | POST     | /dept/update                             | {id: Long,name: String,parentId: LongdepartmentLevel: Integer} | {id: Long,name: String,parentId: LongdepartmentLevel: Integer} |
| 获取所有dept信息                              | GET      | /dept/all                                | \\\\\\\\\\\\\\\\\\\\\\\\                                     | [{id: Long,name: String,parentId: Long,departmentLevel: Integer}]List<Department> |
| 根据编号id获取dept                            | GET      | /dept/getDeptById/{id}                   | id: Long                                                     | {id: Long,name: String,parentId: Long,departmentLevel: Integer}Department |
| 根据名称name获取dept                          | GET      | /dept/getDeptByName/{name}               | name: String                                                 | {id: Long,name: String,parentId: Long,departmentLevel: Integer}Department |
|                                               |          |                                          |                                                              |                                                              |
|                                               |          |                                          |                                                              |                                                              |
| ClassMission                                  |          |                                          |                                                              |                                                              |
| 新增课程学习任务classMission                  | POST     | /classMission/add                        | {cid: Long,type: String,description: String,deadline: String,submitMethod: String,score: double} | {id: Long,class:{id: Long, name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}type: String,description: String,deadline: String,submitMethod: String,score: double} |
| 根据id删除classMission                        | DELETE   | /classMission/delete/{id}                | id: Long                                                     | bool                                                         |
| 更新classMission信息                          | POST     | /classMission/update                     | {id: Longcid: Long,type: String,description: String,deadline: String,submitMethod: String,score: double} | {id: Longclass:{id: Long, name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}type: String,description: String,deadline: String,submitMethod: String,score: double}ClassMission |
| 获取所有st信息                                | GET      | /classMission/all                        | \\\\\\\\\\\\\\\\\\\\                                         | [{id: Longclass:{id: Long, name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}type: String,description: String,deadline: String,submitMethod: String,score: double}]List<ClassMission> |
| 根据课程编号获取st任务信息                    | GET      | /classMission/getClassMittionByCid/{cid} | cid: Long                                                    | [{id: Longclass:{id: Long, name: String,teacher:{name: String, username: String, gender: String, deptId: Long}credit: double,classHours: double,graph: String}type: String,description: String,deadline: String,submitMethod: String,score: double}]List<classMission> |
|                                               |          |                                          |                                                              |                                                              |
| StudyData                                     |          |                                          |                                                              |                                                              |
| // TODO                                       |          |                                          |                                                              |                                                              |
|                                               |          |                                          |                                                              |                                                              |
|                                               |          |                                          |                                                              |                                                              |
| Resource                                      |          |                                          |                                                              |                                                              |
| 新增资源resource                              | POST     | /resource/add                            | {name: String,path: String,type: String}                     | {id: Long,name: String,path: String,type: String}            |
| 删除资源resource                              | DELETE   | /resource/delete/{id}                    | id: Long                                                     | bool                                                         |
| 更新resource信息                              | POST     | /resource/update                         | {id: Long,name: String,path: String,type: String}            | {id: Long,name: String,path: String,type: String}Resource    |
| 获取所有resource信息                          | GET      | /resource/all                            | \\\\\\\\\\\\\\\\\\\\\\                                       | [{id: Long,name: String,path: String,type: String}]List<Resource> |
| 根据编号id获取resource信息                    | GET      | /resource/getResourceById/{id}           | id: Long                                                     | {id: Long,name: String,path: String,type: String}Resource    |
| 根据资源类型type获取resource信息              | GET      | /resource/getResourceByType/{type}       | type: String                                                 | [{id: Long,name: String,path: String,type: String}]List<Resource> |
|                                               |          |                                          |                                                              |                                                              |
|                                               |          |                                          |                                                              |                                                              |

# **错误码**

| **错误类型**             | **错误码** |
| ------------------------ | ---------- |
| 用户名不存在             | 501        |
| 用户名已存在             | 502        |
| 学生不存在               | 503        |
| 组织不存在               | 504        |
| 教师不存在               | 505        |
| 登陆密码错误             | 506        |
| 学生对应的课程信息不存在 | 507        |
| 组织已存在               | 508        |
| 课程不存在               | 509        |
| 提供的父组织级别不匹配   | 510        |
| 课程任务不存在           | 511        |
| 未定义错误，查看后端idea | 500        |
| 页面无法访问             | 404        |
| 请求失败                 | 400        |
|                          |            |

 

 
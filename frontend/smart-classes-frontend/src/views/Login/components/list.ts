// 学生端路由权限列表 - 对应router/index.ts中getStudentRoutes()的路径
const studentList: string[] = [
  '/course',
  '/homework', 
  '/grades'
]

// 教师端路由权限列表 - 对应router/index.ts中getTeacherRoutes()的路径
const teacherList: string[] = [
  '/teacher',
  '/teacher/dashboard',
  '/teacher/studentManage',
  '/task',
  '/task/task'
]

export { studentList, teacherList }
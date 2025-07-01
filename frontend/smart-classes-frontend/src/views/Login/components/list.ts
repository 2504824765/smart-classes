// 学生端路由权限列表 - 对应router/index.ts中getStudentRoutes()的路径
const studentList: string[] = [
  '/course',
  '/course/content',
  '/course/detail',
  '/course/knowledge',
  '/homework',
  '/homework/list',
  '/homework/detail',
  '/grades'
]

// 教师端路由权限列表 - 对应router/index.ts中getTeacherRoutes()的路径
const teacherList: string[] = [
  '/teacher',
  '/teacher/dashboard',
  '/teacher/studentManage',
  '/mission',
  '/mission/content',
  '/mission/create'
]

const adminList: string[] = [
  '/admin',
  '/admin/dashboard',
  '/admin/teacherManage',
  '/admin/studentManage',
  '/admin/courseManage',
  '/admin/missionManage',
  '/admin/gradesManage'
]

export { studentList, teacherList, adminList }

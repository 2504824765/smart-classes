// 学生端路由权限列表 - 对应router/index.ts中getStudentRoutes()的路径
const studentList: string[] = [
  '/course',
  '/course/content',
  '/course/select',
  '/course/detail',
  '/course/knowledge',
  '/homework',
  '/homework/list',
  '/homework/detail',
  '/data',
  '/data/grades',
  '/data/assessment'
]

// 教师端路由权限列表 - 对应router/index.ts中getTeacherRoutes()的路径
const teacherList: string[] = [
  '/teacher',
  '/teacher/dashboard',
  '/studentManage',
  '/studentManage/list',
  '/studentManage/detail',
  '/course',
  '/course/content',
  '/course/detail',
  '/course/detail/form',
  '/course/detail/mission',
  '/course/detail/studentDetail'
]

const adminList: string[] = [
  '/admin',
  '/admin/dashboard',
  '/admin/teacherManage',
  '/admin/studentManage',
  '/admin/courseManage',
  '/admin/departmentManage',
  '/admin/missionManage',
  '/admin/teacherManage/form',
  '/admin/studentManage/form',
  '/admin/courseManage/form',
  '/admin/missionManage/form',
  '/admin/gradesManage'
]

export { studentList, teacherList, adminList }

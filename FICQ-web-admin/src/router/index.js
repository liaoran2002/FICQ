import Vue from 'vue'
import VueRouter from 'vue-router'
import adminHome from '../view/adminHome'

// 安装路由
Vue.use(VueRouter);

// 配置导出路由
export default new VueRouter({
  routes: [{
    path: "/",
    redirect: "/adminHome"
  },
  {
    path: '/adminHome',
    component: adminHome,
    children: [
      {
        name: "adminHome",
        path: "",
        component: () => import("../view/admin/homeBox")
      },
      {
        name: "userList",
        path: "/adminHome/userList",
        component: () => import("../view/admin/userList"),
        props: { isBanned: false }
      },
      {
        name: "bannedUserList",
        path: "/adminHome/bannedUserList",
        component: () => import("../view/admin/userList"),
        props: { isBanned: true }
      },
      {
        name: "groupList",
        path: "/adminHome/groupList",
        component: () => import("../view/admin/groupList"),
        props: { isBanned: false }
      },
      {
        name: "bannedGroupList",
        path: "/adminHome/bannedGroupList",
        component: () => import("../view/admin/groupList"),
        props: { isBanned: true }
      },
	  {
		  name:"sensitiveWordList",
		  path:"/adminHome/sensitiveWordList",
		  component:() => import("../view/admin/sensitiveWordList")
	  },
	  {
		  name:"messageList",
		  path:"/adminHome/messageList",
		  component:() => import("../view/admin/messageList"),
		  props: { messageType:"all" }
	  },
	  {
		  name:"userMessageList",
		  path:"/adminHome/userMessageList",
		  component:() => import("../view/admin/messageList"),
		  props : { messageType:"user" }
	  },
	  {
		  name:"groupMessageList",
		  path:"/adminHome/groupMessageList",
		  component:() => import("../view/admin/messageList"),
		  props : { messageType:"group" }
	  },
	  {
		  name:"adminSetting",
		  path:"/adminHome/adminSetting",
		  component:() => import("../view/admin/adminSetting")
	  },
    ]
  }
  ]

});

<template>
	<div class="home-page">
		<div class="app-container">
			<div class="title">
				<div class="t-left">
					<div class="menuIcon">
						<div :class="isMenuOpen ? 'opened' : 'closed'">
							<span class="icon iconfont icon-shouqicaidan" @click="toggleMenu"></span>
							<span class="icon iconfont icon-caidan" @click="toggleMenu"></span>
						</div>
					</div>
					<router-link v-bind:to="'/adminHome'">
						<div class="logo"></div>
					</router-link>
					<span>飞克尔后台管理系统</span>
				</div>
				<div class="t-right">
					<HeadImage :name="$store.state.userStore.userInfo.nickName" :size="38"
								:url="$store.state.userStore.userInfo.headImageThumb"></HeadImage>
					<span>{{ $store.state.userStore.userInfo.nickName }}</span>
					<span class="icon iconfont icon-exit" @click="logout"></span>
				</div>
			</div>
			<div class="homeMain">
				<div :class="['menu', isMenuOpen ? '' : 'closed']">
					<ul class="item1">
						<router-link v-bind:to="'/adminHome'">
							<li :class="{ 'selected': isSelected('/adminHome') }">
								<span class="icon iconfont icon-ai-home">首页</span>
							</li>
						</router-link>
						<li v-for="(item, index) in menuItems" :key="index" :data-index="index">
							<span @click="showItem(index)" :class="['icon', 'iconfont', item.icon]">{{ item.label
							}}</span><i class="icon iconfont icon-shangjiantou"
								:class="{ 'opened': isOpen(index) }"></i>
							<ul class="item2" :class="{ 'show': isOpen(index) }">
								<router-link v-for="subItem in item.children" :key="subItem.path" :to="subItem.path">
									<li :class="{ 'selected': isSelected(subItem.path) }">{{ subItem.label }}
									</li>
								</router-link>
							</ul>
						</li>
						<router-link v-bind:to="'/adminHome/adminSetting'">
							<li :class="{ 'selected': isSelected('/adminHome/adminSetting') }">
								<span class="icon iconfont icon-setting">设置</span>
							</li>
						</router-link>
					</ul>
				</div>
				<div class="view-box">
					<router-view></router-view>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import HeadImage from '../components/common/HeadImage.vue';

import {
	RouterLink
} from 'vue-router';
// import Setting from '../components/setting/Setting.vue';

export default {
	components: {
		HeadImage,
		RouterLink
	},
	data() {
		return {
			openMenuIndex: null, // 打开的菜单索引
			isMenuOpen: true, // 菜单是否打开
			menuItems: [{
				icon: 'icon-friend',
				label: '用户管理',
				children: [{
					path: '/adminHome/userList',
					label: '用户列表'
				},
				{
					path: '/adminHome/bannedUserList',
					label: '封禁用户列表'
				}
				]
			},
			{
				icon: 'icon-group',
				label: '群聊管理',
				children: [{
					path: '/adminHome/groupList',
					label: '群聊列表'
				},
				{
					path: '/adminHome/bannedGroupList',
					label: '封禁群聊列表'
				}
				]
			},
			{
				icon: 'icon-minganciciku',
				label: '敏感词管理',
				children: [{
					path: '/adminHome/sensitiveWordList',
					label: '敏感词列表'
				},
				{
					path: '/adminHome/sensitiveWordHits',
					label: '敏感词命中'
				}
				]
			},
			{
				icon: 'icon-chat',
				label: '消息管理',
				children: [{
					path: '/adminHome/messageList',
					label: '消息列表'
				},
				{
					path: '/adminHome/userMessageList',
					label: '用户消息列表'
				},
				{
					path: '/adminHome/groupMessageList',
					label: '群聊消息列表'
				},
				{
					path: '/adminHome/broadcastMessage',
					label: '广播消息'
				}
				]
			}
			]
		}
	},
	methods: {
		init() {
			if(this.$store.state.userStore.userInfo.userName===undefined){
				sessionStorage.removeItem('assessToken');
				location.href = '/';
			}
			this.openMenuIndex = this.menuItems.findIndex(item =>
				item.children.some(subItem => subItem.path === this.currentRoute)
			);
			
		},
		showItem(index) {
			this.openMenuIndex = this.openMenuIndex === index ? null : index;
		},
		logout() {
			this.$router.push('/home/chat');
		},
		toggleMenu() {
			this.isMenuOpen = !this.isMenuOpen;
		}
	},
	computed: {
		isOpen() {
			return (index) => this.openMenuIndex === index;
		},
		isSelected() {
			return (path) => this.currentRoute === path;
		},
		currentRoute() {
			return this.$route.path;
		}
	},
	mounted() {
		this.init();
		// 监听路由变化
		this.$router.afterEach(() => {
			this.init();
		});
	}
}
</script>

<style scoped lang="scss">
*{
	user-select: none;
}
a {
	color: #000;
	text-decoration: none;
}

.home-page {
	height: 100vh;
	width: 100vw;
}

.app-container {
	height: 100%;
	width: 100%;
	display: flex;
	flex-direction: column;
}

.title {
	flex: 0 0 auto;
	// 可以根据需要设置标题的高度和样式
	height: 10vh;
	background-color: #333333;
	color: #fff;
	display: flex;
	justify-content: space-between;
}

.t-left {
	margin-left: 2vw;
	justify-self: center;
	align-self: center;
	flex: 0 0 auto;
	font: 900 3.5vh "黑体";
	display: flex;
}

.t-left span {
	justify-self: center;
	align-self: center;
}

.t-left .menuIcon {
	justify-self: center;
	align-self: center;
	margin-right: 1vw;
	width: 5vh;
	height: 5vh;
	overflow: hidden;
}

.menuIcon div {
	transition: all 0.5s ease;
}

.menuIcon .opened {
	transform: none;
}

.menuIcon .closed {
	transform: translateX(-5vh);
}

.t-left .icon {
	justify-self: center;
	align-self: center;
	font-size: 5vh;
	cursor: pointer;
}

.logo {
	margin-right: 1vw;
	width: 6vh;
	height: 6vh;
	background: url("../assets/image/logo.png") no-repeat;
	background-size: 100% 100%;
}

.t-right {
	margin-right: 2vw;
	justify-self: center;
	align-self: center;
	display: flex;
	margin-right: 2vw;
	font-size: 3vh;
	line-height: 3vh;
}

.t-right * {
	margin-right: 2vh;
}

.user-avatar {
	border: #fff solid 2px;
	border-radius: 50%;
	width: 6vh;
	height: 6vh;
}

.t-right span {
	justify-content: center;
	align-self: center;
}

.t-right .icon {
	padding: 5px;
	border: #fff solid 2px;
	border-radius: 50%;
	font-size: 3vh;
}

.homeMain {
	flex: 1;
	display: flex;
}

.menu {
	user-select: none;
	flex: 0 0 auto;
	height: 90vh;
	width: 16vw;
	overflow-y: auto;
	cursor: pointer;
	border-right: 3px solid #888;
	transition: all 0.5s ease;
}

.menu.closed {
	width: 0;
}

.menu ul {
	list-style: none;
	padding: 0;
	margin: 0;
	text-indent: 2vw;
}


.menu .icon::before {
	padding-right: 1vw;
}

.menu .item1 span {
	display: block;
	padding: 3vh 0;
}

.menu i {
	float: left;
	position: relative;
	bottom: 6vh;
	left: 13vw;
	margin: 0;
	height: 0;
	text-indent: 0;
	transform: none;
	transform-origin: 8px 8px;
	transition: all 1s ease;
}

.menu .opened {
	transform: rotate(-180deg);
}

.menu .item2 {
	overflow: hidden;
	max-height: 0;
	transition: all 0.5s ease;
}

.menu .show {
	max-height: 1000px;
	transition: all 1s ease;
}

.menu .item2 li {
	padding: 3vh 0;
	text-indent: 4vw;
}

.menu .selected {
	background-color: #ccc;
}

.main-content {
	flex: 1;
	display: flex;
}

.view-box {
	flex: 1;
	height: 90vh;
	width: calc(100vw - 16vw);
	transition: width 0.5s ease;
}

.view-box {
	width: calc(100vw - 16vw);
}

.view-box {
	width: 100vw;
}

.menu.collapsed+.view-box {
	width: 100vw;
}
</style>

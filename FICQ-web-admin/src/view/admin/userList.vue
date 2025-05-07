<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-friend">用户管理>{{ isBanned ? "封禁" : "" }}用户列表</span>
		</div>
		<div class="main">
			<Loading v-if="isLoading" />
			<div class="userList">
				<table class="list">
					<thead>
						<tr>
							<th>ID</th>
							<th>用户名</th>
							<th>昵称</th>
							<th>头像</th>
							<th>性别</th>
							<th>身份</th>
							<th v-if="!isBanned">在线状态</th>
							<th v-else>封禁原因</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="(item, index) in userList" :key="index">
							<td>{{ item.id }}</td>
							<td>{{ item.userName }}</td>
							<td>{{ item.nickName }}</td>
							<td>
								<HeadImage :name="item.nickName" :size="38" :url="item.headImageThumb"></HeadImage>
							</td>
							<td>{{ item.sex == 0 ? "男" : "女" }}</td>
							<td>{{ item.type == 0 ? "普通用户" : "管理员" }}</td>
							<td v-if="!isBanned">{{ item.online ? "在线" : "离线" }}</td>
							<td v-else>{{ item.reason }}</td>
							<td>
								<button v-if="!isBanned" @click="banUser(item.id)">封禁</button>
								<button v-else @click="unbanUser(item.id)">解封</button>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</template>


<script>
import Loading from '../../components/admin/loading.vue';
import HeadImage from '../../components/common/HeadImage.vue';

export default {
	name: "userList",
	props: {
		isBanned: {
			type: Boolean,
			default: false,
			required: true
		}
	},
	components: {
		Loading,
		HeadImage
	},
	data() {
		return {
			isLoading: true,
			page:1,
			size:10,
			userList: [],
		}
	},
	methods: {
		init() {

			this.$http({
				method: "post",
				url: this.isBanned?"/admin/bannedUserList":"/admin/userList",
				data: {
					isBanned:this.isBanned,
					page:this.page,
					size:this.size,
				}
			}).then((res) => {
				this.userList = res;
				this.isLoading = false;
			}).catch((err) => {
				console.log(err);
			})

			/*
			if (this.isBanned) {
				this.userList = [
					{
						id: 1,
						userName: "张三",
						nickName: "张三",
						sex: 0,
						type: 0,
						signature: "",
						headImage: "",
						headImageThumb: "",
						online: false,
						isBanned: true,
						reason: "长太丑了"
					},
					{
						id: 2,
						userName: "李四",
						nickName: "李四",
						sex: 0,
						type: 1,
						signature: "",
						headImage: "",
						headImageThumb: "",
						online: false,
						isBanned: true,
						reason: "长太帅了"
					}
				];
			} else {
				this.userList = [
					{
						id: 1,
						userName: "张三",
						nickName: "张三",
						sex: 0,
						type: 0,
						signature: "",
						headImage: "",
						headImageThumb: "",
						online: false,
						isBanned: false,
						reason: ""
					},
					{
						id: 2,
						userName: "李四",
						nickName: "李四",
						sex: 0,
						type: 1,
						signature: "",
						headImage: "",
						headImageThumb: "",
						online: false,
						isBanned: false,
						reason: ""
					}
				];
			}
			this.isLoading = false;*/
		}
	},
	computed: {
	},
	mounted() {
		this.init();
	},
	watch: {
		$route(to, from) {
			if (to.name === 'userList' || to.name === 'bannedUserList') {
				this.init();
			}
		}
	}
}
</script>

<style>
.box {
	height: 100%;
	width: 100%;
	overflow: scroll;
}

.box .title {
	height: 3%;
	width: 100%;
	padding: 2vh 2vw;
	width: 100%;
	background-color: #ffffff;
}

.box .title .icon::before {
	padding-right: 1vw;
}

.box .main {
	height: 97%;
	width: 100%;
	background-color: #f0f0f0;
}
</style>
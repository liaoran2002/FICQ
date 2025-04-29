<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-friend">用户管理>{{ isBanned ? "封禁" : "" }}用户列表</span>
		</div>
		<div class="main">
			<Loading v-if="isLoading" />
			<div class="userList">
				<el-table :data="userList" stripe :row-class-name="isBanned ? 'banned' : ''"
					:default-sort="{ prop: 'createdAt', order: 'descending' }">
					<el-table-column prop="id" label="序号" width="180" />
					<el-table-column prop="userName" label="用户名" width="180" />
					<el-table-column prop="nickName" label="昵称" width="180" />
					<el-table-column prop="sex" label="性别" width="180">
						<template slot-scope="scope">
							<span v-if="scope.row.sex == 0">男</span>
							<span v-if="scope.row.sex == 1">女</span>
						</template>
					</el-table-column>
					<el-table-column prop="headImageThumb" label="头像" width="180">
						<template slot-scope="scope">
							<div>{{ scope.row.nikeName }}</div>
							<head-image :name="scope.row.nikeName" :url="scope.row.headImageThumb" :size="30"></head-image>
						</template>
					</el-table-column>
					<el-table-column prop="online" label="在线状态" width="180">
						<template slot-scope="scope">
							<span v-if="scope.row.online == false">离线</span>
							<span v-if="scope.row.online == true">在线</span>
						</template>
					</el-table-column>
					<el-table-column label="操作" width="180">
						<template slot-scope="scope">
							<el-button type="primary" size="mini" @click="scope.$emit('edit', scope.row)">
								编辑
							</el-button>
							<el-button type="danger" size="mini" @click="scope.$emit('delete', scope.row)">
								删除
							</el-button>
						</template>
					</el-table-column>
				</el-table>
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
			userList: [],
		}
	},
	methods: {
		init() {
			this.$http({
				method: "GET",
				url: "/admin/userList",
			}).then((res) => {
				this.userList = res;
				this.isLoading = false;
			}).catch((err) => {
				console.log(err);
			})
		}
	},
	computed: {
	},
	mounted() {
		this.init();
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
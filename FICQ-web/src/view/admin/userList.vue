<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-friend">用户管理 > {{ banned ? "封禁" : "" }}用户列表</span>
		</div>
		<div class="main">
			<tablepage :searchUrl="searchUrl" @modify="modify" @ban="ban" @unban="unban" :banned="banned" :url="url"
				:tableHeaders="tableHeaders" />
		</div>
		<backToTop :targetSelector="'box'"/>
	</div>
</template>


<script>
import tablepage from '../../components/admin/tablepage.vue';
import backToTop from '../../components/admin/backToTop.vue';
export default {
	name: "userList",
	props: {
		banned: {
			type: Boolean,
			default: false,
			required: true
		}
	},
	components: {
		tablepage,
		backToTop
	},
	data() {
		return {
			url: 'userList',
			searchUrl: 'findUser',
			tableHeaders: [
				{ label: 'ID', field: 'id', type: "text" },
				{ label: '用户名', field: 'userName', type: "text" },
				{ label: '昵称', field: 'nickName', type: "text" },
				{ label: '头像', field: 'nickName', urlField: 'headImageThumb', type: 'image' },
				{ label: '性别', field: 'sex', type: "bool", formatter: (value) => value === 0 ? "男" : "女" },
				{ label: '身份', field: 'type', type: "bool", formatter: (value) => value === 0 ? "管理员" : "普通用户" },
				{ label: '在线状态', field: 'online', type: 'bool', conditional: false, formatter: (value) => value === true ? "在线" : "离线" },
				{ label: '封禁原因', field: 'reason', type: "text", conditional: true },
				{
					label: '操作', type: 'buttonGroup',
					buttons: [
						{ label: '修改', method: 'modify', conditional: undefined },
						{ label: '解封', method: 'unban', conditional: false },
						{ label: '封禁', method: 'ban', conditional: true }
					]
				}
			]
		}
	},
	methods: {
		modify(event, callback) {
			console.log('修改功能');
			console.log(event);
			callback(1);
		},
		ban(event, callback) {
			console.log('封禁功能');
			console.log(event);
			callback(0);
		},
		unban(event, callback) {
			console.log('解封功能');
			console.log(event);
			callback(1);
		}
	},
	computed: {
	}
}
</script>

<style></style>
<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-friend">用户管理 > {{ banned ? "封禁" : "" }}用户列表</span>
		</div>
		<div class="main">
			<tablepage :banned="banned" :url="name" :tableHeaders="tableHeaders"/>
		</div>
	</div>
</template>


<script>
import tablepage from '../../components/admin/tablepage.vue';
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
		tablepage
	},
	data() {
		return {
			name: 'userList',
			tableHeaders:[
                { label: 'ID', field: 'id' },
                { label: '用户名', field: 'userName' },
                { label: '昵称', field: 'nickName' },
                { label: '头像', field: 'nickName', urlField: 'headImageThumb', type: 'image' },
                { label: '性别', field: 'sex', formatter: (value) => value === 0 ? "男" : "女" },
                { label: '身份', field: 'type', formatter: (value) => value === 0 ? "管理员" : "普通用户" },
                { label: '在线状态', field: 'online', elseField: 'reason', type: 'conditional', conditional: '!banned' },
				{ label: '封禁原因', field: 'reason', conditional: 'banned' },
                { label: '操作', type: 'buttonGroup' }
            ]
		}
	},
	methods: {
	},
	computed: {
	},
	mounted() {
	}
}
</script>

<style>
</style>
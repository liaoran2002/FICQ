<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-group">群聊管理 > {{ banned ? "封禁" : "" }}群聊列表</span>
		</div>
		<div class="main">
			<tablepage :banned="banned" :url="name" :tableHeaders="tableHeaders"/>
		</div>
	</div>
</template>

<script>
import tablepage from '../../components/admin/tablepage.vue';
export default {
	name: "groupList",
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
			name: 'groupList',
			tableHeaders:[
                { label: 'ID', field: 'id' },
                { label: '群聊名称', field: 'name' },
                { label: '群聊头像', field: 'name', urlField: 'headImageThumb', type: 'image' },
                { label: '群聊状态', field: 'status', elseField:'reason', type: 'conditional', conditional: '!banned' },
                { label: '封禁原因', field:'reason', conditional: 'banned' },
                { label: '操作', type: 'buttonGroup' }
            ]
		}
	},
	methods: {},
	computed: {}
}
</script>

<style>
</style>
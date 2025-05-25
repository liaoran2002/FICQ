<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-group">群聊管理 > {{ banned ? "封禁" : "" }}群聊列表</span>
		</div>
		<div class="main">
			<tablepage :searchUrl="searchUrl" @modify="modify" @ban="ban" @unban="unban" :banned="banned" :url="url" :tableHeaders="tableHeaders" />
		</div>
		<backToTop :targetSelector="'box'"/>
	</div>
</template>

<script>
import tablepage from '../../components/admin/tablepage.vue';
import backToTop from '../../components/admin/backToTop.vue';
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
		tablepage,
		backToTop
	},
	data() {
		return {
			url: 'groupList',
			searchUrl: 'findGroup',
			tableHeaders: [
				{ label: 'ID', field: 'id', type: 'text' },
				{ label: '群聊名称', field: 'name', type: 'text' },
				{ label: '群聊头像', field: 'name', urlField: 'headImageThumb', type: 'image' },
				{ label: '群公告', field: 'notice', type: 'text' },
				{ label: '封禁原因', field: 'reason', type: 'text', conditional: true },
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
		modify(event,callback) {
			console.log('修改功能');
			console.log(event);
			callback(1);
		},
		ban(event,callback) {
			console.log('封禁功能');
			console.log(event);
			callback(1);
		},
		unban(event,callback) {
			console.log('解封功能');
			console.log(event);
			callback(1);
		}
	},
	computed: {}
}
</script>

<style></style>
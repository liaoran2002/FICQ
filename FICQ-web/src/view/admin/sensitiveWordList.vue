<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-minganciciku">敏感词管理 > 敏感词列表</span>
		</div>
		<div class="main">
			<tablepage :searchUrl="searchUrl" :url="url" :tableHeaders="tableHeaders" />
		</div>
		<backToTop :targetSelector="'box'"/>
	</div>
</template>

<script>
import tablepage from '../../components/admin/tablepage.vue';
import backToTop from '../../components/admin/backToTop.vue';
export default {
	name: "sensitiveWordList",
	components: {
		tablepage,
		backToTop
	},
	data() {
		return {
			url: "sensitiveWordList", // 请求地址
			searchUrl: "findSensitiveWord", // 搜索地址
			tableHeaders: [
				{ label: "ID", field: "id", type: "text" },
				{ label: "敏感词", field: "content", type: "text" },
				{ label: "创建时间", field: "createTime", type: "date" },
				{ label: "启用状态", field: "enabled", type: "bool", formatter: (value) => value === false ? "禁用" : "启用" },
				{
					label: "操作", type: "buttonGroup",
					buttons: [
						{ label: "修改", method: "modify" },
						{ label: "启用", method: "enable", conditional: false },
						{ label: "禁用", method: "disable", conditional: true }
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
		enable(event,callback) {
			console.log('启用功能');
			console.log(event);
			callback(1);
		},
		disable(event,callback) {
			console.log('禁用功能');
			console.log(event);
			callback(1);
		}
	},
	computed: {}
}
</script>

<style></style>

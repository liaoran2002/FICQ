<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-minganciciku">敏感词管理 > 敏感词列表</span>
		</div>
		<div class="main">
			<Loading />
		</div>
	</div>
</template>

<script>
import Loading from '../../components/admin/loading.vue';
export default {
	name: "sensitiveWordList",
	components: {
		Loading
	},
	data() {
		return {}
	},
	methods: {},
	computed: {}
}
</script>

<style>
</style>

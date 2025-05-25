<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-setting">设置</span>
		</div>
		<div class="main">
		</div>
		<backToTop :targetSelector="'box'" />
	</div>
</template>

<script>
import backToTop from '../../components/admin/backToTop.vue';
export default {
	name: "adminSetting",
	components: {
		backToTop
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

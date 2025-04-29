<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-minganciciku">敏感词管理>敏感词列表</span>
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

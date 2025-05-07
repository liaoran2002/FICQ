<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-ai-home">首页</span>
		</div>
		<div class="main">
			<Loading />
		</div>
	</div>
</template>

<script>
import Loading from '../../components/admin/loading.vue';
export default {
	name: "homeBox",
	components: {
		Loading
	},
	data() {
		return {}
	},
	methods: {
		init(){
			
		}
	},
	computed: {}
}
</script>

<style>
.box {
	height: 100%;
	width: 100%;
	overflow-y: auto;
}

.box .title {
	height: 35px;
	width: 100%;
	line-height: 35px;
	padding-left: 15px;
	background-color: #ffffff;
}

.box .title .icon::before {
	padding-right: 1vw;
}

.box .main {
	min-height: calc(100% - 35px);
	width: 100%;
	background-color: #f0f0f0;
}
</style>
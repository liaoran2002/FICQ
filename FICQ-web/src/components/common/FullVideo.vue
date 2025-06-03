<template>
	<div class="full-video" v-show="visible" :before-close="onClose" :modal="true">
		<div class="video-box">
            <div class="mask" @click="onClose"></div>
			<video ref="videoPlayer" controls autoplay="true" :src="url"></video>
		</div>
		<div class="close" @click="onClose"><i class="el-icon-close"></i></div>
	</div>
</template>
<script>
export default {
	name: "fullVideo",
	data() {
		return {
			fit: 'contain'
		}
	},
	methods: {
		onClose() {
			// 在关闭前暂停视频播放
			if (this.$refs.videoPlayer) {
				this.$refs.videoPlayer.pause();
				this.$refs.videoPlayer.currentTime = 0;
			}
			this.$emit("close");
		}
	},
    watch: {
        visible: function (newData, oldData) {
            if (newData) {
                // 视频播放
                if (this.$refs.videoPlayer) {
                    this.$refs.videoPlayer.play();
                }
            }
        }
    },
	props: {
		visible: {
			type: Boolean
		},
		url: {
			type: String
		}
	}
}
</script>
<style lang="scss">
.full-video {
	position: fixed;
	width: 100%;
	height: 100%;
	left: 0;
	top: 0;
	bottom: 0;
	right: 0;

	.mask {
		position: fixed;
		width: 100%;
		height: 100%;
		background: black;
		opacity: 0.5;

	}

	.video-box {
		position: relative;
		width: 100%;
		height: 100%;

		video {
			position: absolute;
			left: 50%;
			top: 50%;
			transform: translate(-50%, -50%);
			max-height: 100%;
			max-width: 100%;
		}
	}

	.close {
		position: fixed;
		top: 10px;
		right: 10px;
		color: white;
		font-size: 25px;
		cursor: pointer;
	}
}
</style>
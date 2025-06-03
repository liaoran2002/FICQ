<template>
	<el-upload :action="'#'" :http-request="onFileUpload" :accept="fileTypes == null ? '' : fileTypes.join(',')"
		:show-file-list="false" :disabled="disabled" :before-upload="beforeUpload" :multiple="true">
		<slot></slot>
	</el-upload>
</template>

<script>
import { Upload } from 'element-ui';

export default {
	name: "fileUpload",
	data() {
		return {
			loading: null,
			uploadHeaders: {
				"accessToken": sessionStorage.getItem('accessToken')
			}
		}
	},
	props: {
		action: {
			type: String,
			required: false
		},
		fileTypes: {
			type: Array,
			default: null
		},
		maxSize: {
			type: Number,
			default: null
		},
		showLoading: {
			type: Boolean,
			default: false
		},
		disabled: {
			type: Boolean,
			default: false
		}
	},
	methods: {
		onFileUpload(file) {
			// 展示加载条
			if (this.showLoading) {
				this.loading = this.$loading({
					lock: true,
					text: '正在上传...',
					spinner: 'el-icon-loading',
					background: 'rgba(0, 0, 0, 0.7)'
				});
			}
			let formData = new FormData();
			const upload = () => {
				this.$http({
					url: this.action,
					data: formData,
					method: 'post',
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				}).then((data) => {
					this.$emit("success", data, file.file);
				}).catch((e) => {
					this.$emit("fail", e, file.file);
				}).finally(() => {
					this.loading && this.loading.close();
				})
			}
			if (this.action.startsWith('/video')) {
				formData.append('videoFile', file.file)
				const processVideo = async () => {
					return new Promise((resolve, reject) => {
						const video = document.createElement('video');
						video.preload = 'metadata';
						video.onloadedmetadata = () => {
							// 跳转到视频中间位置
							video.currentTime = Math.min(1, video.duration / 2);
						};
						video.oncanplay = () => {
							video.play().then(() => {
								setTimeout(() => {
									video.pause();
									const canvas = document.createElement('canvas');
									canvas.width = video.videoWidth || 320;
									canvas.height = video.videoHeight || 240;
									canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
									canvas.toBlob(blob => {
										if (!blob) reject(new Error('生成缩略图失败'));
										formData.append('imageFile', blob, 'thumbnail.jpg');
										resolve();
									}, 'image/jpeg', 0.8);
									video.remove(); // 清理资源 
								}, Math.min(1, video.duration / 1000));
							})
						};
						video.onerror = reject;
						video.src = URL.createObjectURL(file.file);
						video.load();
					});
				};
				processVideo().then(() => {
					upload();
				})
			} else {
				formData.append('file', file.file)
				upload();
			}
		},
		beforeUpload(file) {
			// 校验文件类型
			if (this.fileTypes && this.fileTypes.length > 0) {
				let fileType = file.type;
				let t = this.fileTypes.find((t) => t.toLowerCase() === fileType);
				if (t === undefined) {
					this.$message.error(`文件格式错误，请上传以下格式的文件：${this.fileTypes.join("、")}`);
					return false;
				}
			}
			// 校验大小
			if (this.maxSize && file.size > this.maxSize) {
				this.$message.error(`文件大小不能超过 ${this.fileSizeStr}!`);
				return false;
			}
			this.$emit("before", file);
			return true;
		}
	},
	computed: {
		fileSizeStr() {
			if (this.maxSize > 1024 * 1024) {
				return Math.round(this.maxSize / 1024 / 1024) + "M";
			}
			if (this.maxSize > 1024) {
				return Math.round(this.maxSize / 1024) + "KB";
			}
			return this.maxSize + "B";
		}
	}
}
</script>

<style></style>
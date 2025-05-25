<template>
	<div class="box">
		<div class="title">
			<span class="icon iconfont icon-chat">消息管理 > {{ messageTypes[messageType] }}消息列表</span>
		</div>
		<div class="main">
			<tablepage :searchUrl="searchUrl" @recall="recall" :url="url" :tableHeaders="tableHeaders" />
		</div>
		<backToTop :targetSelector="'box'"/>
	</div>
</template>

<script>
import tablepage from '../../components/admin/tablepage.vue';
import backToTop from '../../components/admin/backToTop.vue';
export default {
	name: "messageList",
	props: {
		messageType: {
			type: String,
			default: "private",
			required: true
		}
	},
	components: {
		tablepage,
		backToTop
	},
	data() {
		return {
			messageTypes: { "private": "私聊", "group": "群聊" },
			url: this.messageType + 'MessageList',
			searchUrl: 'find' + this.messageType[0].toUpperCase() + this.messageType.substring(1) + 'Message',
			tableHeaders: [
				{ label: 'ID', field: 'id', type: "text" },
				{ label: '发送用户ID', field: 'sendId', type: "text" },
				{ label: '群聊ID', field: 'groupId', type: "text", conditional: "group" },
				{ label: '接受用户ID', field: 'recvId', type: "text", conditional: "private" },
				{ label: '消息内容', field: 'content', type: "text" },
				{
					label: '消息类型', field: 'type', type: "bool",
					formatter: (value) => {
						switch (value) {
							case 0: return "文字消息";
							case 1: return "图片消息";
							case 2: return "文件消息";
							case 3: return "语音消息";
							case 4: return "视频消息";
							case 5: return "敏感消息";
							case 10: return "撤回消息";
							case 13: return "管理撤回";
							case 21: return "系统消息";
							default: return "未知消息";
						}
					}
				},
				{ label: '发送时间', field: 'sendTime', type: "date" },
				{
					label: '操作', type: 'buttonGroup',
					buttons: [
						{ label: '撤回', method: 'recall' }
					]
				}
			]

		}
	},
	methods: {
		recall(event) {
			console.log('撤回功能');
			console.log(event);
		}
	}
}
</script>

<style></style>

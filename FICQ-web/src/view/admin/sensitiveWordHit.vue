<template>
    <div class="box">
        <div class="title">
            <span class="icon iconfont icon-minganciciku">敏感词管理 > 敏感词命中</span>
        </div>
        <div class="main">
            <tablepage :url="messageType + 'SensitiveWordHit'" :tableHeaders="tableHeaders" />
        </div>
        <backToTop :targetSelector="'box'" />
    </div>
</template>
<script>
import tablepage from '../../components/admin/tablepage.vue';
import backToTop from '../../components/admin/backToTop.vue';
export default {
    name: "sensitiveWordHit",
    components: {
        tablepage,
        backToTop
    },
    props: {
        messageType: {
            type: String,
            default: "private",
            required: true
        }
    },
    data() {
        return {
            tableHeaders: [
                { label: "ID", field: "id", type: "text" },
                { label: "发送用户ID", field: "sendId", type: "text" },
                { label: "群聊ID", field: "groupId", type: "text", conditional: "group" },
                { label: "接受用户ID", field: "recvId", type: "text", conditional: "private" },
                { label: "消息内容", field: "content", type: "text" },
                { label: '发送时间', field: 'sendTime', type: "date" },
                {
                    label: "操作", type: "buttonGroup",
                    buttons: [
                        { label: "撤回", method: "exampleMethod" }
                    ]
                },
            ]
        }
    }
}
</script>
<style></style>
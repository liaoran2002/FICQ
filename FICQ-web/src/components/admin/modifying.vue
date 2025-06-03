<template>
    <el-dialog v-dialogDrag class="modifying" title="修改" :before-close="onClose">
        <el-form :model="form">
            
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="onClose()">取 消</el-button>
            <el-button type="primary" @click="onSubmit()">确 定</el-button>
        </span>
    </el-dialog>
</template>
<script>
export default {
    name: "Modifying",
    props: {
        visible: { type: Boolean, default: false },
        form: { type: Object, default: () => ({}) }
    },
    methods: {
        onClose() {
            this.$emit("close");
        },
        onSubmit() {
            this.$refs['settingForm'].validate((valid) => {
                if (!valid) {
                    return false;
                }
                this.$emit("close");
                this.$message.success("修改成功");
            });
        },
    }
}
</script>
<style scoped>
.modifying {
    width: 10%;
    height: 10%;
}
</style>
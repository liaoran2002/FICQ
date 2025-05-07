<template>
    <div class="tabPage">
        <Loading v-if="loading" />
        <div class="tabTitle">
            <div class="t-left">
                <el-input placeholder="请输入用户名" />
                <el-button type="primary">搜索</el-button>
            </div>
            <div class="t-right">
                <el-pagination :current-page="pageNum" :page-size="pageSize" :pager-count="5" :total="total"
                    layout="total, sizes, prev, pager, next, jumper" :page-sizes="pageSizes"
                    @current-change="handlePageChange" @size-change="handleSizeChange" />
            </div>
        </div>
        <div class="userList">
            <table class="list">
                <thead>
                    <tr>
                        <!-- 修改为使用计算属性 filteredTableHeaders -->
                        <th v-for="(header, index) in filteredTableHeaders" :key="index">
                            {{ header.label }}
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(item, index) in dataList" :key="index">
                        <!-- 动态渲染表格单元格 -->
                        <td v-for="(header, colIndex) in filteredTableHeaders" :key="colIndex">
                            <!-- 根据不同的表头类型渲染不同内容 -->
                            <template v-if="header.type === 'image'">
                                <HeadImage :name="item[header.field]" :size="30" :url="item[header.urlField]"></HeadImage>
                            </template>
                            <template v-else-if="header.type === 'buttonGroup'">
                                <button @click="modify(item.id)">修改</button>
                                <button v-if="!banned" @click="ban(item.id)">封禁</button>
                                <button v-else @click="unban(item.id)">解封</button>
                            </template>
                            <template v-else-if="header.type === 'conditional'">
                                <span v-if="!banned">{{ item[header.field] ? "在线" : "离线" }}</span>
                                <span v-else>{{ item[header.elseField] }}</span>
                            </template>
                            <template v-else>
                                {{ item[header.field] }}
                            </template>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>
<script>
import Loading from "./loading.vue";
import HeadImage from "../common/HeadImage.vue";
export default {
    name: "tablepage",
    components: {
        Loading,
        HeadImage
    },
    props: {
        banned: {
            type: Boolean,
            default: false,
            required: false
        },
        url: {
            type: String,
            default: "",
            required: true
        },
        tableHeaders: {
            type: Array,
            default: () => [],
            required: true
        }
    },
    data() {
        return {
            loading: true,
            pageNum: 1,
            pageSize: 10,
            total: 0,
            page: 0,
            pageSizes: [10, 20, 50, 100],
            dataList: []
        }
    },
    computed: {        
        // 新增计算属性，返回过滤后的表头数据
        filteredTableHeaders() {
            return this.tableHeaders.filter(header => {
                if (!header.conditional) {
                    return true;
                }
                if (header.conditional === '!banned') {
                    return !this.banned;
                }
                if (header.conditional === 'banned') {
                    return this.banned;
                }
                return false;
            });
        }
    },
    methods: {
        init() {
            this.getDataList();
        },
        getDataList() {
            this.loading = true;
            this.$http({
                method: "post",
                url: "/admin/" + this.url,
                data: {
                    banned: this.banned,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize
                }
            }).then((res) => {
                this.dataList = res.list;
                this.total = res.total;
                this.page = Math.ceil(res.total / this.pageSize);
                this.loading = false;
            }).catch((err) => {
                console.log(err);
            })
        },
        handlePageChange(event) {
            if (event === this.pageNum) {
                return;
            }
            this.pageNum = event;
            this.getDataList();
        },
        handleSizeChange(event) {
            if (event === this.pageSize) {
                return;
            }
            this.pageSize = event;
            this.getDataList();
        },
        modifyUser(id) {
            // 实现修改用户逻辑
        },
        banUser(id) {
            // 实现封禁用户逻辑
        },
        unbanUser(id) {
            // 实现解封用户逻辑
        }
    },
    mounted() {
        this.init();
    },
    watch: {
        banned() {
            this.pageNum = 1; // 重置页码为1
            this.init();
        }
    }
}
</script>
<style>
.tabTitle {
    width: 95%;
    margin: 0 auto;
    padding: 1vh 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font: 600 2.5vh "黑体";
    color: #000;
}

.tabTitle .t-left {
    display: flex;
    align-items: center;
}

.tabTitle .pageNum span {
    font: 600 2.5vh "黑体";
    padding: 0 0.5vw;
}

.tabTitle.pageNum span:hover {
    cursor: pointer;
    color: blue;
}

.tabTitle .pageNum .active {
    color: blue;
}

.userList {
    width: 95%;
    margin: 0 auto;
    background-color: #fff;
}

.list {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
}

.list th,
.list td {
    border: 1px solid #ccc;
    padding: 0.75vh 0;
}

.list .head-image {
    height: 30px;
    width: 30px;
    position: relative;
    left: 2vw;
}
</style>
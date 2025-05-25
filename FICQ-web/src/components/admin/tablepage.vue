<template>
    <div class="tabPage">
        <Loading v-if="loading" />
        <div class="tabTitle">
            <div :class="['t-left',placeWord==''?'t-left-hidden':'']">
                <el-input :placeholder="'请输入' + placeWord" prefix-icon="el-icon-search" v-model="searchWord" />
                <el-button type="primary" @click="search">搜索</el-button>
                <el-button type="primary" @click="clearSearch">清空</el-button>
            </div>
            <div class="t-right">
                <el-pagination :current-page="pageNum" :page-size="pageSize" :pager-count="5" :total="total"
                    layout="total, sizes, prev, pager, next, jumper" :page-sizes="pageSizes"
                    @current-change="handlePageChange" @size-change="handleSizeChange" />
            </div>
        </div>
        <div class="dataList">
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
                            <template v-if="header.type === 'text'">{{ item[header.field] }}</template>
                            <template v-else-if="header.type === 'image'">
                                <HeadImage :name="item[header.field]" :size="30" :url="item[header.urlField]">
                                </HeadImage>
                            </template>
                            <template v-else-if="header.type === 'date'">
                                {{ formatTime(item[header.field]) }}
                            </template>
                            <template v-else-if="header.type === 'bool'">
                                <span>{{ header.formatter ? header.formatter(item[header.field]) :
                                    item[header.field] }}</span>
                            </template>
                            <template v-else-if="header.type === 'buttonGroup'">
                                <div class="buttonGroup">
                                    <el-button
                                        :type="btn.conditional === undefined ? 'primary' : btn.conditional ? 'danger' : 'success'"
                                        size="small" v-for="(btn, btnIndex) in header.buttons" :key="btnIndex"
                                        v-show="showConditional(btn, dataList[index])"
                                        @click="executeMethod(btn.method, item)">
                                        {{ btn.label }}
                                    </el-button>
                                </div>
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
        searchUrl: {
            type: String,
            default: "",
            required: false
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
            dataList: [],
            searchWord: "",
            searching: false
        }
    },
    computed: {
        // 新增计算属性，返回过滤后的表头数据
        filteredTableHeaders() {
            return this.tableHeaders.filter(header => {
                if ((this.url === "privateMessageList" || this.url === "privateSensitiveWordHit") && header.conditional != "group") {
                    return true;
                } else if ((this.url === "groupMessageList" || this.url === "groupSenstiveWordHit") && header.conditional != "private") {
                    return true;
                } else if (header.conditional === undefined || header.conditional === this.banned) {
                    return true;
                }
                return false;
            });
        },
        placeWord() {
            if (this.url === "userList" || this.url === "bannedUserList" || this.url === "privateMessageList" || this.url === "groupMessageList") {
                return "用户名";
            } else if (this.url === "groupList" || this.url === "bannedGroupList") {
                return "群聊名";
            }else if (this.url === "sensitiveWordList") {
                return "敏感词";
            }else {
                return "";
            }
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
                this.searching = false;
            }).catch((err) => {
                console.log(err);
            })
        },
        search() {
            this.loading = true;
            this.$http({
                method: "post",
                url: "/admin/" + this.searchUrl,
                data: {
                    banned: this.banned,
                    searchWord: this.searchWord,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize
                }
            }).then((res) => {
                this.dataList = res.list;
                this.total = res.total;
                this.page = Math.ceil(res.total / this.pageSize);
                this.loading = false;
                this.searching = true;
                if (this.total === 0) {
                    this.$message({
                        type: "warning",
                        message: "未查询到相关数据"
                    });
                    this.getDataList();
                }
            }).catch((err) => {
                console.log(err);
            })
        },
        handlePageChange(event) {
            if (event === this.pageNum) {
                return;
            }
            this.pageNum = event;
            console.log(this.searching);
            if (this.searching) {
                this.search();
            } else {
                this.getDataList();
            }
        },
        handleSizeChange(event) {
            if (event === this.pageSize) {
                return;
            }
            this.pageSize = event;
            if (this.searching) {
                this.search();
            } else {
                this.getDataList();
            }
        },
        executeMethod(methodName, event) {
            this.$emit(methodName, event,(result)=>{
                if (result === 1) {
                    this.$message({
                        type: "success",
                        message: "操作成功"
                    });
                }else {
                    this.$message({
                        type: "error",
                        message: "操作失败"
                    });
                }
                this.getDataList();
            });
        },
        clearSearch() {
            this.searchWord = "";
            if (this.searching) {
                this.getDataList();
            }
        },
        showConditional(btn, row) {
            if (this.url === "userList" || this.url === "groupList") {
                return btn.conditional === undefined ? true : btn.conditional === !this.banned;
            } else if (this.url === "sensitiveWordList") {
                return btn.conditional === undefined ? true : btn.conditional === row.enabled;
            } else {
                return true;
            }
        },
        formatTime(timestamp) {
            const now = new Date();
            const targetDate = new Date(timestamp);
            const diff = now - targetDate;
            const year = 365 * 24 * 60 * 60 * 1000;
            const month = 30 * 24 * 60 * 60 * 1000;
            const day = 24 * 60 * 60 * 1000;
            const hour = 60 * 60 * 1000;
            const minute = 60 * 1000;
            const second = 1000;

            if (diff >= year) {
                return `${Math.floor(diff / year)}年前`;
            } else if (diff >= month) {
                return `${Math.floor(diff / month)}月前`;
            } else if (diff >= day) {
                return `${Math.floor(diff / day)}天前`;
            } else if (diff >= hour) {
                return `${Math.floor(diff / hour)}小时前`;
            } else if (diff >= minute) {
                return `${Math.floor(diff / minute)}分钟前`;
            } else if (diff >= second) {
                return `${Math.floor(diff / second)}秒前`;
            } else {
                return '刚刚';
            }
        },
    },
    mounted() {
        this.init();
    },
    watch: {
        banned() {
            this.pageNum = 1; // 重置页码为1
            this.init();
        },
        url() {
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

.tabTitle .t-left>*{
    margin: 0;
    margin-right: 0.5vw;
}
.t-left-hidden {
    visibility: hidden;
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

.dataList {
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
    max-width: 20vw;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.list .head-image {
    display: flex;
    align-items: center;
    justify-content: center;
}

.list .buttonGroup {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
}

.list .buttonGroup .redButton {
    background-color: red;
}

.list .buttonGroup .greenButton {
    background-color: green;
}
</style>
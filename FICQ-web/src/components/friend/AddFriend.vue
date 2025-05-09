<template>
    <el-dialog title="添加好友" :visible.sync="dialogVisible" width="400px" :before-close="onClose"
        custom-class="add-friend-dialog">
        <el-input placeholder="输入用户名或昵称按下 enter 搜索，最多展示 20 条" class="input-with-select" v-model="searchText" size="small"
            @keyup.enter.native="onSearch()">
            <i class="el-icon-search el-input__icon" slot="suffix" @click="onSearch()"> </i>
        </el-input>
        <!-- 使用 Flex 布局 -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 10px;">
            <span style="font: 600 20px;">{{ isSearchResult ? '搜索结果' : '随机推荐' }}</span>
            <el-button v-show="!isSearchResult" type="primary" size="small" @click="init">换一批</el-button>
        </div>
        <el-scrollbar style="height:400px">
            <div v-for="(user) in displayedUsers" :key="user.id" v-show="user.id != $store.state.userStore.userInfo.id">
                <div class="item">
                    <div class="avatar">
                        <head-image :name="user.nickName" :url="user.headImage" :online="user.online"></head-image>
                    </div>
                    <div class="add-friend-text">
                        <div class="nick-name">
                            <div>{{ user.nickName }}</div>
                            <div :class="user.online ? 'online-status  online' : 'online-status'">{{
                                user.online ? "[在线]" : "[离线]"}}</div>
                        </div>
                        <div class="user-name">
                            <div>用户名:{{ user.userName }}</div>
                        </div>
                    </div>
                    <el-button type="success" size="mini" v-show="!isFriend(user.id)"
                        @click="onAddFriend(user)">添加</el-button>
                    <el-button type="info" size="mini" v-show="isFriend(user.id)" plain disabled>已添加</el-button>
                </div>
            </div>
        </el-scrollbar>
    </el-dialog>
</template>
<script>
import HeadImage from '../common/HeadImage.vue'


export default {
    name: "addFriend",
    components: { HeadImage },
    data() {
        return {
            allUsers: [], // 存储后台返回的所有 20 个数据
            displayedUsers: [], // 当前显示的 5 个数据
            searchText: "",
            isSearchResult: false,
            currentIndex: 0 // 当前显示数据的起始索引
        }
    },
    props: {
        dialogVisible: {
            type: Boolean
        }
    },
    methods: {
        init() {
            if (this.isSearchResult) {
                return;
            }
            if (this.allUsers.length === 0) {
                // 首次请求 20 个数据
                this.$http({
                    url: "/user/randUsers",
                    method: "get",
                    params: {
                        count: 20
                    }
                }).then((data) => {
                    this.allUsers = data;
                    this.displayedUsers = this.allUsers.slice(0, 5);
                    this.currentIndex = 5;
                });
            } else {
                // 循环显示 5 个数据
                if (this.currentIndex >= this.allUsers.length) {
                    this.currentIndex = 0;
                }
                this.displayedUsers = this.allUsers.slice(this.currentIndex, this.currentIndex + 5);
                this.currentIndex += 5;
            }
        },
        onClose() {
            this.$emit("close");
        },
        onSearch() {
            if (!this.searchText) {
                this.$message.error("请输入用户名或昵称");
                return;
            }
            this.isSearchResult = true;
            this.$http({
                url: "/user/findByName",
                method: "get",
                params: {
                    name: this.searchText
                }
            }).then((data) => {
                this.displayedUsers = data;
                this.allUsers = [];
                this.currentIndex = 0;
            });
        },
        onAddFriend(user) {
            this.$http({
                url: "/friend/add",
                method: "post",
                params: {
                    friendId: user.id
                }
            }).then(() => {
                this.$message.success("添加成功，对方已成为您的好友");
                let friend = {
                    id: user.id,
                    nickName: user.nickName,
                    headImage: user.headImage,
                    online: user.online,
                    deleted: false
                }
                this.$store.commit("addFriend", friend);
            });
        },
        isFriend(userId) {
            return this.$store.getters.isFriend(userId);
        }
    },
    mounted() {
        this.init();
    }
}
</script>
<style lang="scss">
.add-friend-dialog {
	.item {
		height: 65px;
		display: flex;
		position: relative;
		padding-left: 15px;
		align-items: center;
		padding-right: 25px;

		.add-friend-text {
			margin-left: 15px;
			flex: 3;
			display: flex;
			flex-direction: column;
			flex-shrink: 0;
			overflow: hidden;

			.nick-name {
				display: flex;
				flex-direction: row;
				font-weight: 600;
				font-size: 16px;
				line-height: 25px;

				.online-status {
					font-size: 12px;
					font-weight: 600;

					&.online {
						color: #5fb878;
					}
				}
			}

			.user-name {
				display: flex;
				flex-direction: row;
				font-size: 12px;
				line-height: 20px;
			}

		}
	}
}
</style>

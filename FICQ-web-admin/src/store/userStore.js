import http from '../api/httpRequest.js'

export default {
	state: {
		userInfo: {}
	},

	mutations: {
		setUserInfo(state, userInfo) {
			state.userInfo = userInfo
		},
		clear(state) {
			state.userInfo = {};
		}
	},
	actions: {
		// loadUser(context) {
		// 	return new Promise((resolve, reject) => {
		// 		http({
		// 			url: '/user/self',
		// 			method: 'GET'
		// 		}).then((userInfo) => {
		// 			context.commit("setUserInfo", userInfo);
		// 			resolve();
		// 		}).catch((res) => {
		// 			reject(res);
		// 		});
		// 	})
		// }
	}
}
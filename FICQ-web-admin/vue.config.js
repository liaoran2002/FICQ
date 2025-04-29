const path = require('path')
const fs = require('fs')

module.exports = {
	devServer: {
		host: 'localhost',
		proxy: {
			'/api': {
				target: 'http://localhost:8808',
				changeOrigin: true,
				ws: false,
				pathRewrite: {
					'^/api': ''
				}
			}
		}
	}

}
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
			},
			'/images': {
				target: 'http://localhost:8808',
				changeOrigin: true,
				ws: false,
				pathRewrite: {
					'^/images': 'images'
				}
			},
			'/files': {
				target: 'http://localhost:8808',
				changeOrigin: true,
				ws: false,
				pathRewrite: {
					'^/files': 'files'
				}
			},
			'/videos': {
				target: 'http://localhost:8808',
				changeOrigin: true,
				ws: false,
				pathRewrite: {
					'^/videos': 'videos'
				}
			}
		}
	}
}
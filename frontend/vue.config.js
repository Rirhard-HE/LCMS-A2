// vue.config.js
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // My backend address
        changeOrigin: true,
        pathRewrite: { '^/api': '' }
      }
    },
    historyApiFallback: true
  }
}

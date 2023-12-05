import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [sveltekit()],
	define: {
		'process.env.API_URL': JSON.stringify('https://172.24.6.27'),
		'process.env.WS_URL': JSON.stringify('ws://172.24.6.27'),
		'process.env.PROJECT_PATH': JSON.stringify('/api/code'),
		// 'process.env.CLIENT_HOST': JSON.stringify('http://localhost:3000')
	}
});

import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [sveltekit()],
	define: {
		'process.env.URL_API': JSON.stringify('http://localhost:4444'),
		'process.env.WS_URL': JSON.stringify('ws://localhost:4444')
	}
});

import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [sveltekit()],
	define: {
		'process.env.API_URL': JSON.stringify('http://localhost:4444'),
		'process.env.WS_URL': JSON.stringify('ws://localhost:4444'),
		'process.env.PROJECT_PATH': JSON.stringify('/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code'),
	}
});

import { writable } from 'svelte/store';

export const selectedFile = writable(null);
export const openedCode = writable(null);
export const terminalNavbarActiveItem = writable('Problèmes');
export const terminalOutput = writable([]);
export const currentTab = writable('Explorer');
import { writable } from 'svelte/store';

export const selectedFile = writable(null);
export const openedCodes = writable([]);
export const openedTabs = writable([]);
export const terminalNavbarActiveItem = writable('Problèmes');
export const terminalOutput = writable([]);

export const editorUpdateTrigger = writable(null);
export const openedDirectory = writable(null);

/*
openedCodes : {
    name : "test.java",
    code : "public static...",
    status : "saved"
}

selectedFile : "test.java"

openedTabs : {
    file
}

*/

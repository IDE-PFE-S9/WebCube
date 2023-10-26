import { writable } from 'svelte/store';


// file explorer stores
export const openedDirectory = writable(null);
export const selectedFile = writable(null);
export const openedCodes = writable([]);
export const openedTabs = writable([]);

// terminal stores
export const terminalNavbarActiveItem = writable('Problèmes');
export const terminalOutput = writable([]);

// editor store
export const editorUpdateTrigger = writable(null);
export const readOnly = writable(false);

// archive explorer stores
export const archiveMode = writable(false);
export const openedArchive = writable(null);
export const selectedArchiveFile = writable(null);
export const openedArchiveTabs = writable([]);
export const archiveHandle = writable(null);

// navbar stores
export const currentTab = writable("Explorer");

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

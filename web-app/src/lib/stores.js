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
export const markdownMode = writable(false);

// archive explorer stores
export const archiveMode = writable(false);
export const openedArchive = writable(null);
export const selectedArchiveFile = writable(null);
export const openedArchiveTabs = writable([]);
export const archiveHandle = writable(null);

// navbar stores
export const currentTab = writable("Explorer");

// anti-cheat
export const screenChangeCount = persist('screenChangeCount', 0);
export const examMode = writable(false);

export const logs = persist('logs', [])

function persist(key, initialValue) {
    // Initialize the store value
    let value = initialValue;

    // Check if we're in a browser environment
    if (typeof window !== 'undefined') {
        // Get the current value from local storage if available
        const storedValue = localStorage.getItem(key);
        value = storedValue === null ? initialValue : JSON.parse(storedValue);
    }

    const store = writable(value);

    if (typeof window !== 'undefined') {
        // Subscribe to changes in the store and update local storage
        store.subscribe((currentValue) => {
            localStorage.setItem(key, JSON.stringify(currentValue));
        });
    }

    return store;
}

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

import { writable } from 'svelte/store';
import ReconnectingWebSocket from 'reconnecting-websocket';
import Cookies from "js-cookie"

let wsUrl = process.env.WS_URL;

// file explorer stores
export const openedDirectory = writable(null);
export const selectedFile = writable(null);
export const openedCodes = writable([]);
export const openedTabs = writable([]);

// terminal stores
export const terminalNavbarActiveItem = writable('Problèmes');
export const terminalOutput = writable([]);
export const problems = writable([]);

// user panel stores
export const adminNavbarActiveItem = writable('Général');

// editor store
export const editorUpdateTrigger = writable(null);
export const readOnly = writable(false);
export const markdownMode = writable(false);
export const goToLineTrigger = writable({ file: null, line: null });
export const goToLineColumnTrigger = writable({ file: null, line: null, column: null });

// archive explorer stores
export const archiveMode = writable(false);
export const openedArchive = writable(null);
export const selectedArchiveFile = writable(null);
export const openedArchiveTabs = writable([]);
export const archiveHandle = writable(null);
export const tpId = writable(null);
export const dateOpened = writable(null);

// modelling stores
export const entitiesList = writable([]);
export const showedEntities = writable([]);

// CheerpJ stores
export const cheerpjState = writable({
    showPopup: false,
    runJar: false,
    reloadJar: true,
    runTestJar: false,
    reloadTestJar: true,
});

export const cheerpjWidth = writable(500)
export const cheerpjHeight = writable(400)
export const graphical = writable(false)

// navbar stores
export const currentTab = writable("Explorer");

// anti-cheat
export const screenChangeCount = persist('screenChangeCount', 0);


export const logs = persist('logs', [])

// connection
export const token = writable(checkToken());

function checkToken() {
    return Cookies.get('apiJWT');
}

// web-sockets 
// Check if running in a browser and setup WebSocket
let socket;
export const examMode = writable(false);
export const isConnected = writable(false);
export const userCount = writable(0);
export const userDetails = writable({});


if (typeof window !== 'undefined') {
    socket = setupWebSocket();
}

// Function to send messages via WebSocket
export function sendMessage(type, data) {
    if (socket && socket.readyState === WebSocket.OPEN) {
        const message = JSON.stringify({ type, data });
        socket.send(message);
    } else {
        console.error('WebSocket is not open.');
    }
}

function setupWebSocket() {
    const socket = new ReconnectingWebSocket(`${wsUrl}/ws`);

    socket.onopen = () => {
        console.log('WebSocket connection established');
        isConnected.set(true);
        userDetails.subscribe(value => console.log(value))
    };

    socket.onmessage = (event) => {
        const message = JSON.parse(event.data);

        switch (message.type) {
            case 'examMode':
                examMode.set(message.data);
                break;
            case 'userCount':
                userCount.set(message.data);
                break;
            case 'userDetails':
                userDetails.set(message.data);
                // userDetails.subscribe(value => console.log(value))
                break;
            // Add more cases as needed for different message types
        }
    };

    socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        isConnected.set(false);
    };

    socket.onclose = () => {
        console.log('WebSocket connection closed');
        isConnected.set(false);
    };

    return socket;
}


// long term memory into the Local Storage
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
// Import MSAL
import { PublicClientApplication } from "@azure/msal-browser";
import Cookies from "js-cookie";
import { token, sendMessage } from "./stores";
import Swal from "sweetalert2";

let apiUrl = process.env.API_URL;
let clientHost = process.env.CLIENT_HOST;

// MSAL configuration
const msalConfig = {
    auth: {
        clientId: '818da7d4-8cbc-40f9-8054-d03ff4cfbd17', // Replace with your client ID
        authority: 'https://login.microsoftonline.com/organizations', //"https://login.microsoftonline.com/YOUR_TENANT_ID", // Replace with your tenant ID
        redirectUri: clientHost // Replace with your redirect URI
    },
    cache: {
        cacheLocation: "localStorage",
        storeAuthStateInCookie: true,
    },
};

// Function to login
async function login() {
    Object.keys(localStorage).forEach(function(key) {
        if (key !== 'screenChangeCount' && key !== 'logs') {
            localStorage.removeItem(key);
        }
    });
    
    const msalInstance = new PublicClientApplication(msalConfig);
    await msalInstance.initialize();
    try {
        await msalInstance.loginPopup({
            scopes: ["User.Read"],
            prompt: "select_account"
        });

        // After successful login, you can acquire tokens
        const account = msalInstance.getAllAccounts()[0];
        if (account) {
            const response = await msalInstance.acquireTokenSilent({
                scopes: ["User.Read"],
                account: account
            });

            // Use response.accessToken to make your API calls
            await getTokenApi(response.accessToken);
            Swal.close();
        }
    } catch (error) {
        console.log(error);
    }
}

async function getTokenApi(azureJWT) {
    const response = await fetch(`${apiUrl}/api/auth`, {
        headers: { 'Authorization-Azure': 'Bearer ' + azureJWT }
    });

    if (!response.ok) {
        const errorData = await response.json();
        return null;
    }

    const dataReturned = await response.json();
    Cookies.set("apiJWT", dataReturned.token);
    token.set(Cookies.get('apiJWT'))
    sendMessage('token', dataReturned.token)
    return dataReturned;
}

async function getUserInformations() {

    const response = await fetch(`${apiUrl}/api/user`, {
        headers: { 'Authorization-API': 'Bearer ' + Cookies.get("apiJWT") }
    });

    if (await isResponseOk(response)) {
        const dataReturned = await response.json();
        return dataReturned;
    } else {
        return null;
    }
}

async function getPublicKeyTeacher() {
    
    const response = await fetch(`${apiUrl}/api/teacher/publicKey`, {
        headers: { 'Authorization-API': 'Bearer ' + Cookies.get("apiJWT"), 
                     'Content-Type': 'text/plain'}
    });

    if (await isResponseOk(response)) {

        const dataReturned = await response.text();
        return dataReturned;
    }
}

async function getPrivateKeyTeacher() {
    
    const response = await fetch(`${apiUrl}/api/teacher/privateKey`, {
        headers: { 'Authorization-API': 'Bearer ' + Cookies.get("apiJWT"),
                    'Content-Type': 'text/plain' }
    });

    if (await isResponseOk(response)) {

        const dataReturned = await response.text();
        return dataReturned;
    }
}

async function isResponseOk(response) {

    if (response.status === 401) {
        // Si c'est le cas, vérifiez si l'en-tête 'Token-Status' est présent dans la réponse
        if (response.headers.has('Token-Status')) {
            // Lisez la valeur de l'en-tête 'Token-Status'
            const tokenStatus = response.headers.get('Token-Status');
            // Faites quelque chose avec la valeur (par exemple, affichez un message à l'utilisateur)

            // Si le token est expiré, vous pouvez gérer cela ici
            // Par exemple, affichez un message à l'utilisateur ou effectuez une action appropriée
            if (tokenStatus === 'Expired') {
                Cookies.remove('apiJWT');
                Cookies.remove('azureJWT');
                token.set(null);
                return false;
            }
        }
    }
    else if (!response.ok) {
        const errorData = await response.json();
        return false;
    }
    return true;
}


export { login, isResponseOk, getUserInformations, getPublicKeyTeacher, getPrivateKeyTeacher };

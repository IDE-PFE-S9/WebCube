// Import MSAL
import { PublicClientApplication } from "@azure/msal-browser";
import Cookies from "js-cookie";
import { token } from "./stores";
import Swal from "sweetalert2";


// MSAL configuration
const msalConfig = {
    auth: {
        clientId: '818da7d4-8cbc-40f9-8054-d03ff4cfbd17', // Replace with your client ID
        authority: 'https://login.microsoftonline.com/organizations',//"https://login.microsoftonline.com/YOUR_TENANT_ID", // Replace with your tenant ID
        redirectUri: 'http://localhost:5173' // Replace with your redirect URI
    },
    cache: {
        cacheLocation: "localStorage",
        storeAuthStateInCookie: true,
    },
};

// Create an instance of PublicClientApplication with the config


// Function to login
async function login() {
    const msalInstance = new PublicClientApplication(msalConfig);
    await msalInstance.initialize();
    console.log('msalInstance', msalInstance);
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
            console.log('Access Token:', response.accessToken);
            Cookies.set("azureJWT", response.accessToken);
            getUserDetails();
            Swal.close();
        }
    } catch (error) {
        console.error(error);
    }
}

async function getUserDetails() {
    const response = await fetch(`http://localhost:4444/User/Exemple`, {
        headers: {'Authorization-Azure': 'Bearer ' + Cookies.get("azureJWT")}
    });

    if (!response.ok) {
        const errorData = await response.json();
        console.log(errorData);
        alert('Une erreur est survenue lors de la requête API : ' + errorData.error);
        return null;
    }

    const dataReturned = await response.json();
    Cookies.set("apiJWT", dataReturned.token);
    token.set(Cookies.get('apiJWT'))
    return dataReturned;
}

async function exempleToken() {
    const response = await fetch(`http://localhost:4444/User/exemple/tokens`, {
        headers: {
            'Authorization-Azure': 'Bearer ' + Cookies.get("azureJWT"),
            'Authorization-API': 'Bearer ' + Cookies.get("apiJWT")
        }
    });

    if (!response.ok) {
        const errorData = await response.json();
        console.log(errorData);
        alert('Une erreur est survenue lors de la requête API : ' + errorData.error);
        return null;
    }

    const dataReturned = await response.json();
    console.log(dataReturned);
    return dataReturned;
}

// Export the login function to use in your components
export { login, getUserDetails, exempleToken };
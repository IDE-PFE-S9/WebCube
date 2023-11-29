import Swal from "sweetalert2";
import { login } from '$lib/auth.js';

export function showDeniedPopup() {
    Swal.fire({
        title: "Vous devez d'abord sélectionner une archive",
        icon: 'warning',
        background: "#1e1e1e",
        color: "white",
        allowOutsideClick: true,
    });
}

export function showLoginPopup() {
    Swal.fire({
        title: "Vous devez d'abord vous connecter",
        icon: 'warning',
        html: '<button id="loginBtn" style="cursor:pointer;color:white;font-size:1rem;background-color:#1e1e1e;width:40%;height:50px;border-radius:10px;border:1px solid white">Se connecter</button>',
        background: "#1e1e1e",
        color: "white",
        showConfirmButton: false,
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        didOpen: () => {
            const loginBtn = document.getElementById('loginBtn');
            loginBtn.addEventListener('click', login);
        }
    });
}

const Toast = Swal.mixin({
    toast: true,
    position: "top-end",
    showConfirmButton: false,
    timer: 2500,
    width: 300,
    background: "#1e1e1e",
    color: "rgb(187, 187, 187)",
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.onmouseenter = Swal.stopTimer;
        toast.onmouseleave = Swal.resumeTimer;
    }
});

export function workSavePopup() {
    Toast.fire({
        icon: "success",
        title: "Travail enregistré !"
    });
}

export function archiveDownloadPopup() {
    Toast.fire({
        icon: "success",
        title: "Archive téléchargée !"
    });
}

export function workCompilePopup() {
    Toast.fire({
        icon: "success",
        title: "Code compilé !"
    });
}

export function workTestPopup() {
    Toast.fire({
        icon: "success",
        title: "Code testé !"
    });
}
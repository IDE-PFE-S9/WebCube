<script>
	import { onMount } from 'svelte';
	import { login } from '$lib/auth.js'; // Assuming the MSAL code is in auth.js
	import { token } from '$lib/stores.js';
	import { screenChangeCount, examMode, logs, selectedFile } from '$lib/stores.js';
	import Swal from 'sweetalert2';

	let isExamModeActive = false;

	$: if (!$token) {
		showLoginPopup();
	}

	onMount(() => {
		// Subscribe to the examMode store
		const unsubscribe = examMode.subscribe((value) => {
			isExamModeActive = value;
		});

		const handleVisibilityChange = () => {
			if (!isExamModeActive) return; // Exit if exam mode is not active

			if (document.visibilityState === 'visible') {
				let timerInterval;
				Swal.fire({
					title: 'Triche',
					html: "Vous n'avez pas le droit de changer d'onglet. <br> Attendez encore <b></b> secondes",
					timer: 2000,
					allowOutsideClick: false,
					allowEscapeKey: false,
					timerProgressBar: true,
					didOpen: () => {
						Swal.showLoading();
						const timer = Swal.getPopup().querySelector('b');
						timerInterval = setInterval(() => {
							timer.textContent = `${Math.floor(Swal.getTimerLeft() / 1000) + 1}`;
						}, 100);
					},
					willClose: () => {
						clearInterval(timerInterval);
					}
				});
			} else {
				logs.update((currentLogs) => [...currentLogs, `Pasted text in ${$selectedFile.name}`]);
				screenChangeCount.update((count) => count + 1);
			}
		};

		document.addEventListener('visibilitychange', handleVisibilityChange);

		return () => {
			// Clean up
			document.removeEventListener('visibilitychange', handleVisibilityChange);
			unsubscribe();
		};
	});


	function showLoginPopup() {
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
</script>


<style lang="scss">
</style>

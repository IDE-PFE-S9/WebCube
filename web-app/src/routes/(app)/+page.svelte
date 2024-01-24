<script>
	import { onMount } from 'svelte';
	import { token, sendMessage } from '$lib/stores.js';
	import { screenChangeCount, examMode, logs, selectedFile, dateOpened } from '$lib/stores.js';
	import Swal from 'sweetalert2';
	import { showLoginPopup } from '/src/lib/PopUps/popup.js';

	let isExamModeActive = false;
	let quitDate;

	$: {
		if (!$token) {
			showLoginPopup();
		} else {
			sendMessage('token', $token)
		}
	}


	onMount(() => {
		// Subscribe to the examMode store
		const unsubscribe = examMode.subscribe((value) => {
			isExamModeActive = value;
		});

		const handleVisibilityChange = () => {
			if (!isExamModeActive) {
				if (document.visibilityState === "hidden") {
					quitDate = new Date();
				} else {
					if (typeof quitDate === 'undefined') {
						return;
					} else {
						const comebackDate = new Date();
						const timeSpentOutside = comebackDate - quitDate;

						// Assuming dateOpened is a Date object. If it's something else, adjust accordingly.
						dateOpened.set(new Date(new Date($dateOpened).getTime() + timeSpentOutside))
						// Reset quitDate
						quitDate = undefined;
					}
				}
			} else {
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
		};

		document.addEventListener('visibilitychange', handleVisibilityChange);

		return () => {
			// Clean up
			document.removeEventListener('visibilitychange', handleVisibilityChange);
			unsubscribe();
		};
	});
</script>

<style lang="scss">
</style>
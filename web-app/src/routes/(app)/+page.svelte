<script>
	import { onMount } from 'svelte';
	import { screenChangeCount, examMode } from '$lib/stores.js';
	import Swal from 'sweetalert2';

	let isExamModeActive = false;

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
				})
			} else {
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
</script>

<style lang="scss">
</style>

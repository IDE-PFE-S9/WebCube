<script>
	import { sendMessage, examGroups } from '../stores'; // Assuming sendExamStatus can toggle the exam mode
	
	function toggleExamMode(group) {
		if ($examGroups.includes(group))
			$examGroups = $examGroups.filter((g) => g !== group);
		else if (!$examGroups.includes(group)) {
			$examGroups = [...$examGroups, group];
		}
		sendMessage('examMode', $examGroups);
	}

	const groups = ['ROLE_GROUP_A', 'ROLE_GROUP_B', 'ROLE_RATTRAPAGE'];
</script>

<div class="config-panel">
	<h2 id="title">Mode examen</h2>
	<div class="checkboxes">
		{#each groups as group}
			<label class:checked={$examGroups.includes(group)}>
				<input
					type="checkbox"
					checked={$examGroups.includes(group)}
					on:change={() => toggleExamMode(group)}
				/>
				{group.substring(5)}
			</label>
		{/each}
	</div>
</div>

<style lang="scss">
	.config-panel {

		h2 {
			text-align: center;
			color: white;
			margin-bottom: 1rem;
		}

		.checkboxes {
			display: flex;
			flex-direction: row;
			padding: 1rem;
			padding-top: 0;
			padding-bottom: 0;
			flex-grow: 1;
			color: white;
			gap: 20px;

			// Hide the default checkbox
			input[type='checkbox'] {
				display: none;
			}

			// Style the label to look like a button
			label {
				display: inline-block;
				background-color: rgb(52, 52, 52); // Button background color
				color: white; // Text color
				cursor: pointer;
				padding: 0.5rem 1rem;
				margin-bottom: 0.5rem; // Space between buttons
				border: none;
				text-align: center;
				text-decoration: none;
				font-size: 1rem;
				transition: background-color 0.3s ease;
				border-radius: 5px; // Rounded corners

				// Checkbox checked state styles
				&.checked {
					background-color: darken(#3498db, 10%); // Darker background for checked state

					&:hover {
						background-color: lighten(#3498db, 10%);
					}
				}

				// Hover effect
				&:hover {
					background-color: lighten(rgb(52, 52, 52), 10%);
				}
			}

			// Visually indicate the checked state when the checkbox is checked
			input[type='checkbox']:checked + label {
				background-color: darken(#3498db, 10%);
				&:after {
					content: '✓'; // Checkmark symbol
					padding-left: 0.5rem;
				}
			}
		}
	}
</style>


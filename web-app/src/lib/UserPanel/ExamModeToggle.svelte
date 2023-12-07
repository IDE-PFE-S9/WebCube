<script>
	import { sendMessage, examMode } from '../stores'; // Assuming sendExamStatus can toggle the exam mode
	
	function toggleExamMode() {
		sendMessage('examMode', $examMode);
	}
</script>

<div class="toggle-container">
	<span class="toggle-label">Mode Examen</span>
	<label class="toggle-switch">
		<input type="checkbox" bind:checked={$examMode} on:change={toggleExamMode} />
		<span class="slider" />
	</label>
</div>

<style lang="scss">
	$slider-off-background: #a6a6a6;
	$slider-on-background: rgb(52, 120, 198);
	$slider-handle: #ffffff;
	$slider-handle-shadow: rgba(0, 0, 0, 0.2);
	$text-color: #ffffff;

	.toggle-container {
		display: flex;
		align-items: center;
		color: $text-color;
	}

	.toggle-label {
		margin-right: 10px;
		font-size: 16px;
	}

	.toggle-switch {
		position: relative;
		display: inline-block;
		width: 50px;
		height: 28px;
	}

	.toggle-switch input {
		opacity: 0;
		width: 0;
		height: 0;
	}

	.slider {
		position: absolute;
		cursor: pointer;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: $slider-off-background;
		transition: 0.4s;
		border-radius: 28px;

		&:before {
			position: absolute;
			content: '';
			height: 24px;
			width: 24px;
			left: 2px;
			bottom: 2px;
			background-color: $slider-handle;
			transition: 0.4s;
			border-radius: 50%;
			box-shadow: 0 2px 4px $slider-handle-shadow;
		}
	}

	input:checked + .slider {
		background-color: $slider-on-background;
	}

	input:focus + .slider {
		box-shadow: 0 0 1px $slider-on-background;
	}

	input:checked + .slider:before {
		transform: translateX(22px);
	}
</style>

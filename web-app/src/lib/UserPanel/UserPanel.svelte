<script>
	import ExamModeToggle from './ExamModeToggle.svelte';
    import UserCount from './UserCount.svelte';
	// import LiveStatistics from '../components/LiveStatistics.svelte';
</script>

<div class="admin-container">
	<h1>Dashboard administateur</h1>

	<ExamModeToggle />
    <UserCount />
	<h2>More Features Coming Soon...</h2>
</div>

<style lang="scss">
    .admin-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        color: white;
    }
</style>

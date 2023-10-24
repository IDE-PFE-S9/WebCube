<script>
    export let structure;
    let expanded = false;

    function toggleExpand() {
        expanded = !expanded;
    }
</script>

<div id="container">
    <div class="header">
        {structure}
    </div>
    <div class="methods">
       <!-- {#each as }
        
       {/each} -->
    </div>
    <div class="fields">
        <!-- {#each as }
        
        {/each} -->
    </div>
</div>

<style lang='scss'>
    .container {

    }
</style>
<script>
    import '@carbon/charts-svelte/styles.css'
    import { GaugeChart } from '@carbon/charts-svelte'

    export let name;
    export let nbTestStudent;
    export let nbTestTp;

    let percentage = Math.round((nbTestStudent / nbTestTp) * 100);
    let data = [{"group": "value","value": percentage}]
    let options = {
        "title": name,
        "resizable": true,
        "height": "150px    ",
        "width": "100%",
        "gauge": {
            "type": "semi",
            "status": "danger",
        },
        "toolbar":{"enabled":false},
        "theme": "g100"
    } 
</script>

<div class="individuel">
    <GaugeChart {data} {options} />
</div>

<style lang="scss">
    .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        width: 20rem;
    }
</style>
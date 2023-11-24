<script>
    import '@carbon/charts-svelte/styles.css'
	import { BarChartSimple } from '@carbon/charts-svelte'

    export let names;
    export let completions;

    let data = [];
    let chartHeight = data.length*50 + "px"

    let options = {
        "axes": {
            "left": {
                "mapsTo": "name",
                "scaleType": "labels"
            },
            "bottom": {
                "mapsTo": "percentage",
                "scaleType": "linear"
            }
        },
        "height": chartHeight,
        "width": "600px",
        "theme": "g100",
        "toolbar":{"enabled":false},
        "legend": {"enabled": false}
    }

    $: {
        data = names.map((name, index) => ({
            "group": "name",
            "name": name,
            "percentage": completions[index]
        }));
        data.sort((a, b) => a.name.localeCompare(b.name));
        data.reverse();
        chartHeight = data.length*50 + "px"

        options = {
            "axes": {
                "left": {
                    "mapsTo": "name",
                    "scaleType": "labels"
                },
                "bottom": {
                    "mapsTo": "percentage",
                    "scaleType": "linear"
                }
            },
            "height": chartHeight,
            "width": "600px",
            "theme": "g100",
            "toolbar":{"enabled":false},
            "legend": {"enabled": false}
        }
    }
</script>

<div class="individuel">
    <BarChartSimple {data} {options} />
</div>

<style lang="scss">
    .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        margin-bottom: 1rem;
    }
</style>
<script>
    import '@carbon/charts-svelte/styles.css'
	import { BarChartSimple } from '@carbon/charts-svelte'

    let color = "rgb(52,120,198)"
    
    let data = [
        {"group": "name","name": "Guilpin Erwan","percentage": 76},
        {"group": "name","name": "Meyniel Arthur","percentage": 23},
        {"group": "name","name": "Lurat Theo","percentage": 45},
        {"group": "name","name": "Mornet Ronan","percentage": 89},
        {"group": "name","name": "Menard Mathieu","percentage": 100},
        {"group": "name","name": "Menard Mathieu","percentage": 100},
        {"group": "name","name": "Menard Mat","percentage": 100},
        {"group": "name","name": "Guilpin Er","percentage": 76},
        {"group": "name","name": "Meyniel Art","percentage": 23},
        {"group": "name","name": "Lurat T","percentage": 45},
        {"group": "name","name": "Mornet Ro","percentage": 89},
        {"group": "name","name": "Menard Math","percentage": 100},
        {"group": "name","name": "Guilpin Erw","percentage": 76},
        {"group": "name","name": "Meyniel Arth","percentage": 23},
        {"group": "name","name": "Lurat Th","percentage": 0},
        {"group": "name","name": "Mornet Ron","percentage": 89},
        {"group": "name","name": "Menard Mathi","percentage": 100},
        {"group": "name","name": "Guilpin Erwa","percentage": 76},
        {"group": "name","name": "Meyniel Arthu","percentage": 23},
        {"group": "name","name": "Lurat The","percentage": 45},
        {"group": "name","name": "Mornet Rona","percentage": 89},
        {"group": "name","name": "Menard Mathie","percentage": 100}
    ]

    data.sort((a, b) => a.name.localeCompare(b.name));
    data.reverse();

    let dataLength = data.length
    let chartHeight = dataLength*50 + "px"

    let options = {
        "axes": {
            "left": {
                "mapsTo": "name",
                "scaleType": "labels"
            },
            "bottom": {
                "mapsTo": "percentage"
            }
        },
        "height": chartHeight,
        "width": "600px",
        "theme": "g100",
        "toolbar":{"enabled":false},
        "color": {
            "scale": {
                "value": color
            },
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
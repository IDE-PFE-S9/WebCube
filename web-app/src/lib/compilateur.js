const fs = require('fs');

export async function compilation(file) {
    const javaCode = fs.readFileSync(file, 'utf-8');
    const response = await fetch(`http://localhost:4000/jobe/index.php/restapi/runs/`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            run_spec: {
                language_id: 'java',
                sourcecode: javaCode
            }
        })
    });
    try{
         const dataReturned = await response.json();
        if (!response.ok) {
            const errorData = await response.json();
            console.log(errorData);
            return null;
        }
        if(response.status === 200 || response.status === 201){
            const output = response.data.stdout;
                if (output) {
                    console.log(output.trim());
                }
            } else {
                console.error('Erreur de compilation :', response.status);
            }
        return dataReturned;
    }
    catch(error){
        console.log('No response from server');
    }
        
}
import fs from 'fs';

export async function compilation(file) {
    const javaCode = fs.readFileSync(file, 'utf-8');
    const response = await fetch(`http://192.168.4.194:4000/jobe/index.php/restapi/runs/`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            run_spec: {
                language_id: 'java',
                sourcecode: javaCode
            }
        })
    });
    if (!response) {
        console.error('La réponse est indéfinie.');
        return null;
    }
    try{
         const dataReturned = await response.json();
        if (!response.ok) {
            const errorData = await response.json();
            console.log(errorData);
            return null;
        }
        if(response.ok){
            if(dataReturned){
                if(dataReturned.outcome == 11){
                    console.log('Compilation error');
                    console.log(dataReturned.cmpinfo);
                }

                if(dataReturned.outcome == 12){
                    console.log('Runtime error');
                    console.log(dataReturned.stderr);
                }

                if(dataReturned.outcome == 13){
                    console.log('Time limit exceeded');
                }

                if(dataReturned.outcome == 15){
                    const output = dataReturned.stdout
                    console.log(output.trim());
                }

                if(dataReturned.outcome == 17){
                    console.log('Memory limit exceeded');
                }
            }
            else{
                console.error('Error : no data returned');
            }
                
            } else {
                console.error('Error response statut: ', response.status);
            }
        return dataReturned;
        }
    catch(error){
        console.log('No response from server');
    }
}

compilation('/Users/ronanmornet/Downloads/CodeRunner/my-code.java')
//compilation('/Users/ronanmornet/Document/annee_I3/PFE/WebCube/web-app/src/testJava/JavaMainTest.java')
import requests
import json

# Read Java code from a file
with open("my-code.java", "r") as file:
    java_code = file.read()

# Replace with your Jobe server URL
url = "http://192.168.56.10:4000/jobe/index.php/restapi/runs/"

headers = {
    'Content-Type': 'application/json',
}

data = {
    'run_spec': {
        'language_id': 'java',
        'sourcecode': java_code
    }
}

response = requests.post(url, headers=headers, data=json.dumps(data))

if response.status_code in [200, 201]:
    output = response.json().get('stdout', None)
    if output:
        print(output.strip())

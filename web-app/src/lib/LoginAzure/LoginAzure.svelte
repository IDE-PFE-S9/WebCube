<script>

    let login;
    let password;


    let checkUser = () => {
        fetch(`http://localhost:8090/auth/login`, {
            method: 'POST',
            headers: {
                Authentication: 'Bearer Token',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "login": login,
                "password": password,
            })
        })
            .then(response => response.json()).then(data => {
            document.getElementById("password").style.backgroundColor = "#ffffff";
            Cookies.set('monCookie', data.accessToken, {secure: false});
            document.getElementById("message").textContent = "La méthode POST a réussi !";

        })
            .catch(err => {
                document.getElementById("password").value = "";
                //document.getElementById("password").style.backgroundColor = "#ff0000";
                document.getElementById("error_password").style.color = "#ff0000";
            })
    }
</script>

<div>
    <h1 style="text-align: center">Connexion</h1>
    <form on:submit|preventDefault="{checkUser}" class="formulaire">
        <input bind:value="{login}" type="text" id="login" name="login" class="login_username"
               placeholder="Identifiant ESEO" required>
        <div>
            <input bind:value="{password}" type="password" id="password" name="password" class="login_password"
                   placeholder="Mot de passe" required>
            <p class="error-password" id="error_password">Le mot de passe saisi est incorrect.</p>
        </div>
        <button type="submit" id="submit-login"><h2>Connexion</h2></button>
    </form>
    <p id="message"></p>
</div>


<style>
    .error-password {
        margin: 0;
        padding-top: 5px;
        font-size: 15px;
        text-align: center;
        color: white;
        min-height: 0;
    }

    body {
        background-color: #EAEDF2;
        margin-top: 80px; /* Doit être >= à ~3 fois la height du header */
        margin-bottom: 80px; /* Doit être >= à ~3 fois la height du footer */
        margin-left: 10px;
        margin-right: 10px;
        max-width: 96vw;
    }

    .container-login {
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        margin: 10px;
        border-radius: 8px;
        border: 1px solid #ddd;
        background: white;
        transition: box-shadow 0.15s ease;
        width: 500px;
    }

    .center {
        margin: 0;
        position: absolute;
        top: 50%;
        left: 50%;
        -ms-transform: translate(-50%, -50%);
        transform: translate(-50%, -50%);
    }

    .formulaire {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2.5rem;
    }

    .login_username, .login_password {
        width: 30rem;
        height: 4rem;

        background: #FFFFFF;
        border: 1px solid #000000;
        border-radius: 15px;

        /*font-family: 'Inter';*/
        font-style: normal;
        font-weight: 50;
        font-size: 1.5rem;
        text-align: left;
    }

    button {
        width: 80%;
        background-color: #007bff;
        border: none;
        color: #fff;
        padding: 10px 16px;
        border-radius: 4px;
        cursor: pointer;
    }

    button:hover {
        background-color: #0069d9;
        transition: background-color 0.2s ease;
    }

    button:active {
        background-color: #0062cc;
    }
</style>
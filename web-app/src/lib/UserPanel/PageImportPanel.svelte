<script>
    import { isResponseOk } from '$lib/auth.js';
    import Cookies from "js-cookie";
	import { onMount } from 'svelte';

    
    let maxFileSize = 3145728;

    let apiUrl = process.env.API_URL;

    // Variables pour stocker le fichier après le chargement
    let uploadedFile = null;
    let uploader;
    let students = null;
    let columns = ['firstname', 'surname', 'uniqueName'];
    let columnsShowed = ['Prénom', 'Nom', 'Mail'];
    let roles = [];
    let filteredStudents = students;
    let studentsByRole = {};
    let showStudentsByRole = {};
    let showTableByRole = {}; // Nouvelle variable pour indiquer si le tableau doit être affiché
    let selectedStudent = null; // Variable pour stocker l'étudiant sélectionné
    let isModalOpen = false; // Variable pour déterminer si la fenêtre modale est ouverte
    let unableToRemoveLastRole = false;

    onMount(async () => {
        students = await getStudents();
        console.log('students', students);

        // Récupérer tous les rôles uniques
        roles = [];
        if (students) {
            students.forEach(student => {
                student.roles.forEach(role => {
                    const roleId = role.idRole; // Obtenir l'ID du rôle
                    const roleName = role.role; // Obtenir le nom du rôle

                    // Ajouter le rôle à la liste si ce n'est pas déjà présent
                    const roleObject = { idRole: roleId, role: roleName };
                    if (!roles.some(existingRole => existingRole.idRole === roleId)) {
                        roles.push(roleObject);
                    }

                    // Ajouter l'étudiant au tableau du rôle correspondant
                    if (!studentsByRole[roleObject.role]) {
                        studentsByRole[roleObject.role] = [];
                        showStudentsByRole[roleObject.role] = true; // Initialiser à true pour afficher par défaut
                        showTableByRole[roleObject.role] = true; // Initialiser à true pour afficher par défaut
                    }
                    studentsByRole[roleObject.role].push(student);
                });
            });
        }
        console.log('roles', roles);   

        // Initialiser filteredStudents avec tous les étudiants par rôle
        filteredStudents = { ...studentsByRole };

        // Définir un ordre personnalisé pour les rôles
        const customRoleOrder = ['ROLE_GROUP_A', 'ROLE_GROUP_B', 'ROLE_RATTRAPAGE', 'ROLE_TEACHER'];

        // Tri des rôles selon l'ordre personnalisé
        roles = roles.sort((a, b) => customRoleOrder.indexOf(a.role) - customRoleOrder.indexOf(b.role));
    });


async function getStudents() {
    try {
        let studentResponse = await fetch(`${apiUrl}/api/student/all/`, {
            method: 'GET',
            headers: { 'Authorization-API': 'Bearer ' + Cookies.get('apiJWT') }
        });

        if (isResponseOk(studentResponse)) {
            const dataReturned = await studentResponse.json();
            console.log('dataReturned',dataReturned);
            return dataReturned;
        } 
    } catch (error) {
        console.error('An error occurred while fetching students:', error);
        return null;
    }
}

// Fonction pour charger le fichier
async function loadFile(event) {
    event.preventDefault();
    const file = uploader.files[0];
    const fileExtensionArray = file.type.split("/");
    const fileExtension = fileExtensionArray[fileExtensionArray.length-1];

    if (file.size > maxFileSize) {
        console.log("Above the max file size threshold")
        return;
    }

    if (fileExtension.includes('csv') && file.size < maxFileSize) {
        uploadedFile = file;
        console.log('File loaded:', uploadedFile);
    } else {
        console.log("Not an allowed file type");
    }
}

// Fonction pour envoyer le fichier à l'API
async function uploadFile() {
    if (!uploadedFile) {
        console.log("No file loaded. Please load a file first.");
        return;
    }

    const formData = new FormData();
    formData.append('file', uploadedFile);

    const response = await fetch(`${apiUrl}/api/csv/import`, {
        method: 'POST',
        body: formData,
        headers: { 'Authorization-API': 'Bearer ' + Cookies.get("apiJWT") }
    });

    if (await isResponseOk(response)) {
        console.log('File uploaded successfully');
        // Réinitialiser la variable uploadedFile après l'envoi du fichier si nécessaire
        uploadedFile = null;
        uploader.value = null; 
        update();
    }
}

// Fonction pour basculer l'affichage des étudiants par rôle
function toggleStudentsByRole(role) {
    showStudentsByRole[role] = !showStudentsByRole[role];
    showTableByRole[role] = !showTableByRole[role]; // Bascule également l'affichage du tableau
}

// Fonction pour ouvrir la fenêtre modale avec les rôles de l'étudiant
function openModal(student) {
    console.log('student', student);
    selectedStudent = student;
    isModalOpen = true;
}
  // Fonction pour fermer la fenêtre modale
  function closeModal() {
    selectedStudent = null;
    isModalOpen = false;
  }

  function addRoleToStudent(role) {
    console.log("selectedStudent", selectedStudent);
    console.log("role", role);

    // Vérifier si le rôle n'est pas déjà présent
    if (!selectedStudent.roles.some(studentRole => studentRole.role === role.role)) {
        // Utilisez la fonction `set` pour mettre à jour la liste de rôles de manière réactive
        selectedStudent.roles = [...selectedStudent.roles, role];
    }

    // Vérifier si le rôle "ROLE_GROUP_B" est déjà présent
    const hasRoleB = selectedStudent.roles.some(studentRole => studentRole.role === "ROLE_GROUP_B");

    // Vérifier si le rôle "ROLE_GROUP_A" est déjà présent
    const hasRoleA = selectedStudent.roles.some(studentRole => studentRole.role === "ROLE_GROUP_A");

    if (hasRoleB && role.role === "ROLE_GROUP_A") {
        console.log('hasRoleB', hasRoleB);
        removeRoleFromStudent({ idRole: 2, role: "ROLE_GROUP_B" });
    }
    if (hasRoleA && role.role === "ROLE_GROUP_B") {
        console.log('hasRoleA', hasRoleA);
        removeRoleFromStudent({ idRole: 1, role: "ROLE_GROUP_A" });
    }

    unableToRemoveLastRole = false; 
}


  // Fonction pour retirer un rôle à l'étudiant
  function removeRoleFromStudent(role) {
    console.log('role', role);
    if (selectedStudent.roles.length === 1) {
        console.log("Impossible de supprimer le dernier rôle de l'étudiant.");
        unableToRemoveLastRole = true;
        return;
    }
    selectedStudent.roles = selectedStudent.roles.filter(
        studentRole => studentRole.idRole !== role.idRole
    ); 
    unableToRemoveLastRole = false; 
    }

  // Fonction pour sauvegarder les modifications
async function saveChanges() {
    // Préparez les données pour l'envoi (à adapter selon votre structure de données)
    const requestData = {
        uniqueName: selectedStudent.uniqueName,
        role: selectedStudent.roles
    };
    console.log('requestData', requestData);

    // Envoie de la requête fetch avec les données mises à jour
    try {
        const response = await fetch(`${apiUrl}/api/update/role`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization-API': 'Bearer ' + Cookies.get("apiJWT")
            },
            body: JSON.stringify(requestData),
        });

        // Vérifiez la réponse du serveur et traitez-la en conséquence
        if (await isResponseOk(response)) {
            console.log('Modifications enregistrées avec succès');

            // Mettez à jour la liste des étudiants filtrés
            students = await getStudents();
            update();
            
            // Forcer la mise à jour de selectedStudent après la sauvegarde
            selectedStudent = { ...selectedStudent };

            closeModal();
        } else {
            console.error('Erreur lors de l\'enregistrement des modifications:');
        }
    } catch (error) {
        console.error('Erreur lors de l\'enregistrement des modifications:', error);
    }
}

// Fonction pour mettre à jour la liste des étudiants filtrés
async function update() {
    filteredStudents = [];
    students = [];
    studentsByRole = [];
    students = await getStudents();
        console.log('students', students);

        // Récupérer tous les rôles uniques
        roles = [];
        if (students) {
            students.forEach(student => {
                student.roles.forEach(role => {
                    const roleId = role.idRole; // Obtenir l'ID du rôle
                    const roleName = role.role; // Obtenir le nom du rôle

                    // Ajouter le rôle à la liste si ce n'est pas déjà présent
                    const roleObject = { idRole: roleId, role: roleName };
                    if (!roles.some(existingRole => existingRole.idRole === roleId)) {
                        roles.push(roleObject);
                    }

                    // Ajouter l'étudiant au tableau du rôle correspondant
                    if (!studentsByRole[roleObject.role]) {
                        studentsByRole[roleObject.role] = [];
                        showStudentsByRole[roleObject.role] = true; // Initialiser à true pour afficher par défaut
                        showTableByRole[roleObject.role] = true; // Initialiser à true pour afficher par défaut
                    }
                    studentsByRole[roleObject.role].push(student);
                });
            });
        }
        console.log('roles', roles);   

        // Initialiser filteredStudents avec tous les étudiants par rôle
        filteredStudents = { ...studentsByRole };

        // Définir un ordre personnalisé pour les rôles
        const customRoleOrder = ['ROLE_GROUP_A', 'ROLE_GROUP_B', 'ROLE_RATTRAPAGE', 'ROLE_TEACHER'];

        // Tri des rôles selon l'ordre personnalisé
        roles = roles.sort((a, b) => customRoleOrder.indexOf(a.role) - customRoleOrder.indexOf(b.role));
    }
</script>

<div class="import-container">
    <h1 class="title"> Importer des Étudiants :</h1>
    <p class="sub_title">Avec un fichier csv</p>

    <div class="import-file">
        <input 
            bind:this={uploader}
            on:change={loadFile} 
            type="file" 
        />
        <!-- Bouton pour charger le fichier -->
        <button on:click={uploadFile}>Envoyer fichier</button>
    </div>
</div>

<div>
    <div class="container_buton">
        <!-- Utiliser les rôles triés pour afficher les boutons -->
        {#each roles as role}
            <button on:click={() => toggleStudentsByRole(role.role)} class="buton_role"> {role.role}</button>
        {/each}
    </div>
    {#each roles as role}
        <div class="tableau">
            {#if showTableByRole[role.role]}
                <h2>{role.role}</h2>
                {#if showStudentsByRole[role.role]}
                    <table>
                        <thead>
                            <tr>
                                {#each columnsShowed as column}
                                    <th>{column}</th>
                                {/each}
                            </tr>
                        </thead>
                        <tbody>
                            {#each filteredStudents[role.role] || [] as student}
                                <tr>
                                    {#each columns as column}
                                        
                                            <td style="cursor: pointer;" on:click={() => openModal(student)}>{student[column]}</td>
                                        
                                    {/each}
                                </tr>
                            {/each}
                        </tbody>
                    </table>
                {/if}
            {/if}
        </div>
    {/each}
</div>

{#if isModalOpen}
  <div class="modal">
    <div class="modal-content">
      <span class="close" on:click={closeModal}>&times;</span>
      <h2>Rôles de {selectedStudent.firstname} {selectedStudent.surname}</h2>

      <!-- Afficher les rôles de l'étudiant d'un côté -->
      <div class="student-roles">
        <h3>Rôles de l'étudiant</h3>
        <ul>
          {#each selectedStudent.roles as studentRole}
            {#if studentRole.role !== "ROLE_TEACHER"}
                <li>
                <button on:click={() => removeRoleFromStudent(studentRole)} class="remove-button">-</button>
                {studentRole.role}
                </li>
            {/if}
          {/each}
        </ul>
      </div>
      

      <!-- Afficher les autres rôles de l'autre côté -->
      <div class="other-roles">
        <h3>Autres rôles</h3>
        <ul>
          {#each roles as role}
            
              {#if !selectedStudent.roles.some(studentRole => studentRole.role === role.role) && role.role !== "ROLE_TEACHER"}
              <li>
                <button on:click={() => addRoleToStudent(role)} class="add-button">+</button>
                {role.role}
            </li>
              {/if}
          {/each}
        </ul>
        
      </div>
      {#if unableToRemoveLastRole}
            <p>Impossible d'avoir un étudiant sans rôle.</p>
        {/if}

      <button on:click={saveChanges} class="save-button">Enregistrer les modifications</button>

    </div>
    
  </div>
{/if}



<style lang="scss">

    .import-container {
        display: flex;
		flex-direction: column;
		justify-content: center;
        align-items: center;
    }

    .title {
        text-align: left;
    }

    .sub_title {
        text-align: left;
        font-size: 1.5rem;
        color: rgb(200, 195, 195);
        padding-left: 0.5rem;
    }

    .import-file {
        display: flex;

    }

    .import-file input {
        padding: 0.4rem;
        max-width: 260px;
        font-size: 0.8rem;
        border: 2.5px solid;
        border-radius: 20px;
        margin: 10px;
        padding-right: 0rem;
        
    }

    .import-file button {
        padding: 0.3rem;
        max-width: 150px;
        font-size: 0.8rem;
        border: 2.5px solid;
        border-radius: 15px;
        margin: 10px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        &:hover,
        &:focus {
				background-color:  rgb(210, 208, 208);
			}

        padding-right: 1rem;
        padding-left: 1rem;

    }
    
    .container_buton {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        gap: 2rem;
        margin-top: 2rem;
        margin-bottom: 2rem;
        border-radius: 15px;
        margin-left: -5rem;
    }

    .buton_role {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        gap: 0.6rem;
        margin-top: 0rem;
        margin-bottom: 0rem;
        border-radius: 20px;
        height: 2rem;
        width: 10rem;
        padding-left: 1rem;
        padding-right: 1rem;
        &:hover{
            background-color: rgb(210, 208, 208);
        }
        
    }

    .tableau {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-top: 1rem;
    margin-bottom: 2rem;
    border-radius: 2px;
    margin-left: 1rem;
}

.tableau th,
.tableau td {
    border: 1px solid #ffffff; /* Couleur et style des bordures, ajustez selon vos préférences */
    padding: 0.5rem;
    text-align: center;
}

.modal {
    display: block;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
    background-color: #fefefe;
    margin: 15% auto;
    padding: 20px;
    border: 1px solid #888;
    width: 80%;
    color: black; // Assurez-vous que la couleur du texte est différente du fond
}

  .close {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
  }

  .close:hover,
  .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }

  .student-roles,
  .other-roles {
    width: 45%; // Ajustez la largeur selon vos besoins
    display: inline-block;
    vertical-align: top;
    padding: 1rem;
  }

  .student-roles {
    border-right: 1px solid #ccc; // Bordure entre les deux côtés
  }
  .add-button,
  .remove-button {
    margin-right: 0.5rem; // Ajustez l'espacement entre le bouton et le rôle selon vos besoins
    cursor: pointer;
  }

  .add-button {
    color: green; // Couleur pour le bouton d'ajout
  }

  .remove-button {
    color: red; // Couleur pour le bouton de suppression
  }
</style>
<script>
    import { userDetails } from "../stores";
  
    // Function to filter out duplicate username/IP pairs
    function filterUniqueUserDetails(userDetails) {
        const unique = new Map();
        userDetails.forEach(user => {
            const key = `${user.username}-${user.ip}`;
            if (!unique.has(key)) {
                unique.set(key, user);
            }
        });
        return Array.from(unique.values());
    }


    // check for potential frauds
    function isDuplicateConnection(user, userDetails) {
        return userDetails.some(otherUser => 
            (otherUser.username === user.username && otherUser.ip !== user.ip) ||
            (otherUser.ip === user.ip && otherUser.username !== user.username));
    }

    // Use a reactive statement to update when userDetails changes
    $: uniqueUserDetails = filterUniqueUserDetails($userDetails);
</script>

<h2>Utilisateurs connectés</h2>
<table>
    <thead>
        <tr>
            <th>Nom</th>
            <th>IP</th>
        </tr>
    </thead>
    <tbody>
        {#each uniqueUserDetails as user}
            <tr class:is-duplicate={isDuplicateConnection(user, $userDetails)}>
                <td>{user.username}</td>
                <td>{user.ip}</td>
            </tr>
        {/each}
    </tbody>
</table>

<style lang="scss">
    table {
  width: 80%;
  border-collapse: collapse;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);

  thead {
    tr {
      background-color: rgb(54, 117, 182);
      color: white;
      text-align: left;
    }

    th {
      padding: 10px 15px;
    }
  }

  tbody {
    td {
      padding: 10px 15px;
      border-bottom: 1px solid #ddd;
    }
  }
}
</style>
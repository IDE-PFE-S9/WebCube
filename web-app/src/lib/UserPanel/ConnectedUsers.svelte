<script>
    import { userDetails } from "../stores";
    
    console.log($userDetails);
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
        {#each $userDetails as user}
        <tr>
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
document.addEventListener('DOMContentLoaded', () => {
    getUsers();
});

async function getUsers() {
    let params = {
        isResigned: document.getElementById('isResigned').checked
    };
    let response = await fetchGetParams('user/all', params);
    let result = await response.json();

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let team = document.createElement('td');
            let teamDiv = document.createElement('div');

            let jobTitle = document.createElement('td');
            let jobTitleDiv = document.createElement('div');

            let name = document.createElement('td');
            let nameDiv = document.createElement('div');

            let enabled = document.createElement('td');
            let edit = document.createElement('td');
            let btnEdit = document.createElement('button');

            let pw = document.createElement('td');
            let btnPw = document.createElement('button');

            btnEdit.setAttribute('onclick', `newTab('admin/user/profiles/${e.id}')`);
            btnEdit.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnEdit.innerHTML = '<i class="fa-solid fa-pen-clip"></i>';
            edit.append(btnEdit);

            btnPw.setAttribute('onclick', `newTab('admin/user/pw/${e.id}')`);
            btnPw.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnPw.innerHTML = '<i class="fa-solid fa-key"></i>';
            pw.append(btnPw);

            teamDiv.classList.add('text-truncate');
            teamDiv.innerText = e.team;
            team.style.maxWidth = '7rem';
            team.append(teamDiv);

            jobTitleDiv.classList.add('text-truncate');
            jobTitleDiv.innerText = e.jobTitle;
            jobTitle.style.maxWidth = '5rem';
            jobTitle.append(jobTitleDiv);

            nameDiv.classList.add('text-truncate');
            nameDiv.innerText = e.name;
            name.style.maxWidth = '4rem';
            name.append(nameDiv);
            
            if(e.enabled) {
                enabled.classList.add('text-success');
                enabled.innerHTML = '<i class="fa-solid fa-check"></i>';
            } else {
                enabled.classList.add('text-danger');
                enabled.innerHTML = '<i class="fa-solid fa-user-lock"></i>';
            }

            tr.append(team, jobTitle, name, enabled, edit, pw);
            list.append(tr);
        });
    }
}

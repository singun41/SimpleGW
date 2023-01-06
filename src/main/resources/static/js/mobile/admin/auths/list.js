async function getAuths() {
    let menu = document.getElementById('menu').value;
    if(!menu) {
        alert('메뉴를 선택하세요.');
        return;
    }

    let response = await fetchGet(`auths/${menu}`);
    let result = await response.json();
    document.getElementById('tableHeader').classList.add('d-none');

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        document.getElementById('tableHeader').classList.remove('d-none');

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let role = document.createElement('td');
            let accessible = document.createElement('td');
            let rwdRole = document.createElement('td');
            let rwdOther = document.createElement('td');
            let edit = document.createElement('td');
            let btn = document.createElement('button');
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btn.innerHTML = '<i class="fa-solid fa-pen-clip"></i>';
            btn.setAttribute('onclick', `newTab('admin/auths/edit/${e.id}')`);
            edit.append(btn);

            role.innerText = e.role;
            if(e.role === 'ADMIN')
                role.classList.add('text-danger');

            if(e.accessible)
                accessible.innerHTML = '<i class="fa-solid fa-check text-success"></i>';
            else
                accessible.innerHTML = '<i class="fa-solid fa-ban text-danger"></i>';

            rwdRole.innerHTML = (
                e.rwdRole === 'R' ? 'Read-only' :
                e.rwdRole === 'RW' ? 'Read & Write' :
                e.rwdRole === 'RD' ? 'Read & Delete' :
                e.rwdRole === 'NONE' ? '<i class="fa-solid fa-ban text-danger"></i>' : e.rwdRole
            );

            rwdOther.innerHTML = (
                e.rwdOther === 'R' ? 'Read-only' :
                e.rwdOther === 'RW' ? 'Read & Write' :
                e.rwdOther === 'RD' ? 'Read & Delete' :
                e.rwdOther === 'NONE' ? '<i class="fa-solid fa-ban text-danger"></i>' : e.rwdOther
            );

            tr.append(role, accessible, rwdRole, rwdOther, edit);
            list.append(tr);
        });
    }
}

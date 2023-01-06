document.addEventListener('DOMContentLoaded', () => {
    getUsers();
});

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [3, 'asc'],   // order by names.
        ordering: true,
        columnDefs: [
            { targets: 0, width: '13%' },
            { targets: 1, width: '20%' },
            { targets: 2, width: '13%' },
            { targets: 3, width: '13%' },
            { targets: 4, width: '13%' },
            { targets: 5, width: '7%' },
            { targets: 6, width: '7%' },
            { targets: 7, width: '7%' },
            { targets: 8, width: '7%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 650,
        pageLength: 100
    });
}

function openNew() {
    let option = "width=1330, height=550";
    window.open('user/new', '', option);
}

async function getUsers() {
    let params = {
        isResigned: document.getElementById('isResigned').checked
    };
    let response = await fetchGetParams('user/all', params);
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let userId = document.createElement('td');
            let team = document.createElement('td');
            let jobTitle = document.createElement('td');
            let name = document.createElement('td');
            let role = document.createElement('td');
            let enabled = document.createElement('td');
            let edit = document.createElement('td');
            let btnEdit = document.createElement('button');
            let pw = document.createElement('td');
            let btnPw = document.createElement('button');
            let addon = document.createElement('td');
            let btnAddon = document.createElement('button');

            btnEdit.setAttribute('onclick', `openProfile('${e.id}')`);
            btnEdit.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnEdit.innerHTML = '<i class="fa-solid fa-up-right-from-square"></i>';
            edit.append(btnEdit);

            btnPw.setAttribute('onclick', `openPw('${e.id}')`);
            btnPw.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnPw.innerHTML = '<i class="fa-solid fa-up-right-from-square"></i>';
            pw.append(btnPw);

            btnAddon.setAttribute('onclick', `openAddon('${e.id}')`);
            btnAddon.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnAddon.innerHTML = '<i class="fa-solid fa-up-right-from-square"></i>';
            addon.append(btnAddon);

            userId.innerText = e.userId;
            team.innerText = e.team;
            jobTitle.innerText = e.jobTitle;
            name.innerText = e.name;
            
            role.innerText = e.role;
            if(e.role.toLowerCase() !== 'user') {
                role.classList.add('class', 'text-primary');
            }
            if(e.role.toLowerCase() === 'admin') {
                role.classList.remove('text-primary');
                role.classList.add('text-danger');
            }

            if(e.enabled) {
                enabled.classList.add('text-success');
                enabled.innerHTML = '<i class="fa-solid fa-check"></i>';
            } else {
                enabled.classList.add('text-danger');
                enabled.innerHTML = '<i class="fa-solid fa-user-lock"></i>';
            }

            tr.append(userId, team, jobTitle, name, role, enabled, edit, pw, addon);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function openProfile(id) {
    let option = "width=1330, height=550";
    window.open(`user/profiles/${id}`, '', option);
}

function openPw(id) {
    let option = "width=500, height=250";
    window.open(`user/pw/${id}`, '', option);
}

function openAddon(id) {
    let option = "width=700, height=350";
    window.open(`user/add-on/${id}`, '', option);
}

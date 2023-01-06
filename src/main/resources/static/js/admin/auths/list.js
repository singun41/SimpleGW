document.addEventListener('DOMContentLoaded', () => {
    buildDatatable();
});

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [0, 'asc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '15%' },
            { targets: 1, width: '25%' },
            { targets: 2, width: '25%' },
            { targets: 3, width: '25%' },
            { targets: 4, width: '10%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 650,
        pageLength: 100
    });
}

async function getAuths() {
    let menu = document.getElementById('menu').value;
    if(!menu) {
        alert('메뉴를 선택하세요.');
        return;
    }

    let response = await fetchGet(`auths/${menu}`);
    let result = await response.json();
    
    destroyDataTable();
    removeDatalist();
    
    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let role = document.createElement('td');
            let accessible = document.createElement('td');
            let rwdRole = document.createElement('td');
            let rwdOther = document.createElement('td');
            let edit = document.createElement('td');
            let btn = document.createElement('button');
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btn.innerHTML = '<i class="fa-solid fa-up-right-from-square"></i>';
            btn.setAttribute('onclick', `openEdit('${e.id}')`);
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
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function openEdit(id) {
    let option = "width=500, height=350";
    window.open(`auths/edit/${id}`, '', option);
}

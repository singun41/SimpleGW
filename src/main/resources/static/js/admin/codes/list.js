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
        order: [1, 'asc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '7%' },
            { targets: 1, width: '10%' },
            { targets: 2, width: '22%' },
            { targets: 3, width: '47%' },
            { targets: 4, width: '7%' },
            { targets: 5, width: '7%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 650,
        pageLength: 100
    });
}


async function getCodes() {
    let type = document.getElementById('type').value;
    if(!type) {
        alert('Type을 선택하세요.');
        return;
    }

    let response = await fetchGet(`basecode/${type}`);
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            
            let seq = document.createElement('td');
            let code = document.createElement('td');
            let value = document.createElement('td');
            let remarks = document.createElement('td');
            let enabled = document.createElement('td');
            let edit = document.createElement('td');
            
            let i = document.createElement('i');
            i.classList.add('fa-solid', 'fa-arrow-up-right-from-square');

            let btn = document.createElement('button');
            btn.setAttribute('type', 'button');
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btn.setAttribute('onclick', `openEdit('${e.id}')`);
            btn.append(i);
            edit.append(btn);

            seq.innerText = e.seq;
            code.innerText = e.code;
            value.innerText = e.value;
            remarks.innerText = e.remarks;
            remarks.classList.add('text-start');
            
            if(e.enabled) {
                enabled.classList.add('text-success');
                enabled.innerHTML = '<i class="fa-solid fa-check"></i>';
            } else {
                enabled.classList.add('text-danger');
                enabled.innerHTML = '<i class="fa-solid fa-ban"></i>';
            }

            tr.append(seq, code, value, remarks, enabled, edit);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function openEdit(id) {
    let option = "width=1000, height=300";
    window.open(`code/${id}`, '', option);
}

function openNew() {
    let type = document.getElementById('type').value;
    if(!type) {
        alert('Type을 선택하세요.');
        return;
    }
    let option = "width=1000, height=300";
    window.open(`code/new/${type}`, '', option);
}

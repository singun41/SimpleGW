document.addEventListener('DOMContentLoaded', () => {
    buildDatatable();
});

let referrerAddIds = [];
let referrerAddNames = [];
const referrers = document.getElementById('referrers');

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [1, 'asc'],
        ordering: false,
        columnDefs: [
            // 0 = id hidden field.
            { targets: 0, width: '0%' },
            { targets: 1, width: '75%' },
            { targets: 2, width: '25%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 420,
        pageLength: 100,
        paging: false,
        searching: false,
        info: false
    });
}

async function getTeamMembers() {
    let team = document.getElementById('team').value;
    let response = await fetchGet(`team/${team}/without-me`);
    let result = await response.json();
    
    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            id.innerText = e.id;
            id.classList.add('d-none');

            let name = document.createElement('td');
            name.innerText = `${e.jobTitle} ${e.name}`;

            let choose = document.createElement('td');
            let btnChoose = document.createElement('button');
            btnChoose.setAttribute('onclick', 'add(this)');
            let iChoose = document.createElement('i');

            iChoose.classList.add('fa-solid', 'fa-check');
            btnChoose.classList.add('btn', 'btn-outline-secondary', 'btn-sm');

            btnChoose.append(iChoose);
            choose.append(btnChoose);

            tr.append(id, name, choose);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function add(e) {
    let id = e.parentNode.parentNode.childNodes[0].innerText;
    let name = e.parentNode.parentNode.childNodes[1].innerText;

    let isDuplicated = false;
    referrerAddIds.forEach(el => {
        if(el === id)
            isDuplicated = true;
    });

    if(isDuplicated) {
        alert('이미 등록한 멤버입니다.');
        return;
    }

    referrerAddIds.push(id);
    referrerAddNames.push(name);

    let tr = document.createElement('tr');
    tr.classList.add('text-center');

    let tdId = document.createElement('td');
    let tdName = document.createElement('td');
    let tdDel = document.createElement('td');

    tdId.classList.add('d-none');
    tdId.innerText = id;
    tdName.innerText = name;

    let btnDel = document.createElement('a');
    btnDel.setAttribute('role', 'button');
    btnDel.setAttribute('onclick', 'del(this)');

    let i = document.createElement('i');
    i.classList.add('fa-solid', 'fa-xmark');

    btnDel.append(i);
    tdDel.append(btnDel);

    tr.append(tdId, tdName, tdDel);
    referrers.append(tr);
}

function del(e) {
    let id = e.parentNode.parentNode.childNodes[0].innerText;
    let name = e.parentNode.parentNode.childNodes[1].innerText;

    referrerAddIds = referrerAddIds.filter(el => { return el !== id });
    referrerAddNames = referrerAddNames.filter(el => { return el !== name });

    e.parentNode.parentNode.remove();
}

function apply() {
    if(!confirm('선택한 멤버들에게 공유하시겠습니까?'))
        return;
    
    let referrerAddData = {
        arrReferrerId: referrerAddIds,
        arrReferrerName: referrerAddNames
    };
    localStorage.setItem('referrerAddData', JSON.stringify(referrerAddData));
    opener.addReferrer();
    window.close();
}

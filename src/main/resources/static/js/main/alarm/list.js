document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    getAlarms();
});

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [0, 'desc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '10%' },
            { targets: 1, width: '12%' },
            { targets: 2, width: '10%' },
            { targets: 3, width: '40%' },
            { targets: 4, width: '18%' },
            { targets: 5, width: '10%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 520,
        pageLength: 10
    });
}

async function getAlarms() {
    let response = await fetchGet('alarm');
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            let date = document.createElement('td');
            let time = document.createElement('td');
            let title = document.createElement('td');
            let cdt = document.createElement('td');
            let edit = document.createElement('td');

            let btn = document.createElement('button');
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btn.setAttribute('onclick', `openEdit('${e.id}')`);
            btn.innerHTML = '<i class="fa-solid fa-up-right-from-square"></i>';
            edit.append(btn);

            id.innerText = e.id;
            date.innerText = dayjs(e.alarmDate).format('YY. MM. DD.');
            time.innerText = e.alarmTime.substr(0, 5);
            title.innerText = e.title;
            cdt.innerText = dayjs(e.createdDatetime).format('YY. MM. DD. HH:mm:ss');

            tr.append(id, date, time, title, cdt, edit);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function openNew() {
    let option = "width=800, height=450";
    window.open('alarm/new', '', option);
}

function openEdit(id) {
    let option = "width=800, height=450";
    window.open(`alarm/${id}`, '', option);
}

function afterCUD() {
    getAlarms();
    if(opener)   // getAlarms() 메서드를 연속 2번 호출하므로, 서버에서 캐싱이 완료되기 위해서 잠시 멈춘다.
        setTimeout(() => { opener.getTodayAlarms(); }, 1000 * 2);
}

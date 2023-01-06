document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr(searchDtp, {
        mode: 'range',
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: [ dayjs().add(-1, 'month').format('YYYY. MM. DD.'), dayjs().format('YYYY. MM. DD.') ],
        'locale': 'ko'
    });

    search();
});
const searchDtp = document.getElementById('searchDtp');

async function search() {
    if(!searchDtp.value) {
        alert('기간을 선택하세요.');
        return;
    }

    let dt = searchDtp.value.replaceAll(' ', '').replaceAll('.', '-').split('~');
    let dtFrom = dt[0].substr(0, 10);
    let dtTo;

    if(dt.length === 1)
        dtTo = dtFrom;
    else
        dtTo = dt[1].substr(0, 10);

    let params = {
        type: document.getElementById('type').value,
        dateFrom: dtFrom,
        dateTo: dtTo
    };
    let role = document.getElementById('role').value;
    let response = await fetchGetParams(`approval-list/${role}`, params);
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            let type = document.createElement('td');
            
            let writer = document.createElement('td');
            let title = document.createElement('td');

            let status = document.createElement('td');
            let approver = document.createElement('td');
            let createdDate = document.createElement('td');

            id.textContent = e.id;
            type.textContent = e.typeTitle;
            writer.textContent = `${e.writerJobTitle} ${e.writerName}`;

            let link = e.useEditors ? `/page/approval/forms/${e.type.toLowerCase()}/${e.id}` : `/page/approval/${e.type.toLowerCase()}/${e.id}`;

            let btn = document.createElement('button');
            let i = document.createElement('i');
            btn.setAttribute('type', 'button');
            btn.setAttribute('onclick', `openPopup('${link}')`);
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm', 'me-3');
            i.classList.add('fa-solid', 'fa-arrow-up-right-from-square');
            btn.append(i);
            title.append(btn);

            let a = document.createElement('a');
            a.setAttribute('href', link);
            a.innerText = e.title;
            a.classList.add('text-decoration-none', 'text-dark');

            title.append(a)
            title.classList.add('text-start', 'align-middle');

            status.innerHTML = e.sign === 'PROCEED' ? '결재중' :
                                e.sign === 'CONFIRMED' ? '<i class="fa-solid fa-check text-success"></i>'
                                                        : '<i class="fa-solid fa-ban text-danger"></i>';

            approver.textContent = `${e.approverJobTitle} ${e.approverName}`;
            createdDate.textContent = dayjs(e.createdDate).format('YY. MM. DD.');

            tr.append(id, type, writer, title, status, approver, createdDate);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

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
const searchDtp = document.getElementsByClassName('input-date-range')[0];

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
        dateFrom: dtFrom,
        dateTo: dtTo,
        adminSearch: document.getElementById('adminSearch').checked ? true : false
    };

    let docsType = document.getElementById('docsType').innerText;
    let response = await fetchGetParams(`${docsType}/list`, params);
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            let title = document.createElement('td');
            let writer = document.createElement('td');
            let createdDate = document.createElement('td');

            let btn = document.createElement('button');
            let i = document.createElement('i');
            btn.setAttribute('type', 'button');
            btn.setAttribute('onclick', `openPopup('${e.id}')`);
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm', 'me-3');
            i.classList.add('fa-solid', 'fa-arrow-up-right-from-square');
            btn.append(i);
            title.append(btn);

            let a = document.createElement('a');
            a.setAttribute('href', e.id);
            a.innerText = e.title;
            a.classList.add('text-decoration-none', 'text-dark');

            id.textContent = e.id;
            title.append(a);
            writer.textContent = `${e.writerJobTitle} ${e.writerName}`;
            createdDate.textContent = dayjs(e.createdDate).format('YY. MM. DD.');

            title.classList.add('text-start', 'align-middle');

            tr.append(id, title, writer, createdDate);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

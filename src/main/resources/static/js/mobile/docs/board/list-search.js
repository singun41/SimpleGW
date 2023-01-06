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

    let pageType = document.getElementById('pageType').innerText;
    let btnBack = document.getElementById('btnBack');
    if(pageType === 'notice' || pageType === 'freeboard') {
        btnBack.setAttribute('onclick', `page('${pageType}/list')`);
    } else {
        btnBack.setAttribute('onclick', 'page("board")');
    }

    if(pageType === 'archive')
        document.getElementById('btnWritePage').classList.add('d-none');
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

    let type = document.getElementById('pageType').innerText;
    let params = {
        dateFrom: dtFrom,
        dateTo: dtTo
    };

    let response = await fetchGetParams(`${type}/list`, params);
    let result = await response.json();

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);
        
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            let td = document.createElement('td');
            let title = document.createElement('div');
            
            title.classList.add('text-truncate');
            title.innerText = e.title;

            td.classList.add('td-title');
            td.append(title);
            
            tr.append(td);
            tr.setAttribute('onclick', `newTab('${type}/${e.id}')`);

            list.append(tr);
        });
    }
}

function writePage() {
    page(`${document.getElementById('pageType').innerText}/write`);
}

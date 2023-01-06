document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr(searchDtp, {
        mode: 'range',
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: [ dayjs().add(-1, 'month').format('YYYY. MM. DD.'), dayjs().format('YYYY. MM. DD.') ],
        'locale': 'ko'
    });
});
const searchDtp = document.getElementsByClassName('input-date-range')[0];

const searchId = document.getElementById('idSearch');
const inputDocsId = document.getElementById('inputDocsId');

const team = document.getElementById('team');
const user = document.getElementById('user');
const searchWriter = document.getElementById('writerSearch');

function idSearch() {
    inputDocsId.value = '';
    inputDocsId.readOnly = !searchId.checked;
    if(searchWriter.checked)
        searchWriter.click();
}

function writerSearch() {
    user.selectedIndex = 0;
    team.selectedIndex = 0;
    user.disabled = !searchWriter.checked;
    team.disabled = !searchWriter.checked;
    if(searchId.checked)
        searchId.click();
}

function userSelectBoxInit() {
    while(user.hasChildNodes())
        user.removeChild(user.firstChild);

    let defOpt = document.createElement('option');
    defOpt.value = '0';
    defOpt.textContent = '작성자 선택';
    defOpt.selected = true;
    defOpt.disabled = true;
    user.append(defOpt);
}

async function getTeamMember() {
    let response = await fetchGet(`team/${team.value}`);
    let result = await response.json();

    if(response.ok) {
        userSelectBoxInit();

        Array.from(result.obj).forEach(e => {
            let opt = document.createElement('option');
            opt.value = e.id;
            opt.textContent = `${e.jobTitle} ${e.name}`;
            user.append(opt);
        });
    }
}

async function search() {
    let response;
    let result;

    if(searchId.checked) {
        if(inputDocsId.value === '') {
            alert('문서번호를 입력하세요.');
            return;
        }

        let params = {
            docsId: inputDocsId.value
        }
        response = await fetchGetParams('approval-list/no', params)
        result = await response.json();

    } else {
        if(!searchDtp.value) {
            alert('기간을 선택하세요.');
            return;
        }
    
        if(searchWriter.checked && user.value === '') {
            alert('작성자를 선택하세요.');
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
            writerId: user.value,
            type: document.getElementById('type').value,
            dateFrom: dtFrom,
            dateTo: dtTo
        };
        response = await fetchGetParams('approval-list', params);
        result = await response.json();
    }
    
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

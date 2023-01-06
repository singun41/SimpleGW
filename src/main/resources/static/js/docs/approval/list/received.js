document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
});

async function searchNew(type) {   // received-approver.js, received-referrer.js에서 호출.
    let response = await fetchGet(`approval-list/${type}/new`);
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
            btn.setAttribute('onclick', `openPopup('${link}')` );
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

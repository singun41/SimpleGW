document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    getTempDocs();
});

async function getTempDocs() {
    let response = await fetchGet('docs/temp/list');
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            let title = document.createElement('td');
            let type = document.createElement('td');
            let createdDate = document.createElement('td');

            let link = (
                e.group === 'APPROVAL' ? (
                    e.useEditors ? `/page/approval/forms/${e.type.toLowerCase()}/temp/${e.id}` : `/page/approval/${e.type.toLowerCase()}/temp/${e.id}`
                ) : `/page/${e.type.toLowerCase()}/temp/${e.id}`
            );

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

            id.textContent = e.id;
            title.append(a);
            type.textContent = e.typeTitle;
            createdDate.textContent = `${dayjs(e.createdDate).format('YY. MM. DD.')} ${e.createdTime.substr(0, 5)}`;

            title.classList.add('text-start', 'align-middle');

            tr.append(id, title, type, createdDate);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

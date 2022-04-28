window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
    getList();
});

function destroyDataTable() {
    $('#boardListTable').DataTable().destroy();
}

function setDataTable() {
    $('#boardListTable').DataTable({
        order: [0, 'desc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '10%' },
            { targets: 1, width: '15%' },
            { targets: 2, width: '40%' },
            { targets: 3, width: '25%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 585
    });
}

async function getList() {
    destroyDataTable();

    let result = await fetchCustom('GET', 'default', '', 'temporary/list', 'json');
    result.forEach(elem => {
        let tr = document.createElement('tr');
        tr.setAttribute('class', 'text-center');

        let tdNo = document.createElement('td');
        tdNo.textContent = elem.id;

        let tdKind = document.createElement('td');
        tdKind.textContent = elem.kindTitle;

        let docsKind = elem.kind.toLowerCase().replaceAll('_', '-');

        let tdTitle = document.createElement('td');
        tdTitle.setAttribute('class', 'text-left');

        let tdTitleInnerAnchor = document.createElement('a');
        let tdTitlePopupButton = document.createElement('button');
        let tdTitleButtonIcon = document.createElement('i');

        let linkText = (elem.type === 'APPROVAL') ? ('/approval/' + docsKind + '/' + elem.id) : ('/' + docsKind + '/' + elem.id)

        tdTitlePopupButton.setAttribute('onclick', 'openToPopup("' + linkText + '")');
        tdTitlePopupButton.setAttribute('class', 'btn btn-outline-secondary btn-sm mr-4');
        tdTitleButtonIcon.setAttribute('class', 'fas fa-external-link-alt');
        tdTitlePopupButton.append(tdTitleButtonIcon);

        tdTitleInnerAnchor.setAttribute('class', 'text-decoration-none text-dark');
        tdTitleInnerAnchor.setAttribute('href', linkText);
        tdTitleInnerAnchor.textContent = elem.title;

        tdTitle.prepend(tdTitlePopupButton);
        tdTitle.append(tdTitleInnerAnchor);

        let tdCdate = document.createElement('td');
        tdCdate.textContent = elem.createdDate.replaceAll('-', '. ') + '. ' + elem.createdTime;

        tr.append(tdNo, tdKind, tdTitle, tdCdate);
        document.getElementById('boardListTbody').append(tr);
    });

    setDataTable();
}
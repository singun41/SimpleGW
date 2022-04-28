window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
});

function setTitleAndIcon(titleText, iconClass) {
    const icon = document.getElementById('boardListIcon');
    const title = document.getElementById('boardListTitle');

    icon.setAttribute('class', iconClass + ' custom-card-header-fas-icon');
    title.append(icon, titleText);
}

function destroyDataTable() {
    $('#boardListTable').DataTable().destroy();
}

function setDataTable() {
    $('#boardListTable').DataTable({
        order: [0, 'desc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '10%' },
            { targets: 1, width: '50%' },
            { targets: 2, width: '23%' },
            { targets: 3, width: '17%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 585
    });
}

async function searchBoardList(pageTitle) {
    const searchConditions = {
        dateStart: document.getElementById('searchDateStart').value,
        dateEnd: document.getElementById('searchDateEnd').value
    };
    boardListBind(await fetchCustom('GET', 'urlEncoded', searchConditions, pageTitle + '/list', 'json'), pageTitle);
}

function boardListBind(arr, pageTitle) {
    destroyDataTable();

    const boardListTbody = document.getElementById('boardListTbody');
    while(boardListTbody.hasChildNodes()) {
        boardListTbody.removeChild(boardListTbody.firstChild);
    }

    if(arr.length === 0) { setDataTable(); return; }
    arr.forEach(elem => {
        let tr = document.createElement('tr');
        
        let tdDocsId = document.createElement('td');
        let tdTitle = document.createElement('td');
        let tdTitleInnerAnchor = document.createElement('a');
        let tdTitlePopupButton = document.createElement('button');
        let tdTitleButtonIcon = document.createElement('i');
        let tdWriter = document.createElement('td');
        let tdCdate = document.createElement('td');
        
        tdDocsId.textContent = elem.id;
        tdDocsId.setAttribute('class', 'text-center');
        
        tdTitleInnerAnchor.setAttribute('class', 'text-decoration-none text-dark');
        tdTitleInnerAnchor.setAttribute('href', '/' + pageTitle + '/' + elem.id);
        tdTitleInnerAnchor.textContent = elem.title;

        tdTitlePopupButton.setAttribute('onclick', 'openToPopup("/' + pageTitle + '/' + elem.id + '")');
        tdTitlePopupButton.setAttribute('class', 'btn btn-outline-secondary btn-sm mr-4');
        tdTitleButtonIcon.setAttribute('class', 'fas fa-external-link-alt');
        tdTitlePopupButton.append(tdTitleButtonIcon);

        tdTitle.prepend(tdTitlePopupButton);
        tdTitle.append(tdTitleInnerAnchor);
        // tdTitle.setAttribute('onclick', 'location.href="/' + pageTitle + '/' + elem.id + '"');

        tdWriter.textContent = elem.writerTeam + ' ' + elem.writerJobTitle + ' ' + elem.writerName;
        tdCdate.textContent = elem.createdDate.replaceAll('-', '. ') + '.';
        tdCdate.setAttribute('class', 'text-center');
    
        tr.append(tdDocsId, tdTitle, tdWriter, tdCdate);
        boardListTbody.append(tr);
    });

    setDataTable();
}

window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
});

function setTitleAndIcon(titleText, iconClass) {
    const icon = document.getElementById('boardApprovalListIcon');
    const title = document.getElementById('boardApprovalListTitle');

    icon.setAttribute('class', iconClass + ' custom-card-header-fas-icon');
    title.append(icon, titleText);
}

function destroyDataTable() {
    $('#tableApprovalList').DataTable().destroy();
}

function setDataTable() {
    $('#tableApprovalList').DataTable({
        order: [0, 'desc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '10%' },
            { targets: 1, width: '50%' },
            { targets: 2, width: '10%' },
            { targets: 3, width: '15%' },
            { targets: 4, width: '15%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 585
    });
}

async function searchApprovalList(pageTitle) {
    const searchConditions = {
        dateStart: document.getElementById('searchDateStart').value,
        dateEnd: document.getElementById('searchDateEnd').value
    };
    boardApprovalListBind(await fetchCustom('GET', 'urlEncoded', searchConditions, 'approval/' + pageTitle + '/list', 'json'), pageTitle);
}

function boardApprovalListBind(arr, pageTitle) {
    destroyDataTable();

    const approvalList = document.getElementById('approvalList');
    while(approvalList.hasChildNodes()) {
        approvalList.removeChild(approvalList.firstChild);
    }

    if(arr.length === 0) { setDataTable(); return; }
    arr.forEach(elem => {
        let tr = document.createElement('tr');
        
        let tdDocsId = document.createElement('td');
        let tdTitle = document.createElement('td');
        let tdTitleInnerAnchor = document.createElement('a');
        let tdTitlePopupButton = document.createElement('button');
        let tdTitleButtonIcon = document.createElement('i');
        let tdApprover = document.createElement('td');
        let tdStatus = document.createElement('td');
        let tdCdate = document.createElement('td');

        tdDocsId.textContent = elem.id;
        tdDocsId.setAttribute('class', 'text-center');
        
        tdTitleInnerAnchor.setAttribute('class', 'text-decoration-none text-dark');
        tdTitleInnerAnchor.setAttribute('href', '/approval/' + pageTitle + '/' + elem.id);
        tdTitleInnerAnchor.textContent = elem.title;

        tdTitlePopupButton.setAttribute('onclick', 'openToPopup("/approval/' + pageTitle + '/' + elem.id + '")');
        tdTitlePopupButton.setAttribute('class', 'btn btn-outline-secondary btn-sm mr-4');
        tdTitleButtonIcon.setAttribute('class', 'fas fa-external-link-alt');
        tdTitlePopupButton.append(tdTitleButtonIcon);

        tdTitle.prepend(tdTitlePopupButton);
        tdTitle.append(tdTitleInnerAnchor);

        if(elem.status === 'CONFIRMED') {
            tdStatus.textContent = '승인';
            tdStatus.setAttribute('class', 'text-success');
        } else if(elem.status === 'REJECTED') {
            tdStatus.textContent = '반려';
            tdStatus.setAttribute('class', 'text-danger');
        } else {
            tdStatus.textContent = '결재중';
            tdStatus.setAttribute('class', 'text-primary');
        }
        tdStatus.classList.add('text-center');

        // tdApprover.textContent = elem.approverTeam + ' ' + elem.approverName + ' ' + elem.approverJobTitle;
        tdApprover.textContent = elem.approverJobTitle + ' ' + elem.approverName;
        tdApprover.setAttribute('class', 'text-center');

        tdCdate.textContent = elem.createdDate;
        tdCdate.setAttribute('class', 'text-center');
    
        tr.append(tdDocsId, tdTitle, tdStatus, tdApprover, tdCdate);
        approvalList.append(tr);
    });

    setDataTable();
}

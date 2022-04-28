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
            { targets: 0, width: '9%' },
            { targets: 1, width: '15%' },
            { targets: 2, width: '40%' },
            { targets: 3, width: '10%' },
            { targets: 4, width: '13%' },
            { targets: 5, width: '13%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 585
    });
}

async function getSubmittedDocsList(param, url) {
    let result = await fetchCustom('GET', 'urlEncoded', param, url, 'json');

    destroyDataTable();
    const approvalList = document.getElementById('approvalList');
    while(approvalList.hasChildNodes()) {
        approvalList.removeChild(approvalList.firstChild);
    }

    if(result.length == 0) { setDataTable(); return; }
    result.forEach(elem => {
        let tr = document.createElement('tr');

        let tdDocsId = document.createElement('td');
        let tdKind = document.createElement('td');

        let tdTitle = document.createElement('td');
        let tdTitleInnerAnchor = document.createElement('a');
        let tdTitlePopupButton = document.createElement('button');
        let tdTitleButtonIcon = document.createElement('i');

        let tdApprover = document.createElement('td');
        let tdStatus = document.createElement('td');
        let tdCdate = document.createElement('td');

        tdDocsId.textContent = elem.id;
        tdDocsId.setAttribute('class', 'text-center');
        
        tdKind.textContent = elem.kindTitle;
        tdKind.setAttribute('class', 'text-center');

        let docsKind = elem.kind.toLowerCase().replaceAll('_', '-');

        tdTitleInnerAnchor.setAttribute('class', 'text-decoration-none text-dark');
        tdTitleInnerAnchor.setAttribute('href', '/approval/' + docsKind + '/' + elem.id);
        tdTitleInnerAnchor.textContent = elem.title;

        tdTitlePopupButton.setAttribute('onclick', 'openToPopup("/approval/' + docsKind + '/' + elem.id + '")');
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

        tdCdate.textContent = elem.createdDate.replaceAll('-', '. ') + '.';
        tdCdate.setAttribute('class', 'text-center');
    
        tr.append(tdDocsId, tdKind, tdTitle, tdStatus, tdApprover, tdCdate);
        approvalList.append(tr);
    });

    setDataTable();
}
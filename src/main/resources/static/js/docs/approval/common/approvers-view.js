async function confirmed() {
    if(!confirm('승인하시겠습니까?'))
        return;
    
    let response = await fetchPatch(`approval/confirmed/${docsType}/${docsId}`);
    let result = await response.json();
    alert(result.msg);
    if(response.ok)
        location.href = '/page/approval/received-list/approver/new';
}

async function rejected() {
    if(!confirm('반려하시겠습니까?'))
        return;

    let response = await fetchPatch(`approval/rejected/${docsType}/${docsId}`);
    let result = await response.json();
    alert(result.msg);
    if(response.ok)
        location.href = '/page/approval/received-list/approver/new';
}

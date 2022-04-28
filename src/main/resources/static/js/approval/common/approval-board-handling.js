const approvalDocsId = document.getElementById('docsId').innerText;
let approverParam = {
    docsId: approvalDocsId
}

async function confirmed() {
    if(confirm('승인하시겠습니까?')) {
        await fetchCustom('PUT', 'urlEncoded', approverParam, 'approval/confirmed', 'msg');

        if(opener) {   // 팝업창에서 처리하면 리스트 조회하고 창을 닫는다.
            opener.searchReceivedApprovalDocs();
            window.close();
        } else {
            location.href = '/approval/received/current/approver';
        }
    }
}

async function rejected() {
    if(confirm('반려하시겠습니까?')) {
        await fetchCustom('PUT', 'urlEncoded', approverParam, 'approval/rejected', 'msg');

        if(opener) {   // 팝업창에서 처리하면 리스트 조회하고 창을 닫는다.
            opener.searchReceivedApprovalDocs();
            window.close();
        } else {
            location.href = '/approval/received/current/approver';
        }
    }
}

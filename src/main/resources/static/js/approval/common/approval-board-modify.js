window.addEventListener('DOMContentLoaded', event => {
    getApprovalLine(docsId);
});

const docsId = document.getElementById('docsId').innerText;

// async function getContent(contentUrl) {
//     const content = await fetchCustom('GET', 'default', '', contentUrl + docsId, 'text');
//     CKEDITOR.instances.ckeditorTextarea.setData(content);
// }

// 결재문서는 종류에 따라 ckeditor를 사용하지 않을 수 있기 때문에 title과 content 체크를 나눈다.
function isEmptyTitle(title) {
    if(!title) {
        alert('제목을 작성하세요.');
        return true;
    } else {
        return false;
    }
}
function isEmptyContent(content) {
    if(!content) {
        alert('내용을 작성하세요.');
        return true;
    } else {
        return false;
    }
}

async function updateApprovalDocs(docsKind, isRegistered) {
    if(isEmptyTitle(document.getElementById('title').value)) {
        return;
    }
    if(isEmptyContent(CKEDITOR.instances.ckeditorTextarea.getData())) {
        return;
    }
    let result = await saveApprovalDocs(docsKind, isRegistered);

    if(result.message === 'ok') {
        let uploadFilesResult = await uploadFiles(docsId);
        if(uploadFilesResult === 'ok') {
            alert('등록하였습니다.');
            location.href = '/approval/' + docsKind + '/' + docsId;
        } else {
            alert(uploadFilesResult);
        }
    } else {
        alert(result);
    }
}

async function saveApprovalDocs(docsKind, isRegistered) {
    let approvalDocs = {
        id: docsId,
        title: document.getElementById('title').value,
        content: CKEDITOR.instances.ckeditorTextarea.getData(),   // ckeditor 내용 가져오기
        approvers: approver,
        referrers: referrer,
        registered: isRegistered
    };

    return await fetchCustom('PUT', 'default', approvalDocs, 'approval/' + docsKind, 'json');
}
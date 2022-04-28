window.addEventListener('beforeunload', event => {
    // 페이지를 나갈 때
    event.preventDefault();
    if(CKEDITOR.instances.ckeditorTextarea.getData() !== '' && !docSaveComplete) {
        event.returnValue = '';
    }
});
let docSaveComplete = false;

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

// 결재 문서 종류 저장시 공용 function
async function insertApprovalDocs(docsKind, isRegistered) {
    if(isEmptyTitle(document.getElementById('title').value)) {
        return;
    }
    if(isEmptyContent(CKEDITOR.instances.ckeditorTextarea.getData())) {
        return;
    }
    const docsId = await saveApprovalDocs(docsKind, isRegistered);

    if(docsId) {   // 에러없이 리턴값 받아온 경우에 실행하기 위해.
        if(isRegistered) {   // 등록
            let result  = await uploadFiles(docsId);   // attachments.js 의 uploadFiles() function
            if(result === 'ok') {
                alert('등록하였습니다.');
                location.href = '/approval/' + docsKind + '/' + docsId;
            } else {
                alert(result);
            }
        
        } else {   // 임시저장
            alert('임시 저장하였습니다.');
        }
    }
}

// 결재 문서의 임시저장 또는 등록시 사용하는 공용 function
async function saveApprovalDocs(docsKind, isRegistered) {
    let approvalDocs = {
        id: document.getElementById('docsId').innerText,
        title: document.getElementById('title').value,
        content: CKEDITOR.instances.ckeditorTextarea.getData(),   // ckeditor 내용 가져오기
        approvers: approver,
        referrers: referrer,
        registered: isRegistered
    };

    let result = await fetchCustom('POST', 'default', approvalDocs, 'approval/' + docsKind, 'json');
    if(result.returnObj) {   // 에러없이 리턴값을 받아온 경우에 실행. 에러 발생시 fetchCustom에서 메시지 띄우고 종료하므로 undefined가 리턴되고 종료됨.
        let resultDocsId = result.returnObj;
        document.getElementById('docsId').innerText = resultDocsId;
        docSaveComplete = true;
        return resultDocsId;
    }
}

function copyApprovalDocsCheck() {
    let docs = JSON.parse(localStorage.getItem('approvalDocs'));
    if(docs) {
        document.getElementById('title').value = docs.title;
        CKEDITOR.instances.ckeditorTextarea.setData(docs.content);
        localStorage.removeItem('approvalDocs');
    }
}
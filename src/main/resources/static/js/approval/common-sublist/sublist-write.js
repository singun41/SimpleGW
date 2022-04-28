function isEmptyTitle(title) {
    if(!title) {
        alert('제목을 작성하세요.');
        return true;
    } else {
        return false;
    }
}

// 서브리스트가 포함된 양식의 결재문서 임시저장 또는 등록시 사용하는 공용 function
async function saveSubListApprovalDocs(param, docsKind, isRegistered) {
    let result = await fetchCustom('POST', 'default', param, 'approval/' + docsKind, 'json');
    if(result.returnObj) {   // 에러없이 리턴값을 받아온 경우에 실행. 에러 발생시 fetchCustom에서 메시지 띄우고 종료하므로 undefined가 리턴되고 종료됨.
        let resultDocsId = result.returnObj;
        document.getElementById('docsId').innerText = resultDocsId;
        docSaveComplete = true;

        if(isRegistered) {   // 등록
            let result = await uploadFiles(resultDocsId);   // attachments.js 의 uploadFiles() function
            if(result === 'ok') {
                alert('등록하였습니다.');
                location.href = '/approval/' + docsKind + '/' + resultDocsId;
            } else {
                alert(result);
            }
        
        } else {   // 임시저장
            alert('임시 저장하였습니다.');
        }
    }
}

function copyApprovalDocsCheck() {
    let docs = JSON.parse(localStorage.getItem('approvalDocs'));
    if(docs) {
        document.getElementById('title').value = docs.title;
        document.getElementById('content').value = docs.content;
        localStorage.removeItem('approvalDocs');
        getDetails(docs.id);
    }
}
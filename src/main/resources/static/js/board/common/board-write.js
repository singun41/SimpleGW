window.addEventListener('beforeunload', event => {
    // 페이지를 나갈 때
    event.preventDefault();
    if(CKEDITOR.instances.ckeditorTextarea.getData() !== '' && !docSaveComplete) {
        event.returnValue = '';
    }
});
let docSaveComplete = false;

function isEmptyTitleAndContent(title, content) {
    if(!title) {
        alert('제목을 작성하세요.');
        return true;
    } else if(!content) {
        alert('내용을 작성하세요.');
        return true;
    } else {
        return false;
    }
}

// 일반 문서 종류 저장시 공용 function
async function insertBoardDocs(docsKind, isRegistered) {
    if(isEmptyTitleAndContent(document.getElementById('title').value, CKEDITOR.instances.ckeditorTextarea.getData())) {
        return;
    }
    const docsId = await saveDocs(docsKind, isRegistered);

    if(docsId) {   // 에러없이 리턴값 받아온 경우에 실행하기 위해.
        if(isRegistered) {   // 등록
            let result  = await uploadFiles(docsId);   // attachments.js 의 uploadFiles() function
            if(result === 'ok') {
                alert('등록하였습니다.');
                location.href = '/' + docsKind + '/' + docsId;
            } else {
                alert(result);
            }
        
            // 문서에 옵션 설정이 추가된 경우만 실행하도록 함. : 공지사항
            if(document.getElementById('docsOptionsArea')) {
                if(document.getElementById('useOptions').checked) {
                    await optionsSave(docsId)   // docs-options.js에 있는 function
                }
            }

        } else {   // 임시저장
            alert('임시 저장하였습니다.');
        }
    }
}

// 일반 문서 종류의 등록시 사용하는 공용 function
async function saveDocs(docsKind, isRegistered) {
    let docs = {
        id: document.getElementById('docsId').innerText,
        title: document.getElementById('title').value,
        content: CKEDITOR.instances.ckeditorTextarea.getData(),   // ckeditor 내용 가져오기
        registered: isRegistered
    };

    let result = await fetchCustom('POST', 'default', docs, docsKind, 'json');
    if(result.returnObj) {   // 에러없이 리턴값을 받아온 경우에 실행. 에러 발생시 fetchCustom에서 메시지 띄우고 종료하므로 undefined가 리턴되고 종료됨.
        let resultDocsId = result.returnObj;
        document.getElementById('docsId').innerText = resultDocsId;
        docSaveComplete = true;
        return resultDocsId;
    }
}

function copyDocsCheck() {
    let docs = JSON.parse(localStorage.getItem('docs'));
    if(docs) {
        document.getElementById('title').value = docs.title;
        CKEDITOR.instances.ckeditorTextarea.setData(docs.content);
        localStorage.removeItem('docs');
    }
}
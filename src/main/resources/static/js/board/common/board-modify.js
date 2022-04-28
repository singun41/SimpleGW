const docsId = document.getElementById('docsId').innerText;

// async function getContent(contentUrl) {
//     const content = await fetchCustom('GET', 'default', '', contentUrl + docsId, 'text');
//     CKEDITOR.instances.ckeditorTextarea.setData(content);
// }

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

async function updateBoardDocs(docsKind, isRegistered) {
    if(isEmptyTitleAndContent(document.getElementById('title').value, CKEDITOR.instances.ckeditorTextarea.getData())) {
        return;
    }

    let result = await udpateDocs(docsKind, isRegistered);

    if(result.message === 'ok') {
        let uploadFilesResult = await uploadFiles(docsId);
        if(uploadFilesResult === 'ok') {
            alert('등록하였습니다.');
            location.href = '/' + docsKind + '/' + docsId;
        } else {
            alert(uploadFilesResult);
        }

        // 문서에 옵션 설정이 추가된 경우만 실행하도록 함. : 공지사항
        if(document.getElementById('docsOptionsArea')) {
            if(document.getElementById('useOptions').checked) {
                await optionsSave(docsId)   // docs-options.js에 있는 function
            }
        }

    } else {
        alert(result);
    }
}

async function udpateDocs(docsKind, isRegistered) {
    let docs = {
        id: document.getElementById('docsId').innerText,
        title: document.getElementById('title').value,
        content: CKEDITOR.instances.ckeditorTextarea.getData(),   // ckeditor 내용 가져오기
        registered: isRegistered
    };

    return await fetchCustom('PUT', 'default', docs, docsKind, 'json');
}

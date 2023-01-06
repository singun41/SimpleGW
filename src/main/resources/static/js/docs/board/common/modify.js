let saveComplete = false;
window.addEventListener('beforeunload', event => {
    // 페이지를 나갈 때
    event.preventDefault();
    if(CKEDITOR.instances.ckeditorTextarea.getData() !== '' && !saveComplete) {
        event.returnValue = '';
    }
});

const docsId = document.getElementById('docsId').innerText;
const docsType = document.getElementById('docsType').innerText;

async function update() {
    let docsId = await updateBoard();

    if(docsId) {
        saveComplete = true;
        location.href = `/page/${docsType}/${docsId}`;
    }
}

async function updateBoard() {
    if(!confirm('수정하시겠습니까?'))
        return 0;

    let params = {
        title: document.getElementById('title').value,
        content: CKEDITOR.instances.ckeditorTextarea.getData()
    };
    let response = await fetchPatchParams(`${docsType}/${docsId}`, params);
    let result = await response.json();

    if(response.ok) {
        // 문서저장 후 첨부파일 저장 진행.
        if(arrFile.length === 0) {
            alert(result.msg);
            return docsId;
        }

        let uploadResponse = await uploadFiles(docsId);
        let uploadResult = await uploadResponse.json();
        alert(uploadResult.msg);   // 파일 첨부 결과 메시지 띄우기.

        if(uploadResponse.ok) {   // 첨부파일 완료 후 문서번호 리턴.
            return docsId;
        } else {
            return 0;
        }

    } else {   // 문서 저장 실패시 메시지 띄우기.
        alert(result.msg);
        return 0;
    }
}

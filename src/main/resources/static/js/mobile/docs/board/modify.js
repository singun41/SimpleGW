document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnBack').setAttribute('onclick', 'history.back()');
});

async function updateBoard() {
    if(!confirm('수정하시겠습니까?'))
        return 0;

    let docsId = document.getElementById('docsId').innerText;
    let type = document.getElementById('docsType').innerText;
    
    let params = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    };
    let response = await fetchPatchParams(`${type}/${docsId}`, params);
    let result = await response.json();

    if(response.ok) {
        arrFile = document.getElementById('attachments').files;

        // 문서저장 후 첨부파일 저장 진행.
        if(arrFile.length === 0) {
            alert(result.msg);
            page(`${type}/${docsId}`);   // 완료 후 메시지 띄우고 페이지 이동
        }

        let uploadResponse = await uploadFiles(docsId);
        let uploadResult = await uploadResponse.json();
        alert(uploadResult.msg);   // 파일 첨부 결과 메시지 띄우기.

        if(uploadResponse.ok)   // 첨부파일 완료 후 페이지 이동
            page(`${type}/${docsId}`);
        
    } else {   // 문서 저장 실패시 메시지 띄우기.
        alert(result.msg);
    }
}

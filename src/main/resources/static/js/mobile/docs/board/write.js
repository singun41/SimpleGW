document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnBack').setAttribute('onclick', 'history.back()');
});

async function saveBoard() {
    if(!confirm('등록하시겠습니까?'))
        return;

    let params = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    };

    let type = document.getElementById('docsType').innerText;
    let response = await fetchPostParams(type, params);
    let result = await response.json();

    if(response.ok) {
        arrFile = document.getElementById('attachments').files;
        
        // 문서저장 후 첨부파일 저장 진행.
        if(arrFile.length === 0) {
            alert(result.msg);
            page(`${type}/${result.obj}`);   // 첨부파일 없으면 메시지 띄우고 페이지 이동.
        }

        let uploadResponse = await uploadFiles(result.obj);   // 리턴받은 obj 속성에 문서번호가 담겨있음.
        let uploadResult = await uploadResponse.json();
        alert(uploadResult.msg);   // 파일 첨부 결과 메시지 띄우기.

        if(uploadResponse.ok)   // 첨부파일 완료 후 페이지 이동.
            page(`${type}/${result.obj}`);

    } else {   // 문서 저장 실패시 메시지 띄우기.
        alert(result.msg);
    }
}

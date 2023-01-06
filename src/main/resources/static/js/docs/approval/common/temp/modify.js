const docsId = document.getElementById('docsId').innerText;
const docsType = document.getElementById('docsType').innerText;

// 각 결재문서 마다 저장해야 하는 디테일 데이터가 다르므로 params 매개변수를 받는다.
async function saveApprovalDocs(params) {
    if(!confirm('등록하시겠습니까?'))
        return 0;
    
    // 모든 결재문서 공통 파라미터: 제목, 결재자, 참조자
    params.title = document.getElementById('title').value;

    // approvers.js에서 등록한 결재, 참조자 정보
    params.arrApproverId = approverIds;
    params.arrReferrerId = referrerIds;

    let response = await fetchPostParams(`approval/${docsType}`, params);
    let result = await response.json();

    if(response.ok) {
        // 문서저장 후 첨부파일 저장 진행.
        if(arrFile.length === 0) {
            alert(result.msg);
            deleteTemp();   // 정상 등록하면 임시저장 문서를 삭제할 지 묻는다.
            return result.obj;
        }

        let uploadResponse = await uploadFiles(result.obj);   // 리턴받은 obj 속성에 문서번호가 담겨있음.
        let uploadResult = await uploadResponse.json();
        alert(uploadResult.msg);   // 파일 첨부 결과 메시지 띄우기.

        if(uploadResponse.ok) {   // 첨부파일 완료 후 문서번호 리턴.
            deleteTemp();   // 정상 등록하면 임시저장 문서를 삭제할 지 묻는다.
            return result.obj;

        } else {
            return 0;
        }

    } else {   // 문서 저장 실패시 메시지 띄우기.
        alert(result.msg);
        return 0;
    }
}

async function updateTempApprovalDocs(params) {
    if(!confirm('임시저장 하시겠습니까?\n첨부파일과 결재라인은 저장되지 않습니다.'))
        return 0;
    
    // 모든 결재문서 임시저장 공통 파라미터: 제목
    params.title = document.getElementById('title').value;

    let response = await fetchPatchParams(`approval/${docsType}/temp/${docsId}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        return docsId;
    else
        return 0;
}

async function deleteTemp() {
    if(!confirm('현재 임시저장 문서를 삭제하시겠습니까?'))
        return;

    let response = await fetchDelete(`approval/${docsType}/temp/${docsId}`);
    let result = await response.json();
    alert(result.msg);
}

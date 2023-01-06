document.addEventListener('DOMContentLoaded', () => {
    // detail이 있는 문서들만 공용으로 사용한다.
    // default 문서는 url이 다르므로 다른 파일을 사용.
    setApprovalLines();
});

const docsId = document.getElementById('docsId').innerText;
const docsType = document.getElementById('docsType').innerText;

async function setApprovalLines() {
    let response = await fetchGet(`approval/line/${docsId}/${docsType}`);
    let result = await response.json();

    if(response.ok) {
        let approvers = result.obj.approvers;
        let referrers = result.obj.referrers;

        let approverIds = [];
        let approverNames = [];
        Array.from(approvers).forEach(e => { approverIds.push(e.memberId); });
        Array.from(approvers).forEach(e => { approverNames.push(`${e.jobTitle} ${e.name}`); });

        let referrerIds = [];
        let referrerNames = [];
        Array.from(referrers).forEach(e => { referrerIds.push(e.memberId); });
        Array.from(referrers).forEach(e => { referrerNames.push(`${e.jobTitle} ${e.name}`); });

        let approverLineData = {
            approverIds: approverIds,
            approverNames: approverNames,
            referrerIds: referrerIds,
            referrerNames: referrerNames
        };
        localStorage.setItem('approverLineData', JSON.stringify(approverLineData));

        setLine();   // docs/approval/common/approvers-write.js 의 함수 setLine() 호출.
    }
}

async function updateApprovalDocs(params) {
    if(!confirm('수정하시겠습니까?'))
        return 0;

    // 모든 결재문서 공통 파라미터: 제목, 결재자, 참조자
    params.title = document.getElementById('title').value;

    // approvers.js에서 등록한 결재, 참조자 정보
    params.arrApproverId = approverIds;
    params.arrReferrerId = referrerIds;

    let response = await fetchPatchParams(`approval/${docsType}/${docsId}`, params);
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

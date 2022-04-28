function isEmptyTitle(title) {
    if(!title) {
        alert('제목을 작성하세요.');
        return true;
    } else {
        return false;
    }
}

// 서브리스트가 포함된 양식의 결재문서 임시저장 또는 등록시 사용하는 공용 function
async function saveSubListApprovalDocs(param, docsKind) {
    return result = await fetchCustom('PUT', 'default', param, 'approval/' + docsKind, 'json');
}
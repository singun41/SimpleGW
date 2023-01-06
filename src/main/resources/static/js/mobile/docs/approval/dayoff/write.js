async function save() {
    if(!confirm('등록하시겠습니까?'))
        return 0;

    let arrDayoffCode = [];
    Array.from(document.getElementsByClassName('dayoff-code')).forEach(e => { arrDayoffCode.push(e.value === '' ? null : e.value); });

    let arrDateFrom = [];
    let arrDateTo = [];
    Array.from(document.getElementsByClassName('input-date-range')).forEach(e => {
        let dt = e.value.replaceAll(' ', '').replaceAll('.', '-').split('~');
        let dtFrom = dt[0].substr(0, 10);
        let dtTo;

        if(dt.length === 1)
            dtTo = dtFrom;
        else
            dtTo = dt[1].substr(0, 10);
        
        arrDateFrom.push(dtFrom);
        arrDateTo.push(dtTo);
    });

    let arrDetail = [];
    for(let i=0; i<arrDayoffCode.length; i++) {
        let detailData = {
            code: arrDayoffCode[i],
            dateFrom: arrDateFrom[i],
            dateTo: arrDateTo[i]
        };
        arrDetail.push(detailData);
    }
    
    let params = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value,
        details: arrDetail,
        arrApproverId: approverIds,
        arrReferrerId: referrerIds
    };

    let response = await fetchPostParams(`approval/dayoff`, params);
    let result = await response.json();

    if(response.ok) {
        arrFile = document.getElementById('attachments').files;
        
        // 문서저장 후 첨부파일 저장 진행.
        if(arrFile.length === 0) {
            alert(result.msg);
            page(`approval/dayoff/${result.obj}`);   // 첨부파일 없으면 메시지 띄우고 페이지 이동.
        }

        let uploadResponse = await uploadFiles(result.obj);   // 리턴받은 obj 속성에 문서번호가 담겨있음.
        let uploadResult = await uploadResponse.json();
        alert(uploadResult.msg);   // 파일 첨부 결과 메시지 띄우기.

        if(uploadResponse.ok)   // 첨부파일 완료 후 페이지 이동.
            page(`approval/dayoff/${result.obj}`);

    } else {   // 문서 저장 실패시 메시지 띄우기.
        alert(result.msg);
    }
}

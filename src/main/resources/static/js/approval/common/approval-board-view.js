window.addEventListener('DOMContentLoaded', event => {
    getApprovalLine();
});

function setCardHeaderTitle(iconType, title) {
    const icon = document.createElement('i');
    icon.setAttribute('class', iconType + ' custom-card-header-fas-icon-sm');
    
    const typeLabel = document.getElementById('boardType');
    typeLabel.prepend(title + ' ');
    typeLabel.prepend(icon);
}

async function getApprovalLine() {
    let docsId = document.getElementById('docsId').innerText;
    let result = await fetchCustom('GET', 'default', '', 'approval/line/' + docsId, 'json');

    let approvers = result.approvers;
    let referrers = result.referrers;
    let arrApproverStatus = [];
    let arrApproverCheckedDatetime = [];
    let arrReferrerCheckedDatetime = [];
    
    // 결재자 리스트
    if(approvers) {
        let arrApprover = [];
        approvers.forEach(approver => {
            arrApprover.push(approver.jobTitle + ' ' + approver.name);
            arrApproverStatus.push(approver.status);
            if(approver.checkedDatetime !== null) {
                arrApproverCheckedDatetime.push(approver.checkedDatetime.substr(0, 10).replaceAll('-', '. ') + '\n' + approver.checkedDatetime.substr(11, 8));
            }
        });

        bindMemberToLineTables('Approver', arrApprover);
    }

    // 참조자 리스트
    if(referrers) {
        let arrReferrer = [];
        referrers.forEach(referrer => {
            arrReferrer.push(referrer.jobTitle + ' ' + referrer.name);
            if(referrer.checkedDatetime === null) {
                arrReferrerCheckedDatetime.push('');
            } else {
                arrReferrerCheckedDatetime.push(referrer.checkedDatetime.substr(0, 10).replaceAll('-', '. ') + '\n' + referrer.checkedDatetime.substr(11, 8));
            }
        });
        bindMemberToLineTables('Referrer', arrReferrer);
    }


    // ----- ----- ----- ----- ----- 결재자 테이블 렌더링 ----- ----- ----- ----- ----- //
    let approverTableCnt = 0;
    document.getElementById('bodyApprover').childNodes.forEach(node => {
        if(node.nodeName === 'TABLE') {
            approverTableCnt++;
        }
    });

    let arrApproverSignTd = [];
    let arrApproverTimeTd = [];
    for(let i=0; i<approverTableCnt; i++) {
        for(let j=0; j<5; j++) {
            arrApproverSignTd.push(document.getElementById('signApprover' + i + '-' + j));
            arrApproverTimeTd.push(document.getElementById('timeApprover' + i + '-' + j));
        }
    }

    let approverSignIdx = 0;
    arrApproverStatus.forEach(elem => {
        if(elem === 'SUBMITTED' || elem === 'CONFIRMED' || elem === 'REJECTED') {
            let signImg = document.createElement('img');
            signImg.setAttribute('src', location.protocol + '//' + location.host + '/images/' + elem.toLowerCase() + '-kor.png');

            arrApproverSignTd[approverSignIdx].append(signImg);
            arrApproverTimeTd[approverSignIdx].append(arrApproverCheckedDatetime[approverSignIdx]);
            approverSignIdx++;
        }
    });
    // ----- ----- ----- ----- ----- 결재자 테이블 렌더링 ----- ----- ----- ----- ----- //


    
    // ----- ----- ----- ----- ----- 참조자 테이블 렌더링 ----- ----- ----- ----- ----- //
    let referrerTableCnt = 0;
    document.getElementById('bodyReferrer').childNodes.forEach(node => {
        if(node.nodeName === 'TABLE') {
            referrerTableCnt++;
        }
    });

    let arrReferrerSignTd = [];
    let arrReferrerTimeTd = [];
    for(let i=0; i<referrerTableCnt; i++) {
        for(let j=0; j<5; j++) {
            arrReferrerSignTd.push(document.getElementById('signReferrer' + i + '-' + j));
            arrReferrerTimeTd.push(document.getElementById('timeReferrer' + i + '-' + j));
        }
    }

    let referrerSignIdx = 0;
    arrReferrerCheckedDatetime.forEach(elem => {
        let signImg = document.createElement('img');
        if(elem) {
            signImg.setAttribute('src', location.protocol + '//' + location.host + '/images/checked-kor.png');
            arrReferrerSignTd[referrerSignIdx].append(signImg);
            arrReferrerTimeTd[referrerSignIdx].append(arrReferrerCheckedDatetime[referrerSignIdx]);
        }
        referrerSignIdx++;
    });
    // ----- ----- ----- ----- ----- 참조자 테이블 렌더링 ----- ----- ----- ----- ----- //
}

async function deleteApprovalDocs(deleteUrl) {
    if(confirm('문서를 삭제하시겠습니까?')) {
        let docsId = document.getElementById('docsId').innerText;
        await fetchCustom('DELETE', 'default', '', 'approval/' + deleteUrl + '/' + docsId, 'msg');
        location.href = '/approval/' + deleteUrl + '/listpage';
    }
}

function copyApprovalDocs(url) {
    let docs = {
        'docsId': document.getElementById('docsId').innerText,
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').innerHTML
    };
    localStorage.setItem('approvalDocs', JSON.stringify(docs));
    location.href = '/approval/' + url + '/writepage';
}
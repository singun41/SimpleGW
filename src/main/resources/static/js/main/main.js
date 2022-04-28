window.addEventListener('DOMContentLoaded', event => {
    setFixedBoardList('notice/list-top-7', 'mainNoticeList', 'notice');
    setFixedBoardList('freeboard/list-top-5', 'mainFreeboardList', 'freeboard');

    getCount('approval/count/received/APPROVER', 'countApprovalForApprover');
    getCount('approval/count/received/REFERRER', 'countApprovalForReferrer');
    getCount('approval/count/proceeding', 'countProceedingApprovalDocs');
    getCount('docs/count/temporary', 'countTemporarySavedDocs');
    getCount('alarm/count/not-checked', 'countNotCheckedAlarm');
    getCompanyEvent();
    subscribe();
});

window.addEventListener('beforeunload', event => {
    // 페이지를 나갈 때 sse 연결을 끊어서 서버에서 불필요한 데이터 전송을 막는다.
    event.preventDefault();
    // event.returnValue = '';
    disconnectSse();
});

async function setFixedBoardList(url, tbodyId, boardType) {
    let arrNoticeList = await fetchCustom('GET', 'default', '', url, 'json');
    
    const tbody = document.getElementById(tbodyId);
    while(tbody.hasChildNodes()) {
        tbody.removeChild(tbody.firstChild);
    }
    
    let now = new Date();

    arrNoticeList.forEach(elem => {
        let tr = document.createElement('tr');
        
        let tdAnchor = document.createElement('td');
        let anchor = document.createElement('a');
        anchor.setAttribute('class', 'text-decoration-none text-dark');
        anchor.setAttribute('href', '#');
        anchor.setAttribute('onclick', 'openToPopup("/' + boardType + '/' + elem.id + '")');
        anchor.append(elem.title);
        tdAnchor.append(anchor);

        let tdNew = document.createElement('td');
        tdNew.setAttribute('class', 'text-right text-danger');
        
        if(dateDiff(new Date(elem.createdDate), now) <= 3) {
            tdNew.append('New');
        }

        tr.append(tdAnchor, tdNew);
        tbody.append(tr);
    });
}

function dateDiff(from, to) {
    let fromUtcTime = from.getTime();
    let toUtcTime = to.getTime();
    return (toUtcTime - fromUtcTime) / (1000 * 60 * 60 * 24);
}


async function getCount(url, tagId) {
    let tag = document.getElementById(tagId);
    let result = await fetchCustom('GET', 'default', '', url, 'text');
    tag.innerText = result;
    if(result > 0) {
        tag.classList.remove('text-dark');
        tag.classList.add('text-danger');
        tag.classList.add('font-weight-bold');
    } else {
        tag.classList.remove('text-danger');
        tag.classList.remove('font-weight-bold');
        tag.classList.add('text-dark');
    }
}

function subscribe() {
    let sse = new EventSource(location.protocol + '//' + location.host + '/sse/subscribe');
    sse.onmessage = (event) => {
        let result = JSON.parse(event.data);

        if(result.NOTICE) {
            setFixedBoardList('notice/list-top-7', 'mainNoticeList', 'notice'); return;
        }
        if(result.FREEBOARD) {
            setFixedBoardList('freeboard/list-top-5', 'mainFreeboardList', 'freeboard'); return;
        }

        let title;
        let content;

        if(result.CONFIRMED) {
            title = '결재 문서가 승인되었습니다.';
            content = '문서 번호: ' + result.docsId + '\n' + '문서 구분: ' + result.kind + '\n' + '문서 제목: ' + result.title;
            showAlertModal(title, content, 'confirmed');
            getCount('approval/count/proceeding', 'countProceedingApprovalDocs');
            getCount('alarm/count/not-checked', 'countNotCheckedAlarm');
        }

        if(result.REJECTED) {
            title = '결재 문서가 반려되었습니다.';
            content = '문서 번호: ' + result.docsId + '\n' + '문서 구분: ' + result.kind + '\n' + '문서 제목: ' + result.title;
            showAlertModal(title, content, 'rejected');
            getCount('approval/count/proceeding', 'countProceedingApprovalDocs');
            getCount('alarm/count/not-checked', 'countNotCheckedAlarm');
        }

        if(result.APPROVER) {
            title = '새로운 결재요청 문서가 도착했습니다.';
            showAlertModal(title, '', '');
            getCount('approval/count/received/APPROVER', 'countApprovalForApprover');
        }

        if(result.REFERRER) {
            title = '새로운 결재참조 문서가 도착했습니다.';
            showAlertModal(title, '', '');
            getCount('approval/count/received/REFERRER', 'countApprovalForReferrer');
        }

        if(result.ALARM) {
            getCount('alarm/count/not-checked', 'countNotCheckedAlarm');
        }

        if(result.COMPANY) {
            getCompanyEvent();
        }
    };
}

async function disconnectSse() {
    await fetchCustom('GET', 'default', '', 'sse/disconnect', null);
}

function clearAlertContent() {
    document.getElementById('alertTitle').innerText = '';
    document.getElementById('alertContent').innerText = '';
    document.getElementById('alertResultTitle').innerText = '';
    document.getElementById('alertResult').innerText = '';
    document.getElementById('alertResult').classList.remove('text-success', 'text-danger');
}

function showAlertModal(title, content, status) {
    document.getElementById('alertTitle').innerText = title;
    document.getElementById('alertContent').innerText = content;
    
    let resultTitle = document.getElementById('alertResultTitle');
    let result = document.getElementById('alertResult');

    if(status === 'confirmed') {
        resultTitle.innerText = '처리 결과:';
        result.innerText = '승인';
        result.classList.add('text-success');
        
    } else if(status === 'rejected') {
        resultTitle.innerText = '처리 결과:';
        result.innerText = '반려';
        result.classList.add('text-danger');
    }

    $('#alertModal').modal('show');
}

async function getAlarmContent() {
    const alarmContent = document.getElementById('alarmContent');
    while(alarmContent.hasChildNodes()) {
        alarmContent.removeChild(alarmContent.firstChild);
    }

    let result = await fetchCustom('GET', 'default', '', 'alarm/list', 'json');
    
    if(result.length === 0) {
        let tr = document.createElement('tr');
        let td = document.createElement('td');
        let lbl = document.createElement('label');
        lbl.setAttribute('class', 'col-12 col-form-label col-form-label-sm text-center');
        lbl.textContent = '도착한 알림 메시지가 없습니다.';
        td.append(lbl);
        tr.append(td);
        alarmContent.append(tr);
    } else {
        result.forEach(elem => {
            let tr = document.createElement('tr');
            let td = document.createElement('td');
            let lbl = document.createElement('label');
            lbl.setAttribute('class', 'col-12 col-form-label col-form-label-sm');
            lbl.textContent = elem;
            td.append(lbl);
            tr.append(td);
            alarmContent.append(tr);
        });
    }
}

async function showAlarmModal() {
    await getAlarmContent();
    $('#alarmModal').modal('show');
    setTimeout(() => {   // 메시지를 가져옴과 동시에 checkedDatetime을 업데이트하므로 약간의 지연을 걸어준다.
        getCount('alarm/count/not-checked', 'countNotCheckedAlarm');
    }, 500);
}

function callFramefunc() {
    window.parent.postMessage('show-myinfo');
}

async function getCompanyEvent() {
    let result = await fetchCustom('GET', 'default', '', 'schedule/company-event', 'json');

    let eventList = document.getElementById('mainCompanyEventList');
    while(eventList.hasChildNodes()) {
        eventList.removeChild(eventList.firstChild);
    }

    result.forEach(elem => {
        let tr = document.createElement('tr');

        let td = document.createElement('td');
        let anchor = document.createElement('a');
        anchor.setAttribute('class', 'text-decoration-none text-dark');
        anchor.setAttribute('href', '#');
        anchor.setAttribute('onclick', 'openToPopupSm("/schedule/details/' + elem.id + '")');
        anchor.append(elem.datetimeStart.substr(5, 5).replaceAll('-', '. ') + '. ' + elem.title);
        
        td.append(anchor);
        tr.append(td);
        eventList.append(tr);
    });
}
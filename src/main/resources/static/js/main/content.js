document.addEventListener('DOMContentLoaded', () => {
    getNotices();
    getFreeBoardList();
    getTempDocsCount();
    getProceedDocsCnt();
    getApproverDocsCnt();
    getReferrerDocsCnt();
    getNotiCount();
});

window.addEventListener('message', receiveMsgFromParent);   // 'document'가 아니라 'window'다.
function receiveMsgFromParent(e) {   // main.js로부터 메시지 수신: server sent event 알림 용도
    // 전달받은 메시지 = e.data
    if(e.data === 'notice') {
        getNotices();

    } else if(e.data === 'freeboard') {
        getFreeBoardList();

    } else if(e.data === 'approver') {
        getApproverDocsCnt();

    } else if(e.data === 'referrer') {
        getReferrerDocsCnt();

    } else if(e.data ==='confirmed' || e.data === 'rejected') {
        getProceedDocsCnt();
        getNotiCount();
    
    } else if(e.data === 'notification') {
        getNotiCount();
    }
}

function sendMsgToParent(msg) {   // main.js에게 메시지 전달
    window.parent.postMessage(msg, '*');
}

const innerCalendar = document.getElementById('innerCalendar');
function sendMsgToChild(msg) {   // calendar.js에게 메시지 전달
    innerCalendar.contentWindow.postMessage(msg, '*');
}

function updateCalendar() {
    if(confirm('공휴일 api를 다시 호출하여 업데이트 하시겠습니까?'))
        sendMsgToChild('updateCalendar');
}

document.getElementById('btnPostitSave').addEventListener('click', () => {
    let e = document.getElementById('btnPostitSave');
    let tooltip = bootstrap.Tooltip.getInstance(e);
    
    e.setAttribute('data-bs-original-title', '저장하였습니다.');
    tooltip.show();

    setTimeout(() => {
        tooltip.hide();
        e.setAttribute('data-bs-original-title', '저장');
    }, 1500);
});

async function getBoardList(type) {
    let response = await fetchGet(`${type}/main-list`);
    let result = await response.json();
    
    let list = document.getElementById(type);
    if(!list)
        return;
    
    if(response.ok) {
        while(list.hasChildNodes())
        list.removeChild(list.firstChild);

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            let td = document.createElement('td');
            let a = document.createElement('a');
            let title = document.createElement('div');
            
            title.classList.add('text-truncate');
            title.innerText = e.title;

            a.setAttribute('role', 'button');
            a.setAttribute('onclick', `openPopup('/page/${type}/${e.id}')`);
            a.append(title);

            td.style.maxWidth = '1rem';   // maxWidth를 %로 지정하거나 부모요소보다 큰 값이면 title의 text-truncate가 안 됨.
            td.append(a);

            if(e.new) {
                let tdNew = document.createElement('td');
                tdNew.style.width = '1rem';

                let span = document.createElement('span');
                span.classList.add('badge', 'rounded-pill', 'bg-danger', 'float-end');
                span.innerText = 'New';
                tdNew.append(span);

                tr.append(td, tdNew);
            
            } else {
                td.setAttribute('colspan', 2);
                tr.append(td);
            }
            
            list.append(tr);
        });
    }
}

async function getNotices() {
    await getBoardList('notice');
}

async function getFreeBoardList() {
    await getBoardList('freeboard');
}

async function getTempDocsCount() {
    let response = await fetchGet('docs/temp/count');
    let result = await response.json();
    if(response.ok)
        document.getElementById('cntTempDocs').innerText = result.obj === 0 ? '' : result.obj;
}

function popupAlarmPage() {
    sendMsgToParent('alarm');
}

async function getProceedDocsCnt() {
    let response = await fetchGet('approval/proceed-cnt');
    let result = await response.json();
    if(response.ok)
        document.getElementById('cntProceed').innerText = result.obj === 0 ? '' : result.obj;
}

async function getApproverDocsCnt() {
    let response = await fetchGet('approval/approver-cnt');
    let result = await response.json();
    if(response.ok)
        document.getElementById('cntApprover').innerText = result.obj === 0 ? '' : result.obj;
}

async function getReferrerDocsCnt() {
    let response = await fetchGet('approval/referrer-cnt');
    let result = await response.json();
    if(response.ok)
        document.getElementById('cntReferrer').innerText = result.obj === 0 ? '' : result.obj;
}

async function getNotiCount() {
    let response = await fetchGet('notification/count');
    let result = await response.json();
    if(response.ok)
        document.getElementById('cntNotification').innerText = result.obj === 0 ? '' : result.obj;
}

function showNotification() {
    sendMsgToParent('notification');
}

function showProfile() {
    sendMsgToParent('profile');
}

function showEnvSetting() {
    sendMsgToParent('envSetting');
}

function openNewSchedule() {
    window.open('/main/calendar/new', '', 'width=750, height=550');
}

function openNewScheduleWithData(data) {   // 캘린더에서 받은 파라미터를 새 창에 바인딩하기 위해 로컬스토리지 사용.
    localStorage.setItem('mainPageSchedule', JSON.stringify(data));
    openNewSchedule();
}

function getCalendarData() {   // 메인 content 페이지에서 일정 추가 팝업창을 띄우고 등록 완료하면 이 함수를 호출, 여기서 iFrame 캘린더로 전달.
    sendMsgToChild('getCalendarData');
}

window.addEventListener('message', receiveMsgFromChild);
function receiveMsgFromChild(e) {   // calendar.js로부터 메시지 수신
    if(e.data.msg === 'openNewSchedulePage')
        openNewScheduleWithData(e.data);
}

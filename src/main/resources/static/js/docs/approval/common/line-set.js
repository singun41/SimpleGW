document.addEventListener('DOMContentLoaded', () => {
    buildDatatable();
    getSavedLines();
});

let approverIds = [];
let referrerIds = [];

let approverNames = [];
let referrerNames = [];

const approverLine = document.getElementById('approverLine');
const referrerLine = document.getElementById('referrerLine');
const lines = document.getElementById('lines');

async function getSavedLines() {
    let response = await fetchGet('approver-line');
    let result = await response.json();
    
    let defaultOpt = document.createElement('option');
    defaultOpt.setAttribute('value', '');
    defaultOpt.selected = true;
    defaultOpt.disabled = true;

    if(response.ok) {
        while(lines.hasChildNodes())
            lines.removeChild(lines.firstChild);

        let savedLines = Array.from(result.obj);
        
        if(savedLines.length === 0) {
            defaultOpt.text = '저장된 결재라인 없음.';
            lines.append(defaultOpt);
        
        } else {
            defaultOpt.text = '선택...';
            lines.append(defaultOpt);

            savedLines.forEach(e => {
                let opt = document.createElement('option');
                opt.value = e.id;
                opt.text = e.title;
                lines.append(opt);
            });
        }
        
    } else {
        defaultOpt.text = '결재라인 로드 실패.';
        lines.append(opt);
    }
}

async function getLineDetails() {
    let response = await fetchGet(`approver-line/${lines.value}`);
    let result = await response.json();
    if(response.ok) {
        resetLines();
        let approvers = result.obj.approvers;
        let referrers = result.obj.referrers;

        approvers.forEach(e => {
            pushApprover(e.id, `${e.jobTitle} ${e.name}`);
        });

        referrers.forEach(e => {
            pushReferrer(e.id, `${e.jobTitle} ${e.name}`);
        });
    }
}

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [1, 'asc'],
        ordering: false,
        columnDefs: [
            // 0 = id hidden field.
            { targets: 0, width: '0%' },
            { targets: 1, width: '50%' },
            { targets: 2, width: '25%' },
            { targets: 3, width: '25%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 420,
        pageLength: 100,
        paging: false,
        searching: false,
        info: false
    });
}

async function getTeamMembers(e) {
    let response = await fetchGet(`team/${e.value}/without-me`);
    let result = await response.json();
    
    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let id = document.createElement('td');
            id.innerText = e.id;
            id.classList.add('d-none');

            let name = document.createElement('td');
            name.innerText = `${e.jobTitle} ${e.name}`;

            let approver = document.createElement('td');
            let referrer = document.createElement('td');

            let btnApprover = document.createElement('button');
            let btnReferrer = document.createElement('button');
            btnApprover.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnReferrer.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btnApprover.setAttribute('onclick', 'addApprover(this)');
            btnReferrer.setAttribute('onclick', 'addReferrer(this)');

            let iApprover = document.createElement('i');
            let iReferrer = document.createElement('i');
            iApprover.classList.add('fa-solid', 'fa-file-signature');
            iReferrer.classList.add('fa-solid', 'fa-file-circle-check');

            btnApprover.append(iApprover);
            btnReferrer.append(iReferrer);
            approver.append(btnApprover);
            referrer.append(btnReferrer);

            tr.append(id, name, approver, referrer);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

function addApprover(e) {
    let approverId = e.parentNode.parentNode.childNodes[0].innerText;
    let approverName = e.parentNode.parentNode.childNodes[1].innerText;
    pushApprover(approverId, approverName);
}

function pushApprover(approverId, approverName) {
    let isDuplicated = false;
    approverIds.forEach(e => {
        if(e === approverId)
            isDuplicated = true;
    });

    if(isDuplicated) {
        alert('이미 등록한 멤버입니다.');
        return;
    }

    approverIds.push(approverId);
    approverNames.push(approverName);

    let tr = document.createElement('tr');
    tr.classList.add('text-center');
    
    let id = document.createElement('td');
    id.classList.add('d-none');
    id.innerText = approverId;

    let name = document.createElement('td');
    name.setAttribute('colspan', '2');
    name.innerText = `${approverLine.childNodes.length + 1}. ${approverName}`;

    tr.append(id, name);
    approverLine.append(tr);
}

function removeApprover() {
    if(approverLine.hasChildNodes()) {
        approverLine.removeChild(approverLine.lastChild);
        approverIds.pop();
        approverNames.pop();
    }
}




function addReferrer(e) {
    let referrerId = e.parentNode.parentNode.childNodes[0].innerText;
    let referrerName = e.parentNode.parentNode.childNodes[1].innerText;
    pushReferrer(referrerId, referrerName);
}

function pushReferrer(referrerId, referrerName) {
    let isDuplicated = false;
    referrerIds.forEach(e => {
        if(e === referrerId)
            isDuplicated = true;
    });

    if(isDuplicated) {
        alert('이미 등록한 멤버입니다.');
        return;
    }

    referrerIds.push(referrerId);
    referrerNames.push(referrerName);
    
    let tr = document.createElement('tr');
    tr.classList.add('text-center');
    
    let id = document.createElement('td');
    id.classList.add('d-none');
    id.innerText = referrerId;

    let name = document.createElement('td');
    name.setAttribute('colspan', '2');
    name.innerText = referrerName;

    tr.append(id, name);
    referrerLine.append(tr);
}

function removeReferrer() {
    if(referrerLine.hasChildNodes()) {
        referrerLine.removeChild(referrerLine.lastChild);
        referrerIds.pop();
        referrerNames.pop();
    }
}



function reset() {
    lines.selectedIndex = 0;
    resetLines();
}

function resetLines() {
    approverIds = [];
    referrerIds = [];
    approverNames = [];
    referrerNames = [];

    while(approverLine.hasChildNodes())
        approverLine.removeChild(approverLine.firstChild);
    
    while(referrerLine.hasChildNodes())
        referrerLine.removeChild(referrerLine.lastChild);
}



async function save() {
    if( ! approverIds.length ) {
        alert('결재라인을 설정하세요.');
        return;
    }

    let response;
    if(lines.value) {   // update
        if(!confirm('수정하시겠습니까?'))
            return;

        let lineId = lines.value;
        let title = lines.options[lines.selectedIndex].text;
        let params = {
            title: title,
            arrApproverId: approverIds,
            arrReferrerId: referrerIds
        };
        response = await fetchPatchParams(`approver-line/${lineId}`, params);


    } else {   // new
        let title = prompt('결재라인 제목을 입력하세요.');
        if( ! title )
            return;
        
        let params = {
            title: title,
            arrApproverId: approverIds,
            arrReferrerId: referrerIds
        };
        response = await fetchPostParams('approver-line', params);
    }

    
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        await getSavedLines();
}


async function del() {
    if( ! lines.value )
        return;

    if(!confirm('삭제하시겠습니까?'))
        return;
    
    let response = await fetchDelete(`approver-line/${lines.value}`);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        reset();
        getSavedLines();
    }
}


function apply() {
    if( ! approverIds.length ) {
        alert('결재라인을 설정하세요.');
        return;
    }

    if(!confirm('현재 문서에 이 결재라인을 적용하시겠습니까?'))
        return;

    let approverLineData = {
        approverIds: approverIds,
        approverNames: approverNames,
        referrerIds: referrerIds,
        referrerNames: referrerNames
    };
    localStorage.setItem('approverLineData', JSON.stringify(approverLineData));
    opener.setLine();
    window.close();
}

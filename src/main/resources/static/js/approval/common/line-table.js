function createLineTables(type, memberCount) {
    // type = Approver or Referrer
    /* 
        call example
        createLineTables('Approver', 10);
        createLineTables('Referrer', 5);
    */

    const area = document.getElementById('body' + type);
    while(area.hasChildNodes()) {
        area.removeChild(area.firstChild);
    }

    if(memberCount === 0) { return; }

    let count = 0;
    while(memberCount > 5) {
        count++;
        memberCount -= 5;
    }

    for(let rowCnt=0; rowCnt<=count; rowCnt++) {

        if(rowCnt > 0) {
            let separateLine = document.createElement('div');
            separateLine.setAttribute('class', 'form-row my-2');
            area.append(separateLine);
        }

        let trHeader = document.createElement('tr');
        for(let i=0; i<5; i++) {
            let td = document.createElement('td');
            td.setAttribute('class', 'approver-table-td-header');
            td.setAttribute('id', 'header' + type + rowCnt + '-' + i);
            trHeader.append(td);
        }
    
        let trSign = document.createElement('tr');
        for(let i=0; i<5; i++) {
            let td = document.createElement('td');
            td.setAttribute('class', 'approver-table-td-sign');
            td.setAttribute('id', 'sign' + type + rowCnt + '-' + i);
            trSign.append(td);
        }
    
        let trTime = document.createElement('tr');
        for(let i=0; i<5; i++) {
            let td = document.createElement('td');
            td.setAttribute('class', 'approver-table-td-time');
            td.setAttribute('id', 'time' + type + rowCnt + '-' + i);
            trTime.append(td);
        }
    
        let tbody = document.createElement('tbody');
        tbody.append(trHeader, trSign, trTime);
        
        let table = document.createElement('table');
        table.setAttribute('class', 'approver-table-custom');
        table.setAttribute('id', type + rowCnt);
        table.append(tbody);

        area.append(table);
    }
}

function bindMemberToLineTables(type, members) {
    // type = Approver or Referrer
    // members = '직위' + ' ' + '이름' 형태의 배열
    createLineTables(type, members.length);

    if(members.length > 0) {
        let rowCnt = 0;
        let tdIdx = 0;
    
        members.forEach(elem => {
            let td = document.getElementById('header' + type + rowCnt + '-' + tdIdx);

            elem = elem.replace('선임연구원', '선임');
            elem = elem.replace('책임연구원', '책임');

            td.innerText = elem;
            tdIdx++;
    
            if(tdIdx > 4) {
                rowCnt++;
                tdIdx = 0;
            }
        });
    }
}


// 문서 저장시 결재라인도 저장하기 위해서 전역변수로 선언해야 한다.
let approver = [];
let referrer = [];
function applyToLineTables(lineMembers) {
    approver = lineMembers.approver;
    referrer = lineMembers.referrer;

    let approverName = lineMembers.approverName;
    let referrerName = lineMembers.referrerName;

    bindMemberToLineTables('Approver', approverName);
    bindMemberToLineTables('Referrer', referrerName);
}

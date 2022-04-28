window.addEventListener('DOMContentLoaded', event => {
    defaultRow = document.getElementById('overtimeRow0').cloneNode(true);
});
let defaultRow;

function checkTimeStart(elem) {
    let idx = elem.getAttribute('id').replace('timeStart', '');
    let timeEnd = document.getElementById('timeEnd' + idx)
    if(timeEnd.value === '') return;
    if(timeEnd.value < elem.value)
        timeEnd.value = elem.value;
}
function checkTimeEnd(elem) {
    let idx = elem.getAttribute('id').replace('timeEnd', '');
    let timeStart = document.getElementById('timeStart' + idx)
    if(timeStart.value === '') return;
    if(timeStart.value > elem.value)
        timeStart.value = elem.value;
}

async function getDetails(docsId) {
    let details = await fetchCustom('GET', 'default', '', 'approval/overtime/details/' + docsId, 'json');
    let rowCnt = details.length;
    for(let i=0; i<rowCnt - 1; i++) {   // 기본 1행은 있으므로.
        addRow();
    }

    let member = Array.from(document.getElementsByClassName('overtime-member'));
    let code = Array.from(document.getElementsByClassName('overtime-code'));
    let workDate = Array.from(document.getElementsByClassName('input-work-date'));
    let timeStart = Array.from(document.getElementsByClassName('input-time-start'));
    let timeEnd = Array.from(document.getElementsByClassName('input-time-end'));
    let remarks = Array.from(document.getElementsByClassName('input-remarks'));

    for(let i=0; i<rowCnt; i++) {
        member[i].value = details[i].memberId;
        code[i].value = details[i].code;
        workDate[i].value = details[i].workDate;
        timeStart[i].value = details[i].timeStart.substr(0, 5);
        timeEnd[i].value = details[i].timeEnd.substr(0, 5);
        remarks[i].value = details[i].remarks;
    }
}

function addRow() {
    let row = document.getElementsByClassName('overtime-group');
    let idx = row.length;

    document.getElementById('overtimeSection').append(defaultRow.cloneNode(true));

    let divRow = Array.from(row);
    divRow[idx].setAttribute('id', 'overtimeRow' + idx);

    let check = Array.from(document.getElementsByClassName('form-check-input'));
    check[idx].setAttribute('id', 'check' + idx);

    let chkLabel = Array.from(document.getElementsByClassName('form-check-label'));
    chkLabel[idx].setAttribute('for', 'check' + idx);

    let datePrepend = Array.from(document.getElementsByClassName('date-prepend'));
    datePrepend[idx].setAttribute('data-target', '#workDate' + idx);

    let workDate = Array.from(document.getElementsByClassName('input-work-date'));
    workDate[idx].setAttribute('id', 'workDate' + idx);
    workDate[idx].setAttribute('data-target', '#workDate' + idx);

    setDatetimePicker('workDate' + idx, 'date', true);

    let timeStartPrepend = Array.from(document.getElementsByClassName('time-start-prepend'));
    timeStartPrepend[idx].setAttribute('data-target', '#timeStart' + idx);

    let timeStart = Array.from(document.getElementsByClassName('input-time-start'));
    timeStart[idx].setAttribute('id', 'timeStart' + idx);
    timeStart[idx].setAttribute('data-target', '#timeStart' + idx);

    setDatetimePicker('timeStart' + idx, 'time', true);

    let timeEndPrepend = Array.from(document.getElementsByClassName('time-end-prepend'));
    timeEndPrepend[idx].setAttribute('data-target', '#timeEnd' + idx);

    let timeEnd = Array.from(document.getElementsByClassName('input-time-end'));
    timeEnd[idx].setAttribute('id', 'timeEnd' + idx);
    timeEnd[idx].setAttribute('data-target', '#timeEnd' + idx);

    setDatetimePicker('timeEnd' + idx, 'time', true);

    Array.from(document.getElementsByClassName('input-remarks'))[idx].value = '';
}

function removeRow() {
    let row = Array.from(document.getElementsByClassName('overtime-group'));
    let targetRow = row.length - 1;
    if(targetRow > 0)
        row[targetRow].remove();
}

function addAll() {
    checkAll();
    checkRemove();

    let memberSelector = Array.from(document.getElementsByClassName('overtime-member'))[0];
    let values = [];
    for(let i=0; i<memberSelector.childNodes.length; i++) {
        if(memberSelector.childNodes[i].nodeName === 'OPTION' && memberSelector.childNodes[i].value !== '') {
            values.push(memberSelector.childNodes[i].value);
        }
    }
    for(let i=0; i<values.length - 1; i++) {
        addRow();
    }
    let selectors = Array.from(document.getElementsByClassName('overtime-member'));
    for(let i=0; i<values.length; i++) {
        selectors[i].value = values[i];
    }
}

function checkAll() {
    Array.from(document.getElementsByClassName('form-check-input')).forEach(elem => {
        elem.checked = !elem.checked;
    });
}

function checkRemove() {
    let target = [];
    Array.from(document.getElementsByClassName('form-check-input')).forEach(elem => {
        if(elem.checked) {
            let idx = elem.getAttribute('id').replace('check', '');
            target.push(idx);
        }
        elem.checked = false;
    });

    let row = Array.from(document.getElementsByClassName('overtime-group'));
    target.forEach(idx => {
        row[idx].remove();
    });

    let leftRows = Array.from(document.getElementsByClassName('overtime-group'));
    let count = leftRows.length;

    if(count === 0) {
        document.getElementById('overtimeSection').append(defaultRow.cloneNode(true));
        setDatetimePicker('workDate0', 'date', true);
        setDatetimePicker('timeStart0', 'time', true);
        setDatetimePicker('timeEnd0', 'time', true);
    } else {
        for(let idx=0; idx<count; idx++) {   // 남은 요소들의 id를 재정렬
            let divRow = leftRows;
            divRow[idx].setAttribute('id', 'overtimeRow' + idx);

            let check = Array.from(document.getElementsByClassName('form-check-input'));
            check[idx].setAttribute('id', 'check' + idx);

            let chkLabel = Array.from(document.getElementsByClassName('form-check-label'));
            chkLabel[idx].setAttribute('for', 'check' + idx);


            let datePrepend = Array.from(document.getElementsByClassName('date-prepend'));
            datePrepend[idx].setAttribute('data-target', '#workDate' + idx);

            let workDate = Array.from(document.getElementsByClassName('input-work-date'));
            workDate[idx].setAttribute('id', 'workDate' + idx);
            workDate[idx].setAttribute('data-target', '#workDate' + idx);


            let timeStartPrepend = Array.from(document.getElementsByClassName('time-start-prepend'));
            timeStartPrepend[idx].setAttribute('data-target', '#timeStart' + idx);

            let timeStart = Array.from(document.getElementsByClassName('input-time-start'));
            timeStart[idx].setAttribute('id', 'timeStart' + idx);
            timeStart[idx].setAttribute('data-target', '#timeStart' + idx);


            let timeEndPrepend = Array.from(document.getElementsByClassName('time-end-prepend'));
            timeEndPrepend[idx].setAttribute('data-target', '#timeEnd' + idx);

            let timeEnd = Array.from(document.getElementsByClassName('input-time-end'));
            timeEnd[idx].setAttribute('id', 'timeEnd' + idx);
            timeEnd[idx].setAttribute('data-target', '#timeEnd' + idx);
        }
    }
}

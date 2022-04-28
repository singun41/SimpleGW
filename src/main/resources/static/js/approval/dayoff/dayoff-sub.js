async function getDetails(docsId) {
    // let docsId = document.getElementById('docsId').innerText;
    let details = await fetchCustom('GET', 'default', '', 'approval/dayoff/details/' + docsId, 'json');
    let rowCnt = details.length;
    for(let i=0; i<rowCnt - 1; i++) {   // 기본 1행은 있으므로.
        addRow();
    }

    let dayoffCode = Array.from(document.getElementsByClassName('dayoff-code'));
    let dateStartInput = Array.from(document.getElementsByClassName('input-date-start'));
    let dateEndInput = Array.from(document.getElementsByClassName('input-date-end'));
    let dayDuration = Array.from(document.getElementsByClassName('day-duration'));
    let dayCount = Array.from(document.getElementsByClassName('day-count'));
    
    for(let i=0; i<rowCnt; i++) {
        dayoffCode[i].value = details[i].code;
        dateStartInput[i].value = details[i].dateStart;
        dateEndInput[i].value = details[i].dateEnd;
        dayDuration[i].value = details[i].duration;
        dayCount[i].value = details[i].count;
    }
}

function ckeckDate(elem) {
    // start와 end의 날짜를 이동할 때
    // end가 start보다 이전 날짜로 갈 때 start를 end와 같게 만들기
    // start가 end보다 이후 날짜로 갈 때 end를 start와 같게 만들기
    let elemId = elem.getAttribute('id');
    let idx;
    let isStart = false;
    let isEnd = false;
    if(isNaN(elemId.replace('dateStart', ''))) {
        idx = elemId.replace('dateEnd', '');
        isEnd = true;
    }
    if(isNaN(elemId.replace('dateEnd', ''))) {
        idx = elemId.replace('dateStart', '');
        isStart = true;
    }

    let dateStartInput = document.getElementById('dateStart' + idx);
    let dateEndInput = document.getElementById('dateEnd' + idx);

    if(dateStartInput.value === '' || dateEndInput.value === '') {
        return;
    }
    if(dateStartInput.value > dateEndInput.value) {
        if(isEnd) dateStartInput.value = dateEndInput.value;
        if(isStart) dateEndInput.value = dateStartInput.value;
    }

    calcDays(idx, dateStartInput, dateEndInput);
}

function calcDays(idx, dateStartInput, dateEndInput) {
    let codeSelector = document.getElementById('codeSelector' + idx);
    let dayDuration = document.getElementById('duration' + idx);
    let dayoffCount = document.getElementById('count' + idx);
    let dateStart = moment(dateStartInput.value);
    let dateEnd = moment(dateEndInput.value);

    let count = moment.duration(dateEnd.diff(dateStart)).asDays() + 1;
    
    // moment().day(): sun=0, mon=1 ... sat=6
    let weekCnt = 0;
    for(let i=0; i<count; i++) {
        if(moment(dateStart).add(i, 'days').day() === 0 || moment(dateStart).add(i, 'days').day() === 6) {
            weekCnt++;
        }
    }
    dayDuration.value = count - weekCnt;
    dayoffCount.value = (codeSelector.options[codeSelector.selectedIndex].text.indexOf('반차') >= 0 ? dayDuration.value / 2 : dayDuration.value);
}

function addRow() {
    let row = document.getElementsByClassName('dayoff-row');
    let idx = row.length;

    let newRow = document.getElementById('dayoffRow0').cloneNode(true);
    document.getElementById('dayoffRowSection').append(newRow);

    let divRow = Array.from(row);
    divRow[idx].setAttribute('id', 'dayoffRow' + idx);

    let codeSelector = Array.from(document.getElementsByClassName('dayoff-code'));
    codeSelector[idx].setAttribute('id', 'codeSelector' + idx);

    let dateStartPrepend = Array.from(document.getElementsByClassName('date-start-prepend'));
    dateStartPrepend[idx].setAttribute('data-target', '#dateStart' + idx);

    let dateStartInput = Array.from(document.getElementsByClassName('input-date-start'));
    dateStartInput[idx].setAttribute('id', 'dateStart' + idx);
    dateStartInput[idx].setAttribute('data-target', '#dateStart' + idx);

    let dateEndInput = Array.from(document.getElementsByClassName('input-date-end'));
    dateEndInput[idx].setAttribute('id', 'dateEnd' + idx);
    dateEndInput[idx].setAttribute('data-target', '#dateEnd' + idx);

    let dateEndPrepend = Array.from(document.getElementsByClassName('date-end-prepend'));
    dateEndPrepend[idx].setAttribute('data-target', '#dateEnd' + idx);

    let dayDuration = Array.from(document.getElementsByClassName('day-duration'));
    dayDuration[idx].setAttribute('id', 'duration' + idx);

    let dayCount = Array.from(document.getElementsByClassName('day-count'));
    dayCount[idx].setAttribute('id', 'count' + idx);

    setDatetimePicker('dateStart' + idx, 'date', true);
    setDatetimePicker('dateEnd' + idx, 'date', true);
    document.getElementById('duration' + idx).value = 1;
    document.getElementById('count' + idx).value = 1;
}

function removeRow() {
    let row = Array.from(document.getElementsByClassName('dayoff-row'));
    let targetRow = row.length - 1;
    if(targetRow > 0)
        row[targetRow].remove();
}
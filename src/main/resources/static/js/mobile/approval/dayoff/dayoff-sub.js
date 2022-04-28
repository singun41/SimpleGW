window.addEventListener('DOMContentLoaded', event => {

});


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

    setDatetimePicker('dateStart' + idx, 'date', true);
    setDatetimePicker('dateEnd' + idx, 'date', true);
}

function removeRow() {
    let row = Array.from(document.getElementsByClassName('dayoff-row'));
    let targetRow = row.length - 1;
    if(targetRow > 0)
        row[targetRow].remove();
}
async function getDetails(docsId) {
    let details = await fetchCustom('GET', 'default', '', 'approval/purchase/details/' + docsId, 'json');
    let rowCnt = details.length;
    for(let i=0; i<rowCnt - 1; i++) {   // 기본 1행은 있으므로.
        addRow();
    }

    let itemName = Array.from(document.getElementsByClassName('item-name'));
    let itemSpec = Array.from(document.getElementsByClassName('item-spec'));
    let dueDate = Array.from(document.getElementsByClassName('input-due-date'));
    let store = Array.from(document.getElementsByClassName('item-store'));
    let url = Array.from(document.getElementsByClassName('item-url'));
    let unitPrice = Array.from(document.getElementsByClassName('unit-price'));
    let unitQty = Array.from(document.getElementsByClassName('unit-qty'));
    let sumPrice = Array.from(document.getElementsByClassName('sum-price'));

    for(let i=0; i<rowCnt; i++) {
        itemName[i].value = details[i].itemName;
        itemSpec[i].value = details[i].itemSpec;
        dueDate[i].value = details[i].dueDate;
        store[i].value = details[i].store;
        url[i].value = details[i].url;
        unitPrice[i].value = Number(details[i].price).toLocaleString();
        unitQty[i].value = Number(details[i].qty).toLocaleString();
        sumPrice[i].value = Number(details[i].price * details[i].qty).toLocaleString();
    }
}

function calcSumPrice(tagIdx) {
    // tagIdx = id 맨 끝에 붙는 index 값
    // onblur 로 포커스 아웃시 이 function을 호출함.
    
    let unitPrice = Array.from(document.getElementsByClassName('unit-price'));
    let unitQty = Array.from(document.getElementsByClassName('unit-qty'));
    let sumPrice = Array.from(document.getElementsByClassName('sum-price'));

    let price = unitPrice[tagIdx].value.replaceAll(',', '');
    let qty = unitQty[tagIdx].value.replaceAll(',', '')

    if(isNaN(price) || price === '0') {
        unitPrice[tagIdx].value = '';
        return;
    }
    if(isNaN(qty) || qty === '0') {
        unitQty[tagIdx].value = '';
        return;
    }

    let result = price * qty;

    // 계산값에 comma 세팅해주기
    sumPrice[tagIdx].value = (result === 0 ? '' : result.toString().replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,'));
}

function addRow() {
    let row = document.getElementsByClassName('item-group');
    let idx = row.length;

    let newRow = document.getElementById('itemRow0').cloneNode(true);
    document.getElementById('itemSection').append(newRow);

    let divRow = Array.from(row);
    divRow[idx].setAttribute('id', 'itemRow' + idx);

    let datePrepend = Array.from(document.getElementsByClassName('date-prepend'));
    datePrepend[idx].setAttribute('data-target', '#dueDate' + idx);

    let dueDate = Array.from(document.getElementsByClassName('input-due-date'));
    dueDate[idx].setAttribute('id', 'dueDate' + idx);
    dueDate[idx].setAttribute('data-target', '#dueDate' + idx);

    setDatetimePicker('dueDate' + idx, 'date', false);

    let unitPrice = Array.from(document.getElementsByClassName('unit-price'));
    unitPrice[idx].setAttribute('onblur', 'calcSumPrice(' + idx + ')');

    let unitQty = Array.from(document.getElementsByClassName('unit-qty'));
    unitQty[idx].setAttribute('onblur', 'calcSumPrice(' + idx + ')');

    Array.from(document.getElementsByClassName('item-name'))[idx].value = '';
    Array.from(document.getElementsByClassName('item-spec'))[idx].value = '';
    dueDate[idx].value = '';
    Array.from(document.getElementsByClassName('item-store'))[idx].value = '';
    Array.from(document.getElementsByClassName('item-url'))[idx].value = '';
    unitPrice[idx].value = '';
    unitQty[idx].value = '';
    Array.from(document.getElementsByClassName('sum-price'))[idx].value = '';
}

function removeRow() {
    let row = Array.from(document.getElementsByClassName('item-group'));
    let targetRow = row.length - 1;
    if(targetRow > 0)
        row[targetRow].remove();
}

function removeComma(numStr) {
    if(numStr === '') {
        return numStr;
    } else {
        return numStr.replace(',', '');
    }
}
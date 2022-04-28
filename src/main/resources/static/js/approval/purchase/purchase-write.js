window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '물품 구매 신청서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("purchase", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("purchase", 1)');

    setDatetimePicker('dueDate0', 'date', false);

    copyApprovalDocsCheck();
});

async function insertApprovalDocs(docsKind, isRegistered) {
    let title = document.getElementById('title').value;
    let content = document.getElementById('content').value;

    if(isEmptyTitle(title)) return;

    let size = Array.from(document.getElementsByClassName('item-group')).length;
    let arrItemName = [];
    let arrItemSpec = [];
    let arrDueDate = [];
    let arrStore = [];
    let arrUrl = [];
    let arrPrice = [];
    let arrQty = [];
    for(let i=0; i<size; i++) {
        arrItemName.push(document.getElementsByClassName('item-name')[i].value);
        arrItemSpec.push(document.getElementsByClassName('item-spec')[i].value);
        arrDueDate.push(document.getElementsByClassName('input-due-date')[i].value);
        arrStore.push(document.getElementsByClassName('item-store')[i].value);
        
        let urlStr = document.getElementsByClassName('item-url')[i].value;
        if(urlStr !== '') {
            if(!isUrlFormat(urlStr)) {
                alert((i + 1) + ' 번 행의 URL 이 잘못되었습니다.');
                return;
            }
        }
        arrUrl.push(urlStr);
        arrPrice.push( removeComma(document.getElementsByClassName('unit-price')[i].value) );
        arrQty.push( removeComma(document.getElementsByClassName('unit-qty')[i].value) );
    }

    // sum-price는 서버에서 계산한다.

    let arrPurchase = [];
    for(let i=0; i<arrItemName.length; i++) {
        let purchase = {
            seq: i + 1,
            itemName: arrItemName[i],
            itemSpec: arrItemSpec[i],
            dueDate: arrDueDate[i],
            store: arrStore[i],
            url: arrUrl[i],
            price: arrPrice[i],
            qty: arrQty[i]
        };
        arrPurchase.push(purchase);
    }

    let param = {
        title: title,
        content: content,
        approvers: approver,
        referrers: referrer,
        registered: isRegistered,

        purchaseDtoList: arrPurchase
    };

    const docsId = await saveSubListApprovalDocs(param, docsKind, isRegistered);
}
function openReferrerAdd() {
    let option = "width=800, height=650";
    window.open('/page/approval/referrer-add', '', option);
}

async function addReferrer() {
    let referrerAddData = JSON.parse(localStorage.getItem('referrerAddData'));
    localStorage.removeItem('referrerAddData');
    let ids = Array.from(referrerAddData.arrReferrerId);
    let names = Array.from(referrerAddData.arrReferrerName);

    let response = await fetchPatchParams(`approval/referrer-add/${docsType}/${docsId}`, ids);
    let result = await response.json();
    alert(result.msg);

    if(response.ok) {
        let referrerList = document.getElementById('referrerList');
        names.forEach(e => {
            let span = document.createElement('span');
            span.innerText = e;
    
            let div = document.createElement('div');
            div.classList.add('col-4', 'mt-2', 'text-center', 'custom-fs-9', 'custom-col-padding');
            
            div.append(span);
            referrerList.append(div);
        });
    }
}

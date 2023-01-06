document.addEventListener('DOMContentLoaded', () => {
    Array.from(document.getElementsByTagName('img')).forEach(e => {
        e.addEventListener('click', () => {
            window.open(e.getAttribute('src'), '', '');
        });
    });


    let docsType = document.getElementById('docsType').innerText;
    let btnBack = document.getElementById('btnBack');
    if(docsType === 'notice' || docsType === 'freeboard') {
        btnBack.setAttribute('onclick', `page('${docsType}/list')`);
    } else {
        btnBack.setAttribute('onclick', `page('${docsType}/search')`);
    }
});

async function deleteDocs() {
    if(!confirm('문서를 삭제하시겠습니까?'))
        return;
    
    let docsId = document.getElementById('docsId').innerText;
    let docsType = document.getElementById('docsType').innerText;

    let response = await fetchDelete(`${docsType}/${docsId}`);
    let result = await response.json();
    alert(result.msg);
    
    if(response.ok)
        page('board');
}

function editDocs() {
    let docsId = document.getElementById('docsId').innerText;
    let docsType = document.getElementById('docsType').innerText;

    page(`${docsType}/${docsId}/modify`);
}

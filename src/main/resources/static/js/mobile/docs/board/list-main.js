document.addEventListener('DOMContentLoaded', () => {
    docsType = document.getElementById('docsType').innerText;
    getList();

    document.getElementById('btnBack').setAttribute('onclick', 'page("board")');
});

let docsType;
async function getList() {
    let response = await fetchGet(`${docsType}/main-list`);
    let result = await response.json();
    
    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);
    
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            let td = document.createElement('td');
            let title = document.createElement('div');
            
            title.classList.add('text-truncate');
            title.innerText = e.title;

            td.classList.add('td-title-main');
            td.append(title);
    
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
    
            tr.setAttribute('onclick', `page('${docsType}/${e.id}')`);
            list.append(tr);
        });
    }
}

function searchPage() {
    page(`${docsType}/search`);
}

function writePage() {
    page(`${docsType}/write`);
}

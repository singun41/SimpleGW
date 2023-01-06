async function searchNew(type) {   // received-approver.js, received-referrer.js에서 호출.
    let response = await fetchGet(`approval-list/${type}/new`);
    let result = await response.json();

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            
            let type = document.createElement('td');
            type.classList.add('text-center');

            let typeAnchor = document.createElement('a');
            typeAnchor.setAttribute('href', `/page/m/approval/${e.type.toLowerCase()}/${e.id}`);
            typeAnchor.classList.add('text-decoration-none', 'text-dark');
            typeAnchor.innerText = e.typeTitle;

            let title = document.createElement('td');
            title.classList.add('text-start', 'align-middle');

            let titleAnchor = document.createElement('a');
            titleAnchor.setAttribute('href', `/page/m/approval/${e.type.toLowerCase()}/${e.id}`);
            titleAnchor.classList.add('text-decoration-none', 'text-dark');
            titleAnchor.innerText = e.title;
            
            type.append(typeAnchor);
            title.append(titleAnchor);
            tr.append(type, title);
            list.append(tr);
        });
    }
}

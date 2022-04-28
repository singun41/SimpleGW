function setTitle(iconClass, title) {
    document.getElementById('titleIcon').setAttribute('class', iconClass + ' custom-card-header-fas-icon-sm');
    document.getElementById('title').append(title);
}

async function searchList(docsKind) {
    let param = {
        from: document.getElementById('dateFrom').value
    };
    let result = await fetchCustom('GET', 'urlEncoded', param, 'mobile/' + docsKind + '/list', 'json');

    let tbody = document.getElementById('tbodyList');
    while(tbody.hasChildNodes())
        tbody.removeChild(tbody.firstChild);

    result.forEach(e => {
        let tr = document.createElement('tr');
        let tdId = document.createElement('td');
        tdId.textContent = e.id;
        tdId.setAttribute('class', 'd-none');

        let tdTitle = document.createElement('td');
        let tdTitleInnerAnchor = document.createElement('a');
        tdTitleInnerAnchor.setAttribute('class', 'text-decoration-none text-dark');
        tdTitleInnerAnchor.setAttribute('href', '/mobile/' + docsKind + '/view/' + e.id);
        tdTitleInnerAnchor.textContent = e.title;
        tdTitle.append(tdTitleInnerAnchor);

        tr.append(tdId, tdTitle);
        tbody.append(tr);
    });
}
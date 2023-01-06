async function getCodes() {
    let type = document.getElementById('type').value;
    if(!type) {
        alert('Type을 선택하세요.');
        return;
    }

    let response = await fetchGet(`basecode/${type}`);
    let result = await response.json();
    document.getElementById('tableHeader').classList.add('d-none');

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        document.getElementById('tableHeader').classList.remove('d-none');

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            
            let seq = document.createElement('td');
            let code = document.createElement('td');
            let value = document.createElement('td');
            let enabled = document.createElement('td');

            let edit = document.createElement('td');
            let i = document.createElement('i');
            i.classList.add('fa-solid', 'fa-pen-clip');

            let btn = document.createElement('button');
            btn.setAttribute('type', 'button');
            btn.classList.add('btn', 'btn-outline-secondary', 'btn-sm');
            btn.setAttribute('onclick', `newTab('admin/code/${e.id}')`);
            btn.append(i);
            edit.append(btn);

            seq.innerText = e.seq;
            code.innerText = e.code;
            value.innerText = e.value;
            
            if(e.enabled) {
                enabled.classList.add('text-success');
                enabled.innerHTML = '<i class="fa-solid fa-check"></i>';
            } else {
                enabled.classList.add('text-danger');
                enabled.innerHTML = '<i class="fa-solid fa-ban"></i>';
            }

            tr.append(seq, code, value, enabled, edit);
            list.append(tr);
        });
    }
}

function openNew() {
    let type = document.getElementById('type').value;
    if(!type) {
        alert('Type을 선택하세요.');
        return;
    }
    newTab(`admin/code/new/${type}`);
}

async function getProfiles() {
    let response = await fetchGet(`employees/${document.getElementById('team').value}`);
    let result = await response.json();
    document.getElementById('tableHeader').classList.add('d-none');

    if(response.ok) {
        let list = document.getElementById('list');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        document.getElementById('tableHeader').classList.remove('d-none');

        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let name = document.createElement('td');
            let email = document.createElement('td');
            let mobile = document.createElement('td');

            name.innerText = `${e.name}\n${e.jobTitle}`;
            email.innerText = (e.email === '' || e.email === null) ? '' : e.email.replace('@', '\n@');
            mobile.innerText = (e.mobile === '' || e.mobile === null) ? '' : e.mobile;

            tr.append(name, email, mobile);
            list.append(tr);
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');
    buildDatatable();
});

function buildDatatable() {
    $('#datatables').DataTable({
        language: {
            paginate: {
                previous: '<i class="fa-solid fa-chevron-left"></i>',
                next: '<i class="fa-solid fa-chevron-right"></i>'
            }
        },
        order: [1, 'asc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '13%' },
            { targets: 1, width: '8%' },
            { targets: 2, width: '13%' },
            { targets: 3, width: '12%' },
            { targets: 4, width: '13%' },
            { targets: 5, width: '17%' },
            { targets: 6, width: '8%' },
            { targets: 7, width: '16%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 650
    });
}

async function getProfiles() {
    let response = await fetchGet(`employees/${document.getElementById('team').value}`);
    let result = await response.json();

    destroyDataTable();
    removeDatalist();

    if(response.ok) {
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');

            let tdImg = document.createElement('td');
            let name = document.createElement('td');
            let jobTitle = document.createElement('td');
            let dateHire = document.createElement('td');
            let duration = document.createElement('td');
            let email = document.createElement('td');
            let tel = document.createElement('td');
            let mobile = document.createElement('td');

            let img = document.createElement('img');
            img.setAttribute('src', `data:image:jpg;base64,${e.portrait}`);   // e.portrait는 byte 배열.
            img.setAttribute('onerror', 'this.src="/images/portrait/default.jpg";');
            img.style.height = '125px';
            img.classList.add('img-thumbnail');
            tdImg.append(img);
            
            name.innerText = e.name;
            jobTitle.innerText = e.jobTitle;
            dateHire.innerText = (e.dateHire === null || e.dateHire === '') ? '' : dayjs(e.dateHire).format('YY. MM. DD.');
            duration.innerText = e.duration;
            email.innerText = e.email;
            tel.innerText = e.tel;
            mobile.innerText = e.mobile;

            tr.append(tdImg, name, jobTitle, dateHire, duration, email, tel, mobile);
            datalist.append(tr);
        });
    }
    buildDatatable();
}

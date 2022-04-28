window.addEventListener('DOMContentLoaded', event => {
    setDataTable();
});
const memberList = document.getElementById('memberListTbody');

function destroyDataTable() {
    $('#memberListTable').DataTable().destroy();
}

function setDataTable() {
    $('#memberListTable').DataTable({
        order: [1, 'asc'],
        ordering: true,
        columnDefs: [
            { targets: 0, width: '12%' },
            { targets: 1, width: '8%' },
            { targets: 2, width: '6%' },
            { targets: 3, width: '12%' },
            { targets: 4, width: '12%' },
            { targets: 5, width: '12%' },
            { targets: 6, width: '16%' },
            { targets: 7, width: '7%' },
            { targets: 8, width: '15%' }
        ],
        scrollX: true,
        scrollXInner: '100%',
        fixedHeader: true,
        scrollY: 585,
        pageLength: 100
    });
}

async function getTeamMembers() {
    const teamSelector = document.getElementById('selectedTeam');
    let param = {
        team: teamSelector.value
    };

    let result = await fetchCustom('GET', 'urlEncoded', param, 'members-info', 'json');
    return result;
}

async function setTeamMemeberInfoList() {
    destroyDataTable();

    while(memberList.hasChildNodes()) {
        memberList.removeChild(memberList.firstChild);
    }

    let members = await getTeamMembers();
    if(members.length <= 0) {
        return;
    }

    members.forEach(elem => {
        let tr = document.createElement('tr');
        tr.setAttribute('class', 'text-center');

        let tdImg = document.createElement('td');
        let tagImg = document.createElement('img');
        tagImg.setAttribute('class', 'img-thumbnail rounded employee-face');

        if(elem.imgSrc === null || elem.imgSrc === '') {
            tagImg.setAttribute('src', '/images/user-default.png');
        } else {
            tagImg.setAttribute('src', 'data:image/jpg;base64,' + elem.imgSrc);
        }
        tdImg.append(tagImg);

        let tdName = document.createElement('td');
        tdName.textContent = elem.name;

        let tdAge = document.createElement('td');
        tdAge.textContent = e.age;

        let tdJobTitle = document.createElement('td');
        tdJobTitle.textContent = elem.jobTitle;

        let tdDateHire = document.createElement('td');
        tdDateHire.textContent = (elem.dateHire ? elem.dateHire.replaceAll('-', '. ') + '.' : '');

        let tdServiceDays = document.createElement('td');
        tdServiceDays.textContent = elem.serviceDays;

        let tdEmail = document.createElement('td');
        tdEmail.textContent = elem.mailAddress;

        let tdTel = document.createElement('td');
        tdTel.textContent = elem.tel;

        let tdMobile = document.createElement('td');
        tdMobile.textContent = elem.mobileNo;

        tr.append(tdImg, tdName, tdAge, tdJobTitle, tdDateHire, tdServiceDays, tdEmail, tdTel, tdMobile);
        tr.childNodes.forEach(td => {
            td.setAttribute('class', 'align-middle');
        });
        memberList.append(tr);
    });

    setDataTable();
}
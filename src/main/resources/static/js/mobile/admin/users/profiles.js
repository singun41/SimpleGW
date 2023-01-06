document.addEventListener('DOMContentLoaded', () => {
    flatpickr('.input-date', {
        enableTime: false,
        dateFormat: 'Y. m. d.',
        'locale': 'ko',
        disableMobile: 'true'
        // mode 기본값인 single이면 모바일 브라우저에서는 기본 date/time/datetime input으로 전환됨. 이 옵션을 전환되지 않게 설정.
    });
});

async function update() {
    if(!confirm('수정하시겠습니까?'))
        return;
    
    let id = document.getElementById('id').value;
    let params = {
        role: document.getElementById('role').value,
        enabled: (document.getElementById('enabled').value === '1' ? true : false),
        team: document.getElementById('team').value,
        jobTitle: document.getElementById('jobTitle').value,
        name: document.getElementById('name').value,
        nameEng: (
            document.getElementById('nameEng').value === '' ? null :
            document.getElementById('nameEng').value
        ),
        mobile: (
            document.getElementById('mobile').value === '' ? null :
            document.getElementById('mobile').value
        ),
        tel: (
            document.getElementById('tel').value === '' ? null :
            document.getElementById('tel').value
        ),
        email: (
            document.getElementById('email').value === '' ? null :
            document.getElementById('email').value
        ),
        emailUse: (document.getElementById('emailUse').value === '1' ? true : false),
        birthday: (
            document.getElementById('birthday').value === '' ? null :
            document.getElementById('birthday').value.replaceAll('. ', '-').replaceAll('.', '')
        ),
        dateHire: (
            document.getElementById('dateHire').value === '' ? null :
            document.getElementById('dateHire').value.replaceAll('. ', '-').replaceAll('.', '')
        ),
        dateResign: (
            document.getElementById('dateResign').value === '' ? null :
            document.getElementById('dateResign').value.replaceAll('. ', '-').replaceAll('.', '')
        ),
        resigned: (document.getElementById('resigned').value === '1' ? true : false)
    };

    let response = await fetchPatchParams(`user/${id}`, params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        window.close();
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnBack').setAttribute('onclick', 'page("account")');
});

async function updateProfile() {
    if(!confirm('프로필을 수정하시겠습니까?'))
        return;
        
    let portraitInput = document.getElementById('portraitInput');
    if(portraitInput.files[0] !== undefined && portraitInput.files[0] !== null) {
        let formData = new FormData;
        formData.append('img', portraitInput.files[0]);

        let response = await fetchFormData('portrait', formData);
        if(response.ok)
            document.getElementById('userPortrait').setAttribute('src', '/portrait');
    }

    let params = {
        nameEng: document.getElementById('nameEng').value,
        mobile: document.getElementById('mobile').value
    };
    let response = await fetchPatchParams('profile', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        page('account');
}

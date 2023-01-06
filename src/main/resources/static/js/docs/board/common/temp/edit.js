function editTemp() {
    location.href = `${docsId}/modify`;
}

async function deleteTemp() {
    if(!confirm('임시문서를 삭제하시겠습니까?'))
        return;

    let response = await fetchDelete(`${docsType}/temp/${docsId}`);
    let result = await response.json();
    alert(result.msg);

    if(response.ok)
        location.href = '/page/docs/temp/list';
}

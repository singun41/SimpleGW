function getApiUrl(url)  {
    return `${location.protocol}//${location.host}/${url}`;
}

function getQueryString(url, params) {
    return `${getApiUrl(url)}?${new URLSearchParams(params)}`;
}

function getFetchOptions(params, methodType) {
    let fetchOptions = {
        method: methodType,
        headers: { 'Content-Type': 'application/json; charset=utf-8' },
        body: JSON.stringify(params)
    };
    return fetchOptions;
}


// ----- ----- ----- ----- ----- Method GET ----- ----- ----- ----- ----- //
async function fetchGet(url) {
    let fetchOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' }
    };
    return await fetch(getApiUrl(url), fetchOptions);
}
async function fetchGetParams(url, params) {
    let fetchOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' }
    };
    return await fetch(getQueryString(url, params), fetchOptions);
}
// ----- ----- ----- ----- ----- Method GET ----- ----- ----- ----- ----- //



// ----- ----- ----- ----- ----- Method POST ----- ----- ----- ----- ----- //
async function fetchPost(url) {
    let fetchOptions = getFetchOptions('', 'POST');
    return await fetch(getApiUrl(url), fetchOptions);
}
async function fetchPostParams(url, params) {
    let fetchOptions = getFetchOptions(params, 'POST');
    return await fetch(getApiUrl(url), fetchOptions);
}
// ----- ----- ----- ----- ----- Method POST ----- ----- ----- ----- ----- //



// ----- ----- ----- ----- ----- Method PATCH ----- ----- ----- ----- ----- //
async function fetchPatch(url) {
    let fetchOptions = getFetchOptions('', 'PATCH');
    return await fetch(getApiUrl(url), fetchOptions);
}
async function fetchPatchParams(url, params) {
    let fetchOptions = getFetchOptions(params, 'PATCH');
    return await fetch(getApiUrl(url), fetchOptions);
}
// ----- ----- ----- ----- ----- Method PATCH ----- ----- ----- ----- ----- //



// ----- ----- ----- ----- ----- Method DELETE ----- ----- ----- ----- ----- //
async function fetchDelete(url) {
    let fetchOptions = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' }
    };
    return await fetch(getApiUrl(url), fetchOptions);
}
// ----- ----- ----- ----- ----- Method DELETE ----- ----- ----- ----- ----- //



// ----- ----- ----- ----- ----- Method FORM-DATA ----- ----- ----- ----- ----- //
async function fetchFormData(url, formData) {
    let fetchOptions = {
        method: 'POST',
        // headers: { 'Content-Type': 'multipart/form-data' },    // formData를 보낼 땐 헤더를 설정하지 않는다. 주석 해제하면 에러남.
        body: formData
    };
    return await fetch(getApiUrl(url), fetchOptions);
}
// ----- ----- ----- ----- ----- Method FORM-DATA ----- ----- ----- ----- ----- //

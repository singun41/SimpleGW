/*
	Fetch API customizing code - ver 21.09.15 developer: singun41
    
    21.10.28 updated
        - RequestResult 객체로 받으므로 항상 json으로 먼저 파싱한 뒤 type 에 따라 리턴을 다르게 함.
        - type{json, text} --> type{json, text, msg} msg 추가.
        - type: msg인 경우는 .message를 추출해서 알림창으로 띄우도록 변경.

    21.10.22 updated
        - getHttpMethodUrl 코드 변경
        - formData 코드 추가
    
    21.11.03 updated
        - returnType null 추가

    22. 02. 08. updated
        - DELETE를 HTTP 명세에 맞게 변경. PayloadBody를 사용하지 않는다. 서버에서는 requestBody로 받지 않는다.
*/

function getHttpMethodUrl(url)  {
    return location.protocol + '//' + location.host + '/' + url;
    // return location.pathname + '/' + url;
}
function getQueryString(url, params) {
    return getHttpMethodUrl(url) + '?' + new URLSearchParams(params);
}
function getDefaultFetchOptions(params, methodType) {
    let fetchOptions = {
        method: methodType,
        headers: { 'Content-Type': 'application/json; charset=utf-8' },
        body: JSON.stringify(params)
    }
    return fetchOptions;
}
function getUrlEncodedFetchOptions(params, methodType) {
    let fetchPostOptions = {
        method: methodType,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' },
        body: new URLSearchParams(params)
    };
    return fetchPostOptions;
}
async function fetchCustom(methodType, paramsType, params, url, returnType) {
    /*
        methodType = {'GET', 'POST', 'PUT', 'PATCH', 'DELETE'};
        paramsType = {'default', 'urlEncoded'};
        returnType = {'json', 'msg', 'text'};

        call examples.
        
        let params = {
            name: tester,
            age: 33
        }

        await fetchCustom('GET', 'default', params, 'test-method', 'msg');
        await fetchCustom('POST', 'default', params, 'test-method', 'json');
        await fetchCustom('POST', 'urlEncoded', params, 'test-method', 'json');

        결과: ResponseEntity<Object>에 RequestResult 객체를 리턴.
        메시지만 받으려면 json(); 호출 후 .message 로 메시지만 가져와서 알림창으로 보여준다.
    */

    let fetchOptions = null;
    let response = null;

    if(methodType === 'GET' || methodType === 'DELETE') {   // body를 사용하지 않는다.
        if(paramsType === 'default') {   // 기본값: 헤더로 호출(URL)
            fetchOptions = {
                method: methodType,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' }
            };
            response = await fetch(getHttpMethodUrl(url), fetchOptions);
        }
        if(paramsType === 'urlEncoded') {    // 파라미터 있는 경우: 쿼리스트링 작성 후 전송
            fetchOptions = {
                method: methodType,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8' }
            };
            response = await fetch(getQueryString(url, params), fetchOptions);
        }
    }

    if(methodType === 'POST' || methodType === 'PUT' || methodType === 'PATCH') {
        if(paramsType === 'default') {
            fetchOptions = getDefaultFetchOptions(params, methodType);
            response = await fetch(getHttpMethodUrl(url), fetchOptions);
        }
        if(paramsType === 'urlEncoded') {
            fetchOptions = getUrlEncodedFetchOptions(params, methodType);
            response = await fetch(getHttpMethodUrl(url), fetchOptions);
        }
    }

    let result = null;
    if(response !== null) {
        if(response.ok) {   // 결과 성공
            if(returnType === 'json') {             // RequestResult 객체 또는 List<Object>로 받기
                result = await response.json();     // List<Object>로 받는 경우는 그대로 Arrays.forEach 문으로 핸들링 가능.

            } else if(returnType === 'text') {      // 단일 Object로 받기
                result = await response.text();     // 예를 들면 documentId Long 단일 객체 받아와서 엘리먼트에 바인딩하고, 첨부파일 업로드 동작을 진행할 때

            } else if(returnType === 'msg') {       // RequestResult객체의 message 필드를 알림창으로 띄우기
                result = await response.json();
                alert(result.message);

            } else if(returnType === null || returnType === 'null' || returnType === '') {
                return;

            } else {
                result = 'fetchCustom() error' + '\n' + 'methodType: ' + methodType + '\n' + 'url: ' + getHttpMethodUrl(url) + '\n' + 'message: fetchCustom() function\'s \'returnType\' parameter was invalid.';
                alert(result);
                console.log(result);
                return;
            }

        } else {   // 결과 실패
            result = await response.json();
            alert(result.message);

            console.log(result);   // console 창에 에러 메시지가 남는데 내용이 없어서 추가함.
            return result;
        }
    } else {
        // 브라우저 콘솔에도 로그를 남기기 위해 알럿 창을 띄우면서 콘솔로그에도 남긴다.
        result = 'fetchCustom() error' + '\n' + 'methodType: ' + methodType + '\n' + 'url: ' + getHttpMethodUrl(url) + '\n' + 'message: ' + 'response is null.';
        alert(result);
        console.log(result);
        return;
    }
    return result;
}

async function fetchCustomFormData(url, formData) {
    let fetchOptions = {
        method: 'POST',
        // headers: { 'Content-Type': 'application/json; charset=utf-8' },    // formData를 보낼 땐 헤더를 설정하지 않는다.
        body: formData
    }
    let response = await fetch(getHttpMethodUrl(url), fetchOptions);
    let result = await response.json();
    return result.message;   // 컨트롤러에서 ResponseEntity<Object>에 ReqeustResult 객체로 리턴하므로 결과 메시지를 추출한다.
}
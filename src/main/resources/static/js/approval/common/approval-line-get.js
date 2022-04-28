async function getApprovalLine(docsId) {
    let result = await fetchCustom('GET', 'default', '', 'approval/line/' + docsId, 'json');

    if(result) {
        let approvers = result.approvers;
        let referrers = result.referrers;
        let arrApprover = [];
        let arrApproverName= [];
        let arrReferrer = [];
        let arrReferrerName = [];
    
        approvers.forEach(elem => {
            arrApprover.push(elem.memberId);
            arrApproverName.push(elem.jobTitle + ' ' + elem.name);
        });
        referrers.forEach(elem => {
            arrReferrer.push(elem.memberId);
            arrReferrerName.push(elem.jobTitle + ' ' + elem.name);
        });
    
        let lineMembers = {
            approver: arrApprover,
            approverName: arrApproverName,
            referrer: arrReferrer,
            referrerName: arrReferrerName
        };
    
        applyToLineTables(lineMembers);
    }
}
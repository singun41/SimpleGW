package com.project.simplegw.approval.services;

import java.util.List;
import java.util.stream.Collectors;

import com.project.simplegw.approval.dtos.ApprovalDocsDTO;
import com.project.simplegw.approval.entities.SubListEntity;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.entities.Document;

public abstract class SubListService <T extends SubListEntity<T>, E extends ApprovalDocsDTO>  {
    // 세부 리스트를 가진 문서들에 Document 바인딩 공통 메서드
    protected List<T> bindDocsToEntities(List<T> entities, Document docs) {
        return entities.stream().map(entity -> entity.insertDocs(docs)).collect(Collectors.toList());
    }

    protected abstract List<T> searchByDocsId(Long docsId);
    protected abstract void saveEntities(List<T> entities);
    protected abstract void deleteEntities(Long docsId);
    protected abstract RequestResult checkSubList(E dto);
    protected abstract void subListSave(E dto, Document docs);
    protected abstract List<?> getSubDtoList(Long docsId);
}

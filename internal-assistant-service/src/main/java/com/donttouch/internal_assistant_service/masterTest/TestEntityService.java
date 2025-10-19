package com.donttouch.internal_assistant_service.masterTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestEntityService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public void readFromSlave() {
        em.createQuery("SELECT t FROM TestEntity t", TestEntity.class)
                .getResultList(); // 여기서 라우팅 로그가 바로 찍힘
    }

    @Transactional
    public void writeToMaster(String name, Double value) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        entity.setValue(value);
        em.persist(entity);
        em.flush(); // Master 강제 반영
    }
}

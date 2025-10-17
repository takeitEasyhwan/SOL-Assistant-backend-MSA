package com.donttouch.external_assistant_service.domain.news.entity;

import java.util.Map;

public class SectorNewsSource {

    public static final Map<String, String> SECTOR_URL_MAP = Map.ofEntries(
            Map.entry("금속", "https://www.hankyung.com/industry/steel-chemical"),//산업
            Map.entry("제약", "https://search.hankyung.com/search/news?query=제약"),//검색
            Map.entry("운송장비·부품", "https://www.hankyung.com/industry/auto-battery"),//산업
            Map.entry("화학", "https://www.hankyung.com/industry/steel-chemical"),//산업
            Map.entry("통신", "https://search.hankyung.com/search/news?query=통신"),//검색
            Map.entry("기타금융", "https://www.hankyung.com/financial-market/financial-policy"),//금융
            Map.entry("전기·전자", "https://www.hankyung.com/industry/semicon-electronics"),//산업
            Map.entry("IT 서비스", "https://www.hankyung.com/tech/technews"), //테크
            Map.entry("유통", "https://search.hankyung.com/search/news?query=유통"),//검색
            Map.entry("기계·장비", "https://www.hankyung.com/industry/build-machinery"),//산업
            Map.entry("음식료·담배", "https://search.hankyung.com/search/news?query=음식료+담배"),//검색
            Map.entry("전기·가스", "https://search.hankyung.com/search/news?query=전기+가스"),//검색
            Map.entry("건설", "https://www.hankyung.com/industry/build-machinery")//산업
    );
}

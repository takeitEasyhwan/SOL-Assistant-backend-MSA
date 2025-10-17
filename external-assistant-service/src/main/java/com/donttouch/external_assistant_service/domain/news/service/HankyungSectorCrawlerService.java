package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSource;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class HankyungSectorCrawlerService {
    public List<SectorNews> crawlSector(String sector) {
        String url = SectorNewsSource.SECTOR_URL_MAP.get(sector);
        if (url == null) {
            log.warn("[{}] URL 없음", sector);
            return List.of();
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        List<SectorNews> articles  = new ArrayList<>();
        try {
            if (isSearchSector(sector)) {
                articles = crawlSearchPage(driver);
            } else {
                articles = crawlCategoryPage(driver);
            }

            enrichArticleContents(driver, articles.stream().limit(3).toList());
            
        } catch (Exception e) {
            log.error("[{}] 섹터 크롤링 실패: {}", sector, e.getMessage());
        } finally {
            driver.quit();
        }
        return articles.stream().limit(3).toList();
    }

    private boolean isSearchSector(String sector) {
        return List.of("제약", "통신", "유통", "음식료·담배", "전기·가스").contains(sector);
    }

    private List<SectorNews> crawlCategoryPage(WebDriver driver) {
        List<SectorNews> articles = new ArrayList<>();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#contents > ul > li")));

        List<WebElement> list = driver.findElements(By.cssSelector("#contents > ul > li"));
        for (WebElement li : list) {
            try {
                WebElement div = li.findElement(By.cssSelector("div > div"));

                String title = "";
                String url = "";
                String date = "";

                try {
                    WebElement aTag = div.findElement(By.cssSelector("h2 > a"));
                    title = aTag.getText();
                    url = aTag.getAttribute("href");
                } catch (Exception ignored) {}
                try {
                    date = div.findElement(By.cssSelector("p.txt-date")).getText();
                } catch (Exception ignored) {}

                String sectorNewsId = UUID.randomUUID().toString();
                articles.add(SectorNews.builder()
                        .sectorNewsId(sectorNewsId)
                        .title(title)
                        .date(date)
                        .url(url)
                        .journal("한국경제")
                        .contents("")
                        .build());

            } catch (Exception e) {
                log.warn("뉴스 파싱 실패: {}", e.getMessage());
            }
        }
        return articles;
    }

    private List<SectorNews> crawlSearchPage(WebDriver driver) {
        List<SectorNews> articles = new ArrayList<>();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#content > div.left_cont > div > div.section.hk_news > div.section_cont > ul > li")));

        List<WebElement> list = driver.findElements(By.cssSelector("#content > div.left_cont > div > div.section.hk_news > div.section_cont > ul > li"));

        for (WebElement li : list) {
            try {
                WebElement div = li.findElement(By.cssSelector("div"));

                String title = "";
                String url = "";
                String journal = "";
                String date = "";

                try {
                    title = div.findElement(By.cssSelector("a > em")).getText();
                } catch (Exception ignored) {}
                try {
                    url = div.findElement(By.cssSelector("a")).getAttribute("href");
                } catch (Exception ignored) {}
                try {
                    journal = div.findElement(By.cssSelector("p.info > span:nth-child(1)")).getText();
                } catch (Exception ignored) {}
                try {
                    date = div.findElement(By.cssSelector("p.info > span.date_time")).getText();
                } catch (Exception ignored) {}
                String sectorNewsId = UUID.randomUUID().toString();

                articles.add(SectorNews.builder()
                        .sectorNewsId(sectorNewsId)
                        .title(title)
                        .date(date)
                        .url(url)
                        .journal(journal)
                        .contents("")
                        .build());

            } catch (Exception e) {
                log.warn("뉴스 파싱 실패: {}", e.getMessage());
            }
        }
        return articles;
    }

    private String extractArticleContent(WebDriver driver, String url) {
        try {
            driver.get(url);

            String[] selectors = {
                    "#articletxt",          // 일반 기사
                    "#magazineView",        // 매거진/오피니언
                    ".article-body",        // 일부 리디자인 버전
                    "#articletxt_wrap"      // 서브 기사
            };

            WebElement contentElement = null;

            for (String selector : selectors) {
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                    contentElement = driver.findElement(By.cssSelector(selector));
                    if (contentElement != null && !contentElement.getText().isBlank()) {
                        break;
                    }
                } catch (Exception ignored) {}
            }


            if (contentElement == null) return "";

            String content = contentElement.getText().trim();

            content = content.replaceAll("[가-힣]{2,4}\\s?기자\\s?[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+", "");
            content = content.replaceAll("ADVERTISEMENT", "");
            content = content.replaceAll("이미지 크게보기", "");
            content = content.replaceAll("\\n", " ");
            content = content.replaceAll("\\s{2,}", " ");
            content = content.trim();


            log.info("본문 추출 완료 [{}자]: {}", content.length(), url);
            return content;

        } catch (Exception e) {
            log.warn("본문 추출 실패 [{}]: {}", url, e.getMessage());
            return "";
        }
    }

    private void enrichArticleContents(WebDriver driver, List<SectorNews> articles) {
        for (SectorNews article : articles) {
            if (article.getUrl() == null || article.getUrl().isBlank()) continue;
            try {
                String content = extractArticleContent(driver, article.getUrl());
                article.setContents(content);
            } catch (Exception e) {
                log.warn("본문 추출 실패 [{}]: {}", article.getUrl(), e.getMessage());
            }
        }
    }

}


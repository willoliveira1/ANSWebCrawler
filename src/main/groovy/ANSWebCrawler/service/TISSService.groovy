package ANSWebCrawler.service

import ANSWebCrawler.domain.VersionHistory
import ANSWebCrawler.util.*
import org.jsoup.nodes.Document
import java.time.format.DateTimeFormatter
import java.time.LocalDate

class TISSService {

    Document getTISSContent() {
        String url = Paths.URL_PATH
        Document document = FileHandler.populateDocument(url)

        document.getElementsByTag("img").each {img ->
            if (img.attr("alt").equals(SearchTexts.PROVIDER_SPACE)) {
                url = img.parent().attr("href")
                document = FileHandler.populateDocument(url)
            }
        }

        document.getElementsByTag("a").each {span ->
            if (span.text().equals(SearchTexts.STANDARD_EXCHANGE)) {
                url = span.attr("href")
                document = FileHandler.populateDocument(url)
            }
        }

        return document
    }

    Document getActualTISSContent() {
        String url = ""
        Document document = this.getTISSContent()

        document.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains(SearchTexts.STANDARD_VERSION)) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                document = FileHandler.populateDocument(url)
            }
        }

        return document
    }

    private void getErrorTableFile() {
        String url = ""
        Document document = this.getTISSContent()

        document.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains(SearchTexts.RELATED_TABLES)) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                document = FileHandler.populateDocument(url)
            }
        }

        document.getElementsByTag("h2").each {h2 ->
            if (h2.text().equals(SearchTexts.TABLE_OF_ERRORS)) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        FileHandler.downloadFile(url, SearchTexts.SENDING_ERROR_TABLE)
    }

    private void getCommunicationComponentFile() {
        Document document = this.getActualTISSContent()
        String url = UrlHandler.populateUrlByEquals(document, SearchTexts.COMMUNICATION_COMPONENT)
        FileHandler.downloadFile(url, SearchTexts.COMMUNICATION_COMPONENT)
    }

    private void getOrganizationalComponentFile() {
        Document document = this.getActualTISSContent()
        String url = UrlHandler.populateUrlByEquals(document, SearchTexts.ORGANIZATIONAL_COMPONENT)
        FileHandler.downloadFile(url, SearchTexts.ORGANIZATIONAL_COMPONENT)
    }

    private void getContentAndStructureComponentFile() {
        Document document = this.getActualTISSContent()
        String url = UrlHandler.populateUrlByEquals(document, SearchTexts.CONTENT_COMPONENT)
        FileHandler.downloadFile(url, SearchTexts.CONTENT_COMPONENT)
    }

    private void getHealthConceptRepresentationComponentFile() {
        Document document = this.getActualTISSContent()
        String url = UrlHandler.populateUrlByContainsTd(document, SearchTexts.HEALTH_CONCEPT_COMPONENT)
        FileHandler.downloadFile(url, SearchTexts.HEALTH_CONCEPT_COMPONENT)
    }

    private void getSecurityAndPrivacyComponentFile() {
        Document document = this.getActualTISSContent()
        String url = UrlHandler.populateUrlByEquals(document, SearchTexts.SECURITY_COMPONENT)
        FileHandler.downloadFile(url, SearchTexts.SECURITY_COMPONENT)
    }

    void getVersionHistoryFile() {
        String url = ""
        Document document = this.getTISSContent()

        document.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains(SearchTexts.VERSION_HISTORY_COMPONENT)) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                document = FileHandler.populateDocument(url)
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Parameters.DATE_TIME_FORMATTER)
        LocalDate initialDate = LocalDate.parse(Parameters.INITIAL_DATE)
        List<VersionHistory> rows = new ArrayList<>()

        if (document != null) {
            int id = 1

            document.select('tr').drop(1).each {tr ->
                List<String> row = new ArrayList<>()

                row.add(id)
                tr.select('td').take(3).each {td ->
                    row.add(td.text())
                }

                LocalDate publication = LocalDate.parse(row.get(2), formatter)
                LocalDate beginningOfTerm = LocalDate.parse(row.get(3), formatter)

                if (beginningOfTerm >= initialDate) {
                    rows.add(new VersionHistory(row.get(0) as int, row.get(1), publication, beginningOfTerm))
                }
                id++
            }
        }

        FileHandler.writeFile(rows)
    }

    void getTISSFiles() {
        this.getCommunicationComponentFile()
        this.getOrganizationalComponentFile()
        this.getContentAndStructureComponentFile()
        this.getHealthConceptRepresentationComponentFile()
        this.getSecurityAndPrivacyComponentFile()
        this.getErrorTableFile()
    }

}

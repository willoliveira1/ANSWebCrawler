package ANSWebCrawler.util

import org.jsoup.nodes.Document

class UrlHandler {

    static String populateUrlByEquals(Document document, String text) {
        String url = ""
        document.getElementsByTag("td").each {td ->
            if (td.text().equals(text)) {
                url = td.lastElementSibling()
                    .firstElementChild()
                    .attr("href")
            }
        }
        return url
    }

    static String populateUrlByContainsTd(Document document, String text) {
        String url = ""
        document.getElementsByTag("td").each {td ->
            if (td.text().contains(text)) {
                url = td.lastElementSibling()
                    .firstElementChild()
                    .attr("href")
            }
        }
        return url
    }

}
